package com.mysteriousjourney.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mysteriousjourney.data.api.AiApiService
import com.mysteriousjourney.data.settings.CustomModelConfig
import com.mysteriousjourney.data.settings.SettingsRepository
import com.mysteriousjourney.ui.GameViewModel
import com.mysteriousjourney.ui.theme.*
import kotlinx.coroutines.launch

/**
 * AI 模型设置页面
 * 允许用户配置自定义 API URL / Model ID / API Key
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    gameViewModel: GameViewModel,
    onBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val settingsRepo = remember { SettingsRepository() }

    // 状态
    var customModels by remember { mutableStateOf<List<CustomModelConfig>>(emptyList()) }
    var activeModelType by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(true) }

    // 编辑对话框状态
    var showEditDialog by remember { mutableStateOf(false) }
    var editingModel by remember { mutableStateOf<CustomModelConfig?>(null) }

    // 加载配置
    LaunchedEffect(Unit) {
        val config = settingsRepo.getConfig()
        customModels = config.customModels
        activeModelType = config.activeModelType
        isLoading = false
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI 模型设置", color = White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, "返回", tint = GoldPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkGray
                )
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = GoldPrimary)
            }
            return@Scaffold
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Black)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 自定义模型区域
                item {
                    Text(
                        "我的模型",
                        color = GoldPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "配置 OpenAI 兼容的 API 地址、模型 ID 和 Key",
                        color = White.copy(alpha = 0.6f),
                        fontSize = 12.sp
                    )
                }

                // 自定义模型列表
                if (customModels.isEmpty()) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = DarkGray),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "暂无自定义模型\n点击下方按钮添加",
                                    color = White.copy(alpha = 0.4f),
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                } else {
                    itemsIndexed(customModels) { index, model ->
                        CustomModelCard(
                            model = model,
                            isActive = activeModelType == "custom_${model.id}",
                            onActivate = {
                                activeModelType = "custom_${model.id}"
                                scope.launch {
                                    settingsRepo.setActiveModel("custom_${model.id}")
                                    gameViewModel.switchToCustomModel(model)
                                }
                            },
                            onEdit = {
                                editingModel = model
                                showEditDialog = true
                            },
                            onDelete = {
                                scope.launch {
                                    settingsRepo.deleteCustomModel(model.id)
                                    customModels = customModels.filter { it.id != model.id }
                                }
                            }
                        )
                    }
                }

                // 添加按钮
                item {
                    Button(
                        onClick = {
                            editingModel = null
                            showEditDialog = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MysticPurple
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Add, null, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("添加自定义模型")
                    }
                }

                // 底部间距
                item { Spacer(modifier = Modifier.height(32.dp)) }
            }
        }
    }

    // 编辑对话框
    if (showEditDialog) {
        ModelEditDialog(
            initialModel = editingModel,
            onDismiss = { showEditDialog = false },
            onSave = { model ->
                scope.launch {
                    if (editingModel != null) {
                        // 更新
                        settingsRepo.updateCustomModel(editingModel!!.id, model)
                        customModels = customModels.map {
                            if (it.id == editingModel!!.id) model else it
                        }
                    } else {
                        // 新增（id 由仓库生成）
                        val stored = settingsRepo.addCustomModel(model)
                        customModels = customModels + stored
                    }
                    showEditDialog = false
                }
            }
        )
    }
}

@Composable
private fun CustomModelCard(
    model: CustomModelConfig,
    isActive: Boolean,
    onActivate: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val borderColor = if (isActive) GoldPrimary.copy(alpha = 0.5f) else Color.Transparent

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) DarkGray.copy(alpha = 0.8f) else DarkGray
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = isActive,
                    onClick = onActivate,
                    colors = RadioButtonDefaults.colors(
                        selectedColor = GoldPrimary,
                        unselectedColor = LightGray
                    )
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = model.displayName.ifEmpty { "未命名模型" },
                        color = if (isActive) GoldPrimary else White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = model.modelId.ifEmpty { "未设置 Model ID" },
                        color = White.copy(alpha = 0.5f),
                        fontSize = 11.sp
                    )
                }
                Row {
                    IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Edit, "编辑", tint = GoldPrimary, modifier = Modifier.size(18.dp))
                    }
                    IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, "删除", tint = MadnessRed, modifier = Modifier.size(18.dp))
                    }
                }
            }

            // 显示 API URL 简略信息
            if (model.apiUrl.isNotBlank()) {
                Text(
                    text = model.apiUrl.take(60) + if (model.apiUrl.length > 60) "..." else "",
                    color = White.copy(alpha = 0.3f),
                    fontSize = 10.sp,
                    modifier = Modifier.padding(start = 40.dp)
                )
            }
        }
    }
}

@Composable
private fun ModelEditDialog(
    initialModel: CustomModelConfig?,
    onDismiss: () -> Unit,
    onSave: (CustomModelConfig) -> Unit
) {
    val scope = rememberCoroutineScope()
    val testService = remember { AiApiService() }

    var displayName by remember { mutableStateOf(initialModel?.displayName ?: "") }
    var apiUrl by remember { mutableStateOf(initialModel?.apiUrl ?: "") }
    var modelId by remember { mutableStateOf(initialModel?.modelId ?: "") }
    var apiKey by remember { mutableStateOf(initialModel?.apiKey ?: "") }
    var showKey by remember { mutableStateOf(false) }
    var testResult by remember { mutableStateOf<String?>(null) }
    var isTesting by remember { mutableStateOf(false) }

    val isEditing = initialModel != null
    val isValid = displayName.isNotBlank() && apiUrl.isNotBlank() && modelId.isNotBlank() && apiKey.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1A1A1A),
        title = {
            Text(
                if (isEditing) "编辑模型" else "添加自定义模型",
                color = GoldPrimary,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 显示名称
                Text("显示名称", color = White.copy(alpha = 0.7f), fontSize = 12.sp)
                OutlinedTextField(
                    value = displayName,
                    onValueChange = { displayName = it },
                    placeholder = { Text("例: 我的模型", color = LightGray) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = White,
                        unfocusedTextColor = White,
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = LightGray,
                        cursorColor = GoldPrimary
                    ),
                    singleLine = true
                )

                // API URL
                Text("API URL", color = White.copy(alpha = 0.7f), fontSize = 12.sp)
                OutlinedTextField(
                    value = apiUrl,
                    onValueChange = { apiUrl = it },
                    placeholder = { Text("https://api.example.com/v1/chat/completions", color = LightGray) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = White,
                        unfocusedTextColor = White,
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = LightGray,
                        cursorColor = GoldPrimary
                    ),
                    singleLine = true
                )

                // Model ID
                Text("Model ID", color = White.copy(alpha = 0.7f), fontSize = 12.sp)
                OutlinedTextField(
                    value = modelId,
                    onValueChange = { modelId = it },
                    placeholder = { Text("your-model-id", color = LightGray) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = White,
                        unfocusedTextColor = White,
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = LightGray,
                        cursorColor = GoldPrimary
                    ),
                    singleLine = true
                )

                // API Key
                Text("API Key", color = White.copy(alpha = 0.7f), fontSize = 12.sp)
                OutlinedTextField(
                    value = apiKey,
                    onValueChange = { apiKey = it },
                    placeholder = { Text("sk-...", color = LightGray) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = White,
                        unfocusedTextColor = White,
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = LightGray,
                        cursorColor = GoldPrimary
                    ),
                    singleLine = true,
                    visualTransformation = if (showKey) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { showKey = !showKey }) {
                            Icon(
                                if (showKey) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                if (showKey) "隐藏" else "显示",
                                tint = LightGray
                            )
                        }
                    }
                )

                // 测试结果
                if (testResult != null) {
                    Text(
                        text = testResult!!,
                        color = if (testResult!!.contains("成功")) Color(0xFF4CAF50) else MadnessRed,
                        fontSize = 12.sp
                    )
                }
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(onClick = onDismiss) {
                    Text("取消", color = White)
                }
                Spacer(modifier = Modifier.weight(1f))
                TextButton(
                    onClick = {
                        scope.launch {
                            isTesting = true
                            testResult = null
                            val result = testService.testConnection(
                                apiUrl.trim(), modelId.trim(), apiKey.trim()
                            )
                            testResult = if (result.isSuccess) {
                                "连接成功"
                            } else {
                                "连接失败：${result.exceptionOrNull()?.message ?: "未知错误"}"
                            }
                            isTesting = false
                        }
                    },
                    enabled = !isTesting && apiUrl.isNotBlank() && modelId.isNotBlank() && apiKey.isNotBlank()
                ) {
                    Text(
                        if (isTesting) "测试中…" else "测试连接",
                        color = GoldPrimary
                    )
                }
                Button(
                    onClick = {
                        onSave(
                            CustomModelConfig(
                                id = initialModel?.id ?: "",
                                displayName = displayName,
                                apiUrl = apiUrl,
                                modelId = modelId,
                                apiKey = apiKey,
                                isActive = false
                            )
                        )
                    },
                    enabled = isValid,
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        if (isEditing) "保存" else "添加",
                        color = Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    )
}
