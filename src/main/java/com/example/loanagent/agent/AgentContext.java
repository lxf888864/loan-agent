package com.example.loanagent.agent;

import com.example.loanagent.model.ApprovalStatus;
import com.example.loanagent.model.LoanApplication;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AgentContext {
    private final LoanApplication application;
    private final List<AgentStep> steps = new ArrayList<>();
    private Integer creditScore;
    private BigDecimal debtRatio;
    private Boolean blacklisted;
    private BigDecimal repaymentCapacityRatio;
    private ApprovalStatus finalDecision;
    private String finalReason;
    private boolean ruleOverride;

    public AgentContext(LoanApplication application) {
        this.application = application;
    }

    public void addStep(AgentStep step) {
        this.steps.add(step);
    }

    public LoanApplication getApplication() { return application; }
    public List<AgentStep> getSteps() { return Collections.unmodifiableList(steps); }
    public Integer getCreditScore() { return creditScore; }
    public void setCreditScore(Integer creditScore) { this.creditScore = creditScore; }
    public BigDecimal getDebtRatio() { return debtRatio; }
    public void setDebtRatio(BigDecimal debtRatio) { this.debtRatio = debtRatio; }
    public Boolean getBlacklisted() { return blacklisted; }
    public void setBlacklisted(Boolean blacklisted) { this.blacklisted = blacklisted; }
    public BigDecimal getRepaymentCapacityRatio() { return repaymentCapacityRatio; }
    public void setRepaymentCapacityRatio(BigDecimal repaymentCapacityRatio) { this.repaymentCapacityRatio = repaymentCapacityRatio; }
    public ApprovalStatus getFinalDecision() { return finalDecision; }
    public void setFinalDecision(ApprovalStatus finalDecision) { this.finalDecision = finalDecision; }
    public String getFinalReason() { return finalReason; }
    public void setFinalReason(String finalReason) { this.finalReason = finalReason; }
    public boolean isRuleOverride() { return ruleOverride; }
    public void setRuleOverride(boolean ruleOverride) { this.ruleOverride = ruleOverride; }

    public String toPromptText() {
        StringBuilder sb = new StringBuilder();
        sb.append("申请ID: ").append(application.getId()).append('\n');
        sb.append("用户ID: ").append(application.getUserId()).append('\n');
        sb.append("姓名: ").append(application.getApplicantName()).append('\n');
        sb.append("月收入: ").append(application.getMonthlyIncome()).append('\n');
        sb.append("申请金额: ").append(application.getLoanAmount()).append('\n');
        sb.append("期限（月）: ").append(application.getTermMonths()).append('\n');
        sb.append("用途: ").append(application.getPurpose()).append('\n');
        sb.append("已知信用分: ").append(creditScore).append('\n');
        sb.append("已知负债收入比: ").append(debtRatio).append('\n');
        sb.append("是否黑名单: ").append(blacklisted).append('\n');
        sb.append("还款能力比: ").append(repaymentCapacityRatio).append('\n');
        sb.append("历史步骤:\n");
        for (AgentStep step : steps) {
            sb.append("- step ").append(step.getIndex())
                    .append(", thought=").append(step.getThought())
                    .append(", action=").append(step.getAction())
                    .append(", input=").append(step.getToolInput())
                    .append(", observation=").append(step.getObservation())
                    .append('\n');
        }
        return sb.toString();
    }
}
