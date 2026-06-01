package com.example.loanagent.audit;

import com.example.loanagent.agent.AgentStep;
import com.example.loanagent.model.ApprovalStatus;
import com.example.loanagent.model.AuditLogRecord;
import com.example.loanagent.repository.AuditLogRecordRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class AuditLogger {
    private static final Logger log = LoggerFactory.getLogger(AuditLogger.class);
    private final AuditLogRecordRepository repository;
    private final ObjectMapper objectMapper;

    public AuditLogger(AuditLogRecordRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    public void logStep(Long applicationId, AgentStep step) {
        repository.save(AuditLogRecord.step(applicationId, step.getIndex(), step.getThought(), step.getAction(),
                step.getToolName(), step.getToolInput(), step.getObservation()));
        Map<String, Object> event = new LinkedHashMap<>();
        event.put("event", "agent_step");
        event.put("applicationId", applicationId);
        event.put("stepIndex", step.getIndex());
        event.put("thought", step.getThought());
        event.put("action", step.getAction());
        event.put("toolName", step.getToolName());
        event.put("toolInput", step.getToolInput());
        event.put("observation", step.getObservation());
        event.put("timestampMs", System.currentTimeMillis());
        write(event);
    }

    public void logFinalResult(Long applicationId, ApprovalStatus finalDecision, boolean ruleOverride, int totalSteps, long totalDurationMs) {
        repository.save(AuditLogRecord.finalResult(applicationId, finalDecision, ruleOverride, totalSteps, totalDurationMs));
        Map<String, Object> event = new LinkedHashMap<>();
        event.put("event", "agent_final_result");
        event.put("applicationId", applicationId);
        event.put("finalDecision", finalDecision);
        event.put("ruleOverride", ruleOverride);
        event.put("totalSteps", totalSteps);
        event.put("totalDurationMs", totalDurationMs);
        write(event);
    }

    private void write(Map<String, Object> event) {
        try {
            log.info(objectMapper.writeValueAsString(event));
        } catch (Exception ex) {
            log.info("audit_event={}", event);
        }
    }
}
