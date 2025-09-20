package dev.thalha.cabslip.data.dao

import androidx.room.*
import dev.thalha.cabslip.data.entity.Receipt
import kotlinx.coroutines.flow.Flow

@Dao
interface ReceiptDao {
    @Query("SELECT * FROM receipts ORDER BY tripStartDate DESC")
    fun getAllReceipts(): Flow<List<Receipt>>

    @Query("SELECT * FROM receipts ORDER BY tripStartDate DESC LIMIT :limit")
    fun getRecentReceipts(limit: Int): Flow<List<Receipt>>

    @Query("SELECT * FROM receipts WHERE receiptId = :id")
    suspend fun getReceiptById(id: String): Receipt?

    @Query("""
        SELECT * FROM receipts 
        WHERE driverName LIKE '%' || :query || '%' 
        OR receiptId LIKE '%' || :query || '%' 
        OR destination LIKE '%' || :query || '%'
        ORDER BY tripStartDate DESC
    """)
    fun searchReceipts(query: String): Flow<List<Receipt>>

    @Query("""
        SELECT * FROM receipts 
        WHERE tripStartDate >= :fromDate AND tripStartDate <= :toDate
        ORDER BY tripStartDate DESC
    """)
    fun filterByDateRange(fromDate: Long, toDate: Long): Flow<List<Receipt>>

    @Insert
    suspend fun insertReceipt(receipt: Receipt)

    @Update
    suspend fun updateReceipt(receipt: Receipt)

    @Delete
    suspend fun deleteReceipt(receipt: Receipt)

    @Query("SELECT COUNT(*) FROM receipts WHERE receiptId = :receiptId")
    suspend fun checkReceiptIdExists(receiptId: String): Int
}
