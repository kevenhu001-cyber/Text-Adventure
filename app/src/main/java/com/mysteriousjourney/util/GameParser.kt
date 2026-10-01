package com.mysteriousjourney.util

object GameParser {
    /**
     * 解析AI响应中的状态信息
     */
    fun parseStateInfo(response: String): Map<String, Any> {
        val stateInfo = mutableMapOf<String, Any>()
        
        // 解析玩家状态
        stateInfo["playerState"] = parsePlayerState(response)
        
        // 解析世界状态
        stateInfo["worldState"] = parseWorldState(response)
        
        // 解析特殊指令
        stateInfo["specialCommands"] = parseSpecialCommands(response)
        
        return stateInfo
    }

    /**
     * 解析玩家状态
     */
    private fun parsePlayerState(response: String): Map<String, Any> {
        val playerState = mutableMapOf<String, Any>()
        
        // 解析灵性值
        parseStat(response, "灵性值", playerState)
        
        // 解析体力值
        parseStat(response, "体力值", playerState)
        
        // 解析理智值
        parseStat(response, "理智值", playerState)
        
        // 解析金钱
        parseMoney(response, playerState)
        
        // 解析物品栏
        parseInventory(response, playerState)
        
        return playerState
    }

    /**
     * 解析世界状态
     */
    private fun parseWorldState(response: String): Map<String, Any> {
        val worldState = mutableMapOf<String, Any>()
        
        // 解析时间
        parseTime(response, worldState)
        
        // 解析地点
        parseLocation(response, worldState)
        
        // 解析天气
        parseWeather(response, worldState)
        
        // 解析世界事件
        parseWorldEvents(response, worldState)
        
        return worldState
    }

    /**
     * 解析特殊指令
     */
    private fun parseSpecialCommands(response: String): List<String> {
        val specialCommands = mutableListOf<String>()
        
        // 这里可以实现特殊指令的解析逻辑
        // 例如，检测是否有战斗、探索、对话等指令
        
        return specialCommands
    }

    /**
     * 解析属性值
     */
    private fun parseStat(response: String, statName: String, stateMap: MutableMap<String, Any>) {
        val pattern = "$statName：(\\d+)/(\\d+)".toRegex()
        val matchResult = pattern.find(response)
        
        if (matchResult != null) {
            val current = matchResult.groupValues[1].toInt()
            val max = matchResult.groupValues[2].toInt()
            stateMap[statName] = mapOf(
                "current" to current,
                "max" to max
            )
        }
    }

    /**
     * 解析金钱
     */
    private fun parseMoney(response: String, stateMap: MutableMap<String, Any>) {
        val pattern = "金钱：(.*)".toRegex()
        val matchResult = pattern.find(response)
        
        if (matchResult != null) {
            stateMap["金钱"] = matchResult.groupValues[1]
        }
    }

    /**
     * 解析物品栏
     */
    private fun parseInventory(response: String, stateMap: MutableMap<String, Any>) {
        val pattern = """物品栏：\[(.*)\]""".toRegex()
        val matchResult = pattern.find(response)
        
        if (matchResult != null) {
            val items = matchResult.groupValues[1].split("，").map { it.trim() }
            stateMap["物品栏"] = items
        }
    }

    /**
     * 解析时间
     */
    private fun parseTime(response: String, stateMap: MutableMap<String, Any>) {
        val pattern = "当前时间：(.*)".toRegex()
        val matchResult = pattern.find(response)
        
        if (matchResult != null) {
            stateMap["当前时间"] = matchResult.groupValues[1]
        }
    }

    /**
     * 解析地点
     */
    private fun parseLocation(response: String, stateMap: MutableMap<String, Any>) {
        val pattern = "当前地点：(.*)".toRegex()
        val matchResult = pattern.find(response)
        
        if (matchResult != null) {
            stateMap["当前地点"] = matchResult.groupValues[1]
        }
    }

    /**
     * 解析天气
     */
    private fun parseWeather(response: String, stateMap: MutableMap<String, Any>) {
        val pattern = "天气：(.*)".toRegex()
        val matchResult = pattern.find(response)
        
        if (matchResult != null) {
            stateMap["天气"] = matchResult.groupValues[1]
        }
    }

    /**
     * 解析世界事件
     */
    private fun parseWorldEvents(response: String, stateMap: MutableMap<String, Any>) {
        val pattern = "世界事件：(.*)".toRegex()
        val matchResult = pattern.find(response)
        
        if (matchResult != null) {
            stateMap["世界事件"] = matchResult.groupValues[1]
        }
    }
}
