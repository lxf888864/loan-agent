package com.example.loanagent.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
public class LoanApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userId;
    private String applicantName;
    private BigDecimal monthlyIncome;
    private BigDecimal loanAmount;
    private Integer termMonths;
    private String purpose;
    @Enumerated(EnumType.STRING)
    private ApprovalStatus status;
    private Instant createdAt;

    protected LoanApplication() {
    }

    public LoanApplication(String userId, String applicantName, BigDecimal monthlyIncome, BigDecimal loanAmount, Integer termMonths, String purpose) {
        this.userId = userId;
        this.applicantName = applicantName;
        this.monthlyIncome = monthlyIncome;
        this.loanAmount = loanAmount;
        this.termMonths = termMonths;
        this.purpose = purpose;
        this.status = ApprovalStatus.PENDING;
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public String getUserId() { return userId; }
    public String getApplicantName() { return applicantName; }
    public BigDecimal getMonthlyIncome() { return monthlyIncome; }
    public BigDecimal getLoanAmount() { return loanAmount; }
    public Integer getTermMonths() { return termMonths; }
    public String getPurpose() { return purpose; }
    public ApprovalStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
    public void setStatus(ApprovalStatus status) { this.status = status; }
}
