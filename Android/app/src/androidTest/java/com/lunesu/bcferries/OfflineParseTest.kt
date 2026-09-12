package com.lunesu.bcferries

import androidx.test.platform.app.InstrumentationRegistry
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** Parses snapshots of the two feeds (androidTest/assets) so the parsers can be tested offline. */
class OfflineParseTest {
    private val appContext = InstrumentationRegistry.getInstrumentation().context

    @Test
    fun testSchedule() {
        val ferries = BCFerriesApiFetcher.parse(Utils.jsonLoad(appContext, "noncapacity.json"))
        assertNotEquals(0, ferries.size)
        // Every known terminal appears as a departure point
        assertEquals(FerryPier.ENUMS.toSet(), ferries.map { it.from }.toSet())
        // No sailing was listed twice
        assertEquals(ferries.size, ferries.distinctBy { Triple(it.from, it.to, it.time) }.size)
    }

    @Test
    fun testScheduleLive() {
        // The live feed uses actual departure times for past sailings, but still parses
        val ferries = BCFerriesApiFetcher.parse(Utils.jsonLoad(appContext, "capacity.json"))
        assertNotEquals(0, ferries.size)
        // No route connects the two mainland hubs
        assertTrue(ferries.none { it.from.isMainland && it.to.isMainland })
    }

    @Test
    fun testHolidays() {
        val holidays = BCHolidayFetcher.parse(Utils.jsonLoad(appContext, "holidays.json"))
        assertEquals(11, holidays.size)
    }

    @Test
    fun testDumpJson() {
        val ferries = BCFerriesApiFetcher.parse(Utils.jsonLoad(appContext, "noncapacity.json")).map {
            val o = JSONObject()
            o.put("time", it.time.toString())
            o.put("from", it.from.name)
            o.put("to", it.to.name)
            o.put("dur", it.dur.standardMinutes)
            o.put("days", FerryDay.daysToInt(it.days))
            o.put("fare", it.fare)
            o.put("via", it.via?.name)
            o
        }

        val holidays = BCHolidayFetcher.parse(Utils.jsonLoad(appContext, "holidays.json")).map {
            it.toString()
        }

        val root = JSONObject()
        root.put("ferries", JSONArray(ferries))
        root.put("holidays", JSONArray(holidays))

        println(root.toString(2))
    }
}
