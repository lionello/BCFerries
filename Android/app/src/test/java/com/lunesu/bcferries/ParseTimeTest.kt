package com.lunesu.bcferries

import org.joda.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class ParseTimeTest(private val row: String, private val expected: String?) {
    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun params(): List<Array<String?>> = listOf(
            arrayOf("5:14 am", "05:14"),
            arrayOf("12:00 am", "00:00"),
            arrayOf("12:45 pm", "12:45"),
            arrayOf("12:00 pm", "12:00"),
            arrayOf("4:35 PM", "16:35"),
            arrayOf("11:59 p.m.", "23:59"),
            arrayOf("10:05am", "10:05"),
            arrayOf(" 6:00 am ", "06:00"),
            arrayOf("07:30 AM", "07:30"),
            arrayOf("", null),
            arrayOf("noon", null),
            arrayOf("13:00 pm", null),
            arrayOf("6:60 am", null),
            arrayOf("6:00", null)
        )
    }

    @Test
    fun testParseTime() {
        assertEquals(expected?.let { LocalTime.parse(it) }, Utils.parseTime(row))
    }
}
