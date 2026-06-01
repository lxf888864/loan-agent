package com.example.loanagent.llm;

public class LlmRequest {
    private final String systemPrompt;
    private final String userPrompt;

    public LlmRequest(String systemPrompt, String userPrompt) {
        this.systemPrompt = systemPrompt;
        this.userPrompt = userPrompt;
    }

    public String getSystemPrompt() { return systemPrompt; }
    public String getUserPrompt() { return userPrompt; }
}
