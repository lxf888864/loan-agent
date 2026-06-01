package com.example.loanagent.rule;

import com.example.loanagent.agent.AgentContext;
import com.example.loanagent.model.ApprovalStatus;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Order(1)
@Component
public class BlacklistRule implements BusinessRule {
    @Override
    public RuleResult evaluate(AgentContext context) {
        if (Boolean.TRUE.equals(context.getBlacklisted())) {
            return RuleResult.fail("BlacklistRule", "命中黑名单", ApprovalStatus.REJECTED, "您的申请未能通过审核");
        }
        return RuleResult.pass("BlacklistRule");
    }
}
