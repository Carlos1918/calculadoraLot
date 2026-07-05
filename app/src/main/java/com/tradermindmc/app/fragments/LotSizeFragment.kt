package com.tradermindmc.app.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.*
import com.tradermindmc.app.R

class LotSizeFragment : Fragment() {
    private lateinit var adView: AdView
    private val allPairs = linkedMapOf(
        "── FOREX MAJORS ──" to 0.0,"EURUSD" to 10.0,"GBPUSD" to 10.0,"AUDUSD" to 10.0,
        "NZDUSD" to 10.0,"USDCAD" to 7.70,"USDCHF" to 11.20,"USDJPY" to 9.10,
        "── EUR CROSSES ──" to 0.0,"EURGBP" to 12.80,"EURJPY" to 9.10,"EURCHF" to 11.20,
        "EURCAD" to 7.70,"EURAUD" to 10.0,"EURNZD" to 10.0,
        "── GBP CROSSES ──" to 0.0,"GBPJPY" to 9.10,"GBPCHF" to 11.20,"GBPCAD" to 7.70,
        "GBPAUD" to 10.0,"GBPNZD" to 10.0,
        "── AUD CROSSES ──" to 0.0,"AUDJPY" to 9.10,"AUDCAD" to 7.70,"AUDCHF" to 11.20,"AUDNZD" to 10.0,
        "── NZD CROSSES ──" to 0.0,"NZDJPY" to 9.10,"NZDCAD" to 7.70,"NZDCHF" to 11.20,
        "── JPY CROSSES ──" to 0.0,"CADJPY" to 9.10,"CHFJPY" to 9.10,
        "── METALS ──" to 0.0,"XAUUSD (Gold)" to 1.0,"XAGUSD (Silver)" to 50.0,
        "── SYNTHETIC INDICES ──" to 0.0,"Boom 1000" to 1.0,"Boom 500" to 1.0,
        "Crash 1000" to 1.0,"Crash 500" to 0.5,"Crash 600" to 0.6,"Step Index" to 0.1,
        "Volatility 10" to 0.2,"Volatility 25" to 0.5,"Volatility 50" to 1.0,
        "Volatility 75" to 1.5,"Volatility 100" to 2.0,"Range Break 100" to 0.5,"Range Break 200" to 1.0
    )
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflater.inflate(R.layout.fragment_lot_size, container, false)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val balanceInput = view.findViewById<EditText>(R.id.input_balance)
        val riskInput = view.findViewById<EditText>(R.id.input_risk)
        val slInput = view.findViewById<EditText>(R.id.input_sl)
        val pairSpinner = view.findViewById<Spinner>(R.id.spinner_pair)
        val calculateBtn = view.findViewById<Button>(R.id.btn_calculate)
        val resultCard = view.findViewById<View>(R.id.result_card)
        val resultValue = view.findViewById<TextView>(R.id.result_value)
        val resultSubtitle = view.findViewById<TextView>(R.id.result_subtitle)
        val pairNames = allPairs.keys.toList()
        val adapter = object : ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item, pairNames) {
            override fun getView(p: Int, cv: View?, parent: ViewGroup): View {
                val tv = super.getView(p, cv, parent) as TextView
                tv.setTextColor(Color.BLACK); tv.setBackgroundColor(Color.WHITE); return tv
            }
            override fun getDropDownView(p: Int, cv: View?, parent: ViewGroup): View {
                val tv = super.getDropDownView(p, cv, parent) as TextView
                tv.setBackgroundColor(Color.WHITE)
                if (allPairs[pairNames[p]] == 0.0) { tv.setTextColor(Color.parseColor("#4F46E5")); tv.textSize = 11f; tv.setPadding(16,14,16,4) }
                else { tv.setTextColor(Color.BLACK); tv.textSize = 14f; tv.setPadding(32,12,16,12) }
                return tv
            }
            override fun isEnabled(p: Int) = allPairs[pairNames[p]] != 0.0
        }
        pairSpinner.adapter = adapter; pairSpinner.setSelection(1)
        calculateBtn.setOnClickListener {
            val balance = balanceInput.text.toString().toDoubleOrNull()
            val risk = riskInput.text.toString().toDoubleOrNull()
            val sl = slInput.text.toString().toDoubleOrNull()
            val pipValue = allPairs[pairSpinner.selectedItem.toString()] ?: 10.0
            if (pipValue == 0.0 || balance == null || risk == null || sl == null || sl == 0.0) {
                Toast.makeText(requireContext(), getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show(); return@setOnClickListener
            }
            val riskAmount = balance * (risk / 100)
            val lotSize = riskAmount / (sl * pipValue)
            resultCard.visibility = View.VISIBLE
            resultValue.text = String.format("%.2f ${getString(R.string.lots)}", lotSize)
            resultSubtitle.text = String.format("${getString(R.string.risking)} \$%.2f | Pip: \$%.2f", riskAmount, pipValue)
        }
        adView = view.findViewById(R.id.adView); adView.loadAd(AdRequest.Builder().build())
    }
    override fun onPause() { adView.pause(); super.onPause() }
    override fun onResume() { super.onResume(); adView.resume() }
    override fun onDestroy() { adView.destroy(); super.onDestroy() }
}
