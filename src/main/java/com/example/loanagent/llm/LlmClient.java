package com.example.loanagent.llm;

public interface LlmClient {
    LlmResponse chat(LlmRequest request);
}
