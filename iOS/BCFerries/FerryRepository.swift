//
//  FerryRepository.swift
//  BCFerries
//

import Foundation

open class FerryRepository {
    private let ferryDao = FerryDao()

    open func getFerries(from: FerryPier, dow: FerryDay) -> [Ferry] {
        return ferryDao.query(from: from, dow: dow)
    }

    open func shouldRefresh() -> Bool {
        return false
    }

    open func refresh(completion: @escaping () -> Void) {
        BCFerriesApiFetcher.fetch { ferries, _ in
            if ferries.count >= 40 {
                self.ferryDao.save(result: ferries, piers: FerryPier.ENUMS)
            } else {
                // Offline or the feed is down: fall back to the schedule bundled with the app
                JsonFetcher.fetch { ferries, _ in
                    if ferries.count >= 40 {
                        self.ferryDao.save(result: ferries, piers: FerryPier.ENUMS)
                    }
                }
            }
            completion()
        }
    }

}
