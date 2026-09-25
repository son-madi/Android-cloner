package com.example.data.model

import android.graphics.drawable.Drawable

data class InstalledAppInfo(
    val packageName: String,
    val appName: String,
    val versionName: String = "1.0",
    val isSystemApp: Boolean = false,
    val icon: Drawable? = null,
    val iconKey: String = "",
    val cloneCount: Int = 0,
    val isRootTool: Boolean = false
)
