package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.model.CloneEntity
import com.example.data.model.InstalledAppInfo
import com.example.data.model.MemoryAddressEntry
import com.example.data.model.VirtualProcess
import com.example.data.repository.CloneRepository
import com.example.service.BoostResult
import com.example.service.VirtualEnvironmentEngine
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class ScreenDestination {
    object Home : ScreenDestination()
    data class InstallApp(val targetSpace: Int) : ScreenDestination()
    object VirtualRoot : ScreenDestination()
    data class VirtualRunner(val clone: CloneEntity) : ScreenDestination()
}

class MultiClonerViewModel(application: Application) : AndroidViewModel(application) {

    val engine = VirtualEnvironmentEngine(application)
    private val database = AppDatabase.getDatabase(application)
    val repository = CloneRepository(database.cloneDao(), engine)

    val allClones: StateFlow<List<CloneEntity>> = repository.allClones
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _installedApps = MutableStateFlow<List<InstalledAppInfo>>(emptyList())
    val installedApps: StateFlow<List<InstalledAppInfo>> = _installedApps.asStateFlow()

    private val _isLoadingApps = MutableStateFlow(false)
    val isLoadingApps: StateFlow<Boolean> = _isLoadingApps.asStateFlow()

    // Navigation Destination
    private val _currentScreen = MutableStateFlow<ScreenDestination>(ScreenDestination.Home)
    val currentScreen: StateFlow<ScreenDestination> = _currentScreen.asStateFlow()

    // Dialog & Overlay controls
    val isSpeedBoostOpen = MutableStateFlow(false)
    val isGameGuardianOpen = MutableStateFlow(false)
    val activeDetailClone = MutableStateFlow<CloneEntity?>(null)

    // Highest space count
    val maxSpaceCount = MutableStateFlow(4)

    val processes: StateFlow<List<VirtualProcess>> = engine.processes
    val selectedProcess: StateFlow<VirtualProcess?> = engine.selectedTargetProcess
    val memoryEntries: StateFlow<List<MemoryAddressEntry>> = engine.memoryEntries

    init {
        viewModelScope.launch {
            // Check if initial seeding needed
            val count = repository.getCloneCountForPackage("com.microsoft.bing")
            if (count == 0) {
                repository.seedInitialClonesIfEmpty()
            }
            loadInstalledApps()
        }
    }

    fun navigateTo(destination: ScreenDestination) {
        _currentScreen.value = destination
    }

    fun navigateBack(): Boolean {
        return if (_currentScreen.value !is ScreenDestination.Home) {
            _currentScreen.value = ScreenDestination.Home
            true
        } else {
            false
        }
    }

    fun loadInstalledApps() {
        viewModelScope.launch {
            _isLoadingApps.value = true
            _installedApps.value = engine.getInstalledDeviceApps()
            _isLoadingApps.value = false
        }
    }

    fun cloneApp(app: InstalledAppInfo, targetSpace: Int) {
        viewModelScope.launch {
            repository.cloneApp(
                packageName = app.packageName,
                originalAppName = app.appName,
                targetSpace = targetSpace,
                iconKey = app.iconKey,
                isRootTool = app.isRootTool
            )
            if (targetSpace > maxSpaceCount.value) {
                maxSpaceCount.value = targetSpace
            }
        }
    }

    fun cloneAppAgain(clone: CloneEntity) {
        viewModelScope.launch {
            val nextSpace = clone.spaceIndex
            repository.cloneApp(
                packageName = clone.packageName,
                originalAppName = clone.originalAppName,
                targetSpace = nextSpace,
                iconKey = clone.iconKey,
                isRootTool = clone.packageName.contains("gameguardian") || clone.originalAppName.contains("GameGuardian")
            )
        }
    }

    fun addEmptySpace() {
        maxSpaceCount.value = maxSpaceCount.value + 1
    }

    fun deleteSpace(spaceIndex: Int) {
        viewModelScope.launch {
            repository.deleteSpace(spaceIndex)
        }
    }

    fun updateClone(clone: CloneEntity) {
        viewModelScope.launch {
            repository.updateClone(clone)
        }
    }

    fun deleteClone(clone: CloneEntity) {
        viewModelScope.launch {
            repository.deleteClone(clone)
        }
    }

    suspend fun performSpeedBoost(): BoostResult {
        return engine.performSpeedBoost()
    }

    fun refreshProcesses() {
        engine.refreshProcesses()
    }

    fun selectTargetProcess(proc: VirtualProcess) {
        engine.selectTargetProcess(proc)
    }

    fun searchMemory(value: String) {
        engine.searchMemory(value)
    }

    fun editMemory(address: String, value: String, freeze: Boolean) {
        engine.editMemoryValue(address, value, freeze)
    }
}
