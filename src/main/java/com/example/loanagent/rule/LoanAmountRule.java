package com.example.loanagent.rule;

import com.example.loanagent.agent.AgentContext;
import com.example.loanagent.model.ApprovalStatus;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Order(3)
@Component
public class LoanAmountRule implements BusinessRule {
    @Override
    public RuleResult evaluate(AgentContext context) {
        BigDecimal maxAmount = context.getApplication().getMonthlyIncome().multiply(new BigDecimal("24"));
        if (context.getApplication().getLoanAmount().compareTo(maxAmount) > 0) {
            return RuleResult.fail("LoanAmountRule", "贷款金额超过月收入 24 倍", ApprovalStatus.REJECTED, "您的申请金额超过当前收入可支持的额度");
        }
        return RuleResult.pass("LoanAmountRule");
    }
}
