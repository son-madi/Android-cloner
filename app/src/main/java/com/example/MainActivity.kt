package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.CloneEntity
import com.example.ui.components.GameGuardianOverlay
import com.example.ui.components.SpeedBoostDialog
import com.example.ui.screens.AppDetailDialog
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.InstallAppScreen
import com.example.ui.screens.VirtualAppRunnerScreen
import com.example.ui.screens.VirtualRootScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MultiClonerViewModel
import com.example.ui.viewmodel.ScreenDestination

class MainActivity : ComponentActivity() {

    private val viewModel: MultiClonerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MultiClonerApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun MultiClonerApp(viewModel: MultiClonerViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val clones by viewModel.allClones.collectAsStateWithLifecycle()
    val maxSpaces by viewModel.maxSpaceCount.collectAsStateWithLifecycle()
    val installedApps by viewModel.installedApps.collectAsStateWithLifecycle()
    val isLoadingApps by viewModel.isLoadingApps.collectAsStateWithLifecycle()

    val processes by viewModel.processes.collectAsStateWithLifecycle()
    val selectedProcess by viewModel.selectedProcess.collectAsStateWithLifecycle()
    val memoryEntries by viewModel.memoryEntries.collectAsStateWithLifecycle()

    val isSpeedBoostOpen by viewModel.isSpeedBoostOpen.collectAsStateWithLifecycle()
    val isGameGuardianOpen by viewModel.isGameGuardianOpen.collectAsStateWithLifecycle()
    val activeDetailClone by viewModel.activeDetailClone.collectAsStateWithLifecycle()

    // BackHandler for sub-screens
    BackHandler(enabled = currentScreen !is ScreenDestination.Home) {
        viewModel.navigateBack()
    }

    AnimatedContent(
        targetState = currentScreen,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "screen_transition"
    ) { screen ->
        when (screen) {
            is ScreenDestination.Home -> {
                HomeScreen(
                    clones = clones,
                    maxSpaces = maxSpaces,
                    onAppClick = { clone ->
                        viewModel.navigateTo(ScreenDestination.VirtualRunner(clone))
                    },
                    onAppLongClick = { clone ->
                        viewModel.activeDetailClone.value = clone
                    },
                    onAddAppClick = { targetSpace ->
                        viewModel.navigateTo(ScreenDestination.InstallApp(targetSpace))
                    },
                    onDeleteSpace = { spaceIndex ->
                        viewModel.deleteSpace(spaceIndex)
                    },
                    onAddSpaceClick = {
                        viewModel.addEmptySpace()
                    },
                    onOpenSpeedBoost = {
                        viewModel.isSpeedBoostOpen.value = true
                    },
                    onOpenVirtualRoot = {
                        viewModel.navigateTo(ScreenDestination.VirtualRoot)
                    },
                    onOpenGameGuardian = {
                        viewModel.isGameGuardianOpen.value = true
                    }
                )
            }

            is ScreenDestination.InstallApp -> {
                InstallAppScreen(
                    targetSpace = screen.targetSpace,
                    installedApps = installedApps,
                    isLoading = isLoadingApps,
                    onBackClick = { viewModel.navigateBack() },
                    onCloneApp = { app, targetSpace ->
                        viewModel.cloneApp(app, targetSpace)
                    }
                )
            }

            is ScreenDestination.VirtualRoot -> {
                VirtualRootScreen(
                    processes = processes,
                    onBackClick = { viewModel.navigateBack() },
                    onLaunchGameGuardian = {
                        viewModel.isGameGuardianOpen.value = true
                    },
                    onSelectProcess = { proc ->
                        viewModel.selectTargetProcess(proc)
                    },
                    onRefreshProcesses = {
                        viewModel.refreshProcesses()
                    }
                )
            }

            is ScreenDestination.VirtualRunner -> {
                VirtualAppRunnerScreen(
                    clone = screen.clone,
                    onBackClick = { viewModel.navigateBack() },
                    onOpenGameGuardian = {
                        viewModel.isGameGuardianOpen.value = true
                    }
                )
            }
        }
    }

    // Speed Boost Dialog
    if (isSpeedBoostOpen) {
        SpeedBoostDialog(
            onDismiss = { viewModel.isSpeedBoostOpen.value = false },
            onPerformBoost = { viewModel.performSpeedBoost() }
        )
    }

    // Game Guardian Memory & Process Tool Overlay
    if (isGameGuardianOpen) {
        GameGuardianOverlay(
            processes = processes,
            selectedProcess = selectedProcess,
            memoryEntries = memoryEntries,
            onSelectProcess = { proc -> viewModel.selectTargetProcess(proc) },
            onSearchMemory = { valStr -> viewModel.searchMemory(valStr) },
            onEditMemory = { addr, valStr, freeze -> viewModel.editMemory(addr, valStr, freeze) },
            onRefreshProcesses = { viewModel.refreshProcesses() },
            onDismiss = { viewModel.isGameGuardianOpen.value = false }
        )
    }

    // App Detail / Customizer Dialog
    activeDetailClone?.let { targetClone ->
        AppDetailDialog(
            clone = targetClone,
            onDismiss = { viewModel.activeDetailClone.value = null },
            onLaunchApp = { clone ->
                viewModel.activeDetailClone.value = null
                viewModel.navigateTo(ScreenDestination.VirtualRunner(clone))
            },
            onUpdateClone = { updated ->
                viewModel.updateClone(updated)
            },
            onDeleteClone = { toDelete ->
                viewModel.deleteClone(toDelete)
            }
        )
    }
}
