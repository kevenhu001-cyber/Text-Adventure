package com.mysteriousjourney

import com.mysteriousjourney.util.StateParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * StateParser 单元测试
 *
 * 覆盖状态标记解析，以及消耗/恢复冲突、符号剥离、强度词串味等易错分支。
 * 叙事文本的抽取由 ChoiceParser 负责，不在本测试范围内。
 */
class StateParserTest {

    private fun update(text: String) = StateParser.parseStateUpdate(text)

    @Test
    fun `parses core state markers`() {
        val u = update(
            "你站在贝克兰德广场的阴影里，阴冷的雾气包裹着你。" +
                "{location:贝克兰德广场} {time:1349年11月3日 上午10点} " +
                "{spirituality:90/100} {madness:5}"
        )

        assertEquals("贝克兰德广场", u?.location)
        assertEquals("1349年11月3日 上午10点", u?.time)
        assertEquals(90, u?.spirituality?.current)
        assertEquals(100, u?.spirituality?.max)
        assertEquals(5, u?.madness)
    }

    @Test
    fun `parses money and inventory markers`() {
        val u = update("你买下了一枚护身符。{money:15/12/6} {inventory:+生锈的钥匙,-学生证}")

        assertEquals(15, u?.money?.goldPounds)
        assertEquals(12, u?.money?.soles)
        assertEquals(6, u?.money?.pence)

        val changes = u?.inventoryChanges
        assertNotNull(changes)
        assertEquals(listOf("生锈的钥匙"), changes?.added)
        assertEquals(listOf("学生证"), changes?.removed)
    }

    @Test
    fun `parseItemChanges keeps a Pair helper for callers that prefer it`() {
        val (added, removed) = StateParser.parseInventoryChanges("+生锈的钥匙, -学生证")
        assertEquals(listOf("生锈的钥匙"), added)
        assertEquals(listOf("学生证"), removed)
    }

    @Test
    fun `detects natural-language spirit consumption with intensity`() {
        assertTrue((update("你发动了占卜，灵性轻微消耗。")?.spiritChange ?: 0) < 0)
        assertEquals(-30, update("你强行窥视命运，灵性严重消耗。")?.spiritChange)
    }

    @Test
    fun `spirit consumption and recovery in one response are summed, not overwritten`() {
        // 修复前：恢复分支无条件覆盖消耗分支，净结果为 +5（玩家凭空回血）
        val u = update("你大量消耗灵性施展了灵性火焰，随后灵性略微恢复了一些。{location:黑森街}")

        assertEquals(-25, u?.spiritChange)
        assertEquals("黑森街", u?.location)
    }

    @Test
    fun `consume then recover in one clause still nets negative`() {
        // "消耗" 与 "略微恢复" 相距很近，强度词不得被两条判定重复认领
        val u = update("你消耗灵性后略微恢复体力。")

        assertNotNull(u?.spiritChange)
        assertTrue("净变化应为消耗，实际 ${u?.spiritChange}", (u?.spiritChange ?: 0) < 0)
    }

    @Test
    fun `madness loss and recovery in one response are summed, not overwritten`() {
        // 修复前：上升分支无条件覆盖下降分支，理智严重下降会被整段算成回 san
        val u = update("你的理智严重下降，但你靠着仪式保持住了精神稳定。")

        // 失控 +30 与回稳 -10 相抵后仍为净 +20，而不是被覆盖成 -10
        assertEquals(20, u?.madnessChange)
    }

    @Test
    fun `intensity word elsewhere in the narrative does not leak into this change`() {
        // 全文提到"大量"，但消耗本身没有强度限定词 → 按默认 -10 计
        assertEquals(-10, update("你消耗灵性，广场上聚集了大量行人。")?.spiritChange)
    }

    @Test
    fun `status markers strip sign prefixes instead of leaking them into game state`() {
        // 修复前：{status:+疲惫,-中毒} 会被解析成 ["+疲惫", "-中毒"]，
        // 再经由 system prompt 拼进下一轮上下文，符号持续污染 AI 的状态认知
        val u = update("你感到不适。{status:+疲惫,-中毒}")

        assertEquals(listOf("疲惫"), u?.statusChanges?.added)
        assertEquals(listOf("中毒"), u?.statusChanges?.removed)
    }

    @Test
    fun `explicit markers win over natural-language inference`() {
        val u = update("你消耗了大量灵性。{spirituality:70/100}")

        // 显式标记存在时不再叠加自然语言推断
        assertEquals(70, u?.spirituality?.current)
        assertEquals(100, u?.spirituality?.max)
        assertNull(u?.spiritChange)
    }

    @Test
    fun `no state markers at all yields no state update`() {
        // 修复前：正文里出现任意 "{" 都会被强行当成一次状态更新
        assertNull(update("你只是想了一下 {某个念头}，什么也没发生。"))
    }

    @Test
    fun `memory marker splits on commas and drops blanks`() {
        val u = update("{memory:得知灰雾之上的秘密,,遇见了撑伞的男人}")

        assertEquals(listOf("得知灰雾之上的秘密", "遇见了撑伞的男人"), u?.memories)
    }
}
