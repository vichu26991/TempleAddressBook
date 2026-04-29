# TempleAddressBook

## Latest build
- Build ID: 1.2.29-contact-details-edit-flow
- versionCode: 36
- DB schema: 4
- Install guidance: Install over existing app
- Scope: Contact Details cleanup plus Edit Contact update flow.

Android address book application built in Android Studio.

## Branch
- main

## Current stable tracked build
- versionCode: 36
- versionName: 1.2.29-contact-details-edit-flow
- focus: Contact Details cleanup and full Add/Edit Contact lifecycle

## Current build summary
- Contact Details header is cleaner because the duplicate top favorite star is removed.
- Bottom Contact Details action bar remains:
  - Favorite
  - Edit
  - Share
  - More
- Edit opens the Add/Edit Contact screen with saved values prefilled.
- Save from Edit updates the existing contact instead of creating a duplicate.
- Repository and SQLite update methods are added for existing contacts.
- No DB schema change; database remains schema 4.
- Tracking files include catch-up entries for v33, v34, v35, and v36.

## Not included in this build
- Tags section database implementation
- Relationship persistence
- Reciprocal relationship logic
- Donations database
- Groups or Messages feature work

## Commit format
Use:

```text
v<versionCode>: <short fix title>
```

Examples:
- v33: contact details layout favorite hotfix
- v34: contact details bottom actions
- v35: contact details hide bottom nav
- v36: contact details edit flow

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
