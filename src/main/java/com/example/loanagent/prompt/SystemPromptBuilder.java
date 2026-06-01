package com.example.loanagent.prompt;

import com.example.loanagent.tool.AgentTool;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.stream.Collectors;

@Component
public class SystemPromptBuilder {
    public String build(Collection<AgentTool> tools) {
        String rules = loadBusinessRules();
        String toolText = tools.stream()
                .map(tool -> "- " + tool.name() + ": " + tool.description())
                .collect(Collectors.joining("\n"));
        return "你是一名智能信贷审批 AI Agent，既要利用推理能力，也必须严格遵守业务规则。\n"
                + "你需要通过 ReAct 方式工作：先思考，再选择一个工具，或在信息足够时给出 final_answer。\n\n"
                + "可用工具：\n" + toolText + "\n\n"
                + "业务规则：\n" + rules + "\n\n"
                + "输出格式要求：\n" + PromptTemplates.OUTPUT_FORMAT;
    }

    private String loadBusinessRules() {
        try {
            ClassPathResource resource = new ClassPathResource("business-rules.md");
            return StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
        } catch (Exception ex) {
            throw new IllegalStateException("读取业务规则文档失败", ex);
        }
    }
}
