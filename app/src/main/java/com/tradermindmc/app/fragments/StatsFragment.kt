package com.tradermindmc.app.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.tradermindmc.app.R
import com.tradermindmc.app.database.FirestoreRepository
import kotlinx.coroutines.launch

class StatsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflater.inflate(R.layout.fragment_stats, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadStats(view)
    }

    override fun onResume() {
        super.onResume()
        view?.let { loadStats(it) }
    }

    private fun loadStats(view: View) {
        val tvWinrate = view.findViewById<TextView>(R.id.tv_winrate)
        val tvTotalTrades = view.findViewById<TextView>(R.id.tv_total_trades)
        val tvWins = view.findViewById<TextView>(R.id.tv_wins)
        val tvLosses = view.findViewById<TextView>(R.id.tv_losses)
        val tvTotalProfit = view.findViewById<TextView>(R.id.tv_total_profit)
        val tvBestTrade = view.findViewById<TextView>(R.id.tv_best_trade)
        val tvWorstTrade = view.findViewById<TextView>(R.id.tv_worst_trade)
        val tvStreak = view.findViewById<TextView>(R.id.tv_streak)

        lifecycleScope.launch {
            try {
                val trades = FirestoreRepository().getAllTrades()
                val total = trades.size
                val wins = trades.count { it.result > 0 }
                val losses = total - wins
                val totalProfit = trades.sumOf { it.result }
                val bestTrade = trades.maxOfOrNull { it.result } ?: 0.0
                val worstTrade = trades.minOfOrNull { it.result } ?: 0.0
                val winrate = if (total > 0) (wins.toDouble() / total * 100) else 0.0

                var streak = 0
                var streakType = ""
                if (trades.isNotEmpty()) {
                    streakType = if (trades[0].result >= 0) "W" else "L"
                    for (t in trades) {
                        if ((t.result >= 0 && streakType == "W") || (t.result < 0 && streakType == "L")) streak++
                        else break
                    }
                }

                activity?.runOnUiThread {
                    tvWinrate.text = String.format("%.1f%%", winrate)
                    tvWinrate.setTextColor(Color.parseColor(if (winrate >= 50) "#059669" else "#DC2626"))
                    tvTotalTrades.text = total.toString()
                    tvWins.text = wins.toString()
                    tvLosses.text = losses.toString()
                    tvTotalProfit.text = String.format("%s$%.2f", if (totalProfit >= 0) "+" else "", totalProfit)
                    tvTotalProfit.setTextColor(Color.parseColor(if (totalProfit >= 0) "#059669" else "#DC2626"))
                    tvBestTrade.text = String.format("+$%.2f", bestTrade)
                    tvWorstTrade.text = String.format("-$%.2f", Math.abs(worstTrade))
                    tvStreak.text = if (streak > 0) "$streak $streakType" else "-"
                    tvStreak.setTextColor(Color.parseColor(if (streakType == "W") "#059669" else "#DC2626"))
                }
            } catch (e: Exception) {
                // silently fail
            }
        }
    }
}
