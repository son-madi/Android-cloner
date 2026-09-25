package com.example.data.repository

import com.example.data.dao.CloneDao
import com.example.data.model.CloneEntity
import com.example.service.VirtualEnvironmentEngine
import kotlinx.coroutines.flow.Flow

class CloneRepository(
    private val cloneDao: CloneDao,
    private val engine: VirtualEnvironmentEngine
) {
    val allClones: Flow<List<CloneEntity>> = cloneDao.getAllClones()

    fun getClonesBySpace(spaceIndex: Int): Flow<List<CloneEntity>> {
        return cloneDao.getClonesBySpace(spaceIndex)
    }

    suspend fun getCloneById(id: Long): CloneEntity? {
        return cloneDao.getCloneById(id)
    }

    suspend fun getCloneCountForPackage(packageName: String): Int {
        return cloneDao.getCloneCountForPackage(packageName)
    }

    suspend fun insertClone(clone: CloneEntity): Long {
        return cloneDao.insertClone(clone)
    }

    suspend fun updateClone(clone: CloneEntity) {
        cloneDao.updateClone(clone)
    }

    suspend fun deleteClone(clone: CloneEntity) {
        cloneDao.deleteClone(clone)
    }

    suspend fun deleteCloneById(id: Long) {
        cloneDao.deleteCloneById(id)
    }

    suspend fun deleteSpace(spaceIndex: Int) {
        cloneDao.deleteSpace(spaceIndex)
    }

    suspend fun cloneApp(
        packageName: String,
        originalAppName: String,
        targetSpace: Int,
        iconKey: String = "",
        isRootTool: Boolean = false
    ): CloneEntity {
        val existingCount = cloneDao.getCloneCountForPackage(packageName)
        val cloneNumber = existingCount + 1

        val badgeText = when (cloneNumber) {
            1 -> ""
            2 -> "2nd"
            3 -> "3rd"
            else -> "unlimited nd"
        }

        val badgeColor = when (cloneNumber) {
            1 -> "#0284C7" // sky blue
            2 -> "#2563EB" // blue
            3 -> "#06B6D4" // cyan
            else -> "#F97316" // orange-red for unlimited
        }

        val cloneSuffix = if (targetSpace > 1) "($targetSpace)" else if (cloneNumber > 1) "($cloneNumber)" else ""
        val customName = if (cloneSuffix.isNotEmpty()) "$originalAppName$cloneSuffix" else originalAppName

        val entity = CloneEntity(
            packageName = packageName,
            originalAppName = originalAppName,
            customCloneName = customName,
            spaceIndex = targetSpace,
            cloneNumber = cloneNumber,
            badgeText = badgeText,
            badgeColorHex = badgeColor,
            isRootEnabled = true, // Root enabled by default for apps in virtual space
            isRootHidden = false,
            virtualAndroidId = engine.generateRandomAndroidId(),
            virtualImei = engine.generateRandomImei(),
            virtualDeviceModel = "Samsung Galaxy S24 Ultra",
            iconKey = iconKey.ifEmpty { packageName.split(".").last() }
        )

        val id = cloneDao.insertClone(entity)
        return entity.copy(id = id)
    }

    // Seed default instances matching screenshot 1 and user's root request
    suspend fun seedInitialClonesIfEmpty() {
        // Space 1: Bing, DeepSeek, Figma, Discord, GameGuardian (Root)
        cloneApp("com.microsoft.bing", "Bing", targetSpace = 1, iconKey = "bing")
        cloneApp("com.deepseek.chat", "DeepSeek", targetSpace = 1, iconKey = "deepseek")
        cloneApp("com.figma.mirror", "Figma", targetSpace = 1, iconKey = "figma")
        cloneApp("com.discord", "Discord", targetSpace = 1, iconKey = "discord")
        cloneApp("catch_.me_.if_.you_.can", "GameGuardian", targetSpace = 1, iconKey = "gameguardian", isRootTool = true)

        // Space 2: Bing(2), DeepSeek(2), Discord(2)
        cloneApp("com.microsoft.bing", "Bing", targetSpace = 2, iconKey = "bing")
        cloneApp("com.deepseek.chat", "DeepSeek", targetSpace = 2, iconKey = "deepseek")
        cloneApp("com.discord", "Discord", targetSpace = 2, iconKey = "discord")

        // Space 3: Bing(3), Figma(3)
        cloneApp("com.microsoft.bing", "Bing", targetSpace = 3, iconKey = "bing")
        cloneApp("com.figma.mirror", "Figma", targetSpace = 3, iconKey = "figma")

        // Space 4: Bing(4)
        cloneApp("com.microsoft.bing", "Bing", targetSpace = 4, iconKey = "bing")
    }
}
