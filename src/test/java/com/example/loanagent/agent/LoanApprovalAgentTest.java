package com.example.loanagent.agent;

import com.example.loanagent.audit.AuditLogger;
import com.example.loanagent.llm.LlmClient;
import com.example.loanagent.llm.LlmRequest;
import com.example.loanagent.llm.LlmResponse;
import com.example.loanagent.llm.LlmResponseParser;
import com.example.loanagent.model.ApprovalResult;
import com.example.loanagent.model.ApprovalStatus;
import com.example.loanagent.model.LoanApplication;
import com.example.loanagent.prompt.SystemPromptBuilder;
import com.example.loanagent.rule.*;
import com.example.loanagent.tool.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class LoanApprovalAgentTest {
    @Test
    void shouldApproveNormalApplication() {
        LoanApprovalAgent agent = agentWithResponses(
                tool("blacklist", "{\"userId\":\"user_good_001\"}"),
                tool("credit_score", "{\"userId\":\"user_good_001\"}"),
                tool("debt_ratio", "{\"userId\":\"user_good_001\"}"),
                finalAnswer("APPROVED", "综合评估通过")
        );

        ApprovalResult result = agent.approve(application("user_good_001", "20000", "200000"));

        assertThat(result.getStatus()).isEqualTo(ApprovalStatus.APPROVED);
        assertThat(result.isRuleOverride()).isFalse();
        assertThat(result.getTotalSteps()).isEqualTo(3);
    }

    @Test
    void shouldRejectLowCreditScoreEvenWhenLlmApproves() {
        LoanApprovalAgent agent = agentWithResponses(
                tool("credit_score", "{\"userId\":\"user_low_credit_001\"}"),
                finalAnswer("APPROVED", "模型认为可以通过")
        );

        ApprovalResult result = agent.approve(application("user_low_credit_001", "20000", "100000"));

        assertThat(result.getStatus()).isEqualTo(ApprovalStatus.REJECTED);
        assertThat(result.isRuleOverride()).isTrue();
        assertThat(result.getReason()).contains("信用");
    }

    @Test
    void shouldRejectBlacklistedUserWithoutLeakingReason() {
        LoanApprovalAgent agent = agentWithResponses(
                tool("blacklist", "{\"userId\":\"user_blacklisted_001\"}"),
                finalAnswer("APPROVED", "模型认为可以通过")
        );

        ApprovalResult result = agent.approve(application("user_blacklisted_001", "30000", "100000"));

        assertThat(result.getStatus()).isEqualTo(ApprovalStatus.REJECTED);
        assertThat(result.getReason()).isEqualTo("您的申请未能通过审核");
    }

    @Test
    void shouldEscalateBoundaryCase() {
        LoanApprovalAgent agent = agentWithResponses(
                tool("credit_score", "{\"userId\":\"user_boundary_001\"}"),
                tool("debt_ratio", "{\"userId\":\"user_boundary_001\"}"),
                finalAnswer("APPROVED", "模型认为可以通过")
        );

        ApprovalResult result = agent.approve(application("user_boundary_001", "20000", "100000"));

        assertThat(result.getStatus()).isEqualTo(ApprovalStatus.ESCALATED);
        assertThat(result.isRuleOverride()).isTrue();
    }

    @Test
    void shouldEscalateWhenLlmFallbackReturnsEscalation() {
        LoanApprovalAgent agent = agentWithResponses(finalAnswer("ESCALATED", "您的申请需要人工复核"));

        ApprovalResult result = agent.approve(application("user_good_001", "20000", "100000"));

        assertThat(result.getStatus()).isEqualTo(ApprovalStatus.ESCALATED);
        assertThat(result.getReason()).isEqualTo("您的申请需要人工复核");
    }

    private LoanApprovalAgent agentWithResponses(LlmResponse... responses) {
        LlmClient llmClient = mock(LlmClient.class);
        when(llmClient.chat(any(LlmRequest.class))).thenReturn(responses[0], Arrays.copyOfRange(responses, 1, responses.length));
        RuleEngine ruleEngine = new RuleEngine(Arrays.asList(
                new BlacklistRule(),
                new CreditScoreRule(),
                new LoanAmountRule(),
                new DebtRatioRule(),
                new BoundaryEscalationRule()
        ));
        return new LoanApprovalAgent(llmClient, new LlmResponseParser(), ruleEngine, new SystemPromptBuilder(),
                mock(AuditLogger.class),
                Arrays.asList(new CreditScoreTool(), new DebtRatioTool(), new BlacklistTool(), new RepaymentCapacityTool()), 8);
    }

    private LoanApplication application(String userId, String income, String amount) {
        return new LoanApplication(userId, "测试用户", new BigDecimal(income), new BigDecimal(amount), 24, "测试");
    }

    private LlmResponse tool(String action, String input) {
        String escaped = input.replace("\"", "\\\"");
        return new LlmResponse("{\"thought\":\"需要查询工具\",\"action\":\"" + action + "\",\"action_input\":\"" + escaped + "\",\"decision\":null,\"reason\":null}");
    }

    private LlmResponse finalAnswer(String decision, String reason) {
        return new LlmResponse("{\"thought\":\"信息足够，给出结论\",\"action\":\"final_answer\",\"action_input\":null,\"decision\":\"" + decision + "\",\"reason\":\"" + reason + "\"}");
    }
}
