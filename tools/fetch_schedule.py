#!/usr/bin/env python3
"""Regenerate the bundled db.json (today's sailings + BC statutory holidays) for both apps.

Mirrors BCFerriesApiFetcher / BCHolidayFetcher in the apps, so the seed matches what the apps
would fetch themselves. The sailings feed only covers the current day, so every sailing is stored
for every day of the week (days=255) and the apps refresh it daily.

    tools/fetch_schedule.py                       # fetch live and rewrite both db.json files
    tools/fetch_schedule.py --schedule FILE --holidays FILE [FILE ...]   # from saved responses
    tools/fetch_schedule.py --stdout              # print instead of writing
"""
import argparse
import datetime
import json
import re
import sys
import urllib.request
from pathlib import Path

SCHEDULE_URL = "https://www.bcferriesapi.ca/v2/noncapacity/"
HOLIDAY_URL = "https://canada-holidays.ca/api/v1/provinces/BC?year="

ROOT = Path(__file__).resolve().parent.parent
OUTPUTS = [ROOT / "Android/app/src/main/assets/db.json", ROOT / "iOS/BCFerries/db.json"]

# Terminal codes as used by BC Ferries -> FerryPier enum names
PIERS = {
    "TSA": "Tsawwassen",
    "SWB": "SwartzBay",
    "HSB": "HorseshoeBay",
    "NAN": "DepartureBay",
    "DUK": "DukePoint",
    "LNG": "Langdale",
    "BOW": "BowenIsland",
    "FUL": "FulfordHarbour",
}

# TODO: fetch fares from bcferries.com. Adult walk-on fares in CAD; verify before each release.
FARES = {
    frozenset({"Tsawwassen", "SwartzBay"}): "19.10",
    frozenset({"Tsawwassen", "DukePoint"}): "19.10",
    frozenset({"HorseshoeBay", "DepartureBay"}): "19.10",
    frozenset({"HorseshoeBay", "Langdale"}): "15.60",
    frozenset({"HorseshoeBay", "BowenIsland"}): "12.35",
    frozenset({"SwartzBay", "FulfordHarbour"}): "12.30",
}

# Minutes, used when the feed omits the sailing duration
DURATIONS = {
    frozenset({"Tsawwassen", "SwartzBay"}): 95,
    frozenset({"Tsawwassen", "DukePoint"}): 120,
    frozenset({"HorseshoeBay", "DepartureBay"}): 100,
    frozenset({"HorseshoeBay", "Langdale"}): 40,
    frozenset({"HorseshoeBay", "BowenIsland"}): 20,
    frozenset({"SwartzBay", "FulfordHarbour"}): 35,
}

EVERYDAY = 255

TIME_RE = re.compile(r"^\s*(\d{1,2}):(\d{2})\s*([AaPp])\.?[Mm]\.?\s*$")
CLOCK_RE = re.compile(r"^\s*(\d+):(\d{2})\s*$")
DURATION_RE = re.compile(r"^\s*(?:(\d+)\s*h)?\s*(?:(\d+)\s*m)?\s*$")


def parse_time(s):
    """'5:14 am' / '4:35 PM' -> 'HH:MM:SS', or None."""
    m = TIME_RE.match(s or "")
    if not m:
        return None
    hour, minute = int(m.group(1)), int(m.group(2))
    if not (1 <= hour <= 12 and 0 <= minute <= 59):
        return None
    hour = hour % 12 + (12 if m.group(3).lower() == "p" else 0)
    return "%02d:%02d:00" % (hour, minute)


def parse_duration(s):
    """'1h 35m' / '0h 20m' / '30m' / '01:35' -> minutes, or None."""
    m = CLOCK_RE.match(s or "")
    if m:
        return int(m.group(1)) * 60 + int(m.group(2))
    m = DURATION_RE.match(s or "")
    if not m or (m.group(1) is None and m.group(2) is None):
        return None
    return int(m.group(1) or 0) * 60 + int(m.group(2) or 0)


def parse_schedule(doc):
    ferries, seen = [], set()
    for route in doc.get("routes", []):
        frm = PIERS.get(route.get("fromTerminalCode"))
        to = PIERS.get(route.get("toTerminalCode"))
        if not frm or not to:
            continue  # Gulf Islands, north coast, ... are not modelled
        pair = frozenset({frm, to})
        dur = parse_duration(route.get("sailingDuration"))
        if dur is None:
            dur = DURATIONS.get(pair)
        if dur is None:
            continue
        for sailing in route.get("sailings", []):
            time = parse_time(sailing.get("time"))
            if time is None or (frm, to, time) in seen:
                continue
            seen.add((frm, to, time))
            ferries.append({"time": time, "from": frm, "to": to, "dur": dur, "days": EVERYDAY, "fare": FARES.get(pair, "")})
    return sorted(ferries, key=lambda f: (f["from"], f["to"], f["time"]))


def parse_holidays(doc):
    # Ferries run the holiday schedule on the day the holiday is observed
    return [h.get("observedDate") or h["date"] for h in doc["province"]["holidays"]]


def fetch_json(url):
    req = urllib.request.Request(url, headers={"Accept": "application/json", "User-Agent": "BCFerries/tools"})
    with urllib.request.urlopen(req, timeout=15) as resp:
        return json.load(resp)


def main():
    ap = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    ap.add_argument("--schedule", type=Path, help="saved response of the sailings feed")
    ap.add_argument("--holidays", type=Path, nargs="+", help="saved response(s) of the holiday API")
    ap.add_argument("--stdout", action="store_true", help="print the JSON instead of writing the files")
    args = ap.parse_args()

    if args.schedule:
        schedule = json.loads(args.schedule.read_text())
    else:
        schedule = fetch_json(SCHEDULE_URL)
    if args.holidays:
        holiday_docs = [json.loads(p.read_text()) for p in args.holidays]
    else:
        year = datetime.date.today().year
        holiday_docs = [fetch_json(HOLIDAY_URL + str(y)) for y in (year, year + 1)]

    ferries = parse_schedule(schedule)
    holidays = sorted({d for doc in holiday_docs for d in parse_holidays(doc)})
    if len(ferries) < 40:
        sys.exit("only %d sailings parsed; refusing to write a suspiciously small schedule" % len(ferries))

    out = json.dumps({"ferries": ferries, "holidays": holidays}, indent=4, sort_keys=True) + "\n"
    if args.stdout:
        sys.stdout.write(out)
        return
    for path in OUTPUTS:
        path.write_text(out)
        print("wrote %d sailings, %d holidays -> %s" % (len(ferries), len(holidays), path.relative_to(ROOT)))


if __name__ == "__main__":
    main()
