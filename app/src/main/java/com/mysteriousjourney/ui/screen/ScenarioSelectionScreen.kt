package com.mysteriousjourney.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mysteriousjourney.domain.model.OpeningScenario
import com.mysteriousjourney.ui.theme.MysticGold
import com.mysteriousjourney.ui.theme.MysticPurple

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
                    colors = listOf(
                        Color(0xFF1a1a2e),
                        Color(0xFF16213e),
                        Color(0xFF0f0f23)
                    )
                )
            )
            .padding(16.dp)
    ) {
        Text(
            text = "选择你的命运",
            style = MaterialTheme.typography.headlineLarge,
            color = MysticGold,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        Text(
            text = "每个选择都将开启不同的命运轨迹",
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedDifficulty == null,
                onClick = { selectedDifficulty = null },
                label = { Text("全部") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MysticPurple,
                    selectedLabelColor = Color.White
                )
            )
            
            FilterChip(
                selected = selectedDifficulty == OpeningScenario.Difficulty.EASY,
                onClick = { selectedDifficulty = OpeningScenario.Difficulty.EASY },
                label = { Text("简单") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF4CAF50),
                    selectedLabelColor = Color.White
                )
            )
            
            FilterChip(
                selected = selectedDifficulty == OpeningScenario.Difficulty.NORMAL,
                onClick = { selectedDifficulty = OpeningScenario.Difficulty.NORMAL },
                label = { Text("普通") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF2196F3),
                    selectedLabelColor = Color.White
                )
            )
            
            FilterChip(
                selected = selectedDifficulty == OpeningScenario.Difficulty.HARD,
                onClick = { selectedDifficulty = OpeningScenario.Difficulty.HARD },
                label = { Text("困难") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFFFF9800),
                    selectedLabelColor = Color.White
                )
            )
            
            FilterChip(
                selected = selectedDifficulty == OpeningScenario.Difficulty.NIGHTMARE,
                onClick = { selectedDifficulty = OpeningScenario.Difficulty.NIGHTMARE },
                label = { Text("噩梦") },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFFF44336),
                    selectedLabelColor = Color.White
                )
            )
        }
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = showEasterEggsOnly,
                onCheckedChange = { showEasterEggsOnly = it },
                colors = CheckboxDefaults.colors(
                    checkedColor = MysticGold,
                    uncheckedColor = Color.White.copy(alpha = 0.5f)
                )
            )
            Text(
                text = "仅显示彩蛋场景",
                color = Color.White.copy(alpha = 0.7f),
                modifier = Modifier.clickable { showEasterEggsOnly = !showEasterEggsOnly }
            )
        }
        
        Button(
            onClick = onRandomScenario,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MysticPurple
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Casino,
                contentDescription = null,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text("随机选择命运", fontSize = 16.sp)
        }
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
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
fun ScenarioCard(
    scenario: OpeningScenario,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val difficultyColor = when (scenario.difficulty) {
        OpeningScenario.Difficulty.EASY -> Color(0xFF4CAF50)
        OpeningScenario.Difficulty.NORMAL -> Color(0xFF2196F3)
        OpeningScenario.Difficulty.HARD -> Color(0xFFFF9800)
        OpeningScenario.Difficulty.NIGHTMARE -> Color(0xFFF44336)
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
                        color = MysticGold.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(12.dp)
                    )
                } else {
                    Modifier
                }
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1e1e3f).copy(alpha = 0.8f)
        )
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
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (scenario.isEasterEgg) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "彩蛋",
                            tint = MysticGold,
                            modifier = Modifier
                                .size(20.dp)
                                .padding(end = 4.dp)
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.AutoStories,
                        contentDescription = null,
                        tint = MysticPurple,
                        modifier = Modifier
                            .size(20.dp)
                            .padding(end = 8.dp)
                    )
                    Text(
                        text = scenario.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(difficultyColor)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = difficultyText,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = scenario.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                scenario.tags.take(3).forEach { tag ->
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MysticPurple.copy(alpha = 0.3f)
                    ) {
                        Text(
                            text = tag,
                            style = MaterialTheme.typography.labelSmall,
                            color = MysticGold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
                if (scenario.tags.size > 3) {
                    Text(
                        text = "+${scenario.tags.size - 3}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "序列: ${scenario.initialPlayerState.currentSequence.name}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.6f)
                )
                Text(
                    text = "地点: ${scenario.initialWorldState.currentLocation.take(15)}...",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.6f)
                )
            }
        }
    }
}
