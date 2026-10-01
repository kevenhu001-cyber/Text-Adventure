package com.mysteriousjourney.util

import com.mysteriousjourney.domain.model.Money
import com.mysteriousjourney.domain.model.Spirituality

object StateParser {
    
    fun parseResponse(response: String): ParseResult {
        val narrative = extractNarrative(response)
        val stateUpdate = parseStateUpdates(response)
        val consistencyScore = calculateConsistencyScore(response, narrative, stateUpdate)
        return ParseResult(narrative, stateUpdate, consistencyScore)
    }
    
    private fun calculateConsistencyScore(response: String, narrative: String, update: StateUpdate?): Int {
        var score = 100
        
        // 1. 检查状态标记完整性
        if (update?.location == null) score -= 10
        if (update?.time == null) score -= 5
        if (update?.spirituality == null && update?.spiritChange == null) {
             // 如果没消耗也没更新，可能是剧情不需要，扣分较少
             if (response.contains("能力") || response.contains("灵性")) score -= 10
        }
        
        // 2. 检查叙事长度 (400-800字为佳)
        val length = narrative.length
        if (length < 200) score -= 20
        if (length > 1200) score -= 10
        
        // 3. 检查是否有选择项
        if (!response.contains("【选择】")) score -= 15
        
        // 4. 逻辑冲突检测 (模糊匹配)
        if (narrative.contains("灵性枯竭") && (update?.spirituality?.current ?: 100) > 20) score -= 20
        if (narrative.contains("疯狂") && (update?.madness ?: 0) < 10 && update?.madnessChange == null) score -= 15
        
        return score.coerceIn(0, 100)
    }
    
    private fun extractNarrative(response: String): String {
        var result = response
        // 移除状态标记，但保留其中的文本信息
        result = result.replace(Regex("\\{(location|time|spirituality|madness|money|inventory|status):[^}]+\\}")) { matchResult ->
            // 保留位置、时间和状态等信息的文本描述
            when {
                matchResult.value.contains("location:") -> {
                    val location = matchResult.value.substringAfter("location:").substringBefore("}")
                    "（位置：$location）"
                }
                matchResult.value.contains("time:") -> {
                    val time = matchResult.value.substringAfter("time:").substringBefore("}")
                    "（时间：$time）"
                }
                else -> ""
            }
        }
        // 移除其他状态标记
        result = result.replace(Regex("\\{[^}]+\\}"), "")
        
        // 增强文本分段逻辑
        result = optimizeParagraphFormatting(result)
        
        return result.trim()
    }
    
    /**
     * 优化文本分段格式，确保段落划分符合叙事逻辑
     */
    private fun optimizeParagraphFormatting(text: String): String {
        var formattedText = text
        
        // 处理连续空格和换行
        formattedText = formattedText.replace(Regex("\\s+"), " ")
        
        // 1. 在场景转换、时间变化等关键节点后添加换行
        formattedText = formattedText.replace(Regex("(?i)(（位置：[^）]+）|（时间：[^）]+）|场景转换|时间流逝|夜幕降临|清晨到来)", RegexOption.MULTILINE), "$1\n\n")
        
        // 2. 在对话前添加换行（如果前面不是换行）
        formattedText = formattedText.replace(Regex("(?<!\n)\"[^\"\\n]+\""), "\n$0")
        
        // 3. 按句号、感叹号、问号等结束符划分段落，确保每个段落长度适中
        val sentences = formattedText.split(Regex("(?<=[。！？])"))
        val paragraphs = mutableListOf<String>()
        var currentParagraph = StringBuilder()
        
        for (sentence in sentences) {
            val trimmedSentence = sentence.trim()
            if (trimmedSentence.isEmpty()) continue
            
            // 如果当前段落长度超过100字符或包含对话，且不是以状态标记开头，则开始新段落
            if ((currentParagraph.length > 100 && !trimmedSentence.startsWith("（")) || trimmedSentence.startsWith("\"")) {
                if (currentParagraph.isNotEmpty()) {
                    paragraphs.add(currentParagraph.toString().trim())
                    currentParagraph = StringBuilder()
                }
            }
            
            currentParagraph.append(trimmedSentence).append(" ")
        }
        
        if (currentParagraph.isNotEmpty()) {
            paragraphs.add(currentParagraph.toString().trim())
        }
        
        // 4. 确保段落之间有适当的间隔
        return paragraphs.joinToString("\n\n")
    }
    
    private fun parseStateUpdates(response: String): StateUpdate? {
        val updates = StateUpdate()
        var hasUpdates = false
        
        // 1. 处理灵性状态，添加IGNORE_CASE标志
        val spiritRegex = Regex("\\{spirituality:(\\d+)/(\\d+)\\}", RegexOption.IGNORE_CASE)
        spiritRegex.find(response)?.let { match ->
            updates.spirituality = Spirituality(
                current = match.groupValues[1].toInt(),
                max = match.groupValues[2].toInt()
            )
            hasUpdates = true
        }
        
        if (updates.spirituality == null) {
            // 增强灵性消耗识别，支持更多关键词和强度级别
            val spiritConsumeRegex = Regex("灵性?(?:直觉)?(?:轻微|少量|些许|略微|中等|一般|普通|大量|严重|剧烈|大幅)?消耗|消耗了?(?:轻微|少量|些许|略微|中等|一般|普通|大量|严重|剧烈|大幅)?灵性|灵性(?:下降|减少|降低)", RegexOption.IGNORE_CASE)
            if (spiritConsumeRegex.containsMatchIn(response)) {
                val intensity = when {
                    response.contains(Regex("(轻微|少量|些许|略微)", RegexOption.IGNORE_CASE)) -> 1
                    response.contains(Regex("(中等|一般|普通)", RegexOption.IGNORE_CASE)) -> 2
                    response.contains(Regex("(大量|严重|剧烈|大幅)", RegexOption.IGNORE_CASE)) -> 3
                    else -> 0
                }
                val changeAmount = when (intensity) {
                    1 -> -5
                    2 -> -15
                    3 -> -30
                    else -> -10
                }
                updates.spiritChange = changeAmount
                hasUpdates = true
            }
            
            // 增强灵性恢复识别
            val spiritRecoverRegex = Regex("灵性?(?:直觉)?(?:轻微|少量|些许|略微|中等|一般|普通|大量|完全|彻底)?恢复|恢复了?(?:轻微|少量|些许|略微|中等|一般|普通|大量|完全|彻底)?灵性|灵性(?:上升|增加|提高)", RegexOption.IGNORE_CASE)
            if (spiritRecoverRegex.containsMatchIn(response)) {
                val intensity = when {
                    response.contains(Regex("(轻微|少量|些许|略微)", RegexOption.IGNORE_CASE)) -> 1
                    response.contains(Regex("(中等|一般|普通)", RegexOption.IGNORE_CASE)) -> 2
                    response.contains(Regex("(大量|剧烈|大幅)", RegexOption.IGNORE_CASE)) -> 3
                    response.contains(Regex("(完全|彻底)", RegexOption.IGNORE_CASE)) -> 4
                    else -> 0
                }
                val changeAmount = when (intensity) {
                    1 -> 5
                    2 -> 15
                    3 -> 30
                    4 -> 100
                    else -> 10
                }
                updates.spiritChange = changeAmount
                hasUpdates = true
            }
        }
        
        // 2. 处理疯狂值状态，添加IGNORE_CASE标志
        val madnessRegex = Regex("\\{madness:(\\d+)\\}", RegexOption.IGNORE_CASE)
        madnessRegex.find(response)?.let { match ->
            updates.madness = match.groupValues[1].toInt()
            hasUpdates = true
        }
        
        if (updates.madness == null) {
            // 增强疯狂值下降识别
            val madnessDecreaseRegex = Regex("理智?(?:轻微|少量|些许|略微|中等|一般|普通|大量|严重|剧烈|大幅)?下降|精神(?:受损|动摇|崩溃)|san值(?:下降|降低|减少)", RegexOption.IGNORE_CASE)
            if (madnessDecreaseRegex.containsMatchIn(response)) {
                val intensity = when {
                    response.contains(Regex("(轻微|少量|些许|略微)", RegexOption.IGNORE_CASE)) -> 1
                    response.contains(Regex("(中等|一般|普通)", RegexOption.IGNORE_CASE)) -> 2
                    response.contains(Regex("(大量|严重|剧烈|大幅)", RegexOption.IGNORE_CASE)) -> 3
                    else -> 0
                }
                val changeAmount = when (intensity) {
                    1 -> 5
                    2 -> 15
                    3 -> 30
                    else -> 10
                }
                updates.madnessChange = changeAmount
                hasUpdates = true
            }
            
            // 新增：疯狂值上升识别
            val madnessIncreaseRegex = Regex("理智?(?:轻微|少量|些许|略微|中等|一般|普通|大量|严重|剧烈|大幅)?上升|精神(?:恢复|好转|稳定)|san值(?:上升|增加|提高)", RegexOption.IGNORE_CASE)
            if (madnessIncreaseRegex.containsMatchIn(response)) {
                val intensity = when {
                    response.contains(Regex("(轻微|少量|些许|略微)", RegexOption.IGNORE_CASE)) -> 1
                    response.contains(Regex("(中等|一般|普通)", RegexOption.IGNORE_CASE)) -> 2
                    response.contains(Regex("(大量|严重|剧烈|大幅)", RegexOption.IGNORE_CASE)) -> 3
                    else -> 0
                }
                val changeAmount = when (intensity) {
                    1 -> -5
                    2 -> -15
                    3 -> -30
                    else -> -10
                }
                updates.madnessChange = changeAmount
                hasUpdates = true
            }
        }
        
        // 3. 处理金钱状态，添加IGNORE_CASE标志
        val moneyRegex = Regex("\\{money:(\\d+)/(\\d+)/(\\d+)\\}", RegexOption.IGNORE_CASE)
        moneyRegex.find(response)?.let { match ->
            updates.money = MoneyUpdate(
                goldPounds = match.groupValues[1].toInt(),
                soles = match.groupValues[2].toInt(),
                pence = match.groupValues[3].toInt()
            )
            hasUpdates = true
        }
        
        // 4. 处理位置状态，添加IGNORE_CASE标志
        val locationRegex = Regex("\\{location:([^}]+)\\}", RegexOption.IGNORE_CASE)
        locationRegex.find(response)?.let { match ->
            updates.location = match.groupValues[1]
            hasUpdates = true
        }
        
        // 5. 处理时间状态，添加IGNORE_CASE标志
        val timeRegex = Regex("\\{time:([^}]+)\\}", RegexOption.IGNORE_CASE)
        timeRegex.find(response)?.let { match ->
            updates.time = match.groupValues[1]
            hasUpdates = true
        }
        
        // 6. 处理库存变化，添加IGNORE_CASE标志
        val inventoryRegex = Regex("\\{inventory:([^}]+)\\}", RegexOption.IGNORE_CASE)
        inventoryRegex.find(response)?.let { match ->
            updates.inventoryChanges = match.groupValues[1]
            hasUpdates = true
        }
        
        // 7. 处理状态效果，添加IGNORE_CASE标志
        val statusRegex = Regex("\\{status:([^}]+)\\}", RegexOption.IGNORE_CASE)
        statusRegex.find(response)?.let { match ->
            updates.statusEffects = parseStatusEffects(match.groupValues[1])
            hasUpdates = true
        }
        
        // 8. 处理记忆更新，添加IGNORE_CASE标志
        val memoryRegex = Regex("\\{memory:([^}]+)\\}", RegexOption.IGNORE_CASE)
        memoryRegex.find(response)?.let { match ->
            updates.memories = match.groupValues[1].split(",").map { it.trim() }
            hasUpdates = true
        }
        
        // 9. 从自然语言中提取更多状态变化
        // 健康状态变化识别
        val healthRegex = Regex("(健康|受伤|生病|中毒|虚弱|疲惫|昏迷|死亡)", RegexOption.IGNORE_CASE)
        if (healthRegex.containsMatchIn(response)) {
            // 可以在这里添加健康状态的具体处理
            hasUpdates = true
        }
        
        // 10. 确保核心状态标记存在，即使没有变化
        // 检查是否包含任何状态标记
        if (!hasUpdates && response.contains("{")) {
            // 至少返回一个状态更新对象，确保 system 知道需要处理状态
            hasUpdates = true
        }
        
        return if (hasUpdates) updates else null
    }
    
    private fun parseStatusEffects(effectsString: String): List<String> {
        return effectsString.split(",").map { it.trim() }
    }
    
    fun parseInventoryChanges(changesString: String): Pair<List<String>, List<String>> {
        val added = mutableListOf<String>()
        val removed = mutableListOf<String>()
        
        changesString.split(",").forEach { change ->
            val trimmed = change.trim()
            when {
                trimmed.startsWith("+") -> added.add(trimmed.substring(1).trim())
                trimmed.startsWith("-") -> removed.add(trimmed.substring(1).trim())
                else -> added.add(trimmed)
            }
        }
        
        return Pair(added, removed)
    }
    
    data class ParseResult(
        val narrative: String,
        val stateUpdate: StateUpdate?,
        val consistencyScore: Int = 100 // 新增：逻辑一致性评分
    )
    
    data class StateUpdate(
        var spirituality: Spirituality? = null,
        var spiritChange: Int? = null,
        var madness: Int? = null,
        var madnessChange: Int? = null,
        var money: MoneyUpdate? = null,
        var location: String? = null,
        var time: String? = null,
        var inventoryChanges: String? = null,
        var statusEffects: List<String>? = null,
        var memories: List<String>? = null // 新增：叙事记忆更新
    )
    
    data class MoneyUpdate(
        val goldPounds: Int,
        val soles: Int,
        val pence: Int
    ) {
        fun toMoney(currentMoney: Money): Money {
            return Money(goldPounds, soles, pence)
        }
    }
}
