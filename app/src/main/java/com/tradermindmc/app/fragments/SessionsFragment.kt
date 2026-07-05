package com.tradermindmc.app.fragments

import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.tradermindmc.app.R
import java.util.*

class SessionsFragment : Fragment() {

    private val handler = Handler(Looper.getMainLooper())
    private lateinit var updateRunnable: Runnable

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) =
        inflater.inflate(R.layout.fragment_sessions, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvClock = view.findViewById<TextView>(R.id.tv_utc_clock)
        val tvSydneyStatus = view.findViewById<TextView>(R.id.tv_sydney_status)
        val tvTokyoStatus = view.findViewById<TextView>(R.id.tv_tokyo_status)
        val tvLondonStatus = view.findViewById<TextView>(R.id.tv_london_status)
        val tvNewYorkStatus = view.findViewById<TextView>(R.id.tv_newyork_status)
        val tvSydneyTime = view.findViewById<TextView>(R.id.tv_sydney_time)
        val tvTokyoTime = view.findViewById<TextView>(R.id.tv_tokyo_time)
        val tvLondonTime = view.findViewById<TextView>(R.id.tv_london_time)
        val tvNewYorkTime = view.findViewById<TextView>(R.id.tv_newyork_time)
        val tvBestSession = view.findViewById<TextView>(R.id.tv_best_session)

        updateRunnable = Runnable {
            val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            val hour = utc.get(Calendar.HOUR_OF_DAY)
            val minute = utc.get(Calendar.MINUTE)

            tvClock.text = String.format("UTC: %02d:%02d", hour, minute)

            // Sessions in UTC
            // Sydney: 21:00 - 06:00 UTC
            // Tokyo: 23:00 - 08:00 UTC
            // London: 07:00 - 16:00 UTC
            // New York: 12:00 - 21:00 UTC

            fun isOpen(startH: Int, endH: Int): Boolean {
                return if (startH < endH) hour in startH until endH
                else hour >= startH || hour < endH
            }

            val sydneyOpen = isOpen(21, 6)
            val tokyoOpen = isOpen(23, 8)
            val londonOpen = isOpen(7, 16)
            val nyOpen = isOpen(12, 21)

            fun setStatus(tv: TextView, open: Boolean) {
                tv.text = if (open) getString(R.string.open) else getString(R.string.closed)
                tv.setTextColor(Color.parseColor(if (open) "#059669" else "#DC2626"))
            }

            setStatus(tvSydneyStatus, sydneyOpen)
            setStatus(tvTokyoStatus, tokyoOpen)
            setStatus(tvLondonStatus, londonOpen)
            setStatus(tvNewYorkStatus, nyOpen)

            // Local times for each session
            fun utcToLocal(tzId: String): String {
                val cal = Calendar.getInstance(TimeZone.getTimeZone(tzId))
                return String.format("%02d:%02d", cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE))
            }
            tvSydneyTime.text = utcToLocal("Australia/Sydney")
            tvTokyoTime.text = utcToLocal("Asia/Tokyo")
            tvLondonTime.text = utcToLocal("Europe/London")
            tvNewYorkTime.text = utcToLocal("America/New_York")

            // Best session to trade
            val openCount = listOf(sydneyOpen, tokyoOpen, londonOpen, nyOpen).count { it }
            tvBestSession.text = when {
                londonOpen && nyOpen -> getString(R.string.session_london_ny)
                londonOpen && tokyoOpen -> getString(R.string.session_london_tokyo)
                londonOpen -> getString(R.string.session_london)
                nyOpen -> getString(R.string.session_newyork)
                tokyoOpen -> getString(R.string.session_tokyo)
                sydneyOpen -> getString(R.string.session_sydney)
                else -> getString(R.string.session_closed)
            }
            tvBestSession.setTextColor(Color.parseColor(if (openCount == 0) "#DC2626" else "#4F46E5"))

            handler.postDelayed(updateRunnable, 1000)
        }
        handler.post(updateRunnable)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(updateRunnable)
    }
}
