# TempleAddressBook

Android address book application built in Android Studio.

## Branch
- main

## Current stable tracked build
- versionCode: 31
- versionName: 1.2.24-address-labels-map-chooser-fix
- focus: Add Contact address stabilization and map chooser behavior fix

## Current build summary
- District, State, and Country remain textbox fields in Add Contact
- latest Address labels/placeholders are retained
- map icon chooser flow follows the locked 3-use-case behavior:
    - map link exists -> chooser opens with link
    - address exists, map link empty -> chooser opens with address query
    - both empty -> chooser still opens with generic geo intent
- no empty-case snackbar for map icon flow
- phone and email sections were intentionally left unchanged in this build

## Commit format
Use:

v<versionCode>: <short fix title>

Examples:
- v29: fix DOB spacing and compact Rasi/Nakshatra capsule width
- v30: add git tracking files
- v31: address labels retained and map chooser empty-case fixed

## Tracking files
- CHANGELOG.md
- BUILD_HISTORY.md

## Build release checklist
For every tracked build, update:
- `app/build.gradle.kts`
- `CHANGELOG.md`
- `BUILD_HISTORY.md`
- `README.md`

Also record:
- Build ID / versionName
- files changed
- DB impact
- install guidance
- test focus
- regression areas to retest

## Backup zip naming
TempleAddressBook_v<versionCode>_<short-name>_YYYY-MM-DD.zip
