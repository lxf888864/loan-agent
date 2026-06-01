package com.example.loanagent.repository;

import com.example.loanagent.model.ApprovalResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApprovalResultRepository extends JpaRepository<ApprovalResult, Long> {
    Optional<ApprovalResult> findFirstByApplicationIdOrderByCreatedAtDesc(Long applicationId);
}
