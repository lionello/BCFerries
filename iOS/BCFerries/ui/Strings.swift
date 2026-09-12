//
//  Strings.swift
//  BCFerries
//

import SwiftUI

class Strings {
    // Terminal plus the place it serves, for the departure picker
    static let PIERS: [FerryPier?: LocalizedStringKey] = [
        .Tsawwassen: "Tsawwassen · Vancouver",
        .SwartzBay: "Swartz Bay · Victoria",
        .HorseshoeBay: "Horseshoe Bay · West Vancouver",
        .DepartureBay: "Departure Bay · Nanaimo",
        .DukePoint: "Duke Point · Nanaimo",
        .Langdale: "Langdale · Sunshine Coast",
        .BowenIsland: "Bowen Island · Snug Cove",
        .FulfordHarbour: "Fulford Harbour · Salt Spring Island"
    ]

    static let DAYS: [FerryDay?: LocalizedStringKey] = [
        .Monday: "Monday",
        .Tuesday: "Tuesday",
        .Wednesday: "Wednesday",
        .Thursday: "Thursday",
        .Friday: "Friday",
        .Saturday: "Saturday",
        .Sunday: "Sunday",
        .Holiday: "Holiday"
    ]

    static func localized(_ pier: FerryPier, _ bundle: Bundle) -> String {
        bundle.localizedString(forKey: pier.rawValue, value: nil, table: nil)
    }

    static func localized(_ day: FerryDay, _ bundle: Bundle) -> String {
        bundle.localizedString(forKey: day.toString(), value: nil, table: nil)
    }

}
