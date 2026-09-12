package com.lunesu.bcferries

object Constants {
    // Scheduled sailings for today, from the community-run BC Ferries API. bcferries.com itself sits behind
    // a bot-wall so it cannot be scraped; the API's "noncapacity" feed is the published timetable.
    const val scheduleUrl = "https://www.bcferriesapi.ca/v2/noncapacity/"
    // Statutory holidays for British Columbia; the year is appended
    const val holidayUrl = "https://canada-holidays.ca/api/v1/provinces/BC?year="
    const val storeUrl = "https://play.google.com/store/apps/details?id=com.lunesu.bcferries"
}
