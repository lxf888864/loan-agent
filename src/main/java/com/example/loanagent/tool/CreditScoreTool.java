package com.example.loanagent.tool;

import com.example.loanagent.agent.AgentContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CreditScoreTool implements AgentTool {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String name() {
        return "credit_score";
    }

    @Override
    public String description() {
        return "根据 userId 查询模拟信用分，输入示例：{\"userId\":\"user_good_001\"}";
    }

    @Override
    public String execute(String input) {
        String userId = ToolJson.userId(input);
        int score = score(userId);
        return json("creditScore", score);
    }

    @Override
    public void applyObservation(AgentContext context, String observation) {
        context.setCreditScore(ToolJson.integer(observation, "creditScore", 0));
    }

    public int score(String userId) {
        if ("user_low_credit_001".equals(userId)) return 580;
        if ("user_boundary_001".equals(userId)) return 630;
        if ("user_good_001".equals(userId) || "user_blacklisted_001".equals(userId)) return 720;
        return 300 + Math.abs(userId.hashCode() % 551);
    }

    private String json(String key, Object value) {
        try {
            Map<String, Object> map = new HashMap<>();
            map.put(key, value);
            return objectMapper.writeValueAsString(map);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }
}
