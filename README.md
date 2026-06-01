# 智能信贷审批 AI Agent

这是一个 Spring Boot 2.7 + Java 11 的智能信贷审批 Agent 示例工程。它演示了 ReAct 循环、工具调用、LLM 推理、业务规则兜底、异步审批、审计日志和人工审核队列。

## 启动前配置

默认使用智谱 GLM-4：

```yaml
llm:
  model: glm-4
  api-key: ${ZHIPU_API_KEY:YOUR_API_KEY_HERE}
```

推荐使用环境变量配置：

```bash
export ZHIPU_API_KEY=你的智谱APIKey
```

Windows PowerShell：

```powershell
$env:ZHIPU_API_KEY="你的智谱APIKey"
```

## 启动

```bash
mvn spring-boot:run
```

H2 控制台：

```text
http://localhost:8080/h2-console
JDBC URL: jdbc:h2:mem:loan_agent
User Name: sa
Password: 留空
```

## 测试

```bash
mvn test
```

## API 示例

提交申请：

```bash
curl -X POST http://localhost:8080/api/loans/apply \
  -H "Content-Type: application/json" \
  -d '{"userId":"user_good_001","applicantName":"张三","monthlyIncome":20000,"loanAmount":200000,"termMonths":24,"purpose":"装修"}'
```

查询状态：

```bash
curl http://localhost:8080/api/loans/1/status
```

查询人工审核队列：

```bash
curl http://localhost:8080/api/admin/escalations
```

人工处置：

```bash
curl -X PUT "http://localhost:8080/api/admin/escalations/1/resolve?decision=APPROVED&operator=admin"
```

查询审计链路：

```bash
curl http://localhost:8080/api/admin/audit-logs/1
```

## 设计说明

Agent 会让 LLM 在每轮只返回 JSON。LLM 可以选择调用工具，也可以给出最终结论。通过业务规则的硬性约束最大限度减少模型的幻觉。

规则文档位于 `src/main/resources/business-rules.md`，启动时会被注入 System Prompt，用作轻量 RAG。
