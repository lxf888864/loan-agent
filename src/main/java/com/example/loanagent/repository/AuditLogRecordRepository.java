package com.example.loanagent.repository;

import com.example.loanagent.model.AuditLogRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditLogRecordRepository extends JpaRepository<AuditLogRecord, Long> {
    List<AuditLogRecord> findByApplicationIdOrderByTimestampAsc(Long applicationId);
}
