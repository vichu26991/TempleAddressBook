# Changelog

## v36 - 1.2.29-contact-details-edit-flow
- Removed duplicate top favorite star from Contact Details.
- Kept bottom Favorite as the single favorite control.
- Edit now opens Add/Edit Contact with existing values prefilled.
- Save from Edit updates the same contact instead of creating a duplicate.
- Added repository and SQLite update methods for existing contacts.
- No DB schema change; DB remains schema 4.
- Tracking files now catch up v33, v34, v35, and v36.

## v35 - 1.2.28-contact-details-hide-bottom-nav
- Contact Details now hides the main bottom navigation tabs.
- Contact Details bottom action bar remains visible.
- Back from Contact Details restores the Contacts list bottom navigation.

## v34 - 1.2.27-contact-details-bottom-actions
- Added Contact Details bottom actions: Favorite, Edit, Share, More.
- Added More menu actions for group/tag/donation/address/map/duplicate/delete placeholders.
- Wired Share, Copy Address, Share Address, and Open Map actions.
- Edit was placeholder-only in this build and is implemented in v36.

## v33 - 1.2.26-contact-details-layout-favorite-hotfix
- Made Contact Details section cards use full available width.
- Corrected favorite selected/unselected visual state.
- No DB schema change.

## v32 - 1.2.25-contact-details-mirror
- Contact Details now mirrors saved non-relationship Add Contact data.
- Added display for Basic Info, Address, all phone rows, all email rows, Notes, and map action.
- Added storage fields for non-relationship Add Contact data.
- Added safe DB migration from schema 3 to 4 without dropping existing contacts.
- Relationship persistence and reciprocal logic remain planned for Patch 2.


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
