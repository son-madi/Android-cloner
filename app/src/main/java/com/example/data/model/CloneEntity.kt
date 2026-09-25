package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cloned_apps")
data class CloneEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val packageName: String,
    val originalAppName: String,
    val customCloneName: String,
    val spaceIndex: Int, // 1 = Space 1, 2 = Space 2, etc.
    val cloneNumber: Int, // 1st, 2nd, 3rd, 4th...
    val badgeText: String, // "2nd", "3rd", "unlimited nd", etc.
    val badgeColorHex: String,
    val isRootEnabled: Boolean = true,
    val isRootHidden: Boolean = false,
    val virtualAndroidId: String = "",
    val virtualImei: String = "",
    val virtualDeviceModel: String = "Samsung Galaxy S24 Ultra",
    val isAppLocked: Boolean = false,
    val appLockPin: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val lastLaunchedAt: Long = System.currentTimeMillis(),
    val storageUsedBytes: Long = 1024 * 1024 * 32, // Default 32MB sandbox
    val isSystemApp: Boolean = false,
    val iconKey: String = "" // Identifier for custom/pre-installed icons
)
