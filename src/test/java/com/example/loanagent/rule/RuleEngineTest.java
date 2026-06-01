package com.example.loanagent.rule;

import com.example.loanagent.agent.AgentContext;
import com.example.loanagent.model.ApprovalStatus;
import com.example.loanagent.model.LoanApplication;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

class RuleEngineTest {
    private final RuleEngine engine = new RuleEngine(Arrays.asList(
            new BlacklistRule(),
            new CreditScoreRule(),
            new LoanAmountRule(),
            new DebtRatioRule(),
            new BoundaryEscalationRule()
    ));

    @Test
    void creditScoreBoundaryShouldRejectBelow600AndPassAt600WhenDtiLow() {
        AgentContext low = context("10000", "100000");
        low.setCreditScore(599);
        assertThat(engine.validate(low, ApprovalStatus.APPROVED).getForcedDecision()).isEqualTo(ApprovalStatus.REJECTED);

        AgentContext pass = context("10000", "100000");
        pass.setCreditScore(600);
        pass.setDebtRatio(new BigDecimal("0.30"));
        assertThat(engine.validate(pass, ApprovalStatus.APPROVED).isPass()).isTrue();
    }

    @Test
    void loanAmountBoundaryShouldPassAt24TimesAndRejectAbove() {
        AgentContext pass = context("10000", "240000");
        assertThat(engine.validate(pass, ApprovalStatus.APPROVED).isPass()).isTrue();

        AgentContext fail = context("10000", "240001");
        assertThat(engine.validate(fail, ApprovalStatus.APPROVED).getForcedDecision()).isEqualTo(ApprovalStatus.REJECTED);
    }

    @Test
    void debtRatioBoundaryShouldPassAtPointSixAndRejectAboveWhenNotBoundaryScore() {
        AgentContext pass = context("10000", "100000");
        pass.setCreditScore(700);
        pass.setDebtRatio(new BigDecimal("0.60"));
        assertThat(engine.validate(pass, ApprovalStatus.APPROVED).isPass()).isTrue();

        AgentContext fail = context("10000", "100000");
        fail.setDebtRatio(new BigDecimal("0.61"));
        assertThat(engine.validate(fail, ApprovalStatus.APPROVED).getForcedDecision()).isEqualTo(ApprovalStatus.REJECTED);
    }

    @Test
    void blacklistShouldAlwaysReject() {
        AgentContext context = context("10000", "100000");
        context.setBlacklisted(true);

        RuleResult result = engine.validate(context, ApprovalStatus.APPROVED);

        assertThat(result.getForcedDecision()).isEqualTo(ApprovalStatus.REJECTED);
        assertThat(result.getUserVisibleReason()).isEqualTo("您的申请未能通过审核");
    }

    @Test
    void boundaryCaseShouldEscalate() {
        AgentContext context = context("10000", "100000");
        context.setCreditScore(650);
        context.setDebtRatio(new BigDecimal("0.50"));

        RuleResult result = engine.validate(context, ApprovalStatus.APPROVED);

        assertThat(result.getForcedDecision()).isEqualTo(ApprovalStatus.ESCALATED);
    }

    private AgentContext context(String income, String amount) {
        LoanApplication application = new LoanApplication("user", "测试用户", new BigDecimal(income), new BigDecimal(amount), 24, "测试");
        return new AgentContext(application);
    }
}
