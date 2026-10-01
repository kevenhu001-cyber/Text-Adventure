package com.mysteriousjourney.util

/**
 * 选择解析器
 * 从AI响应中解析剧情选择选项。
 * 兼容多种标记写法：【选择】【选项】【行动】以及文末的编号列表（1. xxx）。
 */
object ChoiceParser {

    // 行首的选择标记：【选择】xxx / 【选项】xxx / 【行动】xxx / [选择] xxx
    private val markerLineRegex = Regex(
        """^[ \t]*[【\[](?:选择|选项|行动|抉择)[】\]]\s*(.+?)\s*$""",
        setOf(RegexOption.MULTILINE)
    )

    // 兜底：文末编号/符号列表，如 "1. 跟上那个男人" "· 烧掉信件"
    private val numberedLineRegex = Regex(
        """^[ \t]*(?:[1-9１-９][.、)）．]|[·•◦▪])\s*(.{2,30}?)\s*$""",
        setOf(RegexOption.MULTILINE)
    )

    // 用于从叙事中剔除的内容：选择标记行 + 状态标记 + 未闭合的半截标记（流式场景）
    private val choiceBlockRegex = Regex(
        """^[ \t]*[【\[](?:选择|选项|行动|抉择)[】\]][^\n【]*""",
        setOf(RegexOption.MULTILINE)
    )
    private val stateMarkerRegex = Regex("""\{[^}]*\}""")
    private val danglingMarkerRegex = Regex("""[【\[](?:选择|选项|行动|抉择)?[】\]]?[^\n【】\[]{0,30}$""")

    /**
     * 解析AI响应中的选择选项
     * @param response AI的完整响应
     * @return 解析结果，包含叙事文本和选择列表
     */
    fun parseResponse(response: String): ParseResult {
        val choices = extractChoices(response)
        val narrative = extractNarrative(response)
        return ParseResult(narrative, choices)
    }

    /**
     * 提取选择选项
     */
    private fun extractChoices(response: String): List<Choice> {
        val choices = mutableListOf<Choice>()

        markerLineRegex.findAll(response).forEach { match ->
            val text = cleanChoiceText(match.groupValues[1])
            if (text.isNotEmpty()) {
                choices.add(Choice(id = choices.size + 1, text = text))
            }
        }

        // 兜底：没有标记行时，尝试从文末编号列表提取
        if (choices.isEmpty()) {
            numberedLineRegex.findAll(response).forEach { match ->
                val text = cleanChoiceText(match.groupValues[1])
                if (text.isNotEmpty() && choices.size < 4) {
                    choices.add(Choice(id = choices.size + 1, text = text))
                }
            }
        }

        return choices.distinctBy { it.text }
    }

    /**
     * 清洗选项文本：去掉残余的状态标记和标点
     */
    private fun cleanChoiceText(text: String): String {
        return text
            .replace(stateMarkerRegex, "")
            .trim()
            .trimEnd('。', '，', '、', '；', ';', ',', '.')
            .take(30)
    }

    /**
     * 提取纯叙事文本，移除选择选项与状态标记
     */
    private fun extractNarrative(response: String): String {
        var narrative = response

        // 移除选择标记行
        narrative = choiceBlockRegex.replace(narrative, "")

        // 移除文末编号列表行（已作为选项提取的不应出现在叙事中）
        narrative = numberedLineRegex.replace(narrative, "")

        // 移除状态标记
        narrative = narrative.replace(stateMarkerRegex, "")

        // 移除流式过程中可能出现的半截标记（如 "【选择"、"【"）
        narrative = danglingMarkerRegex.replace(narrative, "")

        // 合并多余空白，保留换行
        narrative = narrative.replace(Regex("""[ \t]+"""), " ")
        narrative = narrative.replace(Regex("""\n{3,}"""), "\n\n")

        return narrative.trim()
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
