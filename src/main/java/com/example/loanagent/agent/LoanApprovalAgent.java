package com.example.loanagent.agent;

import com.example.loanagent.audit.AuditLogger;
import com.example.loanagent.llm.LlmClient;
import com.example.loanagent.llm.LlmRequest;
import com.example.loanagent.llm.LlmResponse;
import com.example.loanagent.llm.LlmResponseParser;
import com.example.loanagent.llm.ParsedAction;
import com.example.loanagent.model.ApprovalResult;
import com.example.loanagent.model.ApprovalStatus;
import com.example.loanagent.model.LoanApplication;
import com.example.loanagent.prompt.SystemPromptBuilder;
import com.example.loanagent.rule.RuleEngine;
import com.example.loanagent.rule.RuleResult;
import com.example.loanagent.tool.AgentTool;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class LoanApprovalAgent {
    private final LlmClient llmClient;
    private final LlmResponseParser parser;
    private final RuleEngine ruleEngine;
    private final SystemPromptBuilder systemPromptBuilder;
    private final AuditLogger auditLogger;
    private final Map<String, AgentTool> toolRegistry;
    private final int maxSteps;

    public LoanApprovalAgent(LlmClient llmClient,
                             LlmResponseParser parser,
                             RuleEngine ruleEngine,
                             SystemPromptBuilder systemPromptBuilder,
                             AuditLogger auditLogger,
                             List<AgentTool> tools,
                             @Value("${agent.max-steps:8}") int maxSteps) {
        this.llmClient = llmClient;
        this.parser = parser;
        this.ruleEngine = ruleEngine;
        this.systemPromptBuilder = systemPromptBuilder;
        this.auditLogger = auditLogger;
        this.toolRegistry = tools.stream().collect(Collectors.toMap(AgentTool::name, Function.identity()));
        this.maxSteps = maxSteps;
    }

    public ApprovalResult approve(LoanApplication application) {
        long start = System.currentTimeMillis();
        AgentContext context = new AgentContext(application);

        for (int i = 0; i < maxSteps; i++) {
            LlmResponse thought = llmClient.chat(new LlmRequest(systemPromptBuilder.build(toolRegistry.values()), buildReActPrompt(context)));
            ParsedAction action = parser.parse(thought);

            if (action.isFinalAnswer()) {
                applyFinalDecision(context, action);
                long duration = System.currentTimeMillis() - start;
                auditLogger.logFinalResult(application.getId(), context.getFinalDecision(), context.isRuleOverride(), context.getSteps().size(), duration);
                return new ApprovalResult(application.getId(), context.getFinalDecision(), context.getFinalReason(),
                        context.isRuleOverride(), context.getSteps().size(), duration);
            }

            AgentTool tool = toolRegistry.get(action.getAction());
            String observation;
            if (tool == null) {
                // 工具名非法时升级人工，避免继续让模型在错误动作上循环。
                action = ParsedAction.finalAnswer("工具不存在，升级人工审核", ApprovalStatus.ESCALATED, "您的申请需要人工复核");
                applyFinalDecision(context, action);
                break;
            }
            observation = tool.execute(action.getActionInput());
            tool.applyObservation(context, observation);
            AgentStep step = AgentStep.of(context.getSteps().size() + 1, thought, action, observation);
            context.addStep(step);
            auditLogger.logStep(application.getId(), step);
        }

        if (context.getFinalDecision() == null) {
            context.setFinalDecision(ApprovalStatus.ESCALATED);
            context.setFinalReason("您的申请需要人工复核");
        }
        long duration = System.currentTimeMillis() - start;
        auditLogger.logFinalResult(application.getId(), context.getFinalDecision(), context.isRuleOverride(), context.getSteps().size(), duration);
        return new ApprovalResult(application.getId(), context.getFinalDecision(), context.getFinalReason(),
                context.isRuleOverride(), context.getSteps().size(), duration);
    }

    private String buildReActPrompt(AgentContext context) {
        return "请根据当前申请上下文决定下一步。若关键信息不足，请调用一个工具；若信息足够，请输出 final_answer。\n\n"
                + context.toPromptText();
    }

    private void applyFinalDecision(AgentContext context, ParsedAction action) {
        context.setFinalDecision(action.getDecision());
        context.setFinalReason(action.getReason());
        RuleResult ruleResult = ruleEngine.validate(context, action.getDecision());
        if (!ruleResult.isPass()) {
            context.setFinalDecision(ruleResult.getForcedDecision());
            context.setFinalReason(ruleResult.getUserVisibleReason());
            context.setRuleOverride(true);
        }
    }
}
