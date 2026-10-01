package com.mysteriousjourney.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mysteriousjourney.data.api.AiApiService
import com.mysteriousjourney.data.repository.SaveRepository
import com.mysteriousjourney.data.settings.CustomModelConfig
import com.mysteriousjourney.data.settings.SettingsRepository
import com.mysteriousjourney.domain.GameEngine
import com.mysteriousjourney.domain.model.GameState
import com.mysteriousjourney.domain.model.OpeningScenario
import com.mysteriousjourney.domain.model.OpeningScenarios
import com.mysteriousjourney.ui.component.NarrativeMessage
import com.mysteriousjourney.util.ChoiceParser
import com.mysteriousjourney.util.StateParser
import com.mysteriousjourney.util.TextFormatter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

data class GameViewModelState(
    val isGameInitialized: Boolean = false,
    val isLoading: Boolean = false,
    val isTypewriterRunning: Boolean = false,
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
    val currentTime: String = "",
    val playerName: String = "",
    val location: String = "",
    val error: String? = null,
    val currentModel: CustomModelConfig? = null,
    val availableModels: List<CustomModelConfig> = emptyList(),
    val currentScenario: OpeningScenario? = null,
    val sequenceName: String = "占卜家",
    val sequenceNumber: Int = 9,
    val digestionProgress: Int = 0
)

class GameViewModel : ViewModel() {

    private val aiApiService = AiApiService()
    private val gameEngine = GameEngine(aiApiService)
    private val saveRepository = SaveRepository()
    private val settingsRepository = SettingsRepository()

    private val _uiState = MutableStateFlow(GameViewModelState())
    val uiState: StateFlow<GameViewModelState> = _uiState.asStateFlow()

    // 不再在 init 中自动初始化，改为由场景选择触发
    init {
        // 监听 AI 模型配置变化，设置页修改后自动生效
        viewModelScope.launch {
            settingsRepository.getAiModelsConfigFlow().collect { config ->
                val active = aiApiService.resolveActiveModel(config)
                aiApiService.setModelConfig(active ?: CustomModelConfig())
                _uiState.update {
                    it.copy(
                        currentModel = active,
                        availableModels = config.customModels.filter { m -> m.isValid() }
                    )
                }
            }
        }
    }

    /**
     * 切换到指定的自定义模型并持久化选择
     */
    fun switchToCustomModel(model: CustomModelConfig) {
        aiApiService.setModelConfig(model)
        _uiState.update { it.copy(currentModel = model) }
        viewModelScope.launch {
            settingsRepository.setActiveModel("custom_${model.id}")
        }
    }

    fun getPlayerState(): com.mysteriousjourney.domain.model.PlayerState {
        return gameEngine.getGameState().player
    }

    fun getGameState(): GameState {
        return gameEngine.getGameState()
    }

    fun getAvailableScenarios(): List<OpeningScenario> {
        return OpeningScenarios.ALL_SCENARIOS
    }

    fun getScenariosByDifficulty(difficulty: OpeningScenario.Difficulty): List<OpeningScenario> {
        return OpeningScenarios.getScenariosByDifficulty(difficulty)
    }

    fun getEasterEggScenarios(): List<OpeningScenario> {
        return OpeningScenarios.getEasterEggScenarios()
    }

    fun isGameInitialized(): Boolean {
        return _uiState.value.isGameInitialized
    }

    /**
     * 随机初始化游戏（随机场景）
     */
    fun initializeGame(playerName: String = "旅行者") {
        val scenario = OpeningScenarios.getRandomScenario()
        initializeGameWithScenario(scenario)
    }

    /**
     * 使用指定场景初始化游戏
     */
    fun initializeGameWithScenario(scenario: OpeningScenario) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, showChoices = false, isGameInitialized = false) }

            val openingNarrative = gameEngine.initializeGameWithScenario(scenario)
            val parseResult = ChoiceParser.parseResponse(openingNarrative)

            val welcomeMessage = NarrativeMessage(
                id = System.currentTimeMillis(),
                content = parseResult.narrative,
                isSystem = true
            )

            val currentState = gameEngine.getGameState()
            _uiState.update {
                it.copy(
                    isGameInitialized = true,
                    isLoading = false,
                    messages = listOf(welcomeMessage),
                    choices = ensureChoices(parseResult.choices),
                    pendingChoices = ensureChoices(parseResult.choices),
                    showChoices = true,
                    spirit = currentState.player.spirituality.current,
                    maxSpirit = currentState.player.spirituality.max,
                    madness = currentState.player.sanity.madnessValue,
                    maxMadness = 100,
                    goldPounds = currentState.player.money.goldPounds,
                    soles = currentState.player.money.soles,
                    pence = currentState.player.money.pence,
                    currentTime = currentState.world.currentTime,
                    playerName = currentState.player.name,
                    location = currentState.world.currentLocation,
                    currentScenario = scenario,
                    sequenceName = currentState.player.currentSequence.name,
                    sequenceNumber = currentState.player.currentSequence.number,
                    digestionProgress = currentState.player.currentSequence.digestionProgress
                )
            }
        }
    }

    /**
     * 解析结果兜底：AI 未输出任何【选择】时注入默认选项，
     * 保证选择按钮始终可用，不会让界面卡在没有可操作入口的状态。
     */
    private fun ensureChoices(parsed: List<ChoiceParser.Choice>): List<ChoiceParser.Choice> =
        if (parsed.isNotEmpty()) parsed
        else listOf(
            ChoiceParser.Choice(1, "继续推进剧情"),
            ChoiceParser.Choice(2, "仔细观察四周"),
            ChoiceParser.Choice(3, "检查随身物品")
        )

    fun onTypewriterComplete() {
        _uiState.update {
            it.copy(
                isTypewriterRunning = false,
                choices = it.pendingChoices,
                showChoices = it.pendingChoices.isNotEmpty()
            )
        }
    }

    fun processPlayerInput(input: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, choices = emptyList(), pendingChoices = emptyList(), showChoices = false) }
            println("开始处理玩家输入: $input")

            val playerMessage = NarrativeMessage(
                id = System.currentTimeMillis(),
                content = input,
                isPlayer = true
            )

            val tempAiMessage = NarrativeMessage(
                id = System.currentTimeMillis() + 1,
                content = "",
                isSystem = false,
                isStreaming = true
            )

            _uiState.update { it.copy(messages = it.messages + playerMessage + tempAiMessage) }

            try {
                var accumulatedContent = ""
                val streamDelayMs = 50L

                val result = aiApiService.sendGameMessageStream(
                    systemPrompt = gameEngine.buildSystemPrompt(),
                    chatHistory = gameEngine.buildChatHistory(),
                    userInput = input
                ) { chunk ->
                    if (chunk.isNotEmpty()) {
                        viewModelScope.launch {
                            delay(streamDelayMs)
                            accumulatedContent += chunk
                            val parseResult = ChoiceParser.parseResponse(accumulatedContent)

                            _uiState.update { currentState ->
                                val updatedMessages = currentState.messages.toMutableList()
                                if (updatedMessages.isNotEmpty()) {
                                    val lastIndex = updatedMessages.lastIndex
                                    updatedMessages[lastIndex] = updatedMessages[lastIndex].copy(
                                        content = parseResult.narrative,
                                        isStreaming = true,
                                        isTypewriterComplete = false
                                    )
                                }
                                currentState.copy(
                                    messages = updatedMessages,
                                    pendingChoices = parseResult.choices,
                                    choices = emptyList(),
                                    showChoices = false,
                                    isTypewriterRunning = true,
                                    isLoading = false
                                )
                            }
                        }
                    }
                }

                if (result.isSuccess) {
                    val fullContent = result.getOrNull() ?: ""
                    println("AI处理结果 - 成功, 内容长度: ${fullContent.length}")

                    val parseResult = ChoiceParser.parseResponse(fullContent)
                    val stateParseResult = StateParser.parseResponse(fullContent)

                    if (stateParseResult.stateUpdate != null) {
                        gameEngine.applyStateUpdate(stateParseResult.stateUpdate)
                    }

                    gameEngine.addToChatHistory(input, fullContent)

                    val formattedNarrative = TextFormatter.formatText(parseResult.narrative)

                    val finalAiMessage = NarrativeMessage(
                        id = System.currentTimeMillis() + 1,
                        content = formattedNarrative,
                        isSystem = false,
                        isStreaming = false
                    )

                    val currentState = gameEngine.getGameState()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            messages = it.messages.dropLast(1) + finalAiMessage,
                            choices = emptyList(),
                            pendingChoices = ensureChoices(parseResult.choices),
                            showChoices = false,
                            isTypewriterRunning = true,
                            spirit = currentState.player.spirituality.current,
                            maxSpirit = currentState.player.spirituality.max,
                            madness = currentState.player.sanity.madnessValue,
                            goldPounds = currentState.player.money.goldPounds,
                            soles = currentState.player.money.soles,
                            pence = currentState.player.money.pence,
                            currentTime = currentState.world.currentTime,
                            location = currentState.world.currentLocation
                        )
                    }
                    println("成功解析出${parseResult.choices.size}个选择选项")
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            messages = it.messages.dropLast(1),
                            error = result.exceptionOrNull()?.message ?: "处理输入时发生错误"
                        )
                    }
                    println("处理失败，错误信息: ${result.exceptionOrNull()?.message}")
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        messages = it.messages.dropLast(1),
                        error = e.message ?: "处理输入时发生错误"
                    )
                }
                println("处理异常: ${e.message}")
            }
        }
    }

    fun advancePlot() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, choices = emptyList(), pendingChoices = emptyList(), showChoices = false) }
            println("开始推进剧情")

            val tempAiMessage = NarrativeMessage(
                id = System.currentTimeMillis(),
                content = "",
                isSystem = false,
                isStreaming = true
            )

            _uiState.update { it.copy(messages = it.messages + tempAiMessage) }

            try {
                var accumulatedContent = ""
                val streamDelayMs = 50L

                val result = aiApiService.sendGameMessageStream(
                    systemPrompt = gameEngine.buildSystemPrompt(),
                    chatHistory = gameEngine.buildChatHistory(),
                    userInput = "请继续推进剧情，描述当前环境并给出下一步选择"
                ) { chunk ->
                    if (chunk.isNotEmpty()) {
                        viewModelScope.launch {
                            delay(streamDelayMs)
                            accumulatedContent += chunk
                            val parseResult = ChoiceParser.parseResponse(accumulatedContent)

                            _uiState.update { currentState ->
                                val updatedMessages = currentState.messages.toMutableList()
                                if (updatedMessages.isNotEmpty()) {
                                    val lastIndex = updatedMessages.lastIndex
                                    updatedMessages[lastIndex] = updatedMessages[lastIndex].copy(
                                        content = parseResult.narrative,
                                        isStreaming = true,
                                        isTypewriterComplete = false
                                    )
                                }
                                currentState.copy(
                                    messages = updatedMessages,
                                    pendingChoices = parseResult.choices,
                                    choices = emptyList(),
                                    showChoices = false,
                                    isTypewriterRunning = true,
                                    isLoading = false
                                )
                            }
                        }
                    }
                }

                if (result.isSuccess) {
                    val fullContent = result.getOrNull() ?: ""
                    println("推进剧情成功 - 内容长度: ${fullContent.length}")

                    val parseResult = ChoiceParser.parseResponse(fullContent)
                    val stateParseResult = StateParser.parseResponse(fullContent)

                    if (stateParseResult.stateUpdate != null) {
                        gameEngine.applyStateUpdate(stateParseResult.stateUpdate)
                    }

                    gameEngine.addToChatHistory("继续剧情", fullContent)

                    val formattedNarrative = TextFormatter.formatText(parseResult.narrative)
                    val currentState = gameEngine.getGameState()

                    _uiState.update {
                        val updatedMessages = it.messages.toMutableList()
                        if (updatedMessages.isNotEmpty()) {
                            val lastIndex = updatedMessages.lastIndex
                            updatedMessages[lastIndex] = updatedMessages[lastIndex].copy(
                                content = formattedNarrative,
                                isStreaming = false
                            )
                        }
                        it.copy(
                            isLoading = false,
                            messages = updatedMessages,
                            choices = emptyList(),
                            pendingChoices = ensureChoices(parseResult.choices),
                            showChoices = false,
                            isTypewriterRunning = true,
                            spirit = currentState.player.spirituality.current,
                            maxSpirit = currentState.player.spirituality.max,
                            madness = currentState.player.sanity.madnessValue,
                            goldPounds = currentState.player.money.goldPounds,
                            soles = currentState.player.money.soles,
                            pence = currentState.player.money.pence,
                            currentTime = currentState.world.currentTime,
                            location = currentState.world.currentLocation
                        )
                    }
                    println("推进剧情成功，解析出${parseResult.choices.size}个选择选项")
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            messages = it.messages.dropLast(1),
                            error = result.exceptionOrNull()?.message ?: "推进剧情时发生错误"
                        )
                    }
                    println("推进剧情失败: ${result.exceptionOrNull()?.message}")
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        messages = it.messages.dropLast(1),
                        error = e.message ?: "推进剧情时发生错误"
                    )
                }
                println("推进剧情异常: ${e.message}")
            }
        }
    }

    fun selectChoice(choice: ChoiceParser.Choice) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null, choices = emptyList(), pendingChoices = emptyList(), showChoices = false) }
            println("用户选择: ${choice.text}")

            val tempAiMessage = NarrativeMessage(
                id = System.currentTimeMillis(),
                content = "",
                isSystem = false,
                isStreaming = true
            )

            _uiState.update { it.copy(messages = it.messages + tempAiMessage) }

            try {
                var accumulatedContent = ""
                var hasReceivedFirstChunk = false

                val result = aiApiService.sendGameMessageStream(
                    systemPrompt = gameEngine.buildSystemPrompt(),
                    chatHistory = gameEngine.buildChatHistory(),
                    userInput = "用户选择了: ${choice.text}"
                ) { chunk ->
                    if (chunk.isNotEmpty()) {
                        if (!hasReceivedFirstChunk) {
                            hasReceivedFirstChunk = true
                            viewModelScope.launch {
                                _uiState.update { it.copy(isLoading = false) }
                            }
                        }

                        accumulatedContent += chunk
                        viewModelScope.launch {
                            val parseResult = ChoiceParser.parseResponse(accumulatedContent)

                            _uiState.update { currentState ->
                                val updatedMessages = currentState.messages.toMutableList()
                                if (updatedMessages.isNotEmpty()) {
                                    val lastIndex = updatedMessages.lastIndex
                                    updatedMessages[lastIndex] = updatedMessages[lastIndex].copy(
                                        content = parseResult.narrative,
                                        isStreaming = true,
                                        isTypewriterComplete = false
                                    )
                                }
                                currentState.copy(
                                    messages = updatedMessages,
                                    pendingChoices = parseResult.choices,
                                    choices = emptyList(),
                                    showChoices = false,
                                    isTypewriterRunning = true
                                )
                            }
                        }
                    }
                }

                if (result.isSuccess) {
                    val fullContent = result.getOrNull() ?: ""
                    println("选择处理成功 - 内容长度: ${fullContent.length}")

                    val parseResult = ChoiceParser.parseResponse(fullContent)
                    val stateParseResult = StateParser.parseResponse(fullContent)

                    if (stateParseResult.stateUpdate != null) {
                        gameEngine.applyStateUpdate(stateParseResult.stateUpdate)
                    }

                    gameEngine.addToChatHistory("选择: ${choice.text}", fullContent)

                    val formattedNarrative = TextFormatter.formatText(parseResult.narrative)
                    val currentState = gameEngine.getGameState()

                    _uiState.update {
                        val updatedMessages = it.messages.toMutableList()
                        if (updatedMessages.isNotEmpty()) {
                            val lastIndex = updatedMessages.lastIndex
                            updatedMessages[lastIndex] = updatedMessages[lastIndex].copy(
                                content = formattedNarrative,
                                isStreaming = false
                            )
                        }
                        it.copy(
                            isLoading = false,
                            messages = updatedMessages,
                            choices = emptyList(),
                            pendingChoices = ensureChoices(parseResult.choices),
                            showChoices = false,
                            isTypewriterRunning = true,
                            spirit = currentState.player.spirituality.current,
                            maxSpirit = currentState.player.spirituality.max,
                            madness = currentState.player.sanity.madnessValue,
                            goldPounds = currentState.player.money.goldPounds,
                            soles = currentState.player.money.soles,
                            pence = currentState.player.money.pence,
                            currentTime = currentState.world.currentTime,
                            location = currentState.world.currentLocation
                        )
                    }
                    println("选择处理成功，解析出${parseResult.choices.size}个选择选项")
                } else {
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            messages = it.messages.dropLast(1),
                            error = result.exceptionOrNull()?.message ?: "处理选择时发生错误"
                        )
                    }
                    println("选择处理失败: ${result.exceptionOrNull()?.message}")
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        messages = it.messages.dropLast(1),
                        error = e.message ?: "处理选择时发生错误"
                    )
                }
                println("选择处理异常: ${e.message}")
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    // ============ 存档/读档 ============

    /**
     * 保存游戏
     */
    fun saveGame(saveName: String, onComplete: (Long) -> Unit = {}) {
        viewModelScope.launch {
            val state = gameEngine.getGameState()
            val uiS = _uiState.value
            val id = saveRepository.saveGame(
                saveName = saveName,
                gameState = state
            ) {
                SaveRepository.SaveMetadata(
                    playerName = uiS.playerName,
                    location = uiS.location,
                    spirit = uiS.spirit,
                    madness = uiS.madness,
                    playTime = uiS.currentTime
                )
            }
            onComplete(id)
        }
    }

    /**
     * 更新存档
     */
    fun updateSave(saveId: Long) {
        viewModelScope.launch {
            val state = gameEngine.getGameState()
            val uiS = _uiState.value
            saveRepository.updateSave(
                saveId = saveId,
                gameState = state
            ) {
                SaveRepository.SaveMetadata(
                    playerName = uiS.playerName,
                    location = uiS.location,
                    spirit = uiS.spirit,
                    madness = uiS.madness,
                    playTime = uiS.currentTime
                )
            }
        }
    }

    /**
     * 加载存档
     */
    fun loadGame(saveId: Long, onComplete: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            val loadedState = saveRepository.loadGame(saveId, GameState::class.java)
            if (loadedState != null) {
                gameEngine.loadGameState(loadedState)
                // 恢复 UI 状态
                val player = loadedState.player
                val world = loadedState.world
                _uiState.update {
                    it.copy(
                        isGameInitialized = true,
                        spirit = player.spirituality.current,
                        maxSpirit = player.spirituality.max,
                        madness = player.sanity.madnessValue,
                        goldPounds = player.money.goldPounds,
                        soles = player.money.soles,
                        pence = player.money.pence,
                        currentTime = world.currentTime,
                        playerName = player.name,
                        location = world.currentLocation,
                        sequenceName = player.currentSequence.name,
                        sequenceNumber = player.currentSequence.number,
                        digestionProgress = player.currentSequence.digestionProgress,
                        messages = emptyList(),
                        choices = emptyList(),
                        showChoices = false,
                        error = null
                    )
                }
                // 加载后生成一条提示消息
                val loadMessage = NarrativeMessage(
                    id = System.currentTimeMillis(),
                    content = "**存档已加载**\n\n你回到了${world.currentLocation}，时间是${world.currentTime}。\n\n命运之轮继续转动...",
                    isSystem = true
                )
                _uiState.update { it.copy(messages = listOf(loadMessage)) }
                onComplete(true)
            } else {
                _uiState.update { it.copy(error = "加载存档失败：找不到存档数据") }
                onComplete(false)
            }
        }
    }

    /**
     * 获取所有存档（供 UI 调用）
     */
    fun getAllSaves(callback: (List<SaveRepository.SaveSlotEntry>) -> Unit) {
        viewModelScope.launch {
            val saves = saveRepository.getAllSaves()
            callback(saves)
        }
    }

    /**
     * 删除存档
     */
    fun deleteSave(saveId: Long) {
        viewModelScope.launch {
            saveRepository.deleteSave(saveId)
        }
    }
}
