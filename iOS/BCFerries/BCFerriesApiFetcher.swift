//
//  BCFerriesApiFetcher.swift
//  BCFerries
//

import Foundation

/// Reads today's scheduled sailings between the known terminals from the sailings feed.
/// The feed only covers the current day, so every sailing is stored for every day of the week and
/// the app refreshes once a day (see FerryViewModel.shouldRefresh).
class BCFerriesApiFetcher {
    // TODO: fetch fares from bcferries.com. Adult walk-on fares in CAD; verify before each release.
    private static let FARES: [Set<FerryPier>: String] = [
        [.Tsawwassen, .SwartzBay]: "19.10",
        [.Tsawwassen, .DukePoint]: "19.10",
        [.HorseshoeBay, .DepartureBay]: "19.10",
        [.HorseshoeBay, .Langdale]: "15.60",
        [.HorseshoeBay, .BowenIsland]: "12.35",
        [.SwartzBay, .FulfordHarbour]: "12.30"
    ]

    // Used when the feed omits the sailing duration (seconds)
    private static let DURATIONS: [Set<FerryPier>: Duration] = [
        [.Tsawwassen, .SwartzBay]: 95 * 60,
        [.Tsawwassen, .DukePoint]: 120 * 60,
        [.HorseshoeBay, .DepartureBay]: 100 * 60,
        [.HorseshoeBay, .Langdale]: 40 * 60,
        [.HorseshoeBay, .BowenIsland]: 20 * 60,
        [.SwartzBay, .FulfordHarbour]: 35 * 60
    ]

    static func fetch(completion: @escaping ([Ferry], Error?) -> Void) {
        guard let url = URL(string: Constants.scheduleUrl) else {
            completion([], ApiError.NoData)
            return
        }
        let request = URLRequest(url: url, cachePolicy: .reloadIgnoringLocalCacheData, timeoutInterval: 10)
        URLSession.shared.dataTask(with: request) { data, _, error in
            let ferries = data
                .flatMap { try? JSONSerialization.jsonObject(with: $0) }
                .map { parse($0) } ?? []
            DispatchQueue.main.async {
                completion(ferries, ferries.isEmpty ? (error ?? ApiError.NoData) : nil)
            }
        }.resume()
    }

    static func parse(_ json: Any) -> [Ferry] {
        guard let root = json as? [String: Any], let routes = root["routes"] as? [[String: Any]] else {
            return []
        }
        var ferries: [Ferry] = []
        var seen = Set<String>()
        for route in routes {
            // Routes between terminals we don't know (Gulf Islands, north coast, ...) are skipped
            guard let from = FerryPier.fromCode(route["fromTerminalCode"] as? String ?? ""),
                let to = FerryPier.fromCode(route["toTerminalCode"] as? String ?? "") else {
                continue
            }
            let pair: Set<FerryPier> = [from, to]
            guard let dur = parseDuration(route["sailingDuration"] as? String ?? "") ?? DURATIONS[pair] else {
                continue
            }
            let fare = FARES[pair] ?? ""
            for sailing in route["sailings"] as? [[String: Any]] ?? [] {
                guard let time = parseTime(sailing["time"] as? String ?? "") else { continue }
                // The feed occasionally lists a sailing twice
                if seen.insert("\(from.rawValue) \(to.rawValue) \(time.secs)").inserted {
                    ferries.append(Ferry(time: time, from: from, to: to, dur: dur, days: FerryDay.EVERYDAY, fare: fare, via: nil))
                }
            }
        }
        return ferries
    }

    // The sailings feed writes times as "5:14 am", "12:45 pm" or "4:35 PM"
    private static let timeRegex = try! NSRegularExpression(pattern: #"^\s*(\d{1,2}):(\d{2})\s*([AaPp])\.?[Mm]\.?\s*$"#)

    static func parseTime(_ s: String) -> LocalTime? {
        guard let m = timeRegex.firstMatch(in: s, range: NSRange(s.startIndex..., in: s)),
            let hour = Int(s[Range(m.range(at: 1), in: s)!]),
            let minute = Int(s[Range(m.range(at: 2), in: s)!]),
            (1...12).contains(hour), (0...59).contains(minute) else {
            return nil
        }
        let pm = s[Range(m.range(at: 3), in: s)!].lowercased() == "p"
        return LocalTime(secs: Double((hour % 12 + (pm ? 12 : 0)) * 60 + minute) * 60.0)
    }

    // Durations come as "1h 35m", "0h 20m", "30m" or "01:35"; blank when unknown
    private static let clockRegex = try! NSRegularExpression(pattern: #"^\s*(\d+):(\d{2})\s*$"#)
    private static let durationRegex = try! NSRegularExpression(pattern: #"^\s*(?:(\d+)\s*h)?\s*(?:(\d+)\s*m)?\s*$"#)

    static func parseDuration(_ s: String) -> Duration? {
        let range = NSRange(s.startIndex..., in: s)
        if let m = clockRegex.firstMatch(in: s, range: range),
            let hours = Int(s[Range(m.range(at: 1), in: s)!]),
            let minutes = Int(s[Range(m.range(at: 2), in: s)!]) {
            return Double(hours * 60 + minutes) * 60.0
        }
        guard let m = durationRegex.firstMatch(in: s, range: range) else { return nil }
        let hours = Range(m.range(at: 1), in: s).flatMap { Int(s[$0]) }
        let minutes = Range(m.range(at: 2), in: s).flatMap { Int(s[$0]) }
        if hours == nil && minutes == nil { return nil }
        return Double((hours ?? 0) * 60 + (minutes ?? 0)) * 60.0
    }
}
