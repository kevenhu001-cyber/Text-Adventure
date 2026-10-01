package com.mysteriousjourney.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mysteriousjourney.ui.screen.GameScreen
import com.mysteriousjourney.ui.screen.GameUiState
import com.mysteriousjourney.ui.screen.SaveLoadScreen
import com.mysteriousjourney.ui.screen.SaveLoadMode
import com.mysteriousjourney.ui.screen.SaveLoadUiState
import com.mysteriousjourney.ui.screen.SaveSlot
import com.mysteriousjourney.ui.screen.ScenarioSelectionScreen
import com.mysteriousjourney.ui.screen.SettingsScreen
import com.mysteriousjourney.ui.theme.MysteriousJourneyTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MysteriousJourneyApp()
        }
    }
}

@Composable
fun MysteriousJourneyApp() {
    val navController = rememberNavController()

    MysteriousJourneyTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            AppNavHost(navController = navController)
        }
    }
}

@Composable
fun AppNavHost(navController: NavHostController) {
    // 共享 GameViewModel（作用域提升到 NavHost 级别）
    val gameViewModel: GameViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "worldview"
    ) {
        // 世界观介绍页
        composable("worldview") {
            com.mysteriousjourney.ui.screen.WorldviewIntroductionScreen(
                onComplete = {
                    navController.navigate("scenario_selection") {
                        popUpTo("worldview") { inclusive = true }
                    }
                }
            )
        }

        // 场景选择页
        composable("scenario_selection") {
            val scenarios = gameViewModel.getAvailableScenarios()
            ScenarioSelectionScreen(
                scenarios = scenarios,
                onSelectScenario = { scenario ->
                    gameViewModel.initializeGameWithScenario(scenario)
                    navController.navigate("game") {
                        popUpTo("scenario_selection") { inclusive = true }
                    }
                },
                onRandomScenario = {
                    gameViewModel.initializeGame()
                    navController.navigate("game") {
                        popUpTo("scenario_selection") { inclusive = true }
                    }
                }
            )
        }

        // 游戏主界面
        composable("game") {
            GameScreenRoute(
                gameViewModel = gameViewModel,
                onNavigateToSave = { navController.navigate("save") },
                onNavigateToLoad = { navController.navigate("load") },
                onNavigateToSettings = { navController.navigate("settings") }
            )
        }

        // 存档页
        composable("save") {
            SaveLoadScreenRoute(
                gameViewModel = gameViewModel,
                mode = SaveLoadMode.SAVE,
                onBack = { navController.popBackStack() }
            )
        }

        // 读档页
        composable("load") {
            SaveLoadScreenRoute(
                gameViewModel = gameViewModel,
                mode = SaveLoadMode.LOAD,
                onBack = { navController.popBackStack() }
            )
        }

        // 设置页
        composable("settings") {
            SettingsScreen(
                gameViewModel = gameViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

@Composable
fun GameScreenRoute(
    gameViewModel: GameViewModel,
    onNavigateToSave: () -> Unit,
    onNavigateToLoad: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val uiState by gameViewModel.uiState.collectAsState()
    val playerState = gameViewModel.getPlayerState()
    val gameState = gameViewModel.getGameState()

    val gameUiState = GameUiState(
        isLoading = uiState.isLoading,
        isTypewriterRunning = uiState.isTypewriterRunning,
        messages = uiState.messages,
        choices = uiState.choices,
        pendingChoices = uiState.pendingChoices,
        showChoices = uiState.showChoices,
        spirit = uiState.spirit,
        maxSpirit = uiState.maxSpirit,
        madness = uiState.madness,
        maxMadness = uiState.maxMadness,
        goldPounds = uiState.goldPounds,
        soles = uiState.soles,
        pence = uiState.pence,
        currentTime = uiState.currentTime,
        playerName = uiState.playerName,
        location = uiState.location,
        error = uiState.error,
        currentModel = uiState.currentModel,
        availableModels = uiState.availableModels,
        sequenceName = uiState.sequenceName,
        sequenceNumber = uiState.sequenceNumber,
        digestionProgress = uiState.digestionProgress
    )

    GameScreen(
        uiState = gameUiState,
        playerState = playerState,
        gameState = gameState,
        onSendMessage = { gameViewModel.processPlayerInput(it) },
        onSelectChoice = { choice ->
            gameViewModel.selectChoice(choice)
        },
        onAdvancePlot = {
            gameViewModel.advancePlot()
        },
        onSaveGame = onNavigateToSave,
        onLoadGame = onNavigateToLoad,
        onOpenSettings = onNavigateToSettings,
        onClearError = {
            gameViewModel.clearError()
        },
        onSwitchModel = { model ->
            gameViewModel.switchToCustomModel(model)
        },
        onTypewriterComplete = {
            gameViewModel.onTypewriterComplete()
        }
    )
}

@Composable
fun SaveLoadScreenRoute(
    gameViewModel: GameViewModel,
    mode: SaveLoadMode,
    onBack: () -> Unit
) {
    // 使用状态管理存储读取的存档列表
    var saves by remember { mutableStateOf<List<SaveSlot>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    // 加载存档列表
    androidx.compose.runtime.LaunchedEffect(Unit) {
        gameViewModel.getAllSaves { saveEntries ->
            saves = saveEntries.map { entry ->
                SaveSlot(
                    id = entry.id,
                    name = entry.name,
                    timestamp = entry.timestamp,
                    playerName = entry.playerName,
                    location = entry.location,
                    spirit = entry.spirit,
                    madness = entry.madness,
                    playTime = "已保存"
                )
            }
            isLoading = false
        }
    }

    SaveLoadScreen(
        uiState = SaveLoadUiState(
            saves = saves,
            isLoading = isLoading,
            mode = mode
        ),
        onBack = onBack,
        onNewSave = {
            if (mode == SaveLoadMode.SAVE) {
                gameViewModel.saveGame("存档 ${saves.size + 1}") {
                    onBack()
                }
            }
        },
        onLoadSave = { saveId ->
            if (mode == SaveLoadMode.LOAD) {
                gameViewModel.loadGame(saveId) { success ->
                    if (success) onBack()
                }
            }
        },
        onDeleteSave = { saveId ->
            gameViewModel.deleteSave(saveId)
            // 刷新列表
            gameViewModel.getAllSaves { saveEntries ->
                saves = saveEntries.map { entry ->
                    SaveSlot(
                        id = entry.id,
                        name = entry.name,
                        timestamp = entry.timestamp,
                        playerName = entry.playerName,
                        location = entry.location,
                        spirit = entry.spirit,
                        madness = entry.madness,
                        playTime = "已保存"
                    )
                }
            }
        }
    )
}
