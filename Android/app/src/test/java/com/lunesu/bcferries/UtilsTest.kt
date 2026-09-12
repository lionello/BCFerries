package com.lunesu.bcferries

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.joda.time.Duration
import org.junit.Assert.*
import org.junit.Test

@ExperimentalCoroutinesApi
class UtilsTest {
    @Test
    fun testIsEmulator() {
        assertTrue(Utils.isEmulator)
    }

    @Test
    fun testAtLeast() {
        Utils.atLeast(listOf(""), 1)
    }

    @Test(expected = RuntimeException::class)
    fun testAtLeastFail() {
        Utils.atLeast(listOf(""), 2)
    }

    @Test
    fun testRetry() = runBlockingTest {
        var i = 0
        Utils.retry(2, 100) { if (i++ == 0) throw RuntimeException() }
        assertEquals(2, i)
    }

    @Test(expected = RuntimeException::class)
    fun testRetryFail() = runBlockingTest {
        Utils.retry(2, 100) { throw RuntimeException() }
    }

    @Test
    fun testHttpGet() = runBlockingTest {
        assertNotEquals("", Utils.httpGet("https://www.google.com/"))
    }

    @Test(expected = java.net.MalformedURLException::class)
    fun testHttpGetFail() = runBlockingTest {
        Utils.httpGet("asf")
    }

    @Test
    fun testParseDuration() {
        assertEquals(Duration.standardMinutes(95), Utils.parseDuration("1h 35m"))
        assertEquals(Duration.standardMinutes(120), Utils.parseDuration("2h 0m"))
        assertEquals(Duration.standardMinutes(20), Utils.parseDuration("0h 20m"))
        assertEquals(Duration.standardMinutes(30), Utils.parseDuration("30m"))
        assertEquals(Duration.standardMinutes(95), Utils.parseDuration("01:35"))
        assertEquals(Duration.standardMinutes(655), Utils.parseDuration("10:55"))
        assertNull(Utils.parseDuration(""))
        assertNull(Utils.parseDuration("   "))
        assertNull(Utils.parseDuration("soon"))
    }
}
