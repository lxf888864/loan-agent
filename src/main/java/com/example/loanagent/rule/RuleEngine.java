package com.example.loanagent.rule;

import com.example.loanagent.agent.AgentContext;
import com.example.loanagent.model.ApprovalStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RuleEngine {
    private final List<BusinessRule> rules;

    public RuleEngine(List<BusinessRule> rules) {
        this.rules = rules;
    }

    public RuleResult validate(AgentContext context, ApprovalStatus llmDecision) {
        for (BusinessRule rule : rules) {
            RuleResult result = rule.evaluate(context);
            if (!result.isPass()) {
                return result;
            }
        }
        return RuleResult.pass("ALL");
    }
}
