package com.example.loanagent.controller;

import com.example.loanagent.model.ApprovalResult;
import com.example.loanagent.model.LoanApplication;
import com.example.loanagent.service.LoanApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/loans")
public class LoanController {
    private final LoanApplicationService service;

    public LoanController(LoanApplicationService service) {
        this.service = service;
    }

    @PostMapping("/apply")
    public Map<String, Object> apply(@RequestBody ApplyLoanRequest request) {
        LoanApplication application = service.submit(request.getUserId(), request.getApplicantName(),
                request.getMonthlyIncome(), request.getLoanAmount(), request.getTermMonths(), request.getPurpose());
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("applicationId", application.getId());
        body.put("status", application.getStatus());
        return body;
    }

    @GetMapping("/{id}/status")
    public ResponseEntity<Map<String, Object>> status(@PathVariable Long id) {
        LoanApplication application = service.getApplication(id);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("applicationId", application.getId());
        body.put("applicationStatus", application.getStatus());
        service.getLatestResult(id).ifPresent(result -> addResult(body, result));
        return ResponseEntity.ok(body);
    }

    private void addResult(Map<String, Object> body, ApprovalResult result) {
        body.put("decision", result.getStatus());
        body.put("reason", result.getReason());
        body.put("ruleOverride", result.isRuleOverride());
        body.put("totalSteps", result.getTotalSteps());
        body.put("totalDurationMs", result.getTotalDurationMs());
    }

    public static class ApplyLoanRequest {
        private String userId;
        private String applicantName;
        private BigDecimal monthlyIncome;
        private BigDecimal loanAmount;
        private Integer termMonths;
        private String purpose;

        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }
        public String getApplicantName() { return applicantName; }
        public void setApplicantName(String applicantName) { this.applicantName = applicantName; }
        public BigDecimal getMonthlyIncome() { return monthlyIncome; }
        public void setMonthlyIncome(BigDecimal monthlyIncome) { this.monthlyIncome = monthlyIncome; }
        public BigDecimal getLoanAmount() { return loanAmount; }
        public void setLoanAmount(BigDecimal loanAmount) { this.loanAmount = loanAmount; }
        public Integer getTermMonths() { return termMonths; }
        public void setTermMonths(Integer termMonths) { this.termMonths = termMonths; }
        public String getPurpose() { return purpose; }
        public void setPurpose(String purpose) { this.purpose = purpose; }
    }
}
