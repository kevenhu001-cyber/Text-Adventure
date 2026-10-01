package com.mysteriousjourney.ui.component

import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.sp

/**
 * Markdown文本渲染组件
 * 支持常见的Markdown格式：粗体、斜体、删除线、代码、链接等
 */
@Composable
fun MarkdownText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    fontSize: Float = 14.sp.value,
    lineHeight: Float = 20.sp.value,
    onClick: ((String) -> Unit)? = null,
    onTextLayout: (TextLayoutResult) -> Unit = {}
) {
    val annotatedString = parseMarkdown(text, color, fontSize, onClick)
    
    if (onClick != null) {
        ClickableText(
            text = annotatedString,
            modifier = modifier,
            style = TextStyle(
                color = color,
                fontSize = fontSize.sp,
                lineHeight = (lineHeight).sp,
                fontFamily = FontFamily.Default
            ),
            onClick = { offset ->
                annotatedString.getStringAnnotations(offset, offset)
                    .firstOrNull { it.item == "link" }
                    ?.let { onClick(it.tag) }
            },
            onTextLayout = onTextLayout
        )
    } else {
        androidx.compose.material3.Text(
            text = annotatedString,
            modifier = modifier,
            style = TextStyle(
                color = color,
                fontSize = fontSize.sp,
                lineHeight = (lineHeight).sp,
                fontFamily = FontFamily.Default
            ),
            onTextLayout = onTextLayout
        )
    }
}

/**
 * 解析Markdown文本
 */
private fun parseMarkdown(
    text: String,
    baseColor: Color,
    fontSize: Float,
    onClick: ((String) -> Unit)? = null
): AnnotatedString {
    // 首先过滤掉括号内的状态提示信息
    var filteredText = removeParenthesesContent(text)
    
    return androidx.compose.ui.text.buildAnnotatedString {
        var remainingText = filteredText
        var isInCodeBlock = false
        var codeBlockContent = StringBuilder()
        
        while (remainingText.isNotEmpty()) {
            when {
                // 处理代码块
                remainingText.startsWith("```") -> {
                    if (!isInCodeBlock) {
                        // 开始代码块
                        isInCodeBlock = true
                        remainingText = remainingText.substring(3)
                    } else {
                        // 结束代码块
                        isInCodeBlock = false
                        withStyle(
                            SpanStyle(
                                fontFamily = FontFamily.Monospace,
                                background = Color(0xFF2D2D2D),
                                color = Color(0xFFE6E6E6),
                                fontSize = (fontSize - 2).sp
                            )
                        ) {
                            append(codeBlockContent.toString())
                        }
                        codeBlockContent.clear()
                        remainingText = remainingText.substring(3)
                    }
                }
                
                isInCodeBlock -> {
                    // 在代码块内，直接添加字符
                    codeBlockContent.append(remainingText[0])
                    remainingText = remainingText.substring(1)
                }
                
                // 处理粗体 **text**
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
                
                // 处理斜体 *text*
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
                
                // 处理删除线 ~~text~~
                remainingText.startsWith("~~") -> {
                    val endIndex = remainingText.indexOf("~~", 2)
                    if (endIndex != -1) {
                        withStyle(
                            SpanStyle(
                                textDecoration = TextDecoration.LineThrough,
                                color = baseColor
                            )
                        ) {
                            append(remainingText.substring(2, endIndex))
                        }
                        remainingText = remainingText.substring(endIndex + 2)
                    } else {
                        append("~~")
                        remainingText = remainingText.substring(2)
                    }
                }
                
                // 处理行内代码 `text`
                remainingText.startsWith("`") -> {
                    val endIndex = remainingText.indexOf("`", 1)
                    if (endIndex != -1) {
                        withStyle(
                            SpanStyle(
                                fontFamily = FontFamily.Monospace,
                                background = Color(0xFF2D2D2D),
                                color = Color(0xFFE6E6E6),
                                fontSize = (fontSize - 1).sp
                            )
                        ) {
                            append(remainingText.substring(1, endIndex))
                        }
                        remainingText = remainingText.substring(endIndex + 1)
                    } else {
                        append("`")
                        remainingText = remainingText.substring(1)
                    }
                }
                
                // 处理标题 # text
                remainingText.startsWith("# ") -> {
                    val endIndex = remainingText.indexOf("\n")
                    val titleText = if (endIndex != -1) {
                        remainingText.substring(2, endIndex)
                    } else {
                        remainingText.substring(2)
                    }
                    
                    withStyle(
                        SpanStyle(
                            fontWeight = FontWeight.Bold,
                            color = baseColor,
                            fontSize = (fontSize + 4).sp
                        )
                    ) {
                        append(titleText)
                    }
                    
                    remainingText = if (endIndex != -1) {
                        remainingText.substring(endIndex)
                    } else {
                        ""
                    }
                }
                
                // 处理二级标题 ## text
                remainingText.startsWith("## ") -> {
                    val endIndex = remainingText.indexOf("\n")
                    val titleText = if (endIndex != -1) {
                        remainingText.substring(3, endIndex)
                    } else {
                        remainingText.substring(3)
                    }
                    
                    withStyle(
                        SpanStyle(
                            fontWeight = FontWeight.Bold,
                            color = baseColor,
                            fontSize = (fontSize + 2).sp
                        )
                    ) {
                        append(titleText)
                    }
                    
                    remainingText = if (endIndex != -1) {
                        remainingText.substring(endIndex)
                    } else {
                        ""
                    }
                }
                
                // 处理三级标题 ### text
                remainingText.startsWith("### ") -> {
                    val endIndex = remainingText.indexOf("\n")
                    val titleText = if (endIndex != -1) {
                        remainingText.substring(4, endIndex)
                    } else {
                        remainingText.substring(4)
                    }
                    
                    withStyle(
                        SpanStyle(
                            fontWeight = FontWeight.Bold,
                            color = baseColor,
                            fontSize = (fontSize + 1).sp
                        )
                    ) {
                        append(titleText)
                    }
                    
                    remainingText = if (endIndex != -1) {
                        remainingText.substring(endIndex)
                    } else {
                        ""
                    }
                }
                
                // 处理链接 [text](url)
                remainingText.startsWith("[") -> {
                    val linkEndIndex = remainingText.indexOf("]")
                    val urlStartIndex = remainingText.indexOf("(", linkEndIndex)
                    val urlEndIndex = remainingText.indexOf(")", urlStartIndex)
                    
                    if (linkEndIndex != -1 && urlStartIndex != -1 && urlEndIndex != -1) {
                        val linkText = remainingText.substring(1, linkEndIndex)
                        val url = remainingText.substring(urlStartIndex + 1, urlEndIndex)
                        
                        if (onClick != null) {
                            pushStringAnnotation(tag = url, annotation = "link")
                            withStyle(
                                SpanStyle(
                                    color = Color(0xFF2196F3),
                                    textDecoration = TextDecoration.Underline
                                )
                            ) {
                                append(linkText)
                            }
                            pop()
                        } else {
                            withStyle(
                                SpanStyle(
                                    color = Color(0xFF2196F3),
                                    textDecoration = TextDecoration.Underline
                                )
                            ) {
                                append(linkText)
                            }
                        }
                        
                        remainingText = remainingText.substring(urlEndIndex + 1)
                    } else {
                        append("[")
                        remainingText = remainingText.substring(1)
                    }
                }
                
                // 处理换行
                remainingText.startsWith("\n") -> {
                    append("\n")
                    remainingText = remainingText.substring(1)
                }
                
                else -> {
                    append(remainingText[0])
                    remainingText = remainingText.substring(1)
                }
            }
        }
    }
}

/**
 * 移除括号内的内容，保留括号外的文本
 */
private fun removeParenthesesContent(text: String): String {
    var result = text
    // 使用正则表达式匹配圆括号内的内容并移除
    val parenthesesPattern = Regex("\\(([^)]*)\\)")
    result = result.replace(parenthesesPattern, "")
    
    // 清理多余的空格
    result = result.replace(Regex("\\s+"), " ").trim()
    
    return result
}

/**
 * 简化的Markdown文本组件，用于不需要点击链接的场景
 */
@Composable
fun SimpleMarkdownText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.White,
    fontSize: Float = 14.sp.value,
    lineHeight: Float = 20.sp.value
) {
    MarkdownText(
        text = text,
        modifier = modifier,
        color = color,
        fontSize = fontSize,
        lineHeight = lineHeight,
        onClick = null
    )
}
