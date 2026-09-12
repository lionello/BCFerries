# BC Ferry Times
![iOS CI](https://github.com/lionello/BCFerries/workflows/iOS%20CI/badge.svg)
![Android CI](https://github.com/lionello/BCFerries/workflows/Android%20CI/badge.svg)

Unofficial iOS and Android apps that show today's sailings from the ferry terminal nearest to you,
covering the main BC Ferries routes around Vancouver, Victoria, Nanaimo, the Sunshine Coast, Bowen
Island and Salt Spring Island.

> Not affiliated with or endorsed by British Columbia Ferry Services Inc. "BC Ferries" is their
> trademark. Always confirm sailings and fares at [bcferries.com](https://www.bcferries.com/) before
> you travel.

A port of [PengChauFerry](https://github.com/lionello/PengChauFerry) (Peng Chau, Hong Kong) to the
Salish Sea: same app, same architecture, different water.

## Terminals and routes

| Terminal | Serves | Routes |
|---|---|---|
| Tsawwassen | Vancouver | ⇄ Swartz Bay, ⇄ Duke Point |
| Horseshoe Bay | West Vancouver | ⇄ Departure Bay, ⇄ Langdale, ⇄ Bowen Island |
| Swartz Bay | Victoria | ⇄ Tsawwassen, ⇄ Fulford Harbour |
| Departure Bay | Nanaimo | ⇄ Horseshoe Bay |
| Duke Point | Nanaimo | ⇄ Tsawwassen |
| Langdale | Sunshine Coast | ⇄ Horseshoe Bay |
| Bowen Island | Snug Cove | ⇄ Horseshoe Bay |
| Fulford Harbour | Salt Spring Island | ⇄ Swartz Bay |

The app picks the nearest terminal from your location, shows the walking time to it, and highlights
the next sailing you can still make.

## How it works

* **Sailings** come from the community-run [BC Ferries API](https://www.bcferriesapi.ca/)
  (`/v2/noncapacity/`, the published timetable). bcferries.com itself sits behind a bot-wall and
  cannot be scraped. The feed only covers the current day, so the apps refresh it once a day and
  store every sailing for every day of the week.
* **Holidays** come from [canada-holidays.ca](https://canada-holidays.ca/api) (BC statutory
  holidays, this year and next). The day picker shows "Holiday" on those dates and you can toggle
  it by hand.
* **Offline**: both apps ship a `db.json` snapshot (see below) that is used until the first
  successful refresh, and kept when a refresh fails.
* **Fares** are hard-coded adult walk-on fares in CAD (`BCFerriesApiFetcher` on both platforms and
  `tools/fetch_schedule.py`). They are approximate — verify against bcferries.com before a release.
* **Languages**: English and French; switch from the menu (Android) or the EN/FR button (iOS).

## Regenerating the bundled schedule

```sh
tools/fetch_schedule.py            # fetches live data and rewrites both db.json files
tools/fetch_schedule.py --help     # or parse saved responses offline
```

The snapshot is whatever sailed on the day it was generated; it is only a fallback.

## Building

* **Android**: `cd Android && ./gradlew testDebug` (JDK 11). Instrumented tests parse saved copies
  of both feeds from `app/src/androidTest/assets`.
* **iOS**: `cd iOS && xcodebuild test -scheme BCFerries -destination 'platform=iOS Simulator,name=iPhone 17'`.
  No external dependencies (the Carthage frameworks of the original were unused and have been dropped).

## Not done yet

* Southern Gulf Islands (Route 9) and the other multi-stop routes are not modelled; the data model
  supports a `via` stop, so it is mostly a question of picking terminals.
* Only the scheduled timetable is used; the live `/v2/capacity/` feed (actual departures, deck
  space) is parsed by the tests but not shown.
* Fares are not fetched.

## License

MIT, see [LICENSE](LICENSE).
