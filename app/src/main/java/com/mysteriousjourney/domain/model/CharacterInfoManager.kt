package com.mysteriousjourney.domain.model

/**
 * 角色信息管理器
 * 提供角色信息的统一访问接口
 */
object CharacterInfoManager {
    
    /**
     * 获取角色能力信息
     */
    fun getCharacterAbilities(characterId: String): CharacterAbilities? {
        return CharacterDatabase.getCharacterById(characterId)?.abilities
    }
    
    /**
     * 获取角色物品信息
     */
    fun getCharacterInventory(characterId: String): CharacterInventory? {
        return CharacterDatabase.getCharacterById(characterId)?.inventory
    }
    
    /**
     * 获取角色关系信息
     */
    fun getCharacterRelationships(characterId: String): List<CharacterRelationship>? {
        return CharacterDatabase.getCharacterById(characterId)?.relationships
    }
    
    /**
     * 获取角色背景信息
     */
    fun getCharacterBackground(characterId: String): DetailedCharacterBackground? {
        return CharacterDatabase.getCharacterById(characterId)?.background
    }
    
    /**
     * 获取角色基本信息
     */
    fun getCharacterBasicInfo(characterId: String): DetailedCharacterBackground.BasicInfo? {
        return CharacterDatabase.getCharacterById(characterId)?.basicInfo
    }
    
    /**
     * 获取角色状态信息
     */
    fun getCharacterStatus(characterId: String): DetailedCharacter.CharacterStatus? {
        return CharacterDatabase.getCharacterById(characterId)?.currentStatus
    }
    
    /**
     * 获取角色剧情相关信息
     */
    fun getCharacterPlotRelevance(characterId: String): DetailedCharacter.PlotRelevance? {
        return CharacterDatabase.getCharacterById(characterId)?.plotRelevance
    }
}
