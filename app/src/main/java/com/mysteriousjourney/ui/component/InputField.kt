package com.mysteriousjourney.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mysteriousjourney.ui.theme.Black
import com.mysteriousjourney.ui.theme.DarkGray
import com.mysteriousjourney.ui.theme.GoldPrimary
import com.mysteriousjourney.ui.theme.LightGray
import com.mysteriousjourney.ui.theme.MediumGray
import com.mysteriousjourney.ui.theme.White

/**
 * 输入框（Socrates/ChatGPT 风格 composer）
 * 圆角胶囊卡片：上方为可多行扩展的编辑区，下方右侧为圆形实心发送键
 */
@Composable
fun InputField(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }
    val canSend = enabled && value.isNotBlank()

    val borderColor by animateColorAsState(
        targetValue = if (isFocused) GoldPrimary.copy(alpha = 0.7f)
                      else GoldPrimary.copy(alpha = 0.22f),
        animationSpec = tween(180),
        label = "composerBorder"
    )

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(26.dp))
            .background(DarkGray)
            .border(width = 1.dp, color = borderColor, shape = RoundedCornerShape(26.dp))
            .padding(start = 16.dp, end = 10.dp, top = 10.dp, bottom = 8.dp)
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 26.dp, max = 132.dp)
                .onFocusChanged { isFocused = it.isFocused },
            textStyle = TextStyle(
                color = White,
                fontSize = 15.sp,
                lineHeight = 24.sp
            ),
            cursorBrush = SolidColor(GoldPrimary),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(
                onSend = { if (canSend) onSend() }
            ),
            decorationBox = { innerTextField ->
                Box {
                    if (value.isEmpty()) {
                        Text(
                            text = "输入你的行动…",
                            color = White.copy(alpha = 0.45f),
                            fontSize = 15.sp
                        )
                    }
                    innerTextField()
                }
            }
        )

        // 控制栏：右侧圆形实心发送按钮
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val interactionSource = remember { MutableInteractionSource() }
            val pressed by interactionSource.collectIsPressedAsState()
            val sendScale by animateFloatAsState(
                targetValue = if (pressed && canSend) 0.88f else 1f,
                animationSpec = tween(120),
                label = "sendScale"
            )

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .scale(sendScale)
                    .clip(CircleShape)
                    .background(if (canSend) GoldPrimary else MediumGray)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        enabled = canSend,
                        onClick = onSend
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "发送",
                    tint = if (canSend) Black else LightGray,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
