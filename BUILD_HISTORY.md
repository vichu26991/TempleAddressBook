# Build History

## 1.2.36-tags-link-text-ui-polish — versionCode 43

### Scope
Patch 1.5.6 — Tags UI polish for Contact Details and Add/Edit Contact.

### Files changed
- `app/build.gradle.kts`
- `README.md`
- `CHANGELOG.md`
- `BUILD_HISTORY.md`
- `app/src/main/java/com/snuggy/templeaddressbook/ui/contacts/AddContactScreen.kt`
- `app/src/main/java/com/snuggy/templeaddressbook/ui/contacts/ContactDetailsScreen.kt`

### DB impact
- DB schema remains 5.
- No destructive migration.
- Existing contacts/tags preserved.

### Install guidance
- Install over existing app.
- Do not uninstall.

### Test focus
1. Open Contact Details for a contact with tags.
2. Verify the Tags section uses link-style tag text rows.
3. Verify there is no left icon, right arrow, or boxed submenu row for tags.
4. Tap a tag row and verify that tag detail opens in Manage Tags.
5. Edit a contact with more than 3 tags and verify the selected tag list is compact and expandable.
6. Verify available tag rows are compact filter-style rows with right-side tick marks.
