package com.mysteriousjourney.util

/**
 * 选择解析器
 * 从AI响应中解析剧情选择选项
 */
object ChoiceParser {
    
    /**
     * 解析AI响应中的选择选项
     * @param response AI的完整响应
     * @return 解析结果，包含叙事文本和选择列表
     */
    fun parseResponse(response: String): ParseResult {
        // 提取选择选项
        val choices = extractChoices(response)
        
        // 提取纯叙事文本（移除选择标记）
        val narrative = extractNarrative(response, choices)
        
        return ParseResult(narrative, choices)
    }
    
    /**
     * 提取选择选项
     */
    private fun extractChoices(response: String): List<Choice> {
        val choices = mutableListOf<Choice>()
        
        // 匹配【选择】标记的选项，包括多行内容
        val choiceRegex = Regex("""【选择】([^【]*(?:\n[^【]*)*?)(?=【选择】|$)""")
        choiceRegex.findAll(response).forEach { match ->
            val choiceText = match.groupValues[1].trim()
            if (choiceText.isNotEmpty()) {
                choices.add(
                    Choice(
                        id = choices.size + 1,
                        text = choiceText
                    )
                )
            }
        }
        
        return choices
    }
    
    /**
     * 提取纯叙事文本，移除选择选项
     */
    private fun extractNarrative(response: String, choices: List<Choice>): String {
        var narrative = response
        
        // 移除所有选择选项，包括多行内容
        val choiceRegex = Regex("""【选择】[^【]*(?:\n[^【]*)*?(?=【选择】|$)""")
        narrative = choiceRegex.replace(narrative, "")
        
        // 移除状态标记
        narrative = narrative.replace(Regex("""\{[^}]+\}"""), "")
        
        // 保留换行符，只移除多余的空格
        narrative = narrative.replace(Regex("""[ \t]+"""), " ").trim()
        
        return narrative
    }
    
    /**
     * 解析结果数据类
     */
    data class ParseResult(
        val narrative: String,
        val choices: List<Choice>
    )
    
    /**
     * 选择选项数据类
     */
    data class Choice(
        val id: Int,
        val text: String
    )
}
