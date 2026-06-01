package com.example.loanagent.model;

import javax.persistence.*;
import java.time.Instant;

@Entity
public class ApprovalResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long applicationId;
    @Enumerated(EnumType.STRING)
    private ApprovalStatus status;
    @Column(length = 1000)
    private String reason;
    private boolean ruleOverride;
    private int totalSteps;
    private long totalDurationMs;
    private Instant createdAt;

    protected ApprovalResult() {
    }

    public ApprovalResult(Long applicationId, ApprovalStatus status, String reason, boolean ruleOverride, int totalSteps, long totalDurationMs) {
        this.applicationId = applicationId;
        this.status = status;
        this.reason = reason;
        this.ruleOverride = ruleOverride;
        this.totalSteps = totalSteps;
        this.totalDurationMs = totalDurationMs;
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public Long getApplicationId() { return applicationId; }
    public ApprovalStatus getStatus() { return status; }
    public String getReason() { return reason; }
    public boolean isRuleOverride() { return ruleOverride; }
    public int getTotalSteps() { return totalSteps; }
    public long getTotalDurationMs() { return totalDurationMs; }
    public Instant getCreatedAt() { return createdAt; }
}
