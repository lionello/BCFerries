package com.lunesu.bcferries

import org.joda.time.LocalDate

open class HolidayRepository(db: DbOpenHelper) {
    companion object {
        // Canada Day is a statutory holiday every year: if it's missing we have no holidays for this year
        private val CANADA_DAY: LocalDate get() = LocalDate(LocalDate.now().year, 7, 1)
    }

    private val holidayDao = HolidayDao(db)

    open fun getHoliday(day: LocalDate): Boolean {
        return holidayDao.query(day)
    }

    open fun setHoliday(day: LocalDate, isHoliday: Boolean) {
        if (isHoliday) {
            holidayDao.insert(day)
        } else {
            holidayDao.delete(day)
        }
    }

    open fun shouldRefresh(): Boolean {
        return !getHoliday(CANADA_DAY)
    }

    open suspend fun refresh() {
        runCatching {
            holidayDao.save(Utils.atLeast(BCHolidayFetcher.fetch(), 10))
        }
    }
}
