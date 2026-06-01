package com.example.loanagent.model;

import javax.persistence.*;
import java.time.Instant;

@Entity
public class AuditLogRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long applicationId;
    private Integer stepIndex;
    @Column(length = 2000)
    private String thought;
    private String action;
    private String toolName;
    @Column(length = 1000)
    private String toolInput;
    @Column(length = 2000)
    private String observation;
    @Enumerated(EnumType.STRING)
    private ApprovalStatus finalDecision;
    private Boolean ruleOverride;
    private Integer totalSteps;
    private Long totalDurationMs;
    private Instant timestamp;

    protected AuditLogRecord() {
    }

    public static AuditLogRecord step(Long applicationId, int stepIndex, String thought, String action, String toolName, String toolInput, String observation) {
        AuditLogRecord record = new AuditLogRecord();
        record.applicationId = applicationId;
        record.stepIndex = stepIndex;
        record.thought = thought;
        record.action = action;
        record.toolName = toolName;
        record.toolInput = toolInput;
        record.observation = observation;
        record.timestamp = Instant.now();
        return record;
    }

    public static AuditLogRecord finalResult(Long applicationId, ApprovalStatus finalDecision, boolean ruleOverride, int totalSteps, long totalDurationMs) {
        AuditLogRecord record = new AuditLogRecord();
        record.applicationId = applicationId;
        record.action = "final_result";
        record.finalDecision = finalDecision;
        record.ruleOverride = ruleOverride;
        record.totalSteps = totalSteps;
        record.totalDurationMs = totalDurationMs;
        record.timestamp = Instant.now();
        return record;
    }

    public Long getId() { return id; }
    public Long getApplicationId() { return applicationId; }
    public Integer getStepIndex() { return stepIndex; }
    public String getThought() { return thought; }
    public String getAction() { return action; }
    public String getToolName() { return toolName; }
    public String getToolInput() { return toolInput; }
    public String getObservation() { return observation; }
    public ApprovalStatus getFinalDecision() { return finalDecision; }
    public Boolean getRuleOverride() { return ruleOverride; }
    public Integer getTotalSteps() { return totalSteps; }
    public Long getTotalDurationMs() { return totalDurationMs; }
    public Instant getTimestamp() { return timestamp; }
}
