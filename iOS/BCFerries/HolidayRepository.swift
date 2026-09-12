//
//  HolidayRepository.swift
//  BCFerries
//

import Foundation

open class HolidayRepository {
    private let holidayDao = HolidayDao()

    // Canada Day is a statutory holiday every year: if it's missing we have no holidays for this year
    private var canadaDay: LocalDate {
        LocalDate.parse("\(LocalDate.now().year!)-07-01")!
    }

    open func getHoliday(day: LocalDate) -> Bool {
        return holidayDao.query(today: day)
    }

    open func setHoliday(day: LocalDate, isHoliday: Bool) {
        if isHoliday {
            holidayDao.insert(date: day)
        } else {
            holidayDao.delete(date: day)
        }
    }

    open func shouldRefresh() -> Bool {
        return !getHoliday(day: canadaDay)
    }

    open func refresh(completion: @escaping () -> Void) {
        BCHolidayFetcher.fetch { holidays, _ in
            if holidays.count >= 10 {
                self.holidayDao.save(holidays: holidays)
            }
            completion()
        }
    }

}
