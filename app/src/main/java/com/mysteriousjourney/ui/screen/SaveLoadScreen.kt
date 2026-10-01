package com.mysteriousjourney.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mysteriousjourney.ui.theme.Black
import com.mysteriousjourney.ui.theme.DarkGray
import com.mysteriousjourney.ui.theme.GoldPrimary
import com.mysteriousjourney.ui.theme.LightGray
import com.mysteriousjourney.ui.theme.MadnessRed
import com.mysteriousjourney.ui.theme.SpiritBlue
import com.mysteriousjourney.ui.theme.White
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class SaveSlot(
    val id: Long,
    val name: String,
    val timestamp: Long,
    val playerName: String,
    val location: String,
    val spirit: Int,
    val madness: Int,
    val playTime: String
)

data class SaveLoadUiState(
    val saves: List<SaveSlot> = emptyList(),
    val isLoading: Boolean = false,
    val mode: SaveLoadMode = SaveLoadMode.SAVE
)

enum class SaveLoadMode {
    SAVE, LOAD
}

@Composable
fun SaveLoadScreen(
    uiState: SaveLoadUiState,
    onBack: () -> Unit,
    onNewSave: () -> Unit,
    onLoadSave: (Long) -> Unit,
    onDeleteSave: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Black)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(DarkGray)
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "返回",
                    tint = GoldPrimary
                )
            }
            
            Text(
                text = if (uiState.mode == SaveLoadMode.SAVE) "存档" else "读档",
                color = GoldPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.width(48.dp))
        }
        
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (uiState.mode == SaveLoadMode.SAVE) {
                ActionButton(
                    icon = Icons.Default.Add,
                    label = "新建存档",
                    onClick = onNewSave,
                    modifier = Modifier.weight(1f)
                )
            }
        }
        
        if (uiState.saves.isEmpty()) {
            EmptySaveState(
                mode = uiState.mode,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = uiState.saves,
                    key = { it.id }
                ) { save ->
                    SaveSlotItem(
                        save = save,
                        mode = uiState.mode,
                        onLoad = { onLoadSave(save.id) },
                        onDelete = { onDeleteSave(save.id) }
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(DarkGray)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = GoldPrimary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = label,
            color = White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun SaveSlotItem(
    save: SaveSlot,
    mode: SaveLoadMode,
    onLoad: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(save.timestamp))
    
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onLoad),
        color = DarkGray
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = save.name,
                    color = GoldPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = formattedDate,
                    color = White.copy(alpha = 0.5f),
                    fontSize = 12.sp
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "角色: ${save.playerName}",
                    color = White.copy(alpha = 0.8f),
                    fontSize = 13.sp
                )
                Text(
                    text = "地点: ${save.location}",
                    color = White.copy(alpha = 0.8f),
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(6.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatusChip(
                        label = "灵性",
                        value = save.spirit.toString(),
                        color = SpiritBlue
                    )
                    StatusChip(
                        label = "疯狂",
                        value = save.madness.toString(),
                        color = MadnessRed
                    )
                    Text(
                        text = "游戏时长: ${save.playTime}",
                        color = White.copy(alpha = 0.6f),
                        fontSize = 12.sp
                    )
                }
                
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "删除",
                        tint = MadnessRed.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusChip(
    label: String,
    value: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label:",
            color = White.copy(alpha = 0.6f),
            fontSize = 12.sp
        )
        Text(
            text = value,
            color = color,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun EmptySaveState(
    mode: SaveLoadMode,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = if (mode == SaveLoadMode.SAVE) Icons.Default.Save else Icons.Default.FolderOpen,
            contentDescription = null,
            tint = LightGray,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (mode == SaveLoadMode.SAVE) "暂无存档" else "暂无可加载的存档",
            color = White.copy(alpha = 0.5f),
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = if (mode == SaveLoadMode.SAVE) "点击上方按钮创建新存档" else "请先创建存档",
            color = White.copy(alpha = 0.3f),
            fontSize = 14.sp
        )
    }
}