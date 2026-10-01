package com.mysteriousjourney.ui.screen

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mysteriousjourney.domain.model.*
import com.mysteriousjourney.ui.component.*

@Composable
fun EnhancedCharacterDetailScreen(
    characterData: EnhancedCharacterData,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("状态", "属性", "能力", "物品", "关系", "历史", "神秘")

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color(0xFF0F0F12)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = characterData.baseState.name,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color.Transparent,
                contentColor = Color(0xFFFFD700),
                edgePadding = 0.dp,
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, fontSize = 14.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .background(Color(0xFF16161A))
                    .padding(16.dp)
            ) {
                when (selectedTab) {
                    0 -> StatusOverviewTab(characterData)
                    1 -> AttributesTab(characterData)
                    2 -> AbilitiesTab(characterData)
                    3 -> InventoryTab(characterData)
                    4 -> RelationsTab(characterData)
                    5 -> HistoryTab(characterData)
                    6 -> MysteryTab(characterData)
                }
            }
        }
    }
}

@Composable
fun StatusOverviewTab(characterData: EnhancedCharacterData) {
    val playerState = characterData.baseState
    val health = characterData.health
    
    Column(modifier = Modifier.fillMaxWidth()) {
        // 基础信息卡片
        DetailSectionTitle("基础信息")
        DetailRow("姓名", playerState.name)
        DetailRow("身份", playerState.surfaceIdentity)
        DetailRow("序列", "${playerState.currentSequence.name} (序列${playerState.currentSequence.number})")
        DetailRow("消化进度", "${playerState.currentSequence.digestionProgress}%")
        DetailRow("当前状态", playerState.healthStatus)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 核心状态指标
        DetailSectionTitle("核心状态")
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            CircularStatRing(
                "灵性",
                playerState.spirituality.current.toFloat(),
                playerState.spirituality.max.toFloat(),
                Color(0xFFFFD700),
                size = 70
            )
            CircularStatRing(
                "疯狂",
                playerState.sanity.madnessValue.toFloat(),
                100f,
                Color(0xFFEC4899),
                size = 70
            )
            CircularStatRing(
                "健康",
                health.currentHealth.toFloat(),
                health.maxHealth.toFloat(),
                Color(0xFFEF4444),
                size = 70
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 进度条
        DetailSectionTitle("状态进度")
        ProgressBarWithLabel(
            "灵性恢复",
            playerState.spirituality.current.toFloat(),
            playerState.spirituality.max.toFloat(),
            Color(0xFFFFD700)
        )
        ProgressBarWithLabel(
            "疯狂值",
            playerState.sanity.madnessValue.toFloat(),
            100f,
            Color(0xFFEC4899)
        )
        ProgressBarWithLabel(
            "健康值",
            health.currentHealth.toFloat(),
            health.maxHealth.toFloat(),
            Color(0xFFEF4444)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 状态效果
        DetailSectionTitle("状态效果")
        if (characterData.activeStatuses.isEmpty()) {
            Text("暂无特殊状态", color = Color.Gray, fontSize = 14.sp)
        } else {
            characterData.activeStatuses.forEach { status ->
                StatusEffectBadge(
                    name = status.name,
                    type = status.type,
                    duration = status.duration
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 物品状态
        DetailSectionTitle("物品状态")
        val equippedCount = characterData.equippedItems.values.count { it != null }
        val totalSlots = characterData.equippedItems.size
        ProgressBarWithLabel(
            "装备",
            equippedCount.toFloat(),
            totalSlots.toFloat(),
            Color(0xFF8B5CF6),
            showValue = false
        )
        Text(
            text = "$equippedCount / $totalSlots 个装备槽位",
            color = Color(0xFF9CA3AF),
            fontSize = 12.sp
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 能力统计
        DetailSectionTitle("能力统计")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatCard(
                title = "战斗力",
                value = characterData.getTotalCombatPower().toString(),
                subtitle = "综合战力",
                color = Color(0xFF1F2937),
                icon = Icons.Default.Info
            )
            StatCard(
                title = "防御力",
                value = characterData.getTotalDefense().toString(),
                subtitle = "战斗防御",
                color = Color(0xFF1F2937),
                icon = Icons.Default.Shield
            )
            StatCard(
                title = "抗疯",
                value = characterData.getMadnessResistance().toString(),
                subtitle = "疯狂抵抗",
                color = Color(0xFF1F2937),
                icon = Icons.Default.Lock
            )
        }
    }
}

@Composable
fun AttributesTab(characterData: EnhancedCharacterData) {
    val playerState = characterData.baseState
    
    Column(modifier = Modifier.fillMaxWidth()) {
        // 基础属性
        DetailSectionTitle("基础属性")
        AttributeGrid(
            mapOf(
                "力量" to playerState.attributes.strength,
                "智力" to playerState.attributes.intelligence,
                "敏捷" to playerState.attributes.agility,
                "感知" to playerState.attributes.perception,
                "意志" to playerState.attributes.willPower,
                "运气" to playerState.attributes.luck
            )
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 精神属性
        DetailSectionTitle("精神属性")
        AttributeGrid(
            mapOf(
                "灵性" to playerState.spirituality.current,
                "灵视" to if(playerState.spiritVisionEnabled) 100 else 0,
                "抗疯" to characterData.getMadnessResistance(),
                "精神" to playerState.sanity.corruptionLevel
            )
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 战斗属性
        DetailSectionTitle("战斗属性")
        AttributeGrid(
            mapOf(
                "攻击" to playerState.attributes.strength * 2,
                "防御" to playerState.attributes.endurance * 2,
                "闪避" to playerState.attributes.agility * 2,
                "暴击" to playerState.attributes.luck * 2,
                "战力" to characterData.getTotalCombatPower()
            )
        )
    }
}

@Composable
fun AbilitiesTab(characterData: EnhancedCharacterData) {
    val playerState = characterData.baseState
    
    Column(modifier = Modifier.fillMaxWidth()) {
        // 技能树
        DetailSectionTitle("技能树")
        if (characterData.skillTree.isEmpty()) {
            Text("尚未解锁任何技能", color = Color.Gray, fontSize = 14.sp)
        } else {
            characterData.skillTree.forEach { skill ->
                SkillTreeItem(skill)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 能力列表
        DetailSectionTitle("能力列表")
        if (playerState.abilities.isEmpty()) {
            Text("暂无已掌握能力", color = Color.Gray, fontSize = 14.sp)
        } else {
            playerState.abilities.forEach { ability ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2937)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF8B5CF6))
                                .padding(6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Star,
                                null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = ability.name,
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Lv.${ability.level}",
                                    color = Color(0xFFFFD700),
                                    fontSize = 12.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = ability.description,
                                color = Color(0xFF9CA3AF),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun InventoryTab(characterData: EnhancedCharacterData) {
    val playerState = characterData.baseState
    
    Column(modifier = Modifier.fillMaxWidth()) {
        // 装备栏
        DetailSectionTitle("装备栏")
        if (characterData.equippedItems.isEmpty()) {
            Text("暂无装备", color = Color.Gray, fontSize = 14.sp)
        } else {
            characterData.equippedItems.forEach { (type, equipment) ->
                if (equipment != null) {
                    EquipmentItem(equipment)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 物品清单
        DetailSectionTitle("物品清单")
        var currentFilter by remember { mutableStateOf<String?>(null) }
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            FilterChip(
                selected = currentFilter == null,
                onClick = { currentFilter = null },
                label = { Text("全部", fontSize = 10.sp) }
            )
            listOf("武器", "防具", "饰品", "消耗品", "材料", "任务").forEach { type ->
                FilterChip(
                    selected = currentFilter == type,
                    onClick = { currentFilter = type },
                    label = { Text(type, fontSize = 10.sp) }
                )
            }
        }
        
        val filteredList = if (currentFilter == null) {
            playerState.inventory
        } else {
            playerState.inventory.filter { it.contains(currentFilter!!) }
        }
        
        if (filteredList.isEmpty()) {
            Text("背包空空如也", color = Color.Gray, fontSize = 14.sp)
        } else {
            filteredList.forEach { item ->
                Text(item, color = Color.White, fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun RelationsTab(characterData: EnhancedCharacterData) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // 势力关系
        DetailSectionTitle("势力关系")
        if (characterData.baseState.factionRelations.isEmpty()) {
            Text("尚未接触任何主要势力", color = Color.Gray, fontSize = 14.sp)
        } else {
            characterData.baseState.factionRelations.forEach { (name, relation) ->
                FactionItem(name, relation.status, relation.reputation)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 人物关系
        DetailSectionTitle("人物关系")
        if (characterData.characterRelations.isEmpty()) {
            Text("暂无人物关系", color = Color.Gray, fontSize = 14.sp)
        } else {
            characterData.characterRelations.forEach { relation ->
                RelationshipNode(
                    name = relation.characterName,
                    type = relation.relationType,
                    score = relation.getOverallRelation()
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 称号与成就
        DetailSectionTitle("称号与成就")
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCard(
                title = "称号",
                value = characterData.titles.size.toString(),
                subtitle = "已获得",
                color = Color(0xFF1F2937),
                icon = Icons.Default.Star
            )
            StatCard(
                title = "成就",
                value = characterData.achievements.size.toString(),
                subtitle = "已完成",
                color = Color(0xFF1F2937),
                icon = Icons.Default.Star
            )
        }
        
        if (characterData.titles.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text("称号列表", color = Color(0xFFFFD700), fontSize = 14.sp, fontWeight = FontWeight.Bold)
            characterData.titles.forEach { title ->
                Text("• $title", color = Color(0xFF9CA3AF), fontSize = 13.sp)
            }
        }
        
        if (characterData.achievements.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text("成就列表", color = Color(0xFFFFD700), fontSize = 14.sp, fontWeight = FontWeight.Bold)
            characterData.achievements.forEach { achievement ->
                Text("• $achievement", color = Color(0xFF9CA3AF), fontSize = 13.sp)
            }
        }
    }
}

@Composable
fun HistoryTab(characterData: EnhancedCharacterData) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // 历史日志
        DetailSectionTitle("历史日志")
        if (characterData.dailyLog.isEmpty()) {
            Text("暂无历史记录", color = Color.Gray, fontSize = 14.sp)
        } else {
            characterData.dailyLog.forEach { log ->
                LogEntry(log)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 命运节点
        DetailSectionTitle("命运节点")
        if (characterData.baseState.fateNodes.isEmpty()) {
            Text("暂无命运节点", color = Color.Gray, fontSize = 14.sp)
        } else {
            characterData.baseState.fateNodes.forEach { node ->
                FateNodeItem(node)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 历史事件
        DetailSectionTitle("历史事件")
        if (characterData.baseState.historyEvents.isEmpty()) {
            Text("你的传说尚未开始", color = Color.Gray, fontSize = 14.sp)
        } else {
            characterData.baseState.historyEvents.forEach { event ->
                HistoryItem(event)
            }
        }
    }
}

@Composable
fun MysteryTab(characterData: EnhancedCharacterData) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // 神秘点
        DetailSectionTitle("神秘点")
        if (characterData.mysteryPoints.isEmpty()) {
            Text("暂无神秘点", color = Color.Gray, fontSize = 14.sp)
        } else {
            characterData.mysteryPoints.forEach { point ->
                MysteryPointCard(point)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 命运路径
        DetailSectionTitle("命运路径")
        if (characterData.destinyPath.isEmpty()) {
            Text("尚未踏上命运之路", color = Color.Gray, fontSize = 14.sp)
        } else {
            characterData.destinyPath.forEach { node ->
                DestinyNodeItem(node)
            }
        }
    }
}

// 辅助组件
@Composable
fun DetailSectionTitle(title: String) {
    Text(
        text = title,
        color = Color(0xFFFFD700),
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color(0xFF9CA3AF), fontSize = 14.sp)
        Text(value, color = Color.White, fontSize = 14.sp)
    }
}

@Composable
fun AttributeRow(label: String, value: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = Color.White, fontSize = 14.sp, modifier = Modifier.width(60.dp))
        Box(
            modifier = Modifier.weight(1f).height(8.dp).background(Color(0xFF374151))
        ) {
            Box(
                modifier = Modifier.fillMaxHeight().fillMaxWidth(value / 20f).background(Color(0xFFFFD700))
            )
        }
        Text(value.toString(), color = Color(0xFFFFD700), fontSize = 14.sp, modifier = Modifier.padding(start = 8.dp))
    }
}

@Composable
fun EquipmentItem(equipment: Equipment) {
    val color = when (equipment.rarity) {
        Equipment.Rarity.COMMON -> Color(0xFF9CA3AF)
        Equipment.Rarity.UNCOMMON -> Color(0xFF10B981)
        Equipment.Rarity.RARE -> Color(0xFF3B82F6)
        Equipment.Rarity.EPIC -> Color(0xFF8B5CF6)
        Equipment.Rarity.LEGENDARY -> Color(0xFFF59E0B)
        Equipment.Rarity.DIVINE -> Color(0xFFEF4444)
    }
    
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2937)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f))
                    .padding(6.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    when (equipment.type) {
                        Equipment.EquipmentType.WEAPON -> Icons.Default.Info
                        Equipment.EquipmentType.ARMOR -> Icons.Default.Shield
                        Equipment.EquipmentType.ACCESSORY -> Icons.Default.Star
                        Equipment.EquipmentType.ARTIFACT -> Icons.Default.Lock
                        Equipment.EquipmentType.CONSUMABLE -> Icons.Default.LocalDrink
                        Equipment.EquipmentType.SPECIAL -> Icons.Default.Info
                    },
                    null,
                    tint = color,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = equipment.name,
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = equipment.rarity.name,
                        color = color,
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = equipment.description,
                    color = Color(0xFF9CA3AF),
                    fontSize = 12.sp,
                    maxLines = 2
                )
            }
        }
    }
}

@Composable
fun FactionItem(name: String, status: String, reputation: Int) {
    val color = when {
        reputation > 50 -> Color(0xFF10B981)
        reputation > 0 -> Color(0xFF10B981)
        reputation < -50 -> Color(0xFFEF4444)
        reputation < 0 -> Color(0xFFF59E0B)
        else -> Color(0xFF6B7280)
    }
    
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2937)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(name, color = Color.White, fontSize = 14.sp, modifier = Modifier.weight(1f))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(status, color = Color(0xFF9CA3AF), fontSize = 12.sp, modifier = Modifier.padding(end = 8.dp))
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(color)
                )
            }
        }
    }
}

@Composable
fun FateNodeItem(node: FateNode) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2937)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(if (node.triggered) Color(0xFF10B981) else Color(0xFF374151))
                    .padding(6.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (node.triggered) Icons.Default.Check else Icons.Default.Lock,
                    null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = node.description,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${node.type} - ${node.id}",
                    color = Color(0xFF9CA3AF),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun HistoryItem(event: HistoryEvent) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2937)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF8B5CF6))
                    .padding(6.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Event,
                    null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = event.title,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = event.description,
                    color = Color(0xFF9CA3AF),
                    fontSize = 12.sp,
                    maxLines = 2
                )
            }
        }
    }
}

@Composable
fun DestinyNodeItem(node: DestinyNode) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2937)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF8B5CF6))
                    .padding(6.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Star,
                    null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = node.name,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "阶段: ${node.stage}/${node.totalStages}",
                    color = Color(0xFF9CA3AF),
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = node.currentEffect,
                    color = Color(0xFF10B981),
                    fontSize = 12.sp
                )
                if (!node.triggered) {
                    Text(
                        text = "下一阶段: ${node.nextEffect}",
                        color = Color(0xFF6B7280),
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
