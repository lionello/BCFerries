package com.lunesu.bcferries.ui.ferry

import android.content.res.Resources
import com.lunesu.bcferries.FerryDay
import com.lunesu.bcferries.FerryPier
import com.lunesu.bcferries.R

object Strings {
    val PIERS = mapOf(
        FerryPier.Tsawwassen to R.string.Tsawwassen,
        FerryPier.SwartzBay to R.string.SwartzBay,
        FerryPier.HorseshoeBay to R.string.HorseshoeBay,
        FerryPier.DepartureBay to R.string.DepartureBay,
        FerryPier.DukePoint to R.string.DukePoint,
        FerryPier.Langdale to R.string.Langdale,
        FerryPier.BowenIsland to R.string.BowenIsland,
        FerryPier.FulfordHarbour to R.string.FulfordHarbour
    )

    // Terminal plus the place it serves, for the departure picker
    val PIERS_DUAL = mapOf(
        FerryPier.Tsawwassen to R.string.Tsawwassen2,
        FerryPier.SwartzBay to R.string.SwartzBay2,
        FerryPier.HorseshoeBay to R.string.HorseshoeBay2,
        FerryPier.DepartureBay to R.string.DepartureBay2,
        FerryPier.DukePoint to R.string.DukePoint2,
        FerryPier.Langdale to R.string.Langdale2,
        FerryPier.BowenIsland to R.string.BowenIsland2,
        FerryPier.FulfordHarbour to R.string.FulfordHarbour2
    )

    val DAYS = mapOf(
        FerryDay.Monday to R.string.monday,
        FerryDay.Tuesday to R.string.tuesday,
        FerryDay.Wednesday to R.string.wednesday,
        FerryDay.Thursday to R.string.thursday,
        FerryDay.Friday to R.string.friday,
        FerryDay.Saturday to R.string.saturday,
        FerryDay.Sunday to R.string.sunday,
        FerryDay.Holiday to R.string.holiday
    )

    fun localized(pier: FerryPier, resources: Resources): String =
        resources.getString(PIERS.getValue(pier))

    fun localized(day: FerryDay, resources: Resources): String =
        resources.getString(DAYS.getValue(day))
}
