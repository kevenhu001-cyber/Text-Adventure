package com.mysteriousjourney.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mysteriousjourney.domain.model.*
import com.mysteriousjourney.ui.component.*

@Composable
fun CharacterDetailScreen(
    playerState: PlayerState,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("状态", "属性", "物品", "势力", "历史")

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
                    text = playerState.name,
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

            // Tab栏
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
                    .verticalScroll(scrollState)
                    .background(Color(0xFF16161A))
                    .padding(16.dp)
            ) {
                when (selectedTab) {
                    0 -> StatusTab(playerState)
                    1 -> AttributesTab(playerState)
                    2 -> InventoryTab(playerState)
                    3 -> FactionsTab(playerState)
                    4 -> HistoryTab(playerState)
                }
            }
        }
    }
}

@Composable
fun StatusTab(playerState: PlayerState) {
    // 基础信息
    BaseDetailSectionTitle("基础信息")
    BaseDetailRow("姓名", playerState.name)
    BaseDetailRow("身份", playerState.surfaceIdentity)
    BaseDetailRow("序列", "${playerState.currentSequence.name} (${playerState.currentSequence.number})")
    BaseDetailRow("消化", "${playerState.currentSequence.digestionProgress}%")
    
    Spacer(modifier = Modifier.height(16.dp))

    // 核心数值
    BaseDetailSectionTitle("状态数值")
    BaseDetailProgressBar("健康", 100, 100, Color.Red)
    BaseDetailProgressBar("灵性", playerState.spirituality.current, playerState.spirituality.max, Color(0xFFFFD700))
    BaseDetailProgressBar("疯狂", playerState.sanity.madnessValue, 100, Color.Magenta)
    BaseDetailRow("污染", playerState.sanity.corruptionLevel.toString())
    BaseDetailRow("灵视", if(playerState.spiritVisionEnabled) "开启" else "关闭")

    Spacer(modifier = Modifier.height(16.dp))

    // 状态效果
    BaseDetailSectionTitle("状态效果")
    if (playerState.healthStatus == "健康") {
        Text("暂无特殊状态", color = Color.Gray, fontSize = 14.sp)
    } else {
        StatusEffectItem(playerState.healthStatus)
    }
}

@Composable
fun AttributesTab(playerState: PlayerState) {
    BaseDetailSectionTitle("能力属性")
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
    
    BaseDetailSectionTitle("扩展属性")
    AttributeGrid(
        mapOf(
            "耐力" to playerState.attributes.endurance,
            "敏捷" to playerState.attributes.dexterity,
            "记忆" to playerState.attributes.memory,
            "分析" to playerState.attributes.analysis,
            "创造力" to playerState.attributes.creativity,
            "魅力" to playerState.attributes.charm
        )
    )
    
    Spacer(modifier = Modifier.height(16.dp))
    
    BaseDetailSectionTitle("社交属性")
    AttributeGrid(
        mapOf(
            "说服" to playerState.attributes.persuasion,
            "恐吓" to playerState.attributes.intimidation,
            "领导" to playerState.attributes.leadership,
            " reputation" to 0
        )
    )
}

@Composable
fun InventoryTab(playerState: PlayerState) {
    BaseDetailSectionTitle("物品清单")
    var currentFilter by remember { mutableStateOf<String?>(null) }
    
    // 分类筛选
    Row(Modifier.fillMaxWidth().padding(bottom = 8.dp), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
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

@Composable
fun FactionsTab(playerState: PlayerState) {
    BaseDetailSectionTitle("势力关系")
    if (playerState.factionRelations.isEmpty()) {
        Text("尚未接触任何主要势力", color = Color.Gray, fontSize = 14.sp)
    } else {
        playerState.factionRelations.forEach { (name, relation) ->
            FactionItem(name, relation.status)
        }
    }
}

@Composable
fun HistoryTab(playerState: PlayerState) {
    BaseDetailSectionTitle("历史大事件")
    if (playerState.knowledge.isEmpty()) {
        Text("你的传说尚未开始", color = Color.Gray, fontSize = 14.sp)
    } else {
        playerState.knowledge.forEach { event ->
            HistoryItem(event)
        }
    }
}

// 辅助组件
@Composable
fun BaseDetailSectionTitle(title: String) {
    Text(
        text = title,
        color = Color(0xFFFFD700),
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
fun BaseDetailRow(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(label, color = Color.Gray, fontSize = 14.sp, modifier = Modifier.width(80.dp))
        Text(value, color = Color.White, fontSize = 14.sp)
    }
}

@Composable
fun BaseAttributeRow(label: String, value: Int) {
    Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = Color.White, fontSize = 14.sp, modifier = Modifier.width(60.dp))
        Box(Modifier.weight(1f).height(8.dp).background(Color.DarkGray)) {
            Box(Modifier.fillMaxHeight().fillMaxWidth(value / 20f).background(Color(0xFFFFD700)))
        }
        Text(value.toString(), color = Color(0xFFFFD700), fontSize = 14.sp, modifier = Modifier.padding(start = 8.dp))
    }
}

@Composable
fun BaseDetailProgressBar(label: String, current: Int, max: Int, color: Color) {
    Column(Modifier.padding(vertical = 4.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, color = Color.White, fontSize = 12.sp)
            Text("$current/$max", color = Color.White, fontSize = 12.sp)
        }
        LinearProgressIndicator(
            progress = { current.toFloat() / max },
            modifier = Modifier.fillMaxWidth().height(4.dp),
            color = color,
            trackColor = Color.DarkGray
        )
    }
}

@Composable
fun StatusEffectItem(status: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF222228))
    ) {
        Column(Modifier.padding(8.dp)) {
            Text(status, color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun FactionItem(name: String, relation: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF222228))
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(name, color = Color.White, fontSize = 14.sp, modifier = Modifier.weight(1f))
            Text(relation, color = Color(0xFFFFD700), fontSize = 12.sp)
        }
    }
}

@Composable
fun HistoryItem(event: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Box(Modifier.size(8.dp).padding(top = 4.dp).background(Color(0xFFFFD700)))
        Column(Modifier.padding(start = 12.dp)) {
            Text(event, color = Color(0xFFFFD700), fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}
