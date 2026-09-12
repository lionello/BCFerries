package com.lunesu.bcferries

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class FerryPierTest {
    @Test
    fun testName() {
        assertEquals("SwartzBay", FerryPier.SwartzBay.name)
    }

    @Test
    fun testToString() {
        assertEquals("SwartzBay", FerryPier.SwartzBay.toString())
    }

    @Test
    fun testValueOf() {
        assertEquals(FerryPier.SwartzBay, FerryPier.valueOf("SwartzBay"))
    }

    @Test
    fun testFromCode() {
        assertEquals(FerryPier.Tsawwassen, FerryPier.fromCode("TSA"))
        assertEquals(FerryPier.DepartureBay, FerryPier.fromCode("NAN"))
        assertNull(FerryPier.fromCode("SGI"))
    }

    @Test
    fun testMainland() {
        assertEquals(true, FerryPier.Tsawwassen.isMainland)
        assertEquals(true, FerryPier.HorseshoeBay.isMainland)
        assertEquals(false, FerryPier.SwartzBay.isMainland)
        assertEquals(false, FerryPier.BowenIsland.isMainland)
    }

    @Test
    fun testNearest() {
        assertEquals(FerryPier.Tsawwassen, FerryPier.findNearest(49.007, -123.129))
        assertEquals(FerryPier.SwartzBay, FerryPier.findNearest(48.689, -123.410))
        assertEquals(FerryPier.BowenIsland, FerryPier.findNearest(49.381, -123.331))
    }

    @Test
    fun testNearestCustom() {
        assertEquals(FerryPier.Langdale, FerryPier.findNearest(49.007, -123.129, arrayOf(FerryPier.Langdale)))
    }
}
