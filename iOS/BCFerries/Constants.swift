//
//  Constants.swift
//  BCFerries
//

import Foundation

enum Constants {
    // Scheduled sailings for today, from the community-run BC Ferries API. bcferries.com itself sits behind
    // a bot-wall so it cannot be scraped; the API's "noncapacity" feed is the published timetable.
    static let scheduleUrl = "https://www.bcferriesapi.ca/v2/noncapacity/"
    // Statutory holidays for British Columbia; the year is appended
    static let holidayUrl = "https://canada-holidays.ca/api/v1/provinces/BC?year="
}
