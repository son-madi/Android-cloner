package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VirtualProcess

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VirtualRootScreen(
    processes: List<VirtualProcess>,
    onBackClick: () -> Unit,
    onLaunchGameGuardian: () -> Unit,
    onSelectProcess: (VirtualProcess) -> Unit,
    onRefreshProcesses: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0 = Root Engine, 1 = Process List (/proc), 2 = Virtual Shell
    var terminalInput by remember { mutableStateOf("su -c id") }
    var terminalLogs by remember {
        mutableStateOf(
            listOf(
                "MultiCloner Virtual Root Shell v2.1",
                "[Root Engine] Initialized /system/xbin/su (uid=0, gid=0)",
                "[SELinux] Permissive mode enforced",
                "[Sandbox] Virtual /proc filesystem mounted with rw permissions",
                "[GameGuardian] Process list hooks ready",
                "Ready for commands."
            )
        )
    }

    var rootEngineActive by remember { mutableStateOf(true) }
    var seLinuxPermissive by remember { mutableStateOf(true) }
    var hideRootFromBankApps by remember { mutableStateOf(true) }
    var processSearchQuery by remember { mutableStateOf("") }

    val filteredProcesses = remember(processes, processSearchQuery) {
        if (processSearchQuery.isEmpty()) processes
        else processes.filter {
            it.processName.contains(processSearchQuery, ignoreCase = true) ||
                    it.packageName.contains(processSearchQuery, ignoreCase = true) ||
                    it.pid.toString().contains(processSearchQuery)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Virtual Root & Process Manager",
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Dual Space GameGuardian & Cheat Environment",
                            fontSize = 11.sp,
                            color = Color(0xFF67E8F9)
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier.testTag("root_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onRefreshProcesses) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0F172A)
                )
            )
        },
        containerColor = Color(0xFF0B0F19),
        modifier = modifier.testTag("virtual_root_screen")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Tab Selector
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Color(0xFF1E293B),
                contentColor = Color(0xFF38BDF8)
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Root Engine", fontSize = 12.sp) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Process List (${processes.size})", fontSize = 12.sp) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Root Terminal", fontSize = 12.sp) }
                )
            }

            when (selectedTab) {
                0 -> {
                    // ROOT ENGINE STATUS & TOOLS
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Quick Action: Launch Game Guardian Overlay
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                            border = CardDefaults.outlinedCardBorder(),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(Color(0xFF2563EB)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "GG",
                                                color = Color.White,
                                                fontWeight = FontWeight.Black,
                                                fontSize = 16.sp
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Column {
                                            Text(
                                                text = "GameGuardian Tool",
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp
                                            )
                                            Text(
                                                text = "Memory Search, Freeze & Speedhack",
                                                color = Color(0xFF94A3B8),
                                                fontSize = 11.5.sp
                                            )
                                        }
                                    }

                                    Button(
                                        onClick = onLaunchGameGuardian,
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.testTag("open_gg_button")
                                    ) {
                                        Text("Open GG", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Status indicators
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Virtual Environment Status",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                StatusRow(
                                    label = "Virtual SU Daemon",
                                    value = if (rootEngineActive) "Active (uid=0)" else "Disabled",
                                    isActive = rootEngineActive
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                StatusRow(
                                    label = "SELinux Policy",
                                    value = if (seLinuxPermissive) "Permissive (getenforce=0)" else "Enforcing",
                                    isActive = seLinuxPermissive
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                StatusRow(
                                    label = "/proc Access",
                                    value = "Unrestricted (GameGuardian enabled)",
                                    isActive = true
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                StatusRow(
                                    label = "Root Cloak / MagiskHide",
                                    value = if (hideRootFromBankApps) "Enabled (Auto-cloak non-mod apps)" else "Disabled",
                                    isActive = hideRootFromBankApps
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Engine Controls
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Virtual Root Switches",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column {
                                        Text(
                                            text = "Root Privilege Provider",
                                            color = Color.White,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = "Gives root access to apps inside virtual space",
                                            color = Color(0xFF94A3B8),
                                            fontSize = 11.sp
                                        )
                                    }
                                    Switch(
                                        checked = rootEngineActive,
                                        onCheckedChange = { rootEngineActive = it },
                                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF10B981))
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                HorizontalDivider(color = Color(0xFF334155))
                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column {
                                        Text(
                                            text = "SELinux Permissive Mode",
                                            color = Color.White,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = "Allows memory injection and ptracing",
                                            color = Color(0xFF94A3B8),
                                            fontSize = 11.sp
                                        )
                                    }
                                    Switch(
                                        checked = seLinuxPermissive,
                                        onCheckedChange = { seLinuxPermissive = it }
                                    )
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                HorizontalDivider(color = Color(0xFF334155))
                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column {
                                        Text(
                                            text = "Root Detection Bypass",
                                            color = Color.White,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = "Hides root from Google Play Protect / SafetyNet",
                                            color = Color(0xFF94A3B8),
                                            fontSize = 11.sp
                                        )
                                    }
                                    Switch(
                                        checked = hideRootFromBankApps,
                                        onCheckedChange = { hideRootFromBankApps = it }
                                    )
                                }
                            }
                        }
                    }
                }

                1 -> {
                    // LIVE PROCESS LIST (/proc inspector for GameGuardian)
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp)
                    ) {
                        OutlinedTextField(
                            value = processSearchQuery,
                            onValueChange = { processSearchQuery = it },
                            placeholder = { Text("Filter processes by name or PID...", color = Color(0xFF94A3B8), fontSize = 12.sp) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF94A3B8)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedContainerColor = Color(0xFF1E293B),
                                unfocusedContainerColor = Color(0xFF1E293B)
                            )
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "PID / Process Name",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "Memory / Threads",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            items(filteredProcesses) { proc ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(Color(0xFF1E293B))
                                        .clickable {
                                            onSelectProcess(proc)
                                            onLaunchGameGuardian()
                                        }
                                        .padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = "${proc.pid}",
                                                color = Color(0xFF38BDF8),
                                                fontFamily = FontFamily.Monospace,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = proc.status,
                                                color = if (proc.status == "ROOT_HOOK") Color(0xFFF59E0B) else Color(0xFF10B981),
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Text(
                                            text = proc.processName,
                                            color = Color.White,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                        Text(
                                            text = "Base: ${proc.virtualBaseAddress}",
                                            color = Color(0xFF64748B),
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 10.sp
                                        )
                                    }

                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "${proc.memoryUsageMb} MB",
                                            color = Color(0xFFFBBF24),
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp
                                        )
                                        Text(
                                            text = "${proc.threadsCount} threads",
                                            color = Color(0xFF94A3B8),
                                            fontSize = 10.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                2 -> {
                    // VIRTUAL ROOT SHELL TERMINAL
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(14.dp)
                    ) {
                        // Terminal Display Box
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF030712))
                                .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(10.dp))
                                .padding(12.dp)
                        ) {
                            LazyColumn(modifier = Modifier.fillMaxSize()) {
                                items(terminalLogs) { log ->
                                    Text(
                                        text = log,
                                        color = if (log.startsWith("#") || log.startsWith("$")) Color(0xFF38BDF8)
                                        else if (log.contains("uid=0")) Color(0xFF34D399)
                                        else Color(0xFFCBD5E1),
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.5.sp,
                                        lineHeight = 16.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Terminal Input
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = terminalInput,
                                onValueChange = { terminalInput = it },
                                placeholder = { Text("Enter root shell command (e.g. su -c whoami)") },
                                singleLine = true,
                                modifier = Modifier.weight(1f),
                                colors = TextFieldDefaults.colors(
                                    focusedTextColor = Color(0xFF38BDF8),
                                    unfocusedTextColor = Color(0xFF38BDF8),
                                    focusedContainerColor = Color(0xFF1E293B),
                                    unfocusedContainerColor = Color(0xFF1E293B)
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = {
                                    val cmd = terminalInput.trim()
                                    if (cmd.isNotEmpty()) {
                                        val output = executeFakeShellCommand(cmd)
                                        terminalLogs = terminalLogs + "# $cmd" + output
                                        terminalInput = ""
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Run")
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Quick Command Chips
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            listOf("su -c id", "whoami", "getenforce", "su -c ps", "ls /data/data").forEach { quickCmd ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0xFF1E293B))
                                        .clickable {
                                            terminalInput = quickCmd
                                        }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = quickCmd,
                                        color = Color(0xFF93C5FD),
                                        fontSize = 10.sp,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatusRow(label: String, value: String, isActive: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = Color(0xFFCBD5E1),
            fontSize = 12.5.sp
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(if (isActive) Color(0xFF10B981) else Color(0xFFEF4444), CircleShape)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = value,
                color = if (isActive) Color(0xFF34D399) else Color(0xFFF87171),
                fontSize = 11.5.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

fun executeFakeShellCommand(cmd: String): List<String> {
    return when {
        cmd.contains("id") -> listOf("uid=0(root) gid=0(root) groups=0(root),1004(input),1007(log),1011(adb),1015(sdcard_rw),1028(sdcard_r) context=u:r:su:s0")
        cmd.contains("whoami") -> listOf("root")
        cmd.contains("getenforce") -> listOf("Permissive")
        cmd.contains("ps") -> listOf(
            "USER      PID   PPID  VSIZE  RSS   WCHAN              PC  NAME",
            "root        1      0  14520  2100  0000000000000000 S init",
            "root      620      1 345000 78000  0000000000000000 S zygote64",
            "system    940    620 580000 240000 0000000000000000 S system_server",
            "root    13010    620 210000 64000  0000000000000000 S catch_.me_.if_.you_.can (GG Daemon)"
        )
        cmd.contains("ls /data/data") -> listOf(
            "com.example.multicloner.vxqzt",
            "com.whatsapp.space1",
            "com.whatsapp.space2",
            "catch_.me_.if_.you_.can",
            "com.discord.space1",
            "com.figma.mirror.space1"
        )
        else -> listOf("Command executed with return code 0 (Virtual Root Shell)")
    }
}
