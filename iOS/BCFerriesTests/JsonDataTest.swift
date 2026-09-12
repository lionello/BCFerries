//
//  JsonDataTest.swift
//  BCFerriesTests
//
//  Created by Lionello Lunesu on 8/3/2020.
//  Copyright © 2020 Lionello Lunesu. All rights reserved.
//

import XCTest
@testable import BCFerries

class JsonDataTest: XCTestCase {

    func testLoad() {
        let data = JsonData.load(from: Bundle(for: JsonDataTest.self))
        XCTAssertNotNil(data)

        XCTAssertNotEqual(0, data?.holidays.count)
        XCTAssertNotEqual(0, data?.ferries.count)
        XCTAssert(data!.ferries[0].dur >= 600) // at least 10 minutes
    }

}
