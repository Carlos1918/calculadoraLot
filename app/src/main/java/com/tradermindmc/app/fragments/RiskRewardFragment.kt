package com.tradermindmc.app.fragments

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.*
import com.tradermindmc.app.R

class RiskRewardFragment : Fragment() {
    private lateinit var adView: AdView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflater.inflate(R.layout.fragment_risk_reward, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val entryInput = view.findViewById<EditText>(R.id.input_entry)
        val slInput = view.findViewById<EditText>(R.id.input_sl)
        val tpInput = view.findViewById<EditText>(R.id.input_tp)
        val dirSpinner = view.findViewById<Spinner>(R.id.spinner_direction)
        val calculateBtn = view.findViewById<Button>(R.id.btn_calculate)
        val resultCard = view.findViewById<View>(R.id.result_card)
        val riskPipsText = view.findViewById<TextView>(R.id.text_risk_pips)
        val profitPipsText = view.findViewById<TextView>(R.id.text_profit_pips)
        val ratioText = view.findViewById<TextView>(R.id.text_ratio)
        val messageText = view.findViewById<TextView>(R.id.text_message)

        val directions = listOf(getString(R.string.buy), getString(R.string.sell))
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, directions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dirSpinner.adapter = adapter

        calculateBtn.setOnClickListener {
            val entry = entryInput.text.toString().toDoubleOrNull()
            val sl = slInput.text.toString().toDoubleOrNull()
            val tp = tpInput.text.toString().toDoubleOrNull()
            if (entry == null || sl == null || tp == null) {
                Toast.makeText(requireContext(), getString(R.string.fill_all_fields), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val isBuy = dirSpinner.selectedItemPosition == 0
            val riskPips = if (isBuy) Math.abs(entry - sl) * 10000 else Math.abs(sl - entry) * 10000
            val profitPips = if (isBuy) Math.abs(tp - entry) * 10000 else Math.abs(entry - tp) * 10000
            val ratio = if (riskPips > 0) profitPips / riskPips else 0.0
            resultCard.visibility = View.VISIBLE
            riskPipsText.text = String.format("%.1f pips", riskPips)
            profitPipsText.text = String.format("%.1f pips", profitPips)
            ratioText.text = String.format("1 : %.2f", ratio)
            ratioText.setTextColor(resources.getColor(when {
                ratio >= 1.5 -> R.color.green
                ratio >= 1.0 -> R.color.orange
                else -> R.color.red
            }, null))
            messageText.text = when {
                ratio >= 2.0 -> getString(R.string.ratio_excellent)
                ratio >= 1.5 -> getString(R.string.ratio_good)
                ratio >= 1.0 -> getString(R.string.ratio_low)
                else -> getString(R.string.ratio_bad)
            }
        }
        adView = view.findViewById(R.id.adView)
        adView.loadAd(AdRequest.Builder().build())
    }
    override fun onPause() { adView.pause(); super.onPause() }
    override fun onResume() { super.onResume(); adView.resume() }
    override fun onDestroy() { adView.destroy(); super.onDestroy() }
}
