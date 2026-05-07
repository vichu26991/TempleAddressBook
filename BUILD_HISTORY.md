# Build History

## 1.2.43-gesture-photo-crop-editor — versionCode 50

### Scope
Patch 1.6.6 — Modern gesture-only Add/Edit Contact photo crop editor.

### Files changed
- `app/build.gradle.kts`
- `README.md`
- `CHANGELOG.md`
- `BUILD_HISTORY.md`
- `app/src/main/java/com/snuggy/templeaddressbook/ui/contacts/AddContactScreen.kt`

### What changed
- Removed the on-screen `+`, `−`, `○+`, and `○−` controls from the photo crop editor.
- Kept the full-photo crop screen with circular overlay, dimmed outside area, and corner handles.
- Image adjustment is now done through touch gestures: drag to reposition and pinch to zoom.
- Bottom actions now match the reference-style flow: `Cancel` and `Done` only.
- Same behavior applies in Add Contact and Edit Contact.

### DB impact
- DB schema remains 6.
- No destructive migration.
- Existing contacts, tags, and relationships are preserved.

### Install guidance
- Install over existing app.
- Do not uninstall.

### Test focus
1. Add Contact → Add Photo → Gallery.
2. Confirm the editor shows full image + circular crop overlay.
3. Confirm there are no `+`, `−`, `○+`, or `○−` controls.
4. Drag the image to reposition.
5. Pinch to zoom in/out.
6. Tap Cancel and verify the previous photo is unchanged.
7. Repeat and tap Done; verify the cropped image appears in Add/Edit Contact, Contacts list, and Contact Details.
8. Open an existing contact → Edit → Change Photo and verify the same flow works.

## 1.2.42-full-photo-crop-editor-corrected — versionCode 49

### Scope
Patch 1.6.5 — Corrected Add/Edit Contact photo crop editor.

### Files changed
- `app/build.gradle.kts`
- `README.md`
- `CHANGELOG.md`
- `BUILD_HISTORY.md`
- `app/src/main/java/com/snuggy/templeaddressbook/ui/contacts/AddContactScreen.kt`

### What changed
- Rebuilt the photo adjust screen into a full-photo crop workspace matching the reference screenshots.
- Shows the full selected/captured photo with a circular crop overlay, dimmed outside area, and crop corner handles.
- Allows image drag, pinch zoom, `+` / `−` zoom buttons, and crop guide size adjustment using `○+` / `○−`.
- `Done` now writes a cropped contact-photo image to cache and applies that image to the contact preview.
- Same flow is used for both Add Contact and Edit Contact.

### DB impact
- DB schema remains 6.
- No destructive migration.
- Existing contacts, tags, and relationships are preserved.

### Install guidance
- Install over existing app.
- Do not uninstall.

### Test focus
1. Add Contact → Add Photo → Gallery.
2. Confirm full image opens with circular crop overlay, not a tiny circular preview.
3. Drag, pinch zoom, use `+` / `−`, and use `○+` / `○−`.
4. Tap Cancel and verify the existing photo is unchanged.
5. Repeat and tap Done; verify the cropped image appears in Add/Edit Contact.
6. Save and verify Contacts list and Contact Details show the same cropped photo.
7. Open an existing contact → Edit → Change Photo and verify the same flow works.

## 1.2.41-full-photo-crop-editor — versionCode 48

### Scope
Patch 1.6.4 — Correct the photo editor to crop from the full selected image, not from a circular-only preview.

### Files changed
- `app/build.gradle.kts`
- `README.md`
- `CHANGELOG.md`
- `BUILD_HISTORY.md`
- `app/src/main/java/com/snuggy/templeaddressbook/ui/contacts/AddContactScreen.kt`

### DB impact
- DB schema remains 6.
- No destructive migration.
- Existing contacts, tags, and relationships are preserved.

### Install guidance
- Install over existing app.
- Do not uninstall.

### Test focus
1. Add Contact → Add Photo → Take Photo / Choose from Gallery.
2. Edit Contact → Change Photo → Take Photo / Choose from Gallery.
3. Confirm the editor shows the full selected image with a circular crop guide.
4. Confirm the saved result remains the circular contact photo.
5. Test drag, pinch zoom, `+`, `−`, Reset, Cancel, Done, and Use as contact photo.
6. Save and verify the crop appears in Add/Edit Contact, Contacts list, and Contact Details.

## 1.2.40-contact-icon-photo-editor — versionCode 47

### Scope
Patch 1.6.3 — Contact icon-focused photo editor after camera/gallery selection.

### Files changed
- `app/build.gradle.kts`
- `README.md`
- `CHANGELOG.md`
- `BUILD_HISTORY.md`
- `app/src/main/java/com/snuggy/templeaddressbook/ui/contacts/AddContactScreen.kt`

### DB impact
- DB schema remains 6.
- No destructive migration.
- Existing contacts, tags, and relationships are preserved.

### Install guidance
- Install over existing app.
- Do not uninstall.

### Test focus
1. Add/Edit Contact → Add/Change Photo.
2. Test both Take Photo and Choose from Gallery.
3. Confirm the contact-icon edit screen opens immediately after selecting/capturing the photo.
4. Confirm the editor shows only the circular contact icon crop area, not a full-screen image preview.
5. Test drag, pinch zoom, `+`, `−`, Reset, Cancel, Done, and Use as contact photo.
6. Save and verify the selected crop appears in Add/Edit Contact, Contacts list, and Contact Details.

## 1.2.39-photo-filter-ui-polish — versionCode 46

### Scope
Patch 1.6.2 — Photo edit screen, Contacts filter UI polish, Contact Details icon cleanup, and Add/Edit bottom Save removal.

### Files changed
- `app/build.gradle.kts`
- `README.md`
- `CHANGELOG.md`
- `BUILD_HISTORY.md`
- `app/src/main/java/com/snuggy/templeaddressbook/ui/contacts/AddContactScreen.kt`
- `app/src/main/java/com/snuggy/templeaddressbook/ui/contacts/ContactDetailsScreen.kt`
- `app/src/main/java/com/snuggy/templeaddressbook/ui/contacts/ContactsScreen.kt`

### DB impact
- DB schema remains 6.
- No destructive migration.
- Existing contacts, tags, and relationships are preserved.

### Install guidance
- Install over existing app.
- Do not uninstall.

### Test focus
1. Select/capture a contact photo and confirm a full edit screen opens before saving the image.
2. Use photo drag/pinch adjustment, Save, Cancel, and Reset.
3. Open Contacts filter and confirm compact search field height and boxed option rows with right-side tick.
4. Verify filter variants like `Tamil Nadu`, `tamilnadu`, and `TamilNadu` behave as one value.
5. Open Contact Details and verify refreshed Basic Info icons.
6. Open Add/Edit Contact and confirm the bottom Save bar is removed while top Save remains.

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
