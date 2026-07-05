package com.tradermindmc.app.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tradermindmc.app.R
import com.tradermindmc.app.model.Trade
import java.text.SimpleDateFormat
import java.util.*

class TradeAdapter(
    private var trades: List<Trade>,
    private val onDelete: (Trade) -> Unit
) : RecyclerView.Adapter<TradeAdapter.TradeViewHolder>() {

    class TradeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val pair: TextView = view.findViewById(R.id.tv_pair)
        val direction: TextView = view.findViewById(R.id.tv_direction)
        val result: TextView = view.findViewById(R.id.tv_result)
        val date: TextView = view.findViewById(R.id.tv_date)
        val pips: TextView = view.findViewById(R.id.tv_pips)
        val deleteBtn: TextView = view.findViewById(R.id.btn_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TradeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_trade, parent, false)
        return TradeViewHolder(view)
    }

    override fun onBindViewHolder(holder: TradeViewHolder, position: Int) {
        val trade = trades[position]
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

        holder.pair.text = trade.pair
        holder.direction.text = trade.direction
        holder.direction.setTextColor(
            if (trade.direction == "BUY") Color.parseColor("#059669")
            else Color.parseColor("#DC2626")
        )
        holder.pips.text = "${if (trade.pips >= 0) "+" else ""}${trade.pips} pips"
        holder.result.text = "${if (trade.result >= 0) "+" else ""}$${String.format("%.2f", trade.result)}"
        holder.result.setTextColor(
            if (trade.result >= 0) Color.parseColor("#059669")
            else Color.parseColor("#DC2626")
        )
        holder.date.text = dateFormat.format(Date(trade.date))
        holder.deleteBtn.setOnClickListener { onDelete(trade) }
    }

    override fun getItemCount() = trades.size

    fun updateTrades(newTrades: List<Trade>) {
        trades = newTrades
        notifyDataSetChanged()
    }
}
