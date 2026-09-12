//
//  BCFerriesApiFetcherTest.swift
//  BCFerriesTests
//

import XCTest
@testable import BCFerries

class BCFerriesApiFetcherTest: XCTestCase {

    func testParseTime() {
        XCTAssertEqual(LocalTime.parse("05:14"), BCFerriesApiFetcher.parseTime("5:14 am"))
        XCTAssertEqual(LocalTime.parse("00:00"), BCFerriesApiFetcher.parseTime("12:00 am"))
        XCTAssertEqual(LocalTime.parse("12:45"), BCFerriesApiFetcher.parseTime("12:45 pm"))
        XCTAssertEqual(LocalTime.parse("16:35"), BCFerriesApiFetcher.parseTime("4:35 PM"))
        XCTAssertEqual(LocalTime.parse("23:59"), BCFerriesApiFetcher.parseTime("11:59 p.m."))
        XCTAssertEqual(LocalTime.parse("06:00"), BCFerriesApiFetcher.parseTime(" 6:00 am "))
        XCTAssertNil(BCFerriesApiFetcher.parseTime(""))
        XCTAssertNil(BCFerriesApiFetcher.parseTime("noon"))
        XCTAssertNil(BCFerriesApiFetcher.parseTime("13:00 pm"))
        XCTAssertNil(BCFerriesApiFetcher.parseTime("6:00"))
    }

    func testParseDuration() {
        XCTAssertEqual(95 * 60, BCFerriesApiFetcher.parseDuration("1h 35m"))
        XCTAssertEqual(20 * 60, BCFerriesApiFetcher.parseDuration("0h 20m"))
        XCTAssertEqual(30 * 60, BCFerriesApiFetcher.parseDuration("30m"))
        XCTAssertEqual(95 * 60, BCFerriesApiFetcher.parseDuration("01:35"))
        XCTAssertNil(BCFerriesApiFetcher.parseDuration(""))
        XCTAssertNil(BCFerriesApiFetcher.parseDuration("soon"))
    }

    func testParse() {
        let json = """
        {"routes":[
          {"routeCode":"HSBBOW","fromTerminalCode":"HSB","toTerminalCode":"BOW","sailingDuration":"0h 20m",
           "sailings":[{"time":"5:45 am"},{"time":"6:50 am"},{"time":"5:45 am"},{"time":""}]},
          {"routeCode":"TSASWB","fromTerminalCode":"TSA","toTerminalCode":"SWB","sailingDuration":"",
           "sailings":[{"time":"7:00 am"}]},
          {"routeCode":"TSASGI","fromTerminalCode":"TSA","toTerminalCode":"SGI","sailingDuration":"1h 28m",
           "sailings":[{"time":"9:12 am"}]}
        ]}
        """
        let ferries = BCFerriesApiFetcher.parse(try! JSONSerialization.jsonObject(with: json.data(using: .utf8)!))
        XCTAssertEqual(3, ferries.count) // duplicate and blank sailing dropped, unknown terminal skipped
        XCTAssertEqual(.HorseshoeBay, ferries[0].from)
        XCTAssertEqual(.BowenIsland, ferries[0].to)
        XCTAssertEqual(20 * 60, ferries[0].dur)
        XCTAssertEqual("12.35", ferries[0].fare)
        XCTAssertEqual(95 * 60, ferries[2].dur) // fallback duration when the feed leaves it blank
        XCTAssertEqual(FerryDay.EVERYDAY, ferries[2].days)
    }

    func testParseEmpty() {
        XCTAssertEqual([], BCFerriesApiFetcher.parse(["routes": []]))
        XCTAssertEqual([], BCFerriesApiFetcher.parse("nonsense"))
    }
}
