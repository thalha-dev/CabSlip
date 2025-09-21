package dev.thalha.cabslip.data.repository

import dev.thalha.cabslip.data.dao.CabInfoDao
import dev.thalha.cabslip.data.dao.ReceiptDao
import dev.thalha.cabslip.data.entity.CabInfo
import dev.thalha.cabslip.data.entity.Receipt
import dev.thalha.cabslip.utils.ReceiptIdGenerator
import kotlinx.coroutines.flow.Flow

class CabSlipRepository(
    private val cabInfoDao: CabInfoDao,
    private val receiptDao: ReceiptDao
) {
    // CabInfo operations
    fun getCabInfo(): Flow<CabInfo?> = cabInfoDao.getCabInfo()

    suspend fun getCabInfoSync(): CabInfo? = cabInfoDao.getCabInfoSync()

    suspend fun insertOrUpdateCabInfo(cabInfo: CabInfo) {
        cabInfoDao.insertOrUpdateCabInfo(cabInfo)
    }

    // Receipt operations
    fun getAllReceipts(): Flow<List<Receipt>> = receiptDao.getAllReceipts()

    fun getRecentReceipts(limit: Int = 6): Flow<List<Receipt>> = receiptDao.getRecentReceipts(limit)

    // Pagination methods
    suspend fun getReceiptsPaginated(limit: Int, offset: Int): List<Receipt> =
        receiptDao.getReceiptsPaginated(limit, offset)

    suspend fun searchReceiptsPaginated(query: String, limit: Int, offset: Int): List<Receipt> =
        receiptDao.searchReceiptsPaginated(query, limit, offset)

    suspend fun filterByDateRangePaginated(fromDate: Long, toDate: Long, limit: Int, offset: Int): List<Receipt> =
        receiptDao.filterByDateRangePaginated(fromDate, toDate, limit, offset)

    suspend fun getTotalReceiptsCount(): Int = receiptDao.getTotalReceiptsCount()

    suspend fun getSearchResultsCount(query: String): Int = receiptDao.getSearchResultsCount(query)

    suspend fun getDateRangeResultsCount(fromDate: Long, toDate: Long): Int =
        receiptDao.getDateRangeResultsCount(fromDate, toDate)

    // Stats methods for dashboard
    suspend fun getTotalKilometers(): Double = receiptDao.getTotalKilometers() ?: 0.0

    suspend fun getTotalRevenue(): Double = receiptDao.getTotalRevenue() ?: 0.0

    suspend fun getReceiptById(id: String): Receipt? = receiptDao.getReceiptById(id)

    fun searchReceipts(query: String): Flow<List<Receipt>> = receiptDao.searchReceipts(query)

    fun filterByDateRange(fromDate: Long, toDate: Long): Flow<List<Receipt>> =
        receiptDao.filterByDateRange(fromDate, toDate)

    suspend fun insertReceipt(receipt: Receipt) {
        receiptDao.insertReceipt(receipt)
    }

    suspend fun updateReceipt(receipt: Receipt) {
        receiptDao.updateReceipt(receipt)
    }

    suspend fun deleteReceipt(receipt: Receipt) {
        receiptDao.deleteReceipt(receipt)
    }

    suspend fun generateUniqueReceiptId(): String {
        var receiptId: String
        do {
            receiptId = ReceiptIdGenerator.generateReceiptId()
        } while (receiptDao.checkReceiptIdExists(receiptId) > 0)
        return receiptId
    }

    // Helper function to calculate receipt totals
    fun calculateReceiptTotals(
        pricePerKm: Double,
        totalKm: Double,
        waitingChargePerHr: Double,
        waitingHrs: Double,
        tollParking: Double,
        bata: Double
    ): Triple<Double, Double, Double> { // baseFare, waitingFee, totalFee
        val baseFare = pricePerKm * totalKm
        val waitingFee = waitingChargePerHr * waitingHrs
        val totalFee = baseFare + tollParking + bata + waitingFee
        return Triple(baseFare, waitingFee, totalFee)
    }
}
