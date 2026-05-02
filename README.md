# Temple Address Book

Current build:

```text
Build ID: 1.2.36-tags-link-text-ui-polish
versionCode: 43
DB schema: 5
Install guidance: Install over existing app
Uninstall required: No
```

## Patch 1.5.6 focus

This build is cumulative over Patch 1.5.3, 1.5.4, and 1.5.5.

Included:

- Real DB-backed Manage Tags integration using `tags` and `contact_tags`.
- Manage Tags create / rename / delete / usage count / add-remove contacts.
- Add/Edit Contact assigns existing tags only.
- Contact Details header uses compact horizontal-scroll tag chips.
- Contact Details Tags section now uses clean link-style tag text rows.
- No left label icon, no right arrow, and no boxed submenu rows in Contact Details Tags section.
- Tapping a Contact Details tag text opens that tag's Manage Tags detail page.
- Add/Edit Contact selected tags are listed in a compact read-only list panel with `+N more` / `Show less`.
- Add/Edit Contact available tags use compact filter-style rows with right-side ticks.

## Test focus

1. Open a contact with assigned tags.
2. Scroll to the Contact Details Tags section.
3. Verify tags look like clean link text rows, not card rows.
4. Tap a tag and confirm its Manage Tags detail opens.
5. Edit a contact with many tags and verify the selected tag summary is compact and expandable.
6. Verify available tags are compact rows with ticks, not large green boxes.
