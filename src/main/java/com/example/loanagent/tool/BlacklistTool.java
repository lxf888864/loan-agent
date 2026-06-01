package com.example.loanagent.tool;

import com.example.loanagent.agent.AgentContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
public class BlacklistTool implements AgentTool {
    private static final Set<String> BLACKLIST = new HashSet<>(Arrays.asList("user_blacklisted_001", "fraud_user_002", "dishonest_003"));
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String name() {
        return "blacklist";
    }

    @Override
    public String description() {
        return "查询用户是否在失信或欺诈黑名单中，输入示例：{\"userId\":\"user_blacklisted_001\"}";
    }

    @Override
    public String execute(String input) {
        String userId = ToolJson.userId(input);
        return json("blacklisted", BLACKLIST.contains(userId));
    }

    @Override
    public void applyObservation(AgentContext context, String observation) {
        context.setBlacklisted(ToolJson.parse(observation).path("blacklisted").asBoolean(false));
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
