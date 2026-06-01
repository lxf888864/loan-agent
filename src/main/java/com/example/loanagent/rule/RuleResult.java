package com.example.loanagent.rule;

import com.example.loanagent.model.ApprovalStatus;

public class RuleResult {
    private final boolean pass;
    private final String ruleName;
    private final String reason;
    private final ApprovalStatus forcedDecision;
    private final String userVisibleReason;

    private RuleResult(boolean pass, String ruleName, String reason, ApprovalStatus forcedDecision, String userVisibleReason) {
        this.pass = pass;
        this.ruleName = ruleName;
        this.reason = reason;
        this.forcedDecision = forcedDecision;
        this.userVisibleReason = userVisibleReason;
    }

    public static RuleResult pass(String ruleName) {
        return new RuleResult(true, ruleName, "通过", null, null);
    }

    public static RuleResult fail(String ruleName, String reason, ApprovalStatus forcedDecision, String userVisibleReason) {
        return new RuleResult(false, ruleName, reason, forcedDecision, userVisibleReason);
    }

    public boolean isPass() { return pass; }
    public String getRuleName() { return ruleName; }
    public String getReason() { return reason; }
    public ApprovalStatus getForcedDecision() { return forcedDecision; }
    public String getUserVisibleReason() { return userVisibleReason; }
}
