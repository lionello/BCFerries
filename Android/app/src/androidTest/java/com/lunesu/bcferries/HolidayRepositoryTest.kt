package com.lunesu.bcferries

import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.joda.time.LocalDate
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HolidayRepositoryTest {
    private val db = DbOpenHelper(null)
    private val newYear = LocalDate(LocalDate.now().year, 1, 1)

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun testClean() {
        val repo = HolidayRepository(db)
        assertFalse(repo.getHoliday(newYear))
    }

    @Test
    fun testRefresh() = runBlocking {
        val repo = HolidayRepository(db)
        repo.refresh()
        assertTrue(repo.getHoliday(newYear))
    }

    @Test
    fun testShouldRefresh() = runBlocking {
        val repo = HolidayRepository(db)
        assertTrue(repo.shouldRefresh())
        repo.refresh()
        assertFalse(repo.shouldRefresh())
    }

    @Test
    fun testSet() {
        val repo = HolidayRepository(db)

        repo.setHoliday(newYear, true)
        assertTrue(repo.getHoliday(newYear))

        repo.setHoliday(newYear, false)
        assertFalse(repo.getHoliday(newYear))
    }
}
