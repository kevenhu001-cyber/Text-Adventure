package com.mysteriousjourney.ui.screen

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mysteriousjourney.domain.model.WorldviewData
import com.mysteriousjourney.ui.theme.Black
import com.mysteriousjourney.ui.theme.GoldPrimary
import com.mysteriousjourney.ui.theme.White

@Composable
fun WorldviewIntroductionScreen(
    onComplete: () -> Unit
) {
    var currentPage by remember { mutableStateOf(0) }
    var showConfirmation by remember { mutableStateOf(false) }
    val pages = WorldviewData.INTRODUCTION_PAGES

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Black)
    ) {
        // 背景渐变
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF1A1A1A), Black)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // 标题
            AnimatedContent(
                targetState = pages[currentPage].title,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                }
            ) { title ->
                Text(
                    text = title,
                    color = GoldPrimary,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 内容区域
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
                AnimatedContent(
                    targetState = currentPage,
                    transitionSpec = {
                        slideInHorizontally { it } + fadeIn() togetherWith
                                slideOutHorizontally { -it } + fadeOut()
                    }
                ) { pageIndex ->
                    val page = pages[pageIndex]
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // 装饰性标题背景
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .background(
                                    Brush.horizontalGradient(
                                        colors = listOf(Color.Transparent, GoldPrimary, Color.Transparent)
                                    )
                                )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // 内容区域 - 分段显示
                        val paragraphs = page.content.split("\n\n").filter { it.isNotBlank() }
                        paragraphs.forEachIndexed { index: Int, paragraph: String ->
                            if (index > 0) {
                                Spacer(modifier = Modifier.height(12.dp))
                            }
                            
                            // 段落装饰
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Top
                            ) {
                                // 段落标记
                                Box(
                                    modifier = Modifier
                                        .size(4.dp)
                                        .padding(top = 8.dp)
                                        .background(GoldPrimary)
                                )
                                
                                Spacer(modifier = Modifier.width(12.dp))
                                
                                Text(
                                    text = paragraph.trim(),
                                    color = White,
                                    fontSize = 16.sp,
                                    lineHeight = 24.sp,
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        if (pageIndex == pages.size - 1) {
                            Spacer(modifier = Modifier.height(32.dp))
                            // 额外信息：时间线和势力
                            WorldviewExtraInfo()
                        }
                    }
                }
            }

            // 底部控制
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { showConfirmation = true }) {
                    Text("跳过设定", color = Color.Gray)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(pages.size) { index ->
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .padding(horizontal = 2.dp)
                                .background(
                                    if (currentPage == index) GoldPrimary else Color.DarkGray,
                                    MaterialTheme.shapes.extraSmall
                                )
                        )
                    }
                }

                Button(
                    onClick = {
                        if (currentPage < pages.size - 1) {
                            currentPage++
                        } else {
                            onComplete()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary)
                ) {
                    Text(
                        text = if (currentPage == pages.size - 1) "开启旅程" else "继续",
                        color = Black
                    )
                    if (currentPage < pages.size - 1) {
                        Icon(Icons.Default.ArrowForward, null, Modifier.size(16.dp))
                    }
                }
            }
        }

        // 跳过确认弹窗
        if (showConfirmation) {
            AlertDialog(
                onDismissRequest = { showConfirmation = false },
                title = { Text("确认跳过？", color = GoldPrimary) },
                text = { Text("深入了解《诡秘之主》的世界观设定能获得更好的游戏体验。你确定要直接开始吗？", color = White) },
                confirmButton = {
                    TextButton(onClick = onComplete) {
                        Text("确定跳过", color = GoldPrimary)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showConfirmation = false }) {
                        Text("取消", color = White)
                    }
                },
                containerColor = Color(0xFF1A1A1A)
            )
        }
    }
}

@Composable
fun WorldviewExtraInfo() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Divider(color = Color.DarkGray, modifier = Modifier.padding(vertical = 16.dp))
        
        Text("关键势力", color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        WorldviewData.FACTIONS.forEach { faction ->
            Text(faction.name, color = GoldPrimary, fontSize = 16.sp, modifier = Modifier.padding(top = 8.dp))
            Text(faction.description, color = White.copy(alpha = 0.7f), fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("历史时间线", color = GoldPrimary, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        WorldviewData.TIMELINE.forEach { event ->
            Row(Modifier.padding(top = 8.dp)) {
                Text(event.year, color = GoldPrimary, fontWeight = FontWeight.Bold, modifier = Modifier.width(60.dp))
                Text(event.description, color = White.copy(alpha = 0.7f), fontSize = 14.sp)
            }
        }
    }
}
