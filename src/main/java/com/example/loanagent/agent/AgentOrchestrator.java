package com.example.loanagent.agent;

import com.example.loanagent.model.ApprovalResult;
import com.example.loanagent.model.ApprovalStatus;
import com.example.loanagent.model.LoanApplication;
import com.example.loanagent.repository.ApprovalResultRepository;
import com.example.loanagent.repository.LoanApplicationRepository;
import com.example.loanagent.service.EscalationService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class AgentOrchestrator {
    private final LoanApprovalAgent agent;
    private final ApprovalResultRepository resultRepository;
    private final LoanApplicationRepository applicationRepository;
    private final EscalationService escalationService;

    public AgentOrchestrator(LoanApprovalAgent agent, ApprovalResultRepository resultRepository,
                             LoanApplicationRepository applicationRepository, EscalationService escalationService) {
        this.agent = agent;
        this.resultRepository = resultRepository;
        this.applicationRepository = applicationRepository;
        this.escalationService = escalationService;
    }

    @Async("agentTaskExecutor")
    public CompletableFuture<ApprovalResult> processAsync(LoanApplication application) {
        ApprovalResult result = agent.approve(application);
        resultRepository.save(result);
        application.setStatus(result.getStatus());
        applicationRepository.save(application);
        if (result.getStatus() == ApprovalStatus.ESCALATED) {
            escalationService.create(application.getId(), result.getReason());
        }
        return CompletableFuture.completedFuture(result);
    }
}
