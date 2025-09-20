package dev.thalha.cabslip.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import dev.thalha.cabslip.data.dao.CabInfoDao
import dev.thalha.cabslip.data.dao.ReceiptDao
import dev.thalha.cabslip.data.entity.CabInfo
import dev.thalha.cabslip.data.entity.Receipt

@Database(
    entities = [CabInfo::class, Receipt::class],
    version = 1,
    exportSchema = false
)
abstract class CabSlipDatabase : RoomDatabase() {
    abstract fun cabInfoDao(): CabInfoDao
    abstract fun receiptDao(): ReceiptDao

    companion object {
        @Volatile
        private var INSTANCE: CabSlipDatabase? = null

        fun getDatabase(context: Context): CabSlipDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CabSlipDatabase::class.java,
                    "cabslip_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
