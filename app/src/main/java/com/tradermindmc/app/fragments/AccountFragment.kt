package com.tradermindmc.app.fragments

import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.tradermindmc.app.LoginActivity
import com.tradermindmc.app.R
import com.tradermindmc.app.database.FirestoreRepository
import kotlinx.coroutines.launch
import android.content.Intent
import java.util.Calendar

class AccountFragment : Fragment() {

    private val firestoreRepo = FirestoreRepository()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflater.inflate(R.layout.fragment_account, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val inputBalance = view.findViewById<EditText>(R.id.input_account_balance)
        val inputGoal = view.findViewById<EditText>(R.id.input_goal)
        val inputDrawdown = view.findViewById<EditText>(R.id.input_max_drawdown)
        val inputMonthly = view.findViewById<EditText>(R.id.input_monthly_target)
        val saveBtn = view.findViewById<Button>(R.id.btn_save_account)
        val logoutBtn = view.findViewById<Button>(R.id.btn_logout)
        val tvEmail = view.findViewById<TextView>(R.id.tv_user_email)
        val tvProgress = view.findViewById<TextView>(R.id.tv_goal_progress)
        val tvDrawdownStatus = view.findViewById<TextView>(R.id.tv_drawdown_status)
        val tvMonthlyProfit = view.findViewById<TextView>(R.id.tv_monthly_profit)
        val tvProjection = view.findViewById<TextView>(R.id.tv_projection)

        tvEmail.text = auth.currentUser?.email ?: ""

        // Load saved settings from Firestore
        lifecycleScope.launch {
            try {
                val settings = firestoreRepo.getAccountSettings()
                inputBalance.setText(if (settings["balance"] != 0.0) settings["balance"].toString() else "")
                inputGoal.setText(if (settings["goal"] != 0.0) settings["goal"].toString() else "")
                inputDrawdown.setText(settings["drawdown"].toString())
                inputMonthly.setText(if (settings["monthlyTarget"] != 0.0) settings["monthlyTarget"].toString() else "")
                updateStats(view, settings)
            } catch (e: Exception) { /* use defaults */ }
        }

        saveBtn.setOnClickListener {
            val balance = inputBalance.text.toString().toDoubleOrNull() ?: 0.0
            val goal = inputGoal.text.toString().toDoubleOrNull() ?: 0.0
            val drawdown = inputDrawdown.text.toString().toDoubleOrNull() ?: 5.0
            val monthly = inputMonthly.text.toString().toDoubleOrNull() ?: 0.0

            lifecycleScope.launch {
                try {
                    firestoreRepo.saveAccountSettings(balance, goal, drawdown, monthly)
                    Toast.makeText(requireContext(), getString(R.string.saved), Toast.LENGTH_SHORT).show()
                    updateStats(view, mapOf("balance" to balance, "goal" to goal, "drawdown" to drawdown, "monthlyTarget" to monthly))
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), getString(R.string.error_saving), Toast.LENGTH_SHORT).show()
                }
            }
        }

        logoutBtn.setOnClickListener {
            auth.signOut()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }
    }

    private fun updateStats(view: View, settings: Map<String, Double>) {
        val tvProgress = view.findViewById<TextView>(R.id.tv_goal_progress)
        val tvDrawdownStatus = view.findViewById<TextView>(R.id.tv_drawdown_status)
        val tvMonthlyProfit = view.findViewById<TextView>(R.id.tv_monthly_profit)
        val tvProjection = view.findViewById<TextView>(R.id.tv_projection)

        val balance = settings["balance"] ?: 0.0
        val goal = settings["goal"] ?: 0.0
        val maxDrawdown = settings["drawdown"] ?: 5.0
        val monthlyTarget = settings["monthlyTarget"] ?: 0.0

        lifecycleScope.launch {
            try {
                val trades = firestoreRepo.getAllTrades()
                val totalProfit = trades.sumOf { it.result }
                val currentBalance = balance + totalProfit

                val startOfMonth = Calendar.getInstance().apply {
                    set(Calendar.DAY_OF_MONTH, 1)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                }.timeInMillis
                val monthlyProfit = trades.filter { it.date >= startOfMonth }.sumOf { it.result }
                val drawdownAmount = balance * (maxDrawdown / 100)

                activity?.runOnUiThread {
                    if (goal > 0 && balance > 0) {
                        val progress = ((currentBalance - balance) / (goal - balance) * 100).coerceIn(0.0, 100.0)
                        tvProgress.text = String.format(getString(R.string.goal_progress), currentBalance, goal, progress)
                        tvProgress.setTextColor(Color.parseColor(if (progress >= 50) "#059669" else "#4F46E5"))
                    }
                    tvMonthlyProfit.text = String.format("%s$%.2f", if (monthlyProfit >= 0) "+" else "", monthlyProfit)
                    tvMonthlyProfit.setTextColor(Color.parseColor(if (monthlyProfit >= 0) "#059669" else "#DC2626"))
                    if (monthlyProfit < -drawdownAmount) {
                        tvDrawdownStatus.text = getString(R.string.drawdown_exceeded)
                        tvDrawdownStatus.setTextColor(Color.parseColor("#DC2626"))
                    } else {
                        tvDrawdownStatus.text = String.format(getString(R.string.drawdown_safe), maxDrawdown)
                        tvDrawdownStatus.setTextColor(Color.parseColor("#059669"))
                    }
                    if (monthlyTarget > 0 && goal > 0 && balance > 0) {
                        val months = Math.ceil((goal - balance) / monthlyTarget).toInt()
                        tvProjection.text = String.format(getString(R.string.projection_months), months, monthlyTarget)
                    }
                }
            } catch (e: Exception) { }
        }
    }
}
