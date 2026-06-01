package com.example.loanagent.rule;

import com.example.loanagent.agent.AgentContext;
import com.example.loanagent.model.ApprovalStatus;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Order(5)
@Component
public class BoundaryEscalationRule implements BusinessRule {
    @Override
    public RuleResult evaluate(AgentContext context) {
        Integer score = context.getCreditScore();
        BigDecimal dti = context.getDebtRatio();
        if (score == null || dti == null) {
            return RuleResult.pass("BoundaryEscalationRule");
        }
        boolean scoreBoundary = score >= 600 && score <= 650;
        boolean dtiBoundary = dti.compareTo(new BigDecimal("0.5")) >= 0 && dti.compareTo(new BigDecimal("0.6")) <= 0;
        if (scoreBoundary && dtiBoundary) {
            return RuleResult.fail("BoundaryEscalationRule", "信用分与负债收入比同时处于边界区间", ApprovalStatus.ESCALATED, "您的申请需要人工复核");
        }
        return RuleResult.pass("BoundaryEscalationRule");
    }
}
