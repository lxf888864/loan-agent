package com.example.loanagent.controller;

import com.example.loanagent.model.ApprovalStatus;
import com.example.loanagent.model.AuditLogRecord;
import com.example.loanagent.model.EscalationQueue;
import com.example.loanagent.repository.AuditLogRecordRepository;
import com.example.loanagent.service.EscalationService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    private final EscalationService escalationService;
    private final AuditLogRecordRepository auditLogRecordRepository;

    public AdminController(EscalationService escalationService, AuditLogRecordRepository auditLogRecordRepository) {
        this.escalationService = escalationService;
        this.auditLogRecordRepository = auditLogRecordRepository;
    }

    @GetMapping("/escalations")
    public List<EscalationQueue> escalations() {
        return escalationService.pending();
    }

    @PutMapping("/escalations/{id}/resolve")
    public EscalationQueue resolve(@PathVariable Long id,
                                   @RequestParam ApprovalStatus decision,
                                   @RequestParam(defaultValue = "admin") String operator) {
        return escalationService.resolve(id, decision, operator);
    }

    @GetMapping("/audit-logs/{applicationId}")
    public List<AuditLogRecord> auditLogs(@PathVariable Long applicationId) {
        return auditLogRecordRepository.findByApplicationIdOrderByTimestampAsc(applicationId);
    }
}
