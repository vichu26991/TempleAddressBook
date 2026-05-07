# Changelog

## 1.2.43-gesture-photo-crop-editor — versionCode 50

### Changed
- Removed the outdated on-screen `+` / `−` zoom buttons and `○+` / `○−` crop-size controls from the photo crop editor.
- Photo adjustment now uses modern touch behavior: drag to reposition and pinch to zoom.
- The crop editor keeps the full selected photo, circular crop overlay, dimmed outside area, corner handles, Cancel, and Done.
- Works from both Add Contact and Edit Contact.

### DB impact
- No DB schema change.

### Install guidance
- Install over existing app.
- No uninstall required.

## 1.2.42-full-photo-crop-editor-corrected — versionCode 49

### Fixed
- Corrected the photo editor to match the shared reference behavior: full selected image behind a circular contact-photo crop overlay.
- Removed the misleading contact-icon-only editor approach.
- The crop workspace now uses the full screen area with black background, dimmed outside-circle area, circular guide, and corner handles.
- `Done` now saves a real cropped contact-photo image from the selected circular area instead of only saving transform values.
- Works from both Add Contact and Edit Contact.

### Controls
- Drag the image to reposition.
- Pinch or use `+` / `−` to zoom.
- Use `○+` / `○−` to adjust the crop guide size when needed.
- `Cancel` exits without applying the image.
- `Done` applies the crop and returns to Add/Edit Contact.

### DB impact
- No DB schema change.

### Install guidance
- Install over existing app.
- No uninstall required.

## 1.2.41-full-photo-crop-editor — versionCode 48

### Changed
- Corrected the photo editor behavior to show the **full selected photo** with a circular contact-icon crop guide.
- User can drag or pinch the full image while seeing which part will be saved inside the contact photo circle.
- The saved result remains the circular contact photo and works in both Add Contact and Edit Contact.
- Kept `+` / `−` zoom buttons, Reset, Cancel, Done, and Use as contact photo actions.

### DB impact
- No DB schema change.

### Install guidance
- Install over existing app.
- No uninstall required.

## 1.2.40-contact-icon-photo-editor — versionCode 47

### Changed
- Reworked the Add/Edit Contact photo adjustment flow into a contact-icon focused edit screen.
- After **Take Photo** or **Choose from Gallery**, the editor opens immediately before the image is applied.
- The editor shows a circular contact-icon crop preview only, not a full-screen photo preview.
- Added drag, pinch zoom, `+` / `−` zoom buttons, Reset, Cancel, Done, and Use as contact photo actions.
- Confirmed crop still reflects across Add/Edit Contact, Contacts list, and Contact Details.

### DB impact
- No DB schema change.

### Install guidance
- Install over existing app.
- No uninstall required.

## 1.2.39-photo-filter-ui-polish — versionCode 46

### Changed
- Photo selection/capture now opens a full edit screen for crop/reposition before applying the image.
- Contacts filter search fields now use compact BasicTextField styling closer to Add/Edit Contact tag search height.
- Contacts filter option lists now use cleaner boxed rows with dividers and right-side ticks.
- Country/state/district/village filter matching now normalizes saved value variants, such as `Tamil Nadu`, `tamilnadu`, and `TamilNadu`.
- Contact Details Basic Info icons were refreshed.
- Removed the bottom Save bar from Add/Edit Contact; top Save remains.

### DB impact
- No DB schema change.

### Install guidance
- Install over existing app.
- No uninstall required.

## 1.2.38-dropdown-ui-polish — versionCode 45

### Changed
- Removed the tag/label-style icon from Contact Details relationship rows.
- Polished compact dropdown menu visuals for Rasi, Nakshatra, phone label, email label, relationship type, and country selector.
- Dropdown options now use cleaner boxed rows with selected tick styling, matching the Tags UI direction.

### DB impact
- No DB schema change.

## 1.2.37-relationship-db-display — versionCode 44

### Added
- Added DB-backed relationship persistence using `contact_relationships`.
- Add/Edit Contact relationship rows are now saved with the contact.
- Edit Contact now restores previously saved direct relationship rows.
- Contact Details now shows a Relationships section.
- Added reciprocal display for clear family/symmetric relationships.
- Added contextual reverse display for helper/staff relationships, such as `Vishwa’s Driver`.

### DB impact
- Schema changes from 5 to 6.
- Safe migration creates `contact_relationships` only.
- Existing contacts and tags are not wiped.

### Install guidance
- Install over existing app.
- No uninstall required.

## 1.2.36-tags-link-text-ui-polish — versionCode 43

### Important
- This patch is cumulative. If `TempleAddressBook_patch1_5_3_manage_tags_real_db_ui_fix`, `TempleAddressBook_patch1_5_4_manage_tags_final_polish`, or `TempleAddressBook_patch1_5_5_manage_tags_tag_link_polish` was not used, install this latest patch directly.

### Fixed / Improved
- Contact Details → Tags section now uses clean link-style tag text rows.
- Removed the left tag icon from Contact Details tag rows.
- Removed the right arrow from Contact Details tag rows.
- Removed boxed submenu-style rows from Contact Details tag list.
- Tapping a tag text row in Contact Details still opens that tag's Manage Tags detail page.
- Add/Edit Contact selected tags now display inside a compact read-only list panel instead of loose text.
- Add/Edit Contact available tags now display as compact filter-style rows with dividers and right-side tick marks.
- Reduced oversized selected/available tag visual weight in Add/Edit Contact.

### Integration
- Carries forward Patch 1.5.3 real DB-backed tag integration.
- Carries forward Patch 1.5.4 Manage Tags visual polish.
- Carries forward Patch 1.5.5 Contact Details tag navigation.
- Uses `tags` as the tag master table.
- Uses `contact_tags` as the contact-tag mapping table.

### DB impact
- Schema remains 5.
- Install over existing app.
- No uninstall required.
