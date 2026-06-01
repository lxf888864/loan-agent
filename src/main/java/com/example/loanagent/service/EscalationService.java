package com.example.loanagent.service;

import com.example.loanagent.model.ApprovalStatus;
import com.example.loanagent.model.EscalationQueue;
import com.example.loanagent.repository.EscalationQueueRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EscalationService {
    private final EscalationQueueRepository repository;

    public EscalationService(EscalationQueueRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public EscalationQueue create(Long applicationId, String reason) {
        return repository.save(new EscalationQueue(applicationId, reason));
    }

    public List<EscalationQueue> pending() {
        return repository.findByStatusOrderByCreatedAtAsc("PENDING");
    }

    @Transactional
    public EscalationQueue resolve(Long id, ApprovalStatus decision, String operator) {
        if (decision == ApprovalStatus.ESCALATED || decision == ApprovalStatus.PENDING) {
            throw new IllegalArgumentException("人工处置结果只能是 APPROVED 或 REJECTED");
        }
        EscalationQueue queue = repository.findById(id).orElseThrow(() -> new IllegalArgumentException("人工审核记录不存在"));
        queue.resolve(decision, operator);
        return repository.save(queue);
    }
}
