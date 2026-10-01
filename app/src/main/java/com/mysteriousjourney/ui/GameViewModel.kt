package com.mysteriousjourney.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mysteriousjourney.data.api.AiApiService
import com.mysteriousjourney.data.model.Message
import com.mysteriousjourney.data.repository.SaveRepository
import com.mysteriousjourney.data.settings.CustomModelConfig
import com.mysteriousjourney.data.settings.SettingsRepository
import com.mysteriousjourney.domain.GameEngine
import com.mysteriousjourney.domain.model.GameState
import com.mysteriousjourney.domain.model.OpeningScenario
import com.mysteriousjourney.domain.model.OpeningScenarios
import com.mysteriousjourney.ui.component.NarrativeMessage
import com.mysteriousjourney.util.ChoiceParser
import com.mysteriousjourney.util.TextFormatter
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

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

    private companion object {
        /** 主动推进剧情时发给 AI 的指令 */
        const val ADVANCE_PROMPT = "请继续推进剧情，描述当前环境并给出下一步选择"

        /** 选项点击发给 AI 的指令前缀，气泡展示时剥掉 */
        const val CHOICE_PREFIX = "用户选择了: "

        /** 流式中间帧的最小渲染间隔（毫秒），限制主线程全量重解析的频率 */
        const val RENDER_INTERVAL_MS = 50L
    }

    private val aiApiService = AiApiService()
    private val gameEngine = GameEngine(aiApiService)
    private val saveRepository = SaveRepository()
    private val settingsRepository = SettingsRepository()

    /**
     * 消息 id 自增计数器。
     *
     * 读档恢复的历史消息用负数 id，与这里生成的正数 id 天然不重叠，
     * 保证 LazyColumn 的 key 全局唯一。
     */
    private var messageSeq = 0L

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
    fun initializeGame() {
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
                id = ++messageSeq,
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

    /**
     * 处理玩家自由输入
     */
    fun processPlayerInput(input: String) {
        sendMessage(userInput = input, echoPlayerMessage = true)
    }

    /**
     * 推进剧情（AI 主动续写）
     */
    fun advancePlot() {
        sendMessage(userInput = ADVANCE_PROMPT, echoPlayerMessage = false)
    }

    /**
     * 玩家点击了某个分支选项
     */
    fun selectChoice(choice: ChoiceParser.Choice) {
        sendMessage(
            userInput = "$CHOICE_PREFIX${choice.text}",
            echoPlayerMessage = true,
            playerEcho = choice.text
        )
    }

    /**
     * 发送一轮对话的单一入口。
     *
     * 三个对外方法（自由输入 / 推进剧情 / 选择分支）只差一个入参和是否回显玩家气泡，
     * 共用这条路径。流式内容按固定节奏刷新，collect 正常结束后才提交最终 UI 状态——
     * 中间帧不可能再覆盖结果，因为根本不存在"迟到的帧"：所有 UI 写入都发生在这一个协程里。
     */
    private fun sendMessage(
        userInput: String,
        echoPlayerMessage: Boolean,
        playerEcho: String = userInput
    ) {
        viewModelScope.launch {
            // 上一轮还在流式或打字机阶段时忽略重复触发
            if (_uiState.value.isLoading || _uiState.value.isTypewriterRunning) return@launch

            _uiState.update {
                it.copy(isLoading = true, error = null, choices = emptyList(), pendingChoices = emptyList(), showChoices = false)
            }

            // 玩家气泡（若需要）与占位气泡成对分配 id
            val playerMessageId = if (echoPlayerMessage) ++messageSeq else 0L
            val messageId = ++messageSeq
            val placeholder = NarrativeMessage(id = messageId, content = "", isStreaming = true)
            val newMessages = buildList {
                if (echoPlayerMessage) {
                    add(NarrativeMessage(id = playerMessageId, content = playerEcho, isPlayer = true))
                }
                add(placeholder)
            }
            _uiState.update { it.copy(messages = it.messages + newMessages) }

            val buffer = StringBuilder()
            var lastRenderAt = 0L

            try {
                gameEngine.streamTurn(userInput).collect { event ->
                    when (event) {
                        is GameEngine.TurnEvent.Chunk -> {
                            buffer.append(event.text)
                            // 节流渲染：上游吐得多快，主线程最多 20 次/秒全量重解析
                            val now = System.nanoTime() / 1_000_000
                            if (now - lastRenderAt >= RENDER_INTERVAL_MS) {
                                lastRenderAt = now
                                // 中间帧也要过 ChoiceParser：原始流末尾带着 {状态标记}
                                // 和【选择】块，直接显示会把协议噪声闪到气泡里
                                renderStreaming(messageId, ChoiceParser.parseResponse(buffer.toString()).narrative)
                            }
                        }
                        // 最终态在 collect 结束后统一提交，这里不做任何写入
                        is GameEngine.TurnEvent.Completed -> Unit
                    }
                }

                finalizeTurn(messageId, buffer.toString())
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        messages = it.messages.filterNot { m -> m.id == messageId },
                        error = e.message ?: "处理消息时发生错误"
                    )
                }
            }
        }
    }

    /** 流式过程中的中间刷新：保持 isStreaming，交由 UI 直接显示，不触发打字机 */
    private fun renderStreaming(messageId: Long, content: String) {
        _uiState.update { state ->
            state.copy(
                messages = state.messages.map { m ->
                    if (m.id == messageId) m.copy(content = content, isStreaming = true) else m
                }
            )
        }
    }

    /**
     * 提交本轮最终状态。
     *
     * 这里把 isStreaming 置为 false，由 NarrativeText 组件自己跑最后一段打字机，
     * 完成后回调 [onTypewriterComplete] 再揭示选项——状态栏的交互锁在这个过程中保持生效。
     */
    private fun finalizeTurn(messageId: Long, fullText: String) {
        val parseResult = ChoiceParser.parseResponse(fullText)
        val currentState = gameEngine.getGameState()

        val finalMessage = NarrativeMessage(
            id = messageId,
            content = TextFormatter.formatText(parseResult.narrative),
            isStreaming = false
        )

        _uiState.update { state ->
            state.copy(
                isLoading = false,
                messages = state.messages.map { m -> if (m.id == messageId) finalMessage else m },
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
            // 流式进行中读档会让 streamTurn 收尾时把旧状态写进新读入的 GameState，
            // 回合的 AI 回复会混进另一个存档的历史里，这里直接拒绝
            if (_uiState.value.isLoading) {
                _uiState.update { it.copy(error = "正在生成回复，请稍后再读档") }
                onComplete(false)
                return@launch
            }

            val loadedState = saveRepository.loadGame(saveId, GameState::class.java)
            if (loadedState == null) {
                _uiState.update { it.copy(error = "加载存档失败：找不到存档数据") }
                onComplete(false)
                return@launch
            }

            gameEngine.loadGameState(loadedState)

            val player = loadedState.player
            val world = loadedState.world
            val restored = restoreMessages(loadedState)

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
                    // 恢复历史叙事，末条提示打字机完成后揭示默认选项
                    messages = restored,
                    choices = emptyList(),
                    pendingChoices = ensureChoices(emptyList()),
                    showChoices = false,
                    isTypewriterRunning = true,
                    isLoading = false,
                    error = null
                )
            }
            onComplete(true)
        }
    }

    /**
     * 从存档的 chatHistory 重建界面上的对话记录。
     *
     * 存档里保存的是 AI 的**原始**响应（含 `{状态标记}` 与 `【选择】` 块），
     * 直接展示会露出协议噪声，所以这里按与实时渲染相同的规则过一遍 ChoiceParser。
     * 消息 id 用负数，与时间戳生成的 id 天然不冲突。
     */
    private fun restoreMessages(state: GameState): List<NarrativeMessage> {
        val history = state.chatHistory.mapIndexedNotNull { index, chatMessage ->
            val isPlayer = chatMessage.role == Message.ROLE_USER
            // 推进剧情的内部指令实时不回显，读档后同样不还原成玩家气泡
            if (isPlayer && chatMessage.content == ADVANCE_PROMPT) return@mapIndexedNotNull null
            NarrativeMessage(
                id = -(index + 1L),
                content = if (isPlayer) {
                    // 选项点击在 chatHistory 里带指令前缀，气泡只显示选项文本
                    chatMessage.content.removePrefix(CHOICE_PREFIX)
                } else {
                    TextFormatter.formatText(ChoiceParser.parseResponse(chatMessage.content).narrative)
                },
                isPlayer = isPlayer,
                isTypewriterComplete = true
            )
        }

        // 末条系统提示负责告知玩家"读档回到了哪里"，放在最后保证一进屏就能看到
        val loadNotice = NarrativeMessage(
            id = 0L,
            content = "**存档已加载**\n\n你回到了${state.world.currentLocation}，时间是${state.world.currentTime}。\n\n命运之轮继续转动...",
            isSystem = true
        )

        return history + loadNotice
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
