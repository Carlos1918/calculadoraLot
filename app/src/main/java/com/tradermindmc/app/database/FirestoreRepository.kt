package com.tradermindmc.app.database

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tradermindmc.app.model.Trade
import kotlinx.coroutines.tasks.await

class FirestoreRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private fun getUserId() = auth.currentUser?.uid ?: throw Exception("No user logged in")

    private fun tradesCollection() = db.collection("users")
        .document(getUserId())
        .collection("trades")

    suspend fun saveTrade(trade: Trade): String {
        val data = hashMapOf(
            "pair" to trade.pair,
            "direction" to trade.direction,
            "lotSize" to trade.lotSize,
            "entryPrice" to trade.entryPrice,
            "stopLoss" to trade.stopLoss,
            "takeProfit" to trade.takeProfit,
            "result" to trade.result,
            "pips" to trade.pips,
            "date" to trade.date,
            "notes" to trade.notes,
            "status" to trade.status
        )
        val ref = tradesCollection().add(data).await()
        return ref.id
    }

    suspend fun deleteTrade(firestoreId: String) {
        tradesCollection().document(firestoreId).delete().await()
    }

    suspend fun getAllTrades(): List<Trade> {
        val snapshot = tradesCollection()
            .orderBy("date", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get().await()

        return snapshot.documents.mapIndexed { index, doc ->
            Trade(
                id = index + 1,
                pair = doc.getString("pair") ?: "",
                direction = doc.getString("direction") ?: "",
                lotSize = doc.getDouble("lotSize") ?: 0.0,
                entryPrice = doc.getDouble("entryPrice") ?: 0.0,
                stopLoss = doc.getDouble("stopLoss") ?: 0.0,
                takeProfit = doc.getDouble("takeProfit") ?: 0.0,
                result = doc.getDouble("result") ?: 0.0,
                pips = doc.getDouble("pips") ?: 0.0,
                date = doc.getLong("date") ?: System.currentTimeMillis(),
                notes = doc.getString("notes") ?: "",
                status = doc.getString("status") ?: "CLOSED",
                firestoreId = doc.id
            )
        }
    }

    suspend fun saveAccountSettings(balance: Double, goal: Double, drawdown: Double, monthlyTarget: Double) {
        val data = hashMapOf(
            "balance" to balance,
            "goal" to goal,
            "drawdown" to drawdown,
            "monthlyTarget" to monthlyTarget
        )
        db.collection("users").document(getUserId())
            .collection("settings").document("account")
            .set(data).await()
    }

    suspend fun getAccountSettings(): Map<String, Double> {
        val doc = db.collection("users").document(getUserId())
            .collection("settings").document("account")
            .get().await()

        return mapOf(
            "balance" to (doc.getDouble("balance") ?: 0.0),
            "goal" to (doc.getDouble("goal") ?: 0.0),
            "drawdown" to (doc.getDouble("drawdown") ?: 5.0),
            "monthlyTarget" to (doc.getDouble("monthlyTarget") ?: 0.0)
        )
    }
}
