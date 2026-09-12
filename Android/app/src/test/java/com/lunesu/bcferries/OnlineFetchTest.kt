package com.lunesu.bcferries

import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.assertNotEquals
import org.junit.BeforeClass
import org.junit.Ignore
import org.junit.Test

class OnlineFetchTest {
    companion object {
        @BeforeClass
        @JvmStatic
        fun setup() {
            // SSL verification fails on the plain JVM: ignore the certs.
            TrustAllCertificates.setup()
        }
    }

    @Test
    fun testSchedule() {
        runBlocking {
            assertNotEquals(0, BCFerriesApiFetcher.fetch().size)
        }
    }

    @Test
    fun testBCHolidays() {
        runBlocking {
            assertNotEquals(0, BCHolidayFetcher.fetch().size)
        }
    }

    // Bug: latest IDEA no longer allows manual runs of ignored tests https://youtrack.jetbrains.com/issue/IDEA-210546
    // tools/fetch_schedule.py does the same from the command line.
    @Test
    @Ignore("Run this manually to export JSON")
    fun testDumpJson() {
        runBlocking {
            val ferries = BCFerriesApiFetcher.fetch().map {
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

            val holidays = BCHolidayFetcher.fetch().map {
                it.toString()
            }

            val root = JSONObject()
            root.put("ferries", JSONArray(ferries))
            root.put("holidays", JSONArray(holidays))
            println(root.toString(2))
        }
    }
}
