package com.lunesu.bcferries

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.joda.time.LocalDate
import org.json.JSONObject

object BCHolidayFetcher {
    suspend fun fetch(): List<LocalDate> = withContext(Dispatchers.IO) {
        // This year and next, so the list doesn't run dry around New Year
        val year = LocalDate.now().year
        parse(JSONObject(Utils.httpGet(Constants.holidayUrl + year))) +
            parse(JSONObject(Utils.httpGet(Constants.holidayUrl + (year + 1))))
    }

    fun parse(json: JSONObject): List<LocalDate> {
        val holidays = json.getJSONObject("province").getJSONArray("holidays")
        return (0 until holidays.length())
            .map { holidays.getJSONObject(it) }
            .mapNotNull {
                // Ferries run the holiday schedule on the day the holiday is observed
                val date = it.optString("observedDate").ifEmpty { it.optString("date") }
                try {
                    LocalDate.parse(date)
                } catch (e: IllegalArgumentException) { null }
            }
    }
}
