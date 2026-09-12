package com.lunesu.bcferries

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.joda.time.Duration
import org.json.JSONObject

/**
 * Reads today's scheduled sailings between the known terminals from the sailings feed.
 * The feed only covers the current day, so every sailing is stored for every day of the week and
 * the app refreshes once a day (see FerryViewModel.shouldRefresh).
 */
object BCFerriesApiFetcher {
    // TODO: fetch fares from bcferries.com. Adult walk-on fares in CAD; verify before each release.
    private val FARES = mapOf(
        setOf(FerryPier.Tsawwassen, FerryPier.SwartzBay) to "19.10",
        setOf(FerryPier.Tsawwassen, FerryPier.DukePoint) to "19.10",
        setOf(FerryPier.HorseshoeBay, FerryPier.DepartureBay) to "19.10",
        setOf(FerryPier.HorseshoeBay, FerryPier.Langdale) to "15.60",
        setOf(FerryPier.HorseshoeBay, FerryPier.BowenIsland) to "12.35",
        setOf(FerryPier.SwartzBay, FerryPier.FulfordHarbour) to "12.30"
    )

    // Used when the feed omits the sailing duration
    private val DURATIONS = mapOf(
        setOf(FerryPier.Tsawwassen, FerryPier.SwartzBay) to Duration.standardMinutes(95),
        setOf(FerryPier.Tsawwassen, FerryPier.DukePoint) to Duration.standardMinutes(120),
        setOf(FerryPier.HorseshoeBay, FerryPier.DepartureBay) to Duration.standardMinutes(100),
        setOf(FerryPier.HorseshoeBay, FerryPier.Langdale) to Duration.standardMinutes(40),
        setOf(FerryPier.HorseshoeBay, FerryPier.BowenIsland) to Duration.standardMinutes(20),
        setOf(FerryPier.SwartzBay, FerryPier.FulfordHarbour) to Duration.standardMinutes(35)
    )

    suspend fun fetch(): List<Ferry> = withContext(Dispatchers.IO) {
        parse(JSONObject(Utils.httpGet(Constants.scheduleUrl)))
    }

    fun parse(json: JSONObject): List<Ferry> {
        val ferries = mutableListOf<Ferry>()
        val routes = json.getJSONArray("routes")
        for (i in 0 until routes.length()) {
            val route = routes.getJSONObject(i)
            // Routes between terminals we don't know (Gulf Islands, north coast, ...) are skipped
            val from = FerryPier.fromCode(route.optString("fromTerminalCode")) ?: continue
            val to = FerryPier.fromCode(route.optString("toTerminalCode")) ?: continue
            val pair = setOf(from, to)
            val dur = Utils.parseDuration(route.optString("sailingDuration")) ?: DURATIONS[pair] ?: continue
            val fare = FARES[pair] ?: ""
            val sailings = route.getJSONArray("sailings")
            for (j in 0 until sailings.length()) {
                val time = Utils.parseTime(sailings.getJSONObject(j).optString("time")) ?: continue
                ferries.add(Ferry(time, from, to, dur, FerryDay.EVERYDAY, fare, null))
            }
        }
        // The feed occasionally lists a sailing twice
        return ferries.distinctBy { Triple(it.from, it.to, it.time) }
    }
}
