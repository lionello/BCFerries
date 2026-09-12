//
//  Ferry.swift
//  BCFerries
//
//  Created by Lionello Lunesu on 7/3/2020.
//  Copyright © 2020 Lionello Lunesu. All rights reserved.
//

import Foundation

public struct Ferry: Decodable {
    let time: LocalTime
    let from: FerryPier
    let to: FerryPier
    let dur: Duration
    let days: FerryDays
    let fare: String
    let via: FerryPier?

    var endTime: LocalTime {
        get { time.plus(seconds: dur) }
    }

    static let DUMMY = Ferry(time: LocalTime.now(), from: .Tsawwassen, to: .SwartzBay, dur: 95 * 60, days: FerryDay.EVERYDAY, fare: "19.10", via: nil)
}

extension Ferry: Identifiable {
    public var id: String {
        get { "\(time.secs) \(from.rawValue) \(to.rawValue) \(days.rawValue)" } // fixme
    }
}

extension Ferry: Equatable {
    public static func == (lhs: Self, rhs: Self) -> Bool {
        return lhs.time == rhs.time && lhs.from == rhs.from && lhs.to == rhs.to && lhs.days == rhs.days
    }
}
