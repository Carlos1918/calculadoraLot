package com.tradermindmc.app.fragments

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.*
import com.tradermindmc.app.R

class PipValueFragment : Fragment() {
    private lateinit var adView: AdView
    private val symbols = mapOf(
        "EURUSD" to 10.0, "GBPUSD" to 10.0, "AUDUSD" to 10.0, "NZDUSD" to 10.0,
        "USDCAD" to 7.70, "USDCHF" to 11.20, "USDJPY" to 9.10,
        "EURGBP" to 12.80, "EURJPY" to 9.10, "GBPJPY" to 9.10,
        "XAUUSD (Gold)" to 1.0, "XAGUSD (Silver)" to 50.0,
        "Boom 1000" to 1.0, "Boom 500" to 1.0,
        "Crash 1000" to 1.0, "Crash 500" to 0.5, "Crash 600" to 0.6,
        "Step Index" to 0.1, "Volatility 10" to 0.2, "Volatility 25" to 0.5,
        "Volatility 50" to 1.0, "Volatility 75" to 1.5, "Volatility 100" to 2.0
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflater.inflate(R.layout.fragment_pip_value, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val symbolSpinner = view.findViewById<Spinner>(R.id.spinner_symbol)
        val lotInput = view.findViewById<EditText>(R.id.input_lot)
        val pipsInput = view.findViewById<EditText>(R.id.input_pips)
        val calculateBtn = view.findViewById<Button>(R.id.btn_calculate)
        val resultCard = view.findViewById<View>(R.id.result_card)
        val pipValueText = view.findViewById<TextView>(R.id.text_pip_value)
        val totalValueText = view.findViewById<TextView>(R.id.text_total_value)

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, symbols.keys.toList())
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        symbolSpinner.adapter = adapter

        calculateBtn.setOnClickListener {
            val lot = lotInput.text.toString().toDoubleOrNull()
            val pips = pipsInput.text.toString().toDoubleOrNull()
            if (lot == null || pips == null) {
                Toast.makeText(requireContext(), getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val basePipValue = symbols[symbolSpinner.selectedItem.toString()] ?: 10.0
            val pipValue = lot * basePipValue
            val total = pipValue * pips
            resultCard.visibility = View.VISIBLE
            pipValueText.text = String.format("\$%.4f / pip", pipValue)
            totalValueText.text = String.format("%.0f pips = \$%.2f", pips, total)
        }
        adView = view.findViewById(R.id.adView)
        adView.loadAd(AdRequest.Builder().build())
    }
    override fun onPause() { adView.pause(); super.onPause() }
    override fun onResume() { super.onResume(); adView.resume() }
    override fun onDestroy() { adView.destroy(); super.onDestroy() }
}
