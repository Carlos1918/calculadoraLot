package com.tradermindmc.app.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.tradermindmc.app.model.Trade

@Dao
interface TradeDao {
    @Insert
    suspend fun insert(trade: Trade)

    @Update
    suspend fun update(trade: Trade)

    @Delete
    suspend fun delete(trade: Trade)

    @Query("SELECT * FROM trades ORDER BY date DESC")
    fun getAllTrades(): LiveData<List<Trade>>

    @Query("SELECT * FROM trades ORDER BY date DESC")
    suspend fun getAllTradesList(): List<Trade>

    @Query("SELECT COUNT(*) FROM trades WHERE status = 'CLOSED'")
    suspend fun getTotalTrades(): Int

    @Query("SELECT COUNT(*) FROM trades WHERE result > 0 AND status = 'CLOSED'")
    suspend fun getWinningTrades(): Int

    @Query("SELECT SUM(result) FROM trades WHERE status = 'CLOSED'")
    suspend fun getTotalProfit(): Double?

    @Query("SELECT MAX(result) FROM trades WHERE status = 'CLOSED'")
    suspend fun getBestTrade(): Double?

    @Query("SELECT MIN(result) FROM trades WHERE status = 'CLOSED'")
    suspend fun getWorstTrade(): Double?

    @Query("SELECT * FROM trades WHERE date >= :startDate ORDER BY date DESC")
    suspend fun getTradesSince(startDate: Long): List<Trade>
}
