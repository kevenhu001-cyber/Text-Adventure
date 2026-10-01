package com.mysteriousjourney.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.collect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mysteriousjourney.ui.theme.DarkGray
import com.mysteriousjourney.ui.theme.GoldPrimary
import com.mysteriousjourney.ui.theme.White
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class NarrativeMessage(
    val id: Long = System.currentTimeMillis(),
    val content: String,
    val isSystem: Boolean = false,
    val isPlayer: Boolean = false,
    val isComplete: Boolean = false,
    val isStreaming: Boolean = false,
    val isTypewriterComplete: Boolean = false // 新增：记录打字机/流式显示是否完成
)

@Composable
fun NarrativeText(
    messages: List<NarrativeMessage>,
    modifier: Modifier = Modifier,
    enableTypewriter: Boolean = true, // 默认开启
    typewriterSpeed: Long = 20L, // 稍微加快速度
    onTypewriterComplete: (() -> Unit)? = null
) {
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    
    // 跟踪哪些消息已经完全打字显示完毕
    val completedMessageIds = remember { mutableStateOf(setOf<Long>()) }
    
    // 跟踪用户滚动行为，区分自动滚动和手动滚动
    var userHasInteracted by remember { mutableStateOf(false) }
    var lastAutoScrollTime by remember { mutableStateOf(0L) }
    
    // 监听用户滚动行为，只有在明显的手动滚动时才标记
    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .collect { isScrolling ->
                if (isScrolling) {
                    val currentTime = System.currentTimeMillis()
                    // 只有在距离上次自动滚动超过500ms时的滚动才认为是用户交互
                    if (currentTime - lastAutoScrollTime > 500) {
                        userHasInteracted = true
                    }
                }
            }
    }
    
    // 当消息数量变化时，更积极地自动滚动
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            lastAutoScrollTime = System.currentTimeMillis()
            // 自动滚动到最新消息
            coroutineScope.launch {
                delay(100) // 短暂延迟确保DOM更新
                listState.animateScrollToItem(messages.size - 1)
            }
        }
    }
    
    val lastMessage = messages.lastOrNull()
    LaunchedEffect(lastMessage?.id, lastMessage?.content, lastMessage?.isStreaming) {
        if (lastMessage != null && lastMessage.content.isNotEmpty()) {
            lastAutoScrollTime = System.currentTimeMillis()
            // 立即滚动到最新内容，特别是流式消息
            coroutineScope.launch {
                listState.animateScrollToItem(messages.size - 1)
            }
        }
    }
    
    // 监听流式消息的内容变化，实时滚动
    LaunchedEffect(lastMessage?.content) {
        if (lastMessage != null && lastMessage.isStreaming) {
            lastAutoScrollTime = System.currentTimeMillis()
            // 流式输出时，实时跟随滚动
            coroutineScope.launch {
                listState.animateScrollToItem(messages.size - 1)
            }
        }
    }
    
    // 每隔一段时间重置用户交互状态，允许自动滚动
    LaunchedEffect(Unit) {
        while (true) {
            delay(5000) // 每5秒重置一次
            userHasInteracted = false
        }
    }
    
    LazyColumn(
        state = listState,
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 8.dp),
    ) {
        items(
            items = messages,
            key = { it.id }
        ) { message ->
            val isLastMessage = message == messages.lastOrNull()
            val hasCompleted = completedMessageIds.value.contains(message.id)
            
            // 流式消息直接显示，不使用打字机效果
            val shouldUseTypewriter = enableTypewriter && 
                isLastMessage && 
                !hasCompleted &&
                !message.isStreaming &&
                message.content.isNotEmpty()
            
            // 流式消息或已完成的消息直接显示完整内容
            val shouldShowComplete = hasCompleted || !isLastMessage || message.isStreaming
            
            NarrativeMessageItem(
                message = message,
                enableTypewriter = shouldUseTypewriter,
                typewriterSpeed = typewriterSpeed,
                onTypewriterComplete = {
                    if (isLastMessage) {
                        completedMessageIds.value = completedMessageIds.value + message.id
                        onTypewriterComplete?.invoke()
                        coroutineScope.launch {
                            delay(150)
                            listState.animateScrollToItem(messages.size - 1)
                        }
                    }
                },
                onTextUpdate = { 
                    // 在打字过程中实时滚动
                    if (isLastMessage) {
                        lastAutoScrollTime = System.currentTimeMillis()
                        coroutineScope.launch {
                            // 立即滚动，确保始终跟随新文本
                            listState.animateScrollToItem(messages.size - 1)
                        }
                    }
                },
                forceShowComplete = shouldShowComplete
            )
        }
    }
}

@Composable
private fun NarrativeMessageItem(
    message: NarrativeMessage,
    enableTypewriter: Boolean,
    typewriterSpeed: Long,
    onTypewriterComplete: (() -> Unit)? = null,
    onTextUpdate: ((String) -> Unit)? = null,
    forceShowComplete: Boolean = false,
    modifier: Modifier = Modifier
) {
    // 存储当前实际显示出来的文本
    var displayedText by remember(message.id) { 
        mutableStateOf(if (forceShowComplete) message.content else "") 
    }
    
    // 跟踪打字机效果是否已完成
    var isTypewriterComplete by remember(message.id) { 
        mutableStateOf(forceShowComplete) 
    }

    // 监听内容变化，实现逐字显示效果
    LaunchedEffect(message.content, enableTypewriter, forceShowComplete, message.isStreaming) {
        if (forceShowComplete) {
            displayedText = message.content
            isTypewriterComplete = true
            onTypewriterComplete?.invoke()
            return@LaunchedEffect
        }

        if (enableTypewriter) {
            // 如果新内容比当前显示的文本长，则逐字追加
            while (displayedText.length < message.content.length) {
                displayedText = message.content.substring(0, displayedText.length + 1)
                onTextUpdate?.invoke(displayedText)
                delay(typewriterSpeed)
            }
            
            // 如果流式传输已结束且打字也赶上了内容长度，则标记完成
            if (!message.isStreaming && displayedText.length >= message.content.length) {
                if (!isTypewriterComplete) {
                    isTypewriterComplete = true
                    onTypewriterComplete?.invoke()
                }
            }
        } else {
            displayedText = message.content
            if (!isTypewriterComplete) {
                isTypewriterComplete = true
                onTypewriterComplete?.invoke()
            }
        }
    }
    
    val backgroundColor = when {
        message.isSystem -> DarkGray.copy(alpha = 0.5f)
        message.isPlayer -> DarkGray.copy(alpha = 0.3f)
        else -> Color.Transparent
    }
    
    val textColor = when {
        message.isSystem -> GoldPrimary
        message.isPlayer -> White
        else -> GoldPrimary
    }
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(backgroundColor)
            .padding(12.dp)
    ) {
        // 显示文本
        if (displayedText.isNotEmpty()) {
            val showCursor = message.isStreaming || !isTypewriterComplete
            SimpleMarkdownText(
                text = if (showCursor) displayedText + "▌" else displayedText,
                color = textColor,
                fontSize = 15f,
                lineHeight = 22f
            )
        }
    }
}

private fun parseRichText(text: String, baseColor: Color): AnnotatedString {
    return buildAnnotatedString {
        var remainingText = text
        
        while (remainingText.isNotEmpty()) {
            when {
                remainingText.startsWith("**") -> {
                    val endIndex = remainingText.indexOf("**", 2)
                    if (endIndex != -1) {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = baseColor)) {
                            append(remainingText.substring(2, endIndex))
                        }
                        remainingText = remainingText.substring(endIndex + 2)
                    } else {
                        append("**")
                        remainingText = remainingText.substring(2)
                    }
                }
                remainingText.startsWith("*") && !remainingText.startsWith("**") -> {
                    val endIndex = remainingText.indexOf("*", 1)
                    if (endIndex != -1) {
                        withStyle(SpanStyle(fontStyle = FontStyle.Italic, color = baseColor)) {
                            append(remainingText.substring(1, endIndex))
                        }
                        remainingText = remainingText.substring(endIndex + 1)
                    } else {
                        append("*")
                        remainingText = remainingText.substring(1)
                    }
                }
                else -> {
                    append(remainingText[0])
                    remainingText = remainingText.substring(1)
                }
            }
        }
    }
}
