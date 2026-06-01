package com.example.loanagent.llm;

import com.example.loanagent.model.ApprovalStatus;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

@Component
public class LlmResponseParser {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ParsedAction parse(LlmResponse response) {
        try {
            String json = stripMarkdown(response.getContent());
            JsonNode node = objectMapper.readTree(json);
            String thought = text(node, "thought");
            String action = text(node, "action");
            String actionInput = node.hasNonNull("action_input") ? node.get("action_input").asText() : null;
            String reason = text(node, "reason");
            ApprovalStatus decision = null;
            if ("final_answer".equals(action)) {
                String decisionText = text(node, "decision");
                decision = ApprovalStatus.valueOf(decisionText);
                if (reason == null || reason.trim().isEmpty()) {
                    reason = "您的申请需要人工复核";
                }
            }
            return new ParsedAction(thought, action, actionInput, decision, reason);
        } catch (Exception ex) {
            // 解析失败说明模型输出不可信，直接升级人工审核更稳妥。
            return ParsedAction.finalAnswer("LLM 输出无法解析，触发保护性升级", ApprovalStatus.ESCALATED, "您的申请需要人工复核");
        }
    }

    private String stripMarkdown(String content) {
        String trimmed = content == null ? "" : content.trim();
        if (trimmed.startsWith("```")) {
            int firstNewLine = trimmed.indexOf('\n');
            int lastFence = trimmed.lastIndexOf("```");
            if (firstNewLine >= 0 && lastFence > firstNewLine) {
                return trimmed.substring(firstNewLine + 1, lastFence).trim();
            }
        }
        return trimmed;
    }

    private String text(JsonNode node, String field) {
        return node.hasNonNull(field) ? node.get(field).asText() : null;
    }
}
