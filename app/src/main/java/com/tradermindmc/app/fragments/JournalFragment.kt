package com.tradermindmc.app.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.tradermindmc.app.AddTradeActivity
import com.tradermindmc.app.R
import com.tradermindmc.app.adapter.TradeAdapter
import com.tradermindmc.app.database.FirestoreRepository
import com.tradermindmc.app.model.Trade
import kotlinx.coroutines.launch

class JournalFragment : Fragment() {

    private val firestoreRepo = FirestoreRepository()
    private lateinit var tradeAdapter: TradeAdapter
    private var trades = mutableListOf<Trade>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflater.inflate(R.layout.fragment_journal, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_trades)
        val emptyText = view.findViewById<TextView>(R.id.tv_empty)
        val fab = view.findViewById<FloatingActionButton>(R.id.fab_add_trade)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        tradeAdapter = TradeAdapter(trades) { trade ->
            lifecycleScope.launch {
                if (trade.firestoreId.isNotEmpty()) {
                    firestoreRepo.deleteTrade(trade.firestoreId)
                }
                loadTrades(recyclerView, emptyText)
            }
        }
        recyclerView.adapter = tradeAdapter

        loadTrades(recyclerView, emptyText)

        fab.setOnClickListener {
            startActivity(Intent(requireContext(), AddTradeActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        view?.let {
            loadTrades(it.findViewById(R.id.recycler_trades), it.findViewById(R.id.tv_empty))
        }
    }

    private fun loadTrades(recyclerView: RecyclerView, emptyText: TextView) {
        lifecycleScope.launch {
            try {
                val loaded = firestoreRepo.getAllTrades()
                trades.clear()
                trades.addAll(loaded)
                tradeAdapter.updateTrades(trades)
                emptyText.visibility = if (trades.isEmpty()) View.VISIBLE else View.GONE
                recyclerView.visibility = if (trades.isEmpty()) View.GONE else View.VISIBLE
            } catch (e: Exception) {
                emptyText.text = getString(R.string.error_loading)
                emptyText.visibility = View.VISIBLE
            }
        }
    }
}
