package com.example.loanagent.rule;

import com.example.loanagent.agent.AgentContext;

public interface BusinessRule {
    RuleResult evaluate(AgentContext context);
}
