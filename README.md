# Temple Address Book

Current build:

```text
Build ID: 1.2.38-dropdown-ui-polish
versionCode: 45
DB schema: 6
Install guidance: Install over existing app
Uninstall required: No
```

## Patch 1.6 focus

This build adds real relationship persistence and Contact Details display.

Included:

- New `contact_relationships` table.
- Add/Edit Contact relationship rows are saved to DB.
- Existing saved relationship rows are restored when editing a contact.
- Contact Details shows direct relationships.
- Contact Details shows reciprocal family/symmetric relationships.
- Contact Details shows contextual reverse helper/staff relationships such as `Vishwa’s Driver`.
- Existing contacts, tags, and contact-tag mappings are preserved.

## Test focus

1. Add a contact relationship using an existing saved contact.
2. Add a reference-only relationship name.
3. Save and open Contact Details.
4. Verify direct relationship rows appear.
5. Open the related contact and verify reciprocal/context relationship appears.
6. Edit the original contact and verify relationship rows are prefilled.
7. Retest Tags, Phone, Email, Address, Add Contact save, and Edit Contact save.
