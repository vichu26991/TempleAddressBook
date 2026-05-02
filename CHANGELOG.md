# Changelog

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
