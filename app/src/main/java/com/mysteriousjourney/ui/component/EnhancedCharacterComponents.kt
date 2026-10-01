package com.mysteriousjourney.ui.component

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mysteriousjourney.domain.model.*

@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String? = null,
    color: Color = Color(0xFF1A1A2E),
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp),
        colors = CardDefaults.cardColors(containerColor = color),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFFD700))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = Color.Black
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
            }
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = Color(0xFF9CA3AF),
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = value,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                if (subtitle != null) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = subtitle,
                        color = Color(0xFFFFD700),
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
fun ProgressBarWithLabel(
    label: String,
    current: Float,
    max: Float,
    color: Color,
    modifier: Modifier = Modifier,
    showValue: Boolean = true,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null
) {
    Column(modifier = modifier.padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (icon != null) {
                    Icon(icon, null, modifier = Modifier.size(16.dp), tint = color)
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = label,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
            if (showValue) {
                Text(
                    text = "${current.toInt()}/${max.toInt()}",
                    color = color,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        LinearProgressIndicator(
            progress = current / max,
            modifier = Modifier.fillMaxWidth().height(6.dp),
            color = color,
            trackColor = Color(0xFF374151)
        )
    }
}

@Composable
fun CircularStatRing(
    label: String,
    value: Float,
    max: Float,
    color: Color,
    size: Int = 80,
    strokeWidth: Int = 6
) {
    val progress = if (max > 0) value / max else 0f
    val rotation by animateFloatAsState(
        targetValue = progress * 360,
        animationSpec = tween(durationMillis = 1000, easing = EaseOut),
        label = "rotation"
    )
    
    Box(
        modifier = Modifier.size(size.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val circleSize = size.dp.toPx()
            val strokeWidthPx = strokeWidth.dp.toPx()
            
            drawCircle(
                color = Color(0xFF374151),
                radius = circleSize / 2 - strokeWidthPx / 2,
                center = center
            )
            
            drawArc(
                color = color,
                startAngle = -90f,
                sweepAngle = rotation,
                useCenter = false,
                size = Size(circleSize - strokeWidthPx, circleSize - strokeWidthPx),
                topLeft = Offset(strokeWidthPx / 2, strokeWidthPx / 2)
            )
        }
        
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${(progress * 100).toInt()}%",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = label,
                color = Color(0xFF9CA3AF),
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun StatusEffectBadge(
    name: String,
    type: GameStatus.StatusType,
    duration: Int = -1,
    modifier: Modifier = Modifier
) {
    val color = when (type) {
        GameStatus.StatusType.BUFF -> Color(0xFF10B981)
        GameStatus.StatusType.DEBUFF -> Color(0xFFEF4444)
        GameStatus.StatusType.NEUTRAL -> Color(0xFF6B7280)
        GameStatus.StatusType.SPECIAL -> Color(0xFF8B5CF6)
        GameStatus.StatusType.RELATIONSHIP -> Color(0xFFF59E0B)
    }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2937)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(4.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                if (duration > 0) {
                    Text(
                        text = "剩余: $duration 回合",
                        color = Color(0xFF9CA3AF),
                        fontSize = 12.sp
                    )
                }
            }
            
            Icon(
                when (type) {
                    GameStatus.StatusType.BUFF -> Icons.Default.Star
                    GameStatus.StatusType.DEBUFF -> Icons.Default.Warning
                    GameStatus.StatusType.NEUTRAL -> Icons.Default.Info
                    GameStatus.StatusType.SPECIAL -> Icons.Default.Lock
                    GameStatus.StatusType.RELATIONSHIP -> Icons.Default.Person
                },
                null,
                modifier = Modifier.size(16.dp),
                tint = color
            )
        }
    }
}

@Composable
fun RelationshipNode(
    name: String,
    type: CharacterRelation.RelationType,
    score: Int,
    modifier: Modifier = Modifier
) {
    val color = when (type) {
        CharacterRelation.RelationType.ALLY, CharacterRelation.RelationType.FRIEND -> Color(0xFF10B981)
        CharacterRelation.RelationType.ENEMY -> Color(0xFFEF4444)
        CharacterRelation.RelationType.NEUTRAL -> Color(0xFF6B7280)
        CharacterRelation.RelationType.RIVAL -> Color(0xFFF59E0B)
        CharacterRelation.RelationType.MENTOR, CharacterRelation.RelationType.STUDENT -> Color(0xFF8B5CF6)
        else -> Color(0xFF9CA3AF)
    }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2937)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f))
                    .padding(8.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    when (type) {
                        CharacterRelation.RelationType.ALLY -> Icons.Default.Shield
                        CharacterRelation.RelationType.FRIEND -> Icons.Default.Favorite
                        CharacterRelation.RelationType.ENEMY -> Icons.Default.Warning
                        CharacterRelation.RelationType.NEUTRAL -> Icons.Default.Person
                        CharacterRelation.RelationType.RIVAL -> Icons.Default.SportsEsports
                        CharacterRelation.RelationType.MENTOR -> Icons.Default.School
                        CharacterRelation.RelationType.STUDENT -> Icons.Default.Book
                        else -> Icons.Default.Person
                    },
                    null,
                    tint = color
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = name,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = type.name,
                    color = Color(0xFF9CA3AF),
                    fontSize = 12.sp
                )
            }
            
            Text(
                text = "$score",
                color = color,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ProgressRing(
    progress: Float,
    color: Color,
    size: Int = 60,
    strokeWidth: Int = 4
) {
    val rotation by animateFloatAsState(
        targetValue = progress * 360,
        animationSpec = tween(durationMillis = 500),
        label = "rotation"
    )
    
    Canvas(modifier = Modifier.size(size.dp)) {
        val circleSize = size.dp.toPx()
        val strokeWidthPx = strokeWidth.dp.toPx()
        
        drawCircle(
            color = Color(0xFF374151),
            radius = circleSize / 2 - strokeWidthPx / 2,
            center = center
        )
        
        drawArc(
            color = color,
            startAngle = -90f,
            sweepAngle = rotation,
            useCenter = false,
            size = Size(circleSize - strokeWidthPx, circleSize - strokeWidthPx),
            topLeft = Offset(strokeWidthPx / 2, strokeWidthPx / 2)
        )
    }
}

@Composable
fun AttributeGrid(
    attributes: Map<String, Int>,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        attributes.forEach { (name, value) ->
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = name,
                    color = Color(0xFF9CA3AF),
                    fontSize = 13.sp
                )
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .width((value / 20f).coerceIn(0f, 100f).dp)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFFFFD700))
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = value.toString(),
                        color = Color(0xFFFFD700),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun LogEntry(
    log: DailyLog,
    modifier: Modifier = Modifier
) {
    val color = when (log.type) {
        DailyLog.LogType.COMBAT -> Color(0xFFEF4444)
        DailyLog.LogType.INVESTIGATION -> Color(0xFF3B82F6)
        DailyLog.LogType.SOCIAL -> Color(0xFF10B981)
        DailyLog.LogType.TRAINING -> Color(0xFF8B5CF6)
        DailyLog.LogType.MYSTERY -> Color(0xFFF59E0B)
        DailyLog.LogType.ACHIEVEMENT -> Color(0xFFEAB308)
        DailyLog.LogType.RELATIONSHIP -> Color(0xFFF472B6)
        else -> Color(0xFF6B7280)
    }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1F2937)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    when (log.type) {
                        DailyLog.LogType.COMBAT -> Icons.Default.Info
                        DailyLog.LogType.INVESTIGATION -> Icons.Default.Search
                        DailyLog.LogType.SOCIAL -> Icons.Default.Chat
                        DailyLog.LogType.TRAINING -> Icons.Default.FitnessCenter
                        DailyLog.LogType.MYSTERY -> Icons.Default.Lock
                        DailyLog.LogType.ACHIEVEMENT -> Icons.Default.Star
                        DailyLog.LogType.RELATIONSHIP -> Icons.Default.Person
                        else -> Icons.Default.Info
                    },
                    null,
                    tint = color
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = log.title,
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${log.impact}点影响",
                        color = color,
                        fontSize = 11.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = log.description,
                    color = Color(0xFF9CA3AF),
                    fontSize = 12.sp,
                    maxLines = 2,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
fun SkillTreeItem(
    skill: SkillNode,
    modifier: Modifier = Modifier
) {
    val color = when (skill.path) {
        SkillNode.SkillPath.COMBAT -> Color(0xFFEF4444)
        SkillNode.SkillPath.MYSTERY -> Color(0xFF8B5CF6)
        SkillNode.SkillPath.SOCIAL -> Color(0xFF10B981)
        SkillNode.SkillPath.STEALTH -> Color(0xFF6B7280)
        SkillNode.SkillPath.SPECIAL -> Color(0xFFF59E0B)
    }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (skill.unlocked) Color(0xFF1F2937) else Color(0xFF111827)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (skill.unlocked) color else Color(0xFF374151)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${skill.level}/${skill.maxLevel}",
                    color = if (skill.unlocked) Color.White else Color(0xFF9CA3AF),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = skill.name,
                        color = if (skill.unlocked) Color.White else Color(0xFF6B7280),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = skill.path.name,
                        color = color,
                        fontSize = 10.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = skill.description,
                    color = Color(0xFF9CA3AF),
                    fontSize = 11.sp,
                    maxLines = 2
                )
            }
        }
    }
}

@Composable
fun MysteryPointCard(
    point: MysteryPoint,
    modifier: Modifier = Modifier
) {
    val color = when (point.category) {
        MysteryPoint.MysteryCategory.PERSON -> Color(0xFFF59E0B)
        MysteryPoint.MysteryCategory.LOCATION -> Color(0xFF3B82F6)
        MysteryPoint.MysteryCategory.ORGANIZATION -> Color(0xFF8B5CF6)
        MysteryPoint.MysteryCategory.EVENT -> Color(0xFFEF4444)
        MysteryPoint.MysteryCategory.MYTHOS -> Color(0xFF10B981)
    }
    
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (point.discovered) Color(0xFF1F2937) else Color(0xFF111827)
        ),
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
                    .background(if (point.discovered) color else Color(0xFF374151)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    when (point.category) {
                        MysteryPoint.MysteryCategory.PERSON -> Icons.Default.Person
                        MysteryPoint.MysteryCategory.LOCATION -> Icons.Default.Map
                        MysteryPoint.MysteryCategory.ORGANIZATION -> Icons.Default.Group
                        MysteryPoint.MysteryCategory.EVENT -> Icons.Default.Event
                        MysteryPoint.MysteryCategory.MYTHOS -> Icons.Default.Lock
                    },
                    null,
                    tint = if (point.discovered) Color.White else Color(0xFF6B7280),
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
                        text = point.name,
                        color = if (point.discovered) Color.White else Color(0xFF6B7280),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = point.category.name,
                        color = color,
                        fontSize = 10.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (point.discovered) point.description else "???",
                    color = if (point.discovered) Color(0xFF9CA3AF) else Color(0xFF4B5563),
                    fontSize = 11.sp,
                    maxLines = 2
                )
            }
        }
    }
}
