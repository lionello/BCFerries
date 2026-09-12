//
//  FerryPier.swift
//  BCFerries
//

import Foundation

/// A ferry terminal. The raw value is the enum name; `code` is the terminal code used by BC Ferries.
public enum FerryPier: String, Codable {
    case Tsawwassen
    case SwartzBay
    case HorseshoeBay
    case DepartureBay
    case DukePoint
    case Langdale
    case BowenIsland // Snug Cove
    case FulfordHarbour // Salt Spring Island

    static let ENUMS = [Tsawwassen, SwartzBay, HorseshoeBay, DepartureBay, DukePoint, Langdale, BowenIsland, FulfordHarbour]

    static let CODES: [String: FerryPier] = [
        "TSA": .Tsawwassen,
        "SWB": .SwartzBay,
        "HSB": .HorseshoeBay,
        "NAN": .DepartureBay,
        "DUK": .DukePoint,
        "LNG": .Langdale,
        "BOW": .BowenIsland,
        "FUL": .FulfordHarbour
    ]

    static let COORDS: [FerryPier: Coordinate] = [
        .Tsawwassen: Coordinate(latitude: 49.0068, longitude: -123.1290),
        .SwartzBay: Coordinate(latitude: 48.6890, longitude: -123.4105),
        .HorseshoeBay: Coordinate(latitude: 49.3757, longitude: -123.2717),
        .DepartureBay: Coordinate(latitude: 49.1935, longitude: -123.9553),
        .DukePoint: Coordinate(latitude: 49.1626, longitude: -123.8912),
        .Langdale: Coordinate(latitude: 49.4336, longitude: -123.4738),
        .BowenIsland: Coordinate(latitude: 49.3800, longitude: -123.3300),
        .FulfordHarbour: Coordinate(latitude: 48.7690, longitude: -123.4510)
    ]

    var coordinate: Coordinate {
        FerryPier.COORDS[self]!
    }

    /// The two Metro Vancouver hubs; every route has exactly one non-mainland end.
    var isMainland: Bool {
        self == .Tsawwassen || self == .HorseshoeBay
    }

    var code: String {
        FerryPier.CODES.first { $0.value == self }!.key
    }

    static func findNearest(latitude: Double, longitude: Double, piers: [FerryPier] = ENUMS) -> FerryPier? {
        let dists = piers.map { (key: $0, value: COORDS[$0]!.distance(lat: latitude, long: longitude)) }
        return dists.min { $0.value < $1.value }?.key
    }

    static func valueOf(_ s: String) -> FerryPier? {
        FerryPier(rawValue: s)
    }

    static func fromCode(_ code: String) -> FerryPier? {
        CODES[code]
    }

    func toString() -> String {
        return rawValue
    }
}

extension FerryPier: Identifiable {
    public var id: String { self.rawValue }
}
