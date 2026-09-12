package com.lunesu.bcferries

/** A ferry terminal; [code] is the terminal code used by BC Ferries and the sailings feed. */
enum class FerryPier(val code: String) {
    Tsawwassen("TSA"),
    SwartzBay("SWB"),
    HorseshoeBay("HSB"),
    DepartureBay("NAN"),
    DukePoint("DUK"),
    Langdale("LNG"),
    BowenIsland("BOW"), // Snug Cove
    FulfordHarbour("FUL"); // Salt Spring Island

    val coordinate get() = COORDS.getValue(this)

    /** The two Metro Vancouver hubs; every route has exactly one non-mainland end. */
    val isMainland get() = this == Tsawwassen || this == HorseshoeBay

    companion object {
        internal val ENUMS = values()

        val COORDS = mapOf(
            Tsawwassen to Coordinate(49.0068, -123.1290),
            SwartzBay to Coordinate(48.6890, -123.4105),
            HorseshoeBay to Coordinate(49.3757, -123.2717),
            DepartureBay to Coordinate(49.1935, -123.9553),
            DukePoint to Coordinate(49.1626, -123.8912),
            Langdale to Coordinate(49.4336, -123.4738),
            BowenIsland to Coordinate(49.3800, -123.3300),
            FulfordHarbour to Coordinate(48.7690, -123.4510)
        )

        fun fromCode(code: String): FerryPier? = ENUMS.find { it.code == code }

        fun findNearest(latitude: Double, longitude: Double, piers: Array<FerryPier> = ENUMS): FerryPier? {
            return piers.minBy { it.coordinate.distance(latitude, longitude) }
        }
    }
}
