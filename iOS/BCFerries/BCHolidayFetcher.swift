//
//  BCHolidayFetcher.swift
//  BCFerries
//

import Foundation

class BCHolidayFetcher {

    static func fetch(completion: @escaping ([LocalDate], Error?) -> Void) {
        // This year and next, so the list doesn't run dry around New Year
        let year = LocalDate.now().year!
        let group = DispatchGroup()
        let lock = NSLock()
        var all: [LocalDate] = []
        var firstError: Error?

        for y in [year, year + 1] {
            guard let url = URL(string: Constants.holidayUrl + String(y)) else { continue }
            group.enter()
            URLSession.shared.dataTask(with: url) { data, _, error in
                let dates = data
                    .flatMap { try? JSONSerialization.jsonObject(with: $0) }
                    .map { parse($0) } ?? []
                lock.lock()
                all += dates
                if firstError == nil { firstError = error }
                lock.unlock()
                group.leave()
            }.resume()
        }

        group.notify(queue: .main) {
            if !all.isEmpty {
                completion(all, nil)
            } else if let data = JsonData.load(from: Bundle.main) {
                // Offline: fall back to the holidays bundled with the app
                completion(data.holidays.compactMap { LocalDate.parse($0) }, nil)
            } else {
                completion([], firstError ?? ApiError.NoData)
            }
        }
    }

    static func parse(_ json: Any) -> [LocalDate] {
        guard let root = json as? [String: Any],
            let province = root["province"] as? [String: Any],
            let holidays = province["holidays"] as? [[String: Any]] else {
            return []
        }
        return holidays.compactMap { h in
            // Ferries run the holiday schedule on the day the holiday is observed
            let date = (h["observedDate"] as? String) ?? (h["date"] as? String) ?? ""
            return LocalDate.parse(date)
        }
    }
}
