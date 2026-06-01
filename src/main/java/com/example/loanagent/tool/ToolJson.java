package com.example.loanagent.tool;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;

final class ToolJson {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private ToolJson() {
    }

    static String userId(String input) {
        JsonNode node = parse(input);
        if (node != null && node.hasNonNull("userId")) {
            return node.get("userId").asText();
        }
        return input == null ? "" : input.replace("\"", "").trim();
    }

    static BigDecimal decimal(String input, String field, BigDecimal fallback) {
        JsonNode node = parse(input);
        return node != null && node.hasNonNull(field) ? node.get(field).decimalValue() : fallback;
    }

    static int integer(String input, String field, int fallback) {
        JsonNode node = parse(input);
        return node != null && node.hasNonNull(field) ? node.get(field).asInt() : fallback;
    }

    static JsonNode parse(String input) {
        try {
            if (input == null || !input.trim().startsWith("{")) {
                return null;
            }
            return MAPPER.readTree(input);
        } catch (Exception ex) {
            return null;
        }
    }
}
