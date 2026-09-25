package com.example.data.model

data class VirtualProcess(
    val pid: Int,
    val packageName: String,
    val processName: String,
    val spaceIndex: Int,
    val memoryUsageMb: Int,
    val cpuUsagePercent: Float,
    val threadsCount: Int,
    val virtualBaseAddress: String = "0x7f" + Integer.toHexString(pid) + "0000",
    val hasRootPrivilege: Boolean = true,
    val isHookedByTool: Boolean = false,
    val status: String = "RUNNING"
)

data class MemoryAddressEntry(
    val address: String,
    val offset: String,
    var value: String,
    val type: String, // "DWORD", "FLOAT", "BYTE", "QWORD"
    val isFrozen: Boolean = false
)
