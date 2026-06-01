package com.example.loanagent.llm;

import com.example.loanagent.model.ApprovalStatus;

public class ParsedAction {
    private final String thought;
    private final String action;
    private final String actionInput;
    private final ApprovalStatus decision;
    private final String reason;

    public ParsedAction(String thought, String action, String actionInput, ApprovalStatus decision, String reason) {
        this.thought = thought;
        this.action = action;
        this.actionInput = actionInput;
        this.decision = decision;
        this.reason = reason;
    }

    public static ParsedAction finalAnswer(String thought, ApprovalStatus decision, String reason) {
        return new ParsedAction(thought, "final_answer", null, decision, reason);
    }

    public boolean isFinalAnswer() {
        return "final_answer".equals(action);
    }

    public String getThought() { return thought; }
    public String getAction() { return action; }
    public String getActionInput() { return actionInput; }
    public ApprovalStatus getDecision() { return decision; }
    public String getReason() { return reason; }
}
