package dev.thalha.cabslip.data.dao

import androidx.room.*
import dev.thalha.cabslip.data.entity.CabInfo
import kotlinx.coroutines.flow.Flow

@Dao
interface CabInfoDao {
    @Query("SELECT * FROM cab_info WHERE id = 1")
    fun getCabInfo(): Flow<CabInfo?>

    @Query("SELECT * FROM cab_info WHERE id = 1")
    suspend fun getCabInfoSync(): CabInfo?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateCabInfo(cabInfo: CabInfo)
}
