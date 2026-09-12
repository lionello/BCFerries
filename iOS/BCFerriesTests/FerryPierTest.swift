//
//  FerryPierTest.swift
//  BCFerriesTests
//

import XCTest
@testable import BCFerries

class FerryPierTest: XCTestCase {

    func testName() {
        XCTAssertEqual("SwartzBay", FerryPier.SwartzBay.rawValue)
    }

    func testToString() {
        XCTAssertEqual("SwartzBay", FerryPier.SwartzBay.toString())
    }

    func testValueOf() {
        XCTAssertEqual(FerryPier.SwartzBay, FerryPier.valueOf("SwartzBay"))
    }

    func testCode() {
        XCTAssertEqual(FerryPier.Tsawwassen, FerryPier.fromCode("TSA"))
        XCTAssertEqual(FerryPier.DepartureBay, FerryPier.fromCode("NAN"))
        XCTAssertNil(FerryPier.fromCode("SGI"))
        for pier in FerryPier.ENUMS {
            XCTAssertEqual(pier, FerryPier.fromCode(pier.code))
        }
    }

    func testMainland() {
        XCTAssertTrue(FerryPier.Tsawwassen.isMainland)
        XCTAssertTrue(FerryPier.HorseshoeBay.isMainland)
        XCTAssertFalse(FerryPier.SwartzBay.isMainland)
        XCTAssertFalse(FerryPier.BowenIsland.isMainland)
    }

    func testNearest() {
        XCTAssertEqual(FerryPier.Tsawwassen, FerryPier.findNearest(latitude: 49.007, longitude: -123.129))
        XCTAssertEqual(FerryPier.SwartzBay, FerryPier.findNearest(latitude: 48.689, longitude: -123.410))
        XCTAssertEqual(FerryPier.BowenIsland, FerryPier.findNearest(latitude: 49.381, longitude: -123.331))
    }

    func testNearestCustom() {
        XCTAssertEqual(FerryPier.Langdale, FerryPier.findNearest(latitude: 49.007, longitude: -123.129, piers: [.Langdale]))
    }
}
