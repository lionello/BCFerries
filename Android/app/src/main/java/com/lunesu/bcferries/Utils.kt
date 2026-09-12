package com.lunesu.bcferries

import android.content.Context
import android.os.Build
import android.util.Log
import java.net.URL
import kotlinx.coroutines.delay
import org.joda.time.Duration
import org.joda.time.LocalTime
import org.json.JSONObject

object Utils {
    private const val TAG = "Utils"

    val isEmulator = Build.FINGERPRINT?.contains("generic") != false

    @Throws(RuntimeException::class)
    fun <T> atLeast(list: List<T>, size: Int): List<T> {
        if (list.size < size) throw RuntimeException("Expected $size entries but got ${list.size}")
        return list
    }

    suspend fun <T> retry(max: Int, millis: Long, f: suspend () -> T): T {
        var retries = 1
        while (true) {
            try {
                return f()
            } catch (e: Exception) {
                Log.e(TAG, "retry $retries failed", e)
                if (++retries > max) throw e
            }
            delay(millis * retries)
        }
    }

    fun jsonLoad(context: Context, asset: String): JSONObject {
        val inputStream = context.assets.open(asset)
        return JSONObject(String(inputStream.readBytes(), Charsets.UTF_8))
    }

    suspend fun httpGet(url: String): String {
        return retry(2, 1000L) {
            URL(url).openConnection().run {
                connectTimeout = 5000
                readTimeout = 5000
                setRequestProperty("Accept", "application/json")
                getInputStream().bufferedReader().use { it.readText() }
            }
        }
    }

    // The sailings feed writes times as "5:14 am", "12:45 pm" or "4:35 PM"
    private val timeRegex = Regex("""^\s*(\d{1,2}):(\d{2})\s*([AaPp])\.?[Mm]\.?\s*$""")

    fun parseTime(str: String): LocalTime? {
        val match = timeRegex.find(str) ?: return null
        val hour = match.groupValues[1].toInt()
        val minute = match.groupValues[2].toInt()
        if (hour !in 1..12 || minute !in 0..59) return null
        val pm = match.groupValues[3].equals("p", ignoreCase = true)
        return LocalTime(hour % 12 + if (pm) 12 else 0, minute)
    }

    // Durations come as "1h 35m", "0h 20m", "30m" or "01:35"; blank when unknown
    private val clockRegex = Regex("""^\s*(\d+):(\d{2})\s*$""")
    private val durationRegex = Regex("""^\s*(?:(\d+)\s*h)?\s*(?:(\d+)\s*m)?\s*$""")

    fun parseDuration(str: String): Duration? {
        clockRegex.find(str)?.let {
            return Duration.standardMinutes(it.groupValues[1].toLong() * 60 + it.groupValues[2].toLong())
        }
        val match = durationRegex.find(str) ?: return null
        val hours = match.groupValues[1]
        val minutes = match.groupValues[2]
        if (hours.isEmpty() && minutes.isEmpty()) return null
        return Duration.standardMinutes(hours.ifEmpty { "0" }.toLong() * 60 + minutes.ifEmpty { "0" }.toLong())
    }
}
