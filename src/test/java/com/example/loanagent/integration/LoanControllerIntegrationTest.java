package com.example.loanagent.integration;

import com.example.loanagent.llm.LlmClient;
import com.example.loanagent.llm.LlmRequest;
import com.example.loanagent.llm.LlmResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LoanControllerIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @MockBean
    private LlmClient llmClient;

    @Test
    void shouldSubmitAndQueryLoanApplication() throws Exception {
        when(llmClient.chat(any(LlmRequest.class))).thenReturn(
                tool("blacklist", "{\"userId\":\"user_good_001\"}"),
                tool("credit_score", "{\"userId\":\"user_good_001\"}"),
                tool("debt_ratio", "{\"userId\":\"user_good_001\"}"),
                finalAnswer("APPROVED", "综合评估通过")
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String body = "{\"userId\":\"user_good_001\",\"applicantName\":\"张三\",\"monthlyIncome\":20000,\"loanAmount\":200000,\"termMonths\":24,\"purpose\":\"装修\"}";
        ResponseEntity<Map> apply = restTemplate.postForEntity("/api/loans/apply", new HttpEntity<>(body, headers), Map.class);
        assertThat(apply.getStatusCode()).isEqualTo(HttpStatus.OK);
        Number applicationId = (Number) apply.getBody().get("applicationId");

        Map status = waitForResult(applicationId.longValue());
        assertThat(status.get("decision")).isEqualTo("APPROVED");

        ResponseEntity<Object[]> logs = restTemplate.getForEntity("/api/admin/audit-logs/" + applicationId, Object[].class);
        assertThat(logs.getBody()).isNotEmpty();
    }

    private Map waitForResult(Long applicationId) throws InterruptedException {
        for (int i = 0; i < 20; i++) {
            ResponseEntity<Map> status = restTemplate.getForEntity("/api/loans/" + applicationId + "/status", Map.class);
            if (status.getBody().containsKey("decision")) {
                return status.getBody();
            }
            Thread.sleep(100);
        }
        throw new AssertionError("审批结果未在预期时间内生成");
    }

    private LlmResponse tool(String action, String input) {
        String escaped = input.replace("\"", "\\\"");
        return new LlmResponse("{\"thought\":\"需要查询工具\",\"action\":\"" + action + "\",\"action_input\":\"" + escaped + "\",\"decision\":null,\"reason\":null}");
    }

    private LlmResponse finalAnswer(String decision, String reason) {
        return new LlmResponse("{\"thought\":\"信息足够，给出结论\",\"action\":\"final_answer\",\"action_input\":null,\"decision\":\"" + decision + "\",\"reason\":\"" + reason + "\"}");
    }
}
