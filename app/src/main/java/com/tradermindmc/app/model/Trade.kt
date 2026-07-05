package com.tradermindmc.app.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "trades")
data class Trade(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val pair: String,
    val direction: String,
    val lotSize: Double,
    val entryPrice: Double,
    val stopLoss: Double,
    val takeProfit: Double,
    val result: Double,
    val pips: Double,
    val date: Long = System.currentTimeMillis(),
    val notes: String = "",
    val status: String = "CLOSED",
    val firestoreId: String = ""
)
