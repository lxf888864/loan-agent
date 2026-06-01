package com.example.loanagent.tool;

import com.example.loanagent.agent.AgentContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@Component
public class DebtRatioTool implements AgentTool {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String name() {
        return "debt_ratio";
    }

    @Override
    public String description() {
        return "根据 userId 查询模拟负债收入比 DTI，输入示例：{\"userId\":\"user_good_001\"}";
    }

    @Override
    public String execute(String input) {
        String userId = ToolJson.userId(input);
        return json("debtRatio", ratio(userId));
    }

    @Override
    public void applyObservation(AgentContext context, String observation) {
        context.setDebtRatio(ToolJson.decimal(observation, "debtRatio", BigDecimal.ZERO));
    }

    public BigDecimal ratio(String userId) {
        if ("user_boundary_001".equals(userId)) return new BigDecimal("0.55");
        if ("user_good_001".equals(userId) || "user_blacklisted_001".equals(userId) || "user_low_credit_001".equals(userId)) return new BigDecimal("0.30");
        int bucket = Math.abs(userId.hashCode() % 71);
        return new BigDecimal("0.10").add(new BigDecimal(bucket).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP));
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
