package com.example.loanagent.repository;

import com.example.loanagent.model.EscalationQueue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EscalationQueueRepository extends JpaRepository<EscalationQueue, Long> {
    List<EscalationQueue> findByStatusOrderByCreatedAtAsc(String status);
}
