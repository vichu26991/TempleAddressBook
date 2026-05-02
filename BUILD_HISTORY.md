# Build History

## 1.2.38-dropdown-ui-polish — versionCode 45

### Scope
Patch 1.6.1 — Dropdown UI polish and Contact Details relationship icon cleanup.

### Files changed
- `app/build.gradle.kts`
- `README.md`
- `CHANGELOG.md`
- `BUILD_HISTORY.md`
- `app/src/main/java/com/snuggy/templeaddressbook/ui/contacts/AddContactScreen.kt`
- `app/src/main/java/com/snuggy/templeaddressbook/ui/contacts/ContactDetailsScreen.kt`

### DB impact
- DB schema remains 6.
- No destructive migration.
- Existing contacts, tags, and relationships are preserved.

### Install guidance
- Install over existing app.
- Do not uninstall.

### Test focus
1. Open Contact Details and verify relationship rows have no tag/label icon.
2. Open Rasi and Nakshatra dropdowns and verify the cleaner boxed dropdown option style.
3. Open phone label, email label, and relationship type dropdowns and verify the same visual direction.
4. Save/edit contacts and confirm no behavior regression in relationships, tags, phone, and email.

## 1.2.37-relationship-db-display — versionCode 44

### Scope
Patch 1.6 — Relationship persistence plus Contact Details relationship display.

### Files changed
- `app/build.gradle.kts`
- `README.md`
- `CHANGELOG.md`
- `BUILD_HISTORY.md`
- `app/src/main/java/com/snuggy/templeaddressbook/data/TempleDbHelper.kt`
- `app/src/main/java/com/snuggy/templeaddressbook/ui/contacts/AddContactScreen.kt`
- `app/src/main/java/com/snuggy/templeaddressbook/ui/contacts/ContactDetailsScreen.kt`
- `app/src/main/java/com/snuggy/templeaddressbook/ui/contacts/ContactsModels.kt`

### DB impact
- DB schema changes from 5 to 6.
- Adds `contact_relationships`.
- No destructive migration.
- Existing contacts, tags, and contact-tag mappings are preserved.

### Install guidance
- Install over existing app.
- Do not uninstall.

### Test focus
1. Add a relationship using an existing contact.
2. Add a reference-only relationship name.
3. Save and verify the Relationships section in Contact Details.
4. Open the related contact and verify reciprocal/context reverse display.
5. Edit the original contact and verify relationship rows are prefilled.
6. Retest tags and contact edit/save flow.

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
