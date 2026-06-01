package com.example.loanagent.rule;

import com.example.loanagent.agent.AgentContext;
import com.example.loanagent.model.ApprovalStatus;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(2)
@Component
public class CreditScoreRule implements BusinessRule {
    @Override
    public RuleResult evaluate(AgentContext context) {
        if (context.getCreditScore() != null && context.getCreditScore() < 600) {
            return RuleResult.fail("CreditScoreRule", "信用分低于 600", ApprovalStatus.REJECTED, "您的信用评估未达到本产品准入要求");
        }
        return RuleResult.pass("CreditScoreRule");
    }
}
