package com.example.loanagent.model;

import javax.persistence.*;
import java.time.Instant;

@Entity
public class EscalationQueue {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long applicationId;
    @Column(length = 1000)
    private String reason;
    private String status;
    @Enumerated(EnumType.STRING)
    private ApprovalStatus resolvedDecision;
    private String resolvedBy;
    private Instant createdAt;
    private Instant resolvedAt;

    protected EscalationQueue() {
    }

    public EscalationQueue(Long applicationId, String reason) {
        this.applicationId = applicationId;
        this.reason = reason;
        this.status = "PENDING";
        this.createdAt = Instant.now();
    }

    public void resolve(ApprovalStatus decision, String operator) {
        this.resolvedDecision = decision;
        this.resolvedBy = operator;
        this.status = "RESOLVED";
        this.resolvedAt = Instant.now();
    }

    public Long getId() { return id; }
    public Long getApplicationId() { return applicationId; }
    public String getReason() { return reason; }
    public String getStatus() { return status; }
    public ApprovalStatus getResolvedDecision() { return resolvedDecision; }
    public String getResolvedBy() { return resolvedBy; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getResolvedAt() { return resolvedAt; }
}
