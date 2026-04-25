# Build History

## v31
- versionCode: 31
- versionName: 1.2.24-address-labels-map-chooser-fix
- buildDate: 2026-04-25
- title: address labels retained and map chooser empty-case fixed
- changed files:
  - app/build.gradle.kts
  - app/src/main/java/com/snuggy/templeaddressbook/ui/contacts/AddContactScreen.kt
  - CHANGELOG.md
  - BUILD_HISTORY.md
  - README.md
- notes:
  - kept District, State, and Country as textbox fields
  - retained the latest Address label/placeholder updates already present in source
  - fixed only openMapChooser() to restore locked chooser behavior
  - both-empty map case now opens chooser instead of snackbar
  - no DB schema change
- install guidance:
  - Install over existing app
- test focus:
  - tap map icon with map link filled
  - tap map icon with address filled and map link empty
  - tap map icon with both address and map link empty
  - verify no snackbar appears in the empty case
  - verify address labels/placeholders remain unchanged
  - verify phone and email sections remain unchanged
- regression retest:
  - Add Contact save flow
  - Basic Info section
  - Phone Numbers section
  - Email Address section
  - Google Map Link field UI
  - EN / TA toggle behavior in Add Contact

## v30
- versionCode: 30
- title: add git tracking files
- changed files:
  - .gitignore
  - CHANGELOG.md
  - BUILD_HISTORY.md
  - README.md
- notes:
  - added project tracking files for GitHub workflow

## v29
- versionCode: 29
- title: fix DOB spacing and compact Rasi/Nakshatra capsule width
- changed files:
  - app/src/main/java/com/snuggy/templeaddressbook/ui/contacts/AddContactScreen.kt
- notes:
  - increased spacing below DOB field
  - tightened Rasi/Nakshatra closed capsule width
  - preserved dropdown menu behavior
  - no picker/dialog reintroduced
