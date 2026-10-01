package com.mysteriousjourney

import com.mysteriousjourney.util.StateParser
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * StateParser 单元测试
 * 覆盖状态标记解析、叙事提取和一致性评分
 */
class StateParserTest {

    @Test
    fun `parseResponse extracts narrative and core state markers`() {
        val response = "你站在贝克兰德广场的阴影里，阴冷的雾气包裹着你。" +
            "{location:贝克兰德广场} {time:1349年11月3日 上午10点} " +
            "{spirituality:90/100} {madness:5}"

        val result = StateParser.parseResponse(response)

        assertTrue(result.narrative.contains("你站在贝克兰德广场"))
        assertEquals("贝克兰德广场", result.stateUpdate?.location)
        assertEquals("1349年11月3日 上午10点", result.stateUpdate?.time)
        assertEquals(90, result.stateUpdate?.spirituality?.current)
        assertEquals(100, result.stateUpdate?.spirituality?.max)
        assertEquals(5, result.stateUpdate?.madness)
    }

    @Test
    fun `parseResponse parses money and inventory markers`() {
        val response = "你买下了一枚护身符。{money:15/12/6} {inventory:+生锈的钥匙,-学生证}"

        val result = StateParser.parseResponse(response)

        assertEquals(15, result.stateUpdate?.money?.goldPounds)
        assertEquals(12, result.stateUpdate?.money?.soles)
        assertEquals(6, result.stateUpdate?.money?.pence)

        val (added, removed) = StateParser.parseInventoryChanges(
            result.stateUpdate!!.inventoryChanges!!
        )
        assertEquals(listOf("生锈的钥匙"), added)
        assertEquals(listOf("学生证"), removed)
    }

    @Test
    fun `parseResponse detects natural-language spirit consumption with intensity`() {
        val light = StateParser.parseResponse("你发动了占卜，灵性轻微消耗。")
        val heavy = StateParser.parseResponse("你强行窥视命运，灵性严重消耗。")

        assertTrue((light.stateUpdate?.spiritChange ?: 0) < 0)
        assertEquals(-30, heavy.stateUpdate?.spiritChange)
    }

    @Test
    fun `consistency score penalizes missing choice markers`() {
        val withChoices = StateParser.parseResponse(
            "你环顾四周，雾气渐浓。{location:贝克兰德广场} {time:上午}【选择】向前走"
        )
        val withoutChoices = StateParser.parseResponse("你环顾四周，雾气渐浓。")

        assertTrue(withChoices.consistencyScore > withoutChoices.consistencyScore)
    }
}
