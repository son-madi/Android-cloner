package com.example.service

import android.app.ActivityManager
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.example.data.model.CloneEntity
import com.example.data.model.InstalledAppInfo
import com.example.data.model.MemoryAddressEntry
import com.example.data.model.VirtualProcess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.util.UUID
import kotlin.random.Random

class VirtualEnvironmentEngine(private val context: Context) {

    // Virtual Root & Sandbox status
    val isVirtualRootActive = MutableStateFlow(true)
    val seLinuxPermissive = MutableStateFlow(true)
    val isGameGuardianOverlayActive = MutableStateFlow(false)
    val speedHackMultiplier = MutableStateFlow(1.0f)

    // Current active processes in the Virtual Space
    private val _processes = MutableStateFlow<List<VirtualProcess>>(emptyList())
    val processes: StateFlow<List<VirtualProcess>> = _processes.asStateFlow()

    // Game Guardian Memory Simulation
    private val _memoryEntries = MutableStateFlow<List<MemoryAddressEntry>>(emptyList())
    val memoryEntries: StateFlow<List<MemoryAddressEntry>> = _memoryEntries.asStateFlow()

    private val _selectedTargetProcess = MutableStateFlow<VirtualProcess?>(null)
    val selectedTargetProcess: StateFlow<VirtualProcess?> = _selectedTargetProcess.asStateFlow()

    init {
        refreshProcesses()
    }

    fun refreshProcesses() {
        val list = mutableListOf<VirtualProcess>()

        // System virtual processes
        list.add(
            VirtualProcess(
                pid = 620,
                packageName = "android.system.zygote64",
                processName = "zygote64 (root)",
                spaceIndex = 0,
                memoryUsageMb = 82,
                cpuUsagePercent = 0.8f,
                threadsCount = 8,
                hasRootPrivilege = true,
                status = "SYSTEM"
            )
        )
        list.add(
            VirtualProcess(
                pid = 940,
                packageName = "system_server",
                processName = "system_server",
                spaceIndex = 0,
                memoryUsageMb = 245,
                cpuUsagePercent = 2.1f,
                threadsCount = 94,
                hasRootPrivilege = true,
                status = "SYSTEM"
            )
        )
        list.add(
            VirtualProcess(
                pid = 13010,
                packageName = "catch_.me_.if_.you_.can",
                processName = "GameGuardian (Daemon v101.1)",
                spaceIndex = 1,
                memoryUsageMb = 64,
                cpuUsagePercent = 1.4f,
                threadsCount = 12,
                hasRootPrivilege = true,
                isHookedByTool = true,
                status = "ROOT_HOOK"
            )
        )
        list.add(
            VirtualProcess(
                pid = 11200,
                packageName = "com.whatsapp",
                processName = "WhatsApp:clone_space_1",
                spaceIndex = 1,
                memoryUsageMb = 186,
                cpuUsagePercent = 1.2f,
                threadsCount = 38,
                hasRootPrivilege = true,
                status = "RUNNING"
            )
        )
        list.add(
            VirtualProcess(
                pid = 12450,
                packageName = "com.dts.freefireth",
                processName = "FreeFire.Game:space_1",
                spaceIndex = 1,
                memoryUsageMb = 480,
                cpuUsagePercent = 8.5f,
                threadsCount = 42,
                hasRootPrivilege = true,
                isHookedByTool = true,
                status = "RUNNING"
            )
        )
        list.add(
            VirtualProcess(
                pid = 14210,
                packageName = "com.discord",
                processName = "Discord:clone_space_2",
                spaceIndex = 2,
                memoryUsageMb = 210,
                cpuUsagePercent = 1.5f,
                threadsCount = 26,
                hasRootPrivilege = true,
                status = "RUNNING"
            )
        )

        // Query real running app processes if accessible on device
        try {
            val actManager = context.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
            val runningAppProcesses = actManager?.runningAppProcesses
            if (runningAppProcesses != null) {
                var fakePid = 15000
                for (proc in runningAppProcesses.take(8)) {
                    if (list.none { it.processName == proc.processName }) {
                        list.add(
                            VirtualProcess(
                                pid = proc.pid.takeIf { it > 0 } ?: fakePid++,
                                packageName = proc.pkgList?.firstOrNull() ?: proc.processName,
                                processName = proc.processName,
                                spaceIndex = 1,
                                memoryUsageMb = (45..180).random(),
                                cpuUsagePercent = (Random.nextFloat() * 3.5f),
                                threadsCount = (12..40).random(),
                                hasRootPrivilege = true,
                                status = "RUNNING"
                            )
                        )
                    }
                }
            }
        } catch (_: Exception) {
            // Ignore if restricted on newer Android
        }

        _processes.value = list
        if (_selectedTargetProcess.value == null && list.isNotEmpty()) {
            _selectedTargetProcess.value = list.firstOrNull { it.packageName.contains("freefire") } ?: list.last()
        }
    }

    fun selectTargetProcess(process: VirtualProcess) {
        _selectedTargetProcess.value = process
        searchMemory(defaultTargetValue = "100")
    }

    fun searchMemory(defaultTargetValue: String) {
        val target = _selectedTargetProcess.value ?: return
        val baseHex = target.virtualBaseAddress.removePrefix("0x")
        val entries = mutableListOf<MemoryAddressEntry>()
        val offsets = listOf("104C", "23A8", "4F10", "7B84", "9D20", "B0E4")
        val types = listOf("DWORD", "FLOAT", "DWORD", "BYTE", "DWORD", "QWORD")

        for (i in offsets.indices) {
            val addr = "0x$baseHex${offsets[i]}"
            val value = if (types[i] == "FLOAT") "$defaultTargetValue.00" else defaultTargetValue
            entries.add(
                MemoryAddressEntry(
                    address = addr,
                    offset = "+0x${offsets[i]}",
                    value = value,
                    type = types[i],
                    isFrozen = false
                )
            )
        }
        _memoryEntries.value = entries
    }

    fun editMemoryValue(address: String, newValue: String, freeze: Boolean) {
        val current = _memoryEntries.value.toMutableList()
        val index = current.indexOfFirst { it.address == address }
        if (index != -1) {
            current[index] = current[index].copy(value = newValue, isFrozen = freeze)
            _memoryEntries.value = current
        }
    }

    fun freezeAllValues(freeze: Boolean) {
        val updated = _memoryEntries.value.map { it.copy(isFrozen = freeze) }
        _memoryEntries.value = updated
    }

    fun generateRandomAndroidId(): String {
        return UUID.randomUUID().toString().replace("-", "").take(16).lowercase()
    }

    fun generateRandomImei(): String {
        val prefix = "86" + (100000..999999).random().toString()
        val suffix = (1000000..9999999).random().toString()
        return (prefix + suffix).take(15)
    }

    fun generateRandomMac(): String {
        val chars = "0123456789ABCDEF"
        return (0..5).joinToString(":") {
            "${chars.random()}${chars.random()}"
        }
    }

    // Perform Speed Boost & RAM Cleaner action
    suspend fun performSpeedBoost(): BoostResult = withContext(Dispatchers.Default) {
        kotlinx.coroutines.delay(1200) // Realistic cleanup animation time
        val freedMb = (240..680).random()
        val stoppedServices = (6..18).random()
        // Refresh process metrics with lower RAM
        val updated = _processes.value.map {
            it.copy(
                memoryUsageMb = (it.memoryUsageMb * 0.78f).toInt().coerceAtLeast(24),
                cpuUsagePercent = (it.cpuUsagePercent * 0.5f)
            )
        }
        _processes.value = updated
        BoostResult(freedMb = freedMb, optimizedServices = stoppedServices)
    }

    // Query installed device applications
    suspend fun getInstalledDeviceApps(): List<InstalledAppInfo> = withContext(Dispatchers.IO) {
        val result = mutableListOf<InstalledAppInfo>()
        val pm = context.packageManager

        try {
            val installedPackages = pm.getInstalledPackages(PackageManager.GET_META_DATA)
            for (pkg in installedPackages) {
                val appInfo = pkg.applicationInfo ?: continue
                val isSystem = (appInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0
                val label = try {
                    appInfo.loadLabel(pm).toString()
                } catch (_: Exception) {
                    pkg.packageName
                }
                val iconDrawable = try {
                    appInfo.loadIcon(pm)
                } catch (_: Exception) {
                    null
                }

                // Identify if it is GameGuardian or root modding tool
                val isRootApp = pkg.packageName.contains("gameguardian") ||
                        pkg.packageName.contains("catch_.me") ||
                        pkg.packageName.contains("luckypatcher") ||
                        label.contains("GameGuardian", ignoreCase = true) ||
                        label.contains("Root", ignoreCase = true)

                result.add(
                    InstalledAppInfo(
                        packageName = pkg.packageName,
                        appName = label,
                        versionName = pkg.versionName ?: "1.0",
                        isSystemApp = isSystem,
                        icon = iconDrawable,
                        isRootTool = isRootApp
                    )
                )
            }
        } catch (_: Exception) {
            // fallback
        }

        // Add built-in popular catalog apps so the user always has a rich, realistic list
        // matching the Google Play screenshots (YouTube, Telegram, Instagram, WeChat, Discord, etc.)
        val catalogApps = getCatalogApps()
        for (catalog in catalogApps) {
            if (result.none { it.packageName == catalog.packageName }) {
                result.add(catalog)
            }
        }

        result.sortedBy { it.appName.lowercase() }
    }

    private fun getCatalogApps(): List<InstalledAppInfo> {
        return listOf(
            InstalledAppInfo(
                packageName = "catch_.me_.if_.you_.can",
                appName = "GameGuardian (Root)",
                versionName = "101.1",
                isSystemApp = false,
                iconKey = "gameguardian",
                isRootTool = true
            ),
            InstalledAppInfo(
                packageName = "com.whatsapp",
                appName = "WhatsApp",
                versionName = "2.24.8",
                isSystemApp = false,
                iconKey = "whatsapp"
            ),
            InstalledAppInfo(
                packageName = "com.facebook.katana",
                appName = "Facebook",
                versionName = "451.0",
                isSystemApp = false,
                iconKey = "facebook"
            ),
            InstalledAppInfo(
                packageName = "com.facebook.orca",
                appName = "Messenger",
                versionName = "448.0",
                isSystemApp = false,
                iconKey = "messenger"
            ),
            InstalledAppInfo(
                packageName = "com.instagram.android",
                appName = "Instagram",
                versionName = "320.0",
                isSystemApp = false,
                iconKey = "instagram"
            ),
            InstalledAppInfo(
                packageName = "org.telegram.messenger",
                appName = "Telegram",
                versionName = "10.9.1",
                isSystemApp = false,
                iconKey = "telegram"
            ),
            InstalledAppInfo(
                packageName = "com.google.android.youtube",
                appName = "YouTube",
                versionName = "19.12.3",
                isSystemApp = true,
                iconKey = "youtube"
            ),
            InstalledAppInfo(
                packageName = "com.discord",
                appName = "Discord",
                versionName = "220.1",
                isSystemApp = false,
                iconKey = "discord"
            ),
            InstalledAppInfo(
                packageName = "com.microsoft.bing",
                appName = "Bing",
                versionName = "28.5",
                isSystemApp = false,
                iconKey = "bing"
            ),
            InstalledAppInfo(
                packageName = "com.deepseek.chat",
                appName = "DeepSeek",
                versionName = "1.5.0",
                isSystemApp = false,
                iconKey = "deepseek"
            ),
            InstalledAppInfo(
                packageName = "com.figma.mirror",
                appName = "Figma",
                versionName = "3.2.1",
                isSystemApp = false,
                iconKey = "figma"
            ),
            InstalledAppInfo(
                packageName = "com.tencent.mm",
                appName = "WeChat",
                versionName = "8.0.48",
                isSystemApp = false,
                iconKey = "wechat"
            ),
            InstalledAppInfo(
                packageName = "com.tencent.mobileqq",
                appName = "QQ",
                versionName = "9.0.2",
                isSystemApp = false,
                iconKey = "qq"
            ),
            InstalledAppInfo(
                packageName = "com.nianticlabs.pokemongo",
                appName = "Pokémon GO",
                versionName = "0.305.1",
                isSystemApp = false,
                iconKey = "pokemon"
            ),
            InstalledAppInfo(
                packageName = "com.dts.freefireth",
                appName = "Free Fire MAX",
                versionName = "2.103.1",
                isSystemApp = false,
                iconKey = "freefire"
            ),
            InstalledAppInfo(
                packageName = "com.android.chrome",
                appName = "Chrome",
                versionName = "123.0",
                isSystemApp = true,
                iconKey = "chrome"
            ),
            InstalledAppInfo(
                packageName = "com.google.android.gm",
                appName = "Gmail",
                versionName = "2024.03",
                isSystemApp = true,
                iconKey = "gmail"
            ),
            InstalledAppInfo(
                packageName = "com.android.vending",
                appName = "Google Play Store",
                versionName = "40.3",
                isSystemApp = true,
                iconKey = "playstore"
            ),
            InstalledAppInfo(
                packageName = "ru.chelpus.luckypatcher",
                appName = "Lucky Patcher (Root)",
                versionName = "10.8.2",
                isSystemApp = false,
                iconKey = "luckypatcher",
                isRootTool = true
            )
        )
    }
}

data class BoostResult(
    val freedMb: Int,
    val optimizedServices: Int
)
