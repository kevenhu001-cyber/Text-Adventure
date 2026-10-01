package com.mysteriousjourney.util

import com.mysteriousjourney.domain.model.Money
import com.mysteriousjourney.domain.model.Spirituality

object StateParser {

    /**
     * 解析 AI 响应中的状态标记。
     *
     * 叙事文本由 ChoiceParser 负责抽取（那是真正展示给玩家的那份），
     * 这里只关心状态数据，不再重复跑一遍全量叙事正则。
     */
    fun parseStateUpdate(response: String): StateUpdate? = parseStateUpdates(response)
    private fun parseStateUpdates(response: String): StateUpdate? {
        val updates = StateUpdate()
        var hasUpdates = false

        // 1. 灵性
        Regex("\\{spirituality:(\\d+)/(\\d+)\\}", RegexOption.IGNORE_CASE).find(response)?.let { match ->
            updates.spirituality = Spirituality(
                current = match.groupValues[1].toInt(),
                max = match.groupValues[2].toInt()
            )
            hasUpdates = true
        }

        if (updates.spirituality == null) {
            // 消耗与恢复是两条互相独立的判定，可能在同一条叙事里同时命中
            // （"消耗灵性施展法术，随后略微恢复"是极常见的写法）。
            // 必须分别取值后累加，若沿用 if/if 让后一条直接覆盖，
            // 消耗就会被吃掉，玩家凭空回血。
            val consumeMatch = SPIRIT_CONSUME_REGEX.find(response)
            val recoverMatch = SPIRIT_RECOVER_REGEX.find(response)

            var delta = 0
            consumeMatch?.let { match ->
                delta -= when (magnitudeOf(response, match, CONSUMPTION_TIERS, recoverMatch)) {
                    1 -> 5
                    2 -> 15
                    3 -> 30
                    else -> 10
                }
            }
            recoverMatch?.let { match ->
                delta += when (magnitudeOf(response, match, RECOVERY_TIERS, consumeMatch)) {
                    1 -> 5
                    2 -> 15
                    3 -> 30
                    4 -> 100
                    else -> 10
                }
            }

            if (delta != 0) {
                updates.spiritChange = delta
                hasUpdates = true
            }
        }

        // 2. 疯狂值
        Regex("\\{madness:(\\d+)\\}", RegexOption.IGNORE_CASE).find(response)?.let { match ->
            updates.madness = match.groupValues[1].toInt()
            hasUpdates = true
        }

        if (updates.madness == null) {
            // 同灵性：失控与恢复分开累加，避免"精神严重受损…但保持了精神稳定"被算成回san
            val lossMatch = MADNESS_LOSS_REGEX.find(response)
            val gainMatch = MADNESS_GAIN_REGEX.find(response)

            var delta = 0
            lossMatch?.let { match ->
                delta += when (magnitudeOf(response, match, CONSUMPTION_TIERS, gainMatch)) {
                    1 -> 5
                    2 -> 15
                    3 -> 30
                    else -> 10
                }
            }
            gainMatch?.let { match ->
                delta -= when (magnitudeOf(response, match, RECOVERY_TIERS, lossMatch)) {
                    1 -> 5
                    2 -> 15
                    3 -> 30
                    else -> 10
                }
            }

            if (delta != 0) {
                updates.madnessChange = delta
                hasUpdates = true
            }
        }

        // 3. 金钱
        Regex("\\{money:(\\d+)/(\\d+)/(\\d+)\\}", RegexOption.IGNORE_CASE).find(response)?.let { match ->
            updates.money = MoneyUpdate(
                goldPounds = match.groupValues[1].toInt(),
                soles = match.groupValues[2].toInt(),
                pence = match.groupValues[3].toInt()
            )
            hasUpdates = true
        }

        // 4~8. 位置 / 时间 / 库存 / 状态效果 / 记忆
        Regex("\\{location:([^}]+)\\}", RegexOption.IGNORE_CASE).find(response)?.let { match ->
            updates.location = match.groupValues[1]
            hasUpdates = true
        }

        Regex("\\{time:([^}]+)\\}", RegexOption.IGNORE_CASE).find(response)?.let { match ->
            updates.time = match.groupValues[1]
            hasUpdates = true
        }

        Regex("\\{inventory:([^}]+)\\}", RegexOption.IGNORE_CASE).find(response)?.let { match ->
            updates.inventoryChanges = parseItemChanges(match.groupValues[1])
            hasUpdates = true
        }

        Regex("\\{status:([^}]+)\\}", RegexOption.IGNORE_CASE).find(response)?.let { match ->
            updates.statusChanges = parseItemChanges(match.groupValues[1])
            hasUpdates = true
        }

        Regex("\\{memory:([^}]+)\\}", RegexOption.IGNORE_CASE).find(response)?.let { match ->
            updates.memories = match.groupValues[1].split(",").map { it.trim() }.filter { it.isNotEmpty() }
            hasUpdates = true
        }

        return if (hasUpdates) updates else null
    }

    /**
     * 解析 `+新增,-移除` 形式的增量列表。背包与状态效果共用同一套语法。
     */
    fun parseItemChanges(changesString: String): ItemChanges {
        val added = mutableListOf<String>()
        val removed = mutableListOf<String>()

        changesString.split(",").forEach { change ->
            val trimmed = change.trim()
            if (trimmed.isEmpty()) return@forEach
            when {
                trimmed.startsWith("+") -> added += trimmed.substring(1).trim()
                trimmed.startsWith("-") -> removed += trimmed.substring(1).trim()
                else -> added += trimmed
            }
        }

        return ItemChanges(
            added = added.filter { it.isNotEmpty() },
            removed = removed.filter { it.isNotEmpty() }
        )
    }

    /**
     * 解析背包变化，保留 [Pair] 形式便于调用方按顺序消费。
     */
    fun parseInventoryChanges(changesString: String): Pair<List<String>, List<String>> {
        val changes = parseItemChanges(changesString)
        return changes.added to changes.removed
    }

    /**
     * 推断一次变化的发生强度，返回 0 表示没有识别到强度词。
     *
     * 强度词优先在匹配片段自身及紧邻范围内查找，而不是扫描整段叙事——
     * 否则正文别处出现的一个"大量"就会被误判成本次变化的强度。
     * [exclude] 用于剔除另一条互斥判定已经占用的区间，避免两个分支读到同一个强度词。
     */
    private fun magnitudeOf(
        response: String,
        match: MatchResult,
        tiers: List<Pair<List<String>, Int>>,
        exclude: MatchResult?
    ): Int {
        val lo = maxOf(0, match.range.first - CONTEXT_WINDOW)
        val hi = minOf(response.length - 1, match.range.last + CONTEXT_WINDOW)

        val window = if (exclude != null && exclude.range.first <= hi && exclude.range.last >= lo) {
            val cutStart = exclude.range.first.coerceIn(lo, hi + 1)
            val cutEnd = (exclude.range.last + 1).coerceIn(lo, hi + 1)
            response.substring(lo, cutStart) + response.substring(cutEnd, hi + 1)
        } else {
            response.substring(lo, hi + 1)
        }

        return tiers.firstOrNull { (words, _) -> words.any { window.contains(it) } }?.second ?: 0
    }

    private const val CONTEXT_WINDOW = 5

    private val MINOR_WORDS = listOf("轻微", "少量", "些许", "略微")
    private val MODERATE_WORDS = listOf("中等", "一般", "普通")
    private val HEAVY_WORDS = listOf("大量", "严重", "剧烈", "大幅")
    private val FULL_WORDS = listOf("完全", "彻底")

    private val CONSUMPTION_TIERS = listOf(
        MINOR_WORDS to 1,
        MODERATE_WORDS to 2,
        HEAVY_WORDS to 3
    )

    private val RECOVERY_TIERS = listOf(
        MINOR_WORDS to 1,
        MODERATE_WORDS to 2,
        listOf("大量", "剧烈", "大幅") to 3,
        FULL_WORDS to 4
    )

    private val SPIRIT_CONSUME_REGEX = Regex(
        "灵性?(?:直觉)?(?:轻微|少量|些许|略微|中等|一般|普通|大量|严重|剧烈|大幅)?消耗|" +
            "消耗了?(?:轻微|少量|些许|略微|中等|一般|普通|大量|严重|剧烈|大幅)?灵性|" +
            "灵性(?:下降|减少|降低)",
        RegexOption.IGNORE_CASE
    )

    private val SPIRIT_RECOVER_REGEX = Regex(
        "灵性?(?:直觉)?(?:轻微|少量|些许|略微|中等|一般|普通|大量|完全|彻底)?恢复|" +
            "恢复了?(?:轻微|少量|些许|略微|中等|一般|普通|大量|完全|彻底)?灵性|" +
            "灵性(?:上升|增加|提高)",
        RegexOption.IGNORE_CASE
    )

    private val MADNESS_LOSS_REGEX = Regex(
        "理智?(?:轻微|少量|些许|略微|中等|一般|普通|大量|严重|剧烈|大幅)?下降|" +
            "精神(?:受损|动摇|崩溃)|san值(?:下降|降低|减少)",
        RegexOption.IGNORE_CASE
    )

    private val MADNESS_GAIN_REGEX = Regex(
        "理智?(?:轻微|少量|些许|略微|中等|一般|普通|大量|严重|剧烈|大幅)?上升|" +
            "精神(?:恢复|好转|稳定)|san值(?:上升|增加|提高)",
        RegexOption.IGNORE_CASE
    )

    /**
     * 一组带符号的增量变更（背包 / 状态效果共用）
     */
    data class ItemChanges(
        val added: List<String> = emptyList(),
        val removed: List<String> = emptyList()
    )

    data class StateUpdate(
        var spirituality: Spirituality? = null,
        var spiritChange: Int? = null,
        var madness: Int? = null,
        var madnessChange: Int? = null,
        var money: MoneyUpdate? = null,
        var location: String? = null,
        var time: String? = null,
        var inventoryChanges: ItemChanges? = null,
        var statusChanges: ItemChanges? = null,
        var memories: List<String>? = null // 叙事记忆更新
    )

    data class MoneyUpdate(
        val goldPounds: Int,
        val soles: Int,
        val pence: Int
    ) {
        fun toMoney(): Money = Money(goldPounds, soles, pence)
    }
}
