package com.example.loanagent.tool;

import com.example.loanagent.agent.AgentContext;

public interface AgentTool {
    String name();
    String description();
    String execute(String input);

    default void applyObservation(AgentContext context, String observation) {
    }
}
