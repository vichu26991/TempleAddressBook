# Changelog

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
