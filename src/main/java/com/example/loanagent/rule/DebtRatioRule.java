package com.example.loanagent.rule;

import com.example.loanagent.agent.AgentContext;
import com.example.loanagent.model.ApprovalStatus;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Order(4)
@Component
public class DebtRatioRule implements BusinessRule {
    @Override
    public RuleResult evaluate(AgentContext context) {
        if (context.getDebtRatio() != null && context.getDebtRatio().compareTo(new BigDecimal("0.6")) > 0) {
            return RuleResult.fail("DebtRatioRule", "负债收入比超过 0.6", ApprovalStatus.REJECTED, "您的当前负债水平较高，暂不符合本产品要求");
        }
        return RuleResult.pass("DebtRatioRule");
    }
}
