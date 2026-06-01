package com.example.loanagent.service;

import com.example.loanagent.agent.AgentOrchestrator;
import com.example.loanagent.model.ApprovalResult;
import com.example.loanagent.model.LoanApplication;
import com.example.loanagent.repository.ApprovalResultRepository;
import com.example.loanagent.repository.LoanApplicationRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class LoanApplicationService {
    private final LoanApplicationRepository applicationRepository;
    private final ApprovalResultRepository resultRepository;
    private final AgentOrchestrator orchestrator;

    public LoanApplicationService(LoanApplicationRepository applicationRepository,
                                  ApprovalResultRepository resultRepository,
                                  AgentOrchestrator orchestrator) {
        this.applicationRepository = applicationRepository;
        this.resultRepository = resultRepository;
        this.orchestrator = orchestrator;
    }

    public LoanApplication submit(String userId, String applicantName, BigDecimal monthlyIncome,
                                  BigDecimal loanAmount, Integer termMonths, String purpose) {
        LoanApplication application = applicationRepository.save(new LoanApplication(userId, applicantName, monthlyIncome, loanAmount, termMonths, purpose));
        orchestrator.processAsync(application);
        return application;
    }

    public LoanApplication getApplication(Long id) {
        return applicationRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("贷款申请不存在"));
    }

    public Optional<ApprovalResult> getLatestResult(Long applicationId) {
        return resultRepository.findFirstByApplicationIdOrderByCreatedAtDesc(applicationId);
    }
}
