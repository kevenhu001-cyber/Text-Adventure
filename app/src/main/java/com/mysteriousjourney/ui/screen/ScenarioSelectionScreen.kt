package com.mysteriousjourney.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mysteriousjourney.domain.model.OpeningScenario
import com.mysteriousjourney.ui.theme.Black
import com.mysteriousjourney.ui.theme.DarkGray
import com.mysteriousjourney.ui.theme.GoldPrimary
import com.mysteriousjourney.ui.theme.LightGray
import com.mysteriousjourney.ui.theme.MediumGray
import com.mysteriousjourney.ui.theme.White

/**
 * 开场剧本选择界面
 * 与应用整体黑金哥特主题保持一致
 */
@Composable
fun ScenarioSelectionScreen(
    scenarios: List<OpeningScenario>,
    onSelectScenario: (OpeningScenario) -> Unit,
    onRandomScenario: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedDifficulty by remember { mutableStateOf<OpeningScenario.Difficulty?>(null) }
    var showEasterEggsOnly by remember { mutableStateOf(false) }

    val filteredScenarios = remember(scenarios, selectedDifficulty, showEasterEggsOnly) {
        var result = scenarios
        if (selectedDifficulty != null) {
            result = result.filter { it.difficulty == selectedDifficulty }
        }
        if (showEasterEggsOnly) {
            result = result.filter { it.isEasterEgg }
        }
        result
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Black, Color(0xFF0D0D0D), Black)
                )
            )
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(28.dp))

        // 顶部标题区
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .border(1.dp, GoldPrimary.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoStories,
                        contentDescription = null,
                        tint = GoldPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "选择你的命运",
                    color = GoldPrimary,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "每一个开端，都通向不同的深渊",
                    color = White.copy(alpha = 0.5f),
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 筛选栏
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DestinyFilterChip("全部", selectedDifficulty == null) {
                selectedDifficulty = null
                showEasterEggsOnly = false
            }
            DestinyFilterChip("简单", selectedDifficulty == OpeningScenario.Difficulty.EASY) {
                selectedDifficulty = OpeningScenario.Difficulty.EASY
            }
            DestinyFilterChip("普通", selectedDifficulty == OpeningScenario.Difficulty.NORMAL) {
                selectedDifficulty = OpeningScenario.Difficulty.NORMAL
            }
            DestinyFilterChip("困难", selectedDifficulty == OpeningScenario.Difficulty.HARD) {
                selectedDifficulty = OpeningScenario.Difficulty.HARD
            }
            DestinyFilterChip("噩梦", selectedDifficulty == OpeningScenario.Difficulty.NIGHTMARE) {
                selectedDifficulty = OpeningScenario.Difficulty.NIGHTMARE
            }
            DestinyFilterChip("彩蛋", showEasterEggsOnly) {
                showEasterEggsOnly = !showEasterEggsOnly
            }
        }

        // 随机命运
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 14.dp)
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, GoldPrimary.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .clickable(onClick = onRandomScenario)
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Casino,
                contentDescription = null,
                tint = GoldPrimary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "交由命运抉择",
                color = GoldPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            items(filteredScenarios) { scenario ->
                ScenarioCard(
                    scenario = scenario,
                    onClick = { onSelectScenario(scenario) }
                )
            }
        }
    }
}

@Composable
private fun DestinyFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (selected) GoldPrimary else Color.Transparent)
            .border(
                width = 1.dp,
                color = if (selected) GoldPrimary else LightGray,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = label,
            color = if (selected) Black else White.copy(alpha = 0.75f),
            fontSize = 12.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun ScenarioCard(
    scenario: OpeningScenario,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val difficultyColor = when (scenario.difficulty) {
        OpeningScenario.Difficulty.EASY -> Color(0xFF6FA96F)
        OpeningScenario.Difficulty.NORMAL -> Color(0xFF6F95C9)
        OpeningScenario.Difficulty.HARD -> Color(0xFFC98A3A)
        OpeningScenario.Difficulty.NIGHTMARE -> Color(0xFFB04141)
    }

    val difficultyText = when (scenario.difficulty) {
        OpeningScenario.Difficulty.EASY -> "简单"
        OpeningScenario.Difficulty.NORMAL -> "普通"
        OpeningScenario.Difficulty.HARD -> "困难"
        OpeningScenario.Difficulty.NIGHTMARE -> "噩梦"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .then(
                if (scenario.isEasterEgg) {
                    Modifier.border(
                        width = 1.dp,
                        color = GoldPrimary.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(12.dp)
                    )
                } else {
                    Modifier.border(
                        width = 1.dp,
                        color = MediumGray,
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = DarkGray)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    if (scenario.isEasterEgg) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "彩蛋",
                            tint = GoldPrimary,
                            modifier = Modifier
                                .size(16.dp)
                                .padding(end = 4.dp)
                        )
                    }
                    Text(
                        text = scenario.title,
                        color = White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = difficultyText,
                    color = difficultyColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier
                        .border(1.dp, difficultyColor.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = scenario.description,
                color = White.copy(alpha = 0.7f),
                fontSize = 13.sp,
                lineHeight = 19.sp
            )

            if (scenario.tags.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    scenario.tags.take(3).forEach { tag ->
                        Text(
                            text = tag,
                            color = GoldPrimary.copy(alpha = 0.8f),
                            fontSize = 10.sp,
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(GoldPrimary.copy(alpha = 0.1f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    if (scenario.tags.size > 3) {
                        Text(
                            text = "+${scenario.tags.size - 3}",
                            color = White.copy(alpha = 0.4f),
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "序列 · ${scenario.initialPlayerState.currentSequence.name}",
                    color = White.copy(alpha = 0.45f),
                    fontSize = 11.sp
                )
                Text(
                    text = scenario.initialWorldState.currentLocation.take(12),
                    color = White.copy(alpha = 0.45f),
                    fontSize = 11.sp,
                    textAlign = TextAlign.End,
                    maxLines = 1
                )
            }
        }
    }
}
