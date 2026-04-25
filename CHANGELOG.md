# Changelog

## v31 - address labels retained and map chooser empty-case fixed
- kept District, State, and Country as textbox fields in Add Contact
- retained the updated Address labels and placeholders already present in the latest stable AddContactScreen source
- fixed only the `openMapChooser()` logic to match the locked 3-use-case behavior
- map link present -> opens chooser with link
- address present and map link empty -> opens chooser with address query
- both empty -> still opens chooser with generic geo intent
- removed the empty-case snackbar dependency for map icon flow
- phone section and email section intentionally left unchanged
- updated build tracking for this stabilized build

## v30 - add git tracking files
- added .gitignore
- added CHANGELOG.md
- added BUILD_HISTORY.md
- added README.md

## v29 - fix DOB spacing and compact Rasi/Nakshatra capsule width
- increased spacing below DOB field
- made Rasi/Nakshatra closed capsule wrap closer to value + arrow
- preserved dropdown menu style
- no picker screen reintroduced
- phone and email dropdown behavior unchanged
