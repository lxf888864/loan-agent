package com.example.loanagent.tool;

import com.example.loanagent.agent.AgentContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;

@Component
public class RepaymentCapacityTool implements AgentTool {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String name() {
        return "repayment_capacity";
    }

    @Override
    public String description() {
        return "根据月收入、申请金额和期数估算月供收入比，输入示例：{\"monthlyIncome\":20000,\"loanAmount\":200000,\"termMonths\":24}";
    }

    @Override
    public String execute(String input) {
        BigDecimal monthlyIncome = ToolJson.decimal(input, "monthlyIncome", BigDecimal.ONE);
        BigDecimal loanAmount = ToolJson.decimal(input, "loanAmount", BigDecimal.ZERO);
        int termMonths = Math.max(1, ToolJson.integer(input, "termMonths", 12));
        BigDecimal monthlyPayment = loanAmount.divide(new BigDecimal(termMonths), 2, RoundingMode.HALF_UP);
        BigDecimal ratio = monthlyPayment.divide(monthlyIncome, 4, RoundingMode.HALF_UP);
        Map<String, Object> map = new HashMap<>();
        map.put("monthlyPayment", monthlyPayment);
        map.put("repaymentCapacityRatio", ratio);
        try {
            return objectMapper.writeValueAsString(map);
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    @Override
    public void applyObservation(AgentContext context, String observation) {
        context.setRepaymentCapacityRatio(ToolJson.decimal(observation, "repaymentCapacityRatio", BigDecimal.ZERO));
    }
}
