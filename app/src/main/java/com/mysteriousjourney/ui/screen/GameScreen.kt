package com.mysteriousjourney.ui.screen

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mysteriousjourney.ui.component.InputField
import com.mysteriousjourney.ui.component.NarrativeMessage
import com.mysteriousjourney.ui.component.NarrativeText
import com.mysteriousjourney.ui.component.StatusBar
import com.mysteriousjourney.util.ChoiceParser
import com.mysteriousjourney.ui.theme.Black
import com.mysteriousjourney.ui.theme.DarkGray
import com.mysteriousjourney.ui.theme.GoldPrimary
import com.mysteriousjourney.ui.theme.LightGray
import com.mysteriousjourney.ui.theme.White
import com.mysteriousjourney.data.settings.CustomModelConfig
import com.mysteriousjourney.domain.model.PlayerState
import com.mysteriousjourney.domain.model.GameState
import com.mysteriousjourney.domain.model.EnhancedCharacterAdapter

/**
 * 游戏状态数据类
 */
data class GameUiState(
    val isLoading: Boolean = false,
    val isTypewriterRunning: Boolean = false, // 新增
    val messages: List<NarrativeMessage> = emptyList(),
    val choices: List<ChoiceParser.Choice> = emptyList(),
    val pendingChoices: List<ChoiceParser.Choice> = emptyList(),
    val showChoices: Boolean = false,
    val spirit: Int = 100,
    val maxSpirit: Int = 100,
    val madness: Int = 0,
    val maxMadness: Int = 100,
    val goldPounds: Int = 0,
    val soles: Int = 0,
    val pence: Int = 0,
    val currentTime: String = "1349年3月1日 上午",
    val playerName: String = "旅行者",
    val location: String = "",
    val error: String? = null,
    val currentModel: CustomModelConfig? = null,
    val availableModels: List<CustomModelConfig> = emptyList(),
    val sequenceName: String = "占卜家",
    val sequenceNumber: Int = 9,
    val digestionProgress: Int = 0
)

/**
 * 主游戏界面
 */
@Composable
fun GameScreen(
    uiState: GameUiState,
    playerState: PlayerState,
    gameState: GameState? = null,
    onSendMessage: (String) -> Unit,
    onSelectChoice: (ChoiceParser.Choice) -> Unit,
    onAdvancePlot: () -> Unit,
    onSaveGame: () -> Unit,
    onLoadGame: () -> Unit,
    onOpenSettings: () -> Unit,
    onClearError: () -> Unit,
    onSwitchModel: (CustomModelConfig) -> Unit,
    onTypewriterComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf("") }
    var showMainMenu by remember { mutableStateOf(false) }
    var showModelMenu by remember { mutableStateOf(false) }
    var showCharacterDetail by remember { mutableStateOf(false) }

    val isInteractionLocked = uiState.isLoading || uiState.isTypewriterRunning

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Black)
    ) {
        // 顶部状态栏和菜单
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkGray)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 菜单按钮
            IconButton(
                onClick = { showMainMenu = !showMainMenu }
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "菜单",
                    tint = GoldPrimary
                )
            }

            // 标题
            Text(
                text = "神秘之旅",
                color = GoldPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            // 设置按钮
            IconButton(
                onClick = onOpenSettings
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "设置",
                    tint = GoldPrimary
                )
            }
        }

        // 下拉菜单
        if (showMainMenu) {
            DropdownMenu(
                onSaveGame = {
                    showMainMenu = false
                    onSaveGame()
                },
                onLoadGame = {
                    showMainMenu = false
                    onLoadGame()
                },
                onDismiss = { showMainMenu = false }
            )
        }

        // 状态栏
        StatusBar(
            spirit = uiState.spirit,
            maxSpirit = uiState.maxSpirit,
            madness = uiState.madness,
            maxMadness = uiState.maxMadness,
            goldPounds = uiState.goldPounds,
            soles = uiState.soles,
            pence = uiState.pence,
            currentTime = uiState.currentTime,
            onClick = { showCharacterDetail = true }
        )

        // 叙事区域
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            NarrativeText(
                messages = uiState.messages,
                modifier = Modifier.fillMaxSize(),
                enableTypewriter = true,
                typewriterSpeed = 80L,
                onTypewriterComplete = onTypewriterComplete
            )

            // 加载指示器
            if (uiState.isLoading) {
                LoadingIndicator(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 16.dp)
                )
            }
        }

        // 底部输入和控制区域
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            // 顶部控制栏 - 包含模型选择和推进剧情按钮
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 左侧：模型选择按钮
                androidx.compose.material3.OutlinedButton(
                    onClick = { showModelMenu = !showModelMenu },
                    modifier = Modifier
                        .height(32.dp)
                        .padding(horizontal = 4.dp),
                    colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                        contentColor = GoldPrimary
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = GoldPrimary.copy(alpha = 0.8f)
                    ),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        horizontal = 8.dp,
                        vertical = 4.dp
                    )
                ) {
                    Text(
                        text = uiState.currentModel?.displayName?.take(3)?.ifBlank { null }
                            ?: uiState.currentModel?.modelId?.take(3)
                            ?: "未配置",
                        color = GoldPrimary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // 模型选择下拉菜单
                androidx.compose.material3.DropdownMenu(
                    expanded = showModelMenu,
                    onDismissRequest = { showModelMenu = false }
                ) {
                    uiState.availableModels.forEach { model ->
                        androidx.compose.material3.DropdownMenuItem(
                            text = {
                                Text(
                                    text = model.displayName.ifBlank { model.modelId },
                                    color = if (model.id == uiState.currentModel?.id) GoldPrimary else White
                                )
                            },
                            onClick = {
                                onSwitchModel(model)
                                showModelMenu = false
                            }
                        )
                    }
                    androidx.compose.material3.DropdownMenuItem(
                        text = {
                            Text(
                                text = if (uiState.availableModels.isEmpty()) "去设置添加模型" else "管理模型…",
                                color = GoldPrimary
                            )
                        },
                        onClick = {
                            showModelMenu = false
                            onOpenSettings()
                        }
                    )
                }
                
                // 右侧：推进剧情按钮
                androidx.compose.material3.TextButton(
                    onClick = onAdvancePlot,
                    enabled = !isInteractionLocked,
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(
                            color = if (!isInteractionLocked) GoldPrimary.copy(alpha = 0.1f) else DarkGray.copy(alpha = 0.1f)
                        )
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "推进剧情",
                        tint = if (!isInteractionLocked) GoldPrimary else LightGray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "推进剧情",
                        color = if (!isInteractionLocked) GoldPrimary else LightGray,
                        fontSize = 12.sp
                    )
                }
            }
            
            // 错误提示
            uiState.error?.let { errorMessage ->
                androidx.compose.material3.Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    colors = androidx.compose.material3.CardDefaults.cardColors(
                        containerColor = androidx.compose.ui.graphics.Color.Red.copy(alpha = 0.9f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        androidx.compose.material3.Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "错误",
                            tint = androidx.compose.ui.graphics.Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        androidx.compose.material3.Text(
                            text = errorMessage,
                            color = androidx.compose.ui.graphics.Color.White,
                            fontSize = 14.sp,
                            modifier = Modifier.weight(1f)
                        )
                        androidx.compose.material3.IconButton(
                            onClick = onClearError,
                            modifier = Modifier.size(24.dp)
                        ) {
                            androidx.compose.material3.Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "关闭",
                                tint = androidx.compose.ui.graphics.Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
            
            // 输入框
            InputField(
                value = inputText,
                onValueChange = { inputText = it },
                onSend = {
                    if (inputText.isNotBlank()) {
                        onSendMessage(inputText)
                        inputText = ""
                    }
                },
                enabled = !isInteractionLocked,
                modifier = Modifier.fillMaxWidth()
            )
            
            // 选择按钮
            if (uiState.showChoices && uiState.choices.isNotEmpty()) {
                ChoiceButtons(
                    choices = uiState.choices,
                    onSelectChoice = onSelectChoice,
                    enabled = !isInteractionLocked
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        
        if (showCharacterDetail) {
            // 使用增强版角色详情页面
            val enhancedData = EnhancedCharacterAdapter.fromPlayerState(
                playerState = playerState,
                gameState = gameState
            )
            EnhancedCharacterDetailScreen(
                characterData = enhancedData,
                onDismiss = { showCharacterDetail = false }
            )
        }
    }
}

@Composable
private fun ChoiceButtons(
    choices: List<ChoiceParser.Choice>,
    onSelectChoice: (ChoiceParser.Choice) -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    androidx.compose.animation.AnimatedVisibility(
        visible = choices.isNotEmpty(),
        enter = fadeIn(animationSpec = tween(durationMillis = 500)) +
               scaleIn(
                   animationSpec = tween(durationMillis = 300, easing = LinearEasing),
                   initialScale = 0.8f
               ),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            choices.forEachIndexed { index, choice ->
                androidx.compose.animation.AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(
                        animationSpec = tween(
                            durationMillis = 300,
                            delayMillis = (index * 100).toInt()
                        )
                    ) + scaleIn(
                        animationSpec = tween(
                            durationMillis = 200,
                            delayMillis = (index * 100).toInt(),
                            easing = LinearEasing
                        ),
                        initialScale = 0.8f
                    )
                ) {
                    androidx.compose.material3.Button(
                        onClick = { onSelectChoice(choice) },
                        enabled = enabled,
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight() // 改为自适应高度
                            .defaultMinSize(minHeight = 44.dp), // 设置最小高度
                        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                            containerColor = DarkGray,
                            contentColor = White
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(
                            horizontal = 16.dp,
                            vertical = 12.dp
                        )
                    ) {
                        Text(
                            text = choice.text,
                            fontSize = 14.sp,
                            lineHeight = 20.sp, // 设置行高
                            maxLines = Int.MAX_VALUE // 允许无限行
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DropdownMenu(
    onSaveGame: () -> Unit,
    onLoadGame: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .background(DarkGray),
        color = DarkGray
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            MenuItem(
                icon = Icons.Default.Save,
                label = "存档",
                onClick = onSaveGame
            )

            MenuItem(
                icon = Icons.Default.Save,
                label = "读档",
                onClick = onLoadGame
            )
        }
    }
}

@Composable
private fun MenuItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = GoldPrimary,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = label,
            color = White,
            fontSize = 14.sp
        )
    }
}

@Composable
private fun LoadingIndicator(
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "loading")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing)
        ),
        label = "rotation"
    )
    val scale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = androidx.compose.animation.core.EaseInOut),
            repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
        ),
        label = "scale"
    )

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(DarkGray.copy(alpha = 0.9f))
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        androidx.compose.material3.CircularProgressIndicator(
            modifier = Modifier
                .size(24.dp)
                .rotate(rotation),
            color = GoldPrimary,
            strokeWidth = 3.dp
        )
        
        Text(
            text = "AI正在思考中...",
            color = White.copy(alpha = 0.9f),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
        
        // 添加一个小动画点
        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            repeat(3) { index ->
                val dotScale by infiniteTransition.animateFloat(
                    initialValue = 0.5f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(600, delayMillis = index * 200),
                        repeatMode = androidx.compose.animation.core.RepeatMode.Reverse
                    ),
                    label = "dot_$index"
                )
                
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .scale(dotScale)
                        .background(
                            color = GoldPrimary.copy(alpha = 0.7f),
                            shape = androidx.compose.foundation.shape.CircleShape
                        )
                )
            }
        }
    }
}
