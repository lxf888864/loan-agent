package com.example.loanagent.prompt;

public final class PromptTemplates {
    private PromptTemplates() {
    }

    public static final String OUTPUT_FORMAT =
            "你每次只能返回一个 JSON 对象，不得使用 Markdown 包裹。格式如下：\n" +
            "{\n" +
            "  \"thought\": \"我的推理过程，简洁说明为什么选择该动作\",\n" +
            "  \"action\": \"credit_score 或 debt_ratio 或 blacklist 或 repayment_capacity 或 final_answer\",\n" +
            "  \"action_input\": \"工具参数字符串，final_answer 时为 null\",\n" +
            "  \"decision\": \"APPROVED 或 REJECTED 或 ESCALATED，仅 final_answer 时填写\",\n" +
            "  \"reason\": \"给用户的理由。黑名单拒绝时必须只说：您的申请未能通过审核\"\n" +
            "}";
}
