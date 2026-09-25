package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CloneEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CloneDao {
    @Query("SELECT * FROM cloned_apps ORDER BY spaceIndex ASC, cloneNumber ASC, id ASC")
    fun getAllClones(): Flow<List<CloneEntity>>

    @Query("SELECT * FROM cloned_apps WHERE spaceIndex = :spaceIndex ORDER BY cloneNumber ASC, id ASC")
    fun getClonesBySpace(spaceIndex: Int): Flow<List<CloneEntity>>

    @Query("SELECT * FROM cloned_apps WHERE id = :id LIMIT 1")
    suspend fun getCloneById(id: Long): CloneEntity?

    @Query("SELECT COUNT(*) FROM cloned_apps WHERE packageName = :packageName")
    suspend fun getCloneCountForPackage(packageName: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClone(clone: CloneEntity): Long

    @Update
    suspend fun updateClone(clone: CloneEntity)

    @Delete
    suspend fun deleteClone(clone: CloneEntity)

    @Query("DELETE FROM cloned_apps WHERE id = :id")
    suspend fun deleteCloneById(id: Long)

    @Query("DELETE FROM cloned_apps WHERE spaceIndex = :spaceIndex")
    suspend fun deleteSpace(spaceIndex: Int)

    @Query("DELETE FROM cloned_apps")
    suspend fun clearAll()
}
