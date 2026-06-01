package com.example.loanagent.agent;

import com.example.loanagent.llm.LlmResponse;
import com.example.loanagent.llm.ParsedAction;

import java.time.Instant;

public class AgentStep {
    private final int index;
    private final String thought;
    private final String action;
    private final String toolName;
    private final String toolInput;
    private final String observation;
    private final Instant timestamp;

    private AgentStep(int index, String thought, String action, String toolName, String toolInput, String observation) {
        this.index = index;
        this.thought = thought;
        this.action = action;
        this.toolName = toolName;
        this.toolInput = toolInput;
        this.observation = observation;
        this.timestamp = Instant.now();
    }

    public static AgentStep of(int index, LlmResponse response, ParsedAction action, String observation) {
        return new AgentStep(index, action.getThought(), action.getAction(), action.getAction(), action.getActionInput(), observation);
    }

    public int getIndex() { return index; }
    public String getThought() { return thought; }
    public String getAction() { return action; }
    public String getToolName() { return toolName; }
    public String getToolInput() { return toolInput; }
    public String getObservation() { return observation; }
    public Instant getTimestamp() { return timestamp; }
}
