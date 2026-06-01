package com.example.loanagent.llm;

import com.example.loanagent.config.LlmConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Component
public class ZhipuLlmClient implements LlmClient {
    private final LlmConfig config;
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    public ZhipuLlmClient(LlmConfig config, ObjectMapper objectMapper) {
        this.config = config;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(config.getTimeout().getConnect()))
                .build();
    }

    @Override
    @RateLimiter(name = "llm-api")
    @CircuitBreaker(name = "llm-api", fallbackMethod = "fallback")
    public LlmResponse chat(LlmRequest request) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("model", config.getModel());
            body.put("temperature", 0.1);
            body.put("messages", Arrays.asList(
                    message("system", request.getSystemPrompt()),
                    message("user", request.getUserPrompt())
            ));

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(config.getBaseUrl() + "/chat/completions"))
                    .timeout(Duration.ofMillis(config.getTimeout().getRead()))
                    .header("Authorization", "Bearer " + config.getApiKey())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)))
                    .build();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException("LLM HTTP error: " + response.statusCode());
            }
            JsonNode root = objectMapper.readTree(response.body());
            String content = root.path("choices").get(0).path("message").path("content").asText();
            return new LlmResponse(content);
        } catch (Exception ex) {
            throw new IllegalStateException("LLM 调用失败", ex);
        }
    }

    public LlmResponse fallback(LlmRequest request, Throwable throwable) {
        return new LlmResponse("{\"thought\":\"LLM 服务不可用，按可靠性策略升级人工审核\",\"action\":\"final_answer\",\"action_input\":null,\"decision\":\"ESCALATED\",\"reason\":\"您的申请需要人工复核\"}");
    }

    private Map<String, String> message(String role, String content) {
        Map<String, String> message = new HashMap<>();
        message.put("role", role);
        message.put("content", content);
        return message;
    }
}
