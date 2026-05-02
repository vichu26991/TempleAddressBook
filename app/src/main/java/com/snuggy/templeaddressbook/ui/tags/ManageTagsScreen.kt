package com.snuggy.templeaddressbook.ui.tags

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.snuggy.templeaddressbook.ui.contacts.ContactDetailsScreen
import com.snuggy.templeaddressbook.ui.contacts.AddContactScreen
import com.snuggy.templeaddressbook.ui.contacts.ContactRecord
import com.snuggy.templeaddressbook.ui.contacts.ContactsRepository
import com.snuggy.templeaddressbook.ui.contacts.TagRecord
import com.snuggy.templeaddressbook.ui.theme.AppBg
import com.snuggy.templeaddressbook.ui.theme.CardBorder
import com.snuggy.templeaddressbook.ui.theme.CardWhite
import com.snuggy.templeaddressbook.ui.theme.MutedText
import com.snuggy.templeaddressbook.ui.theme.OrangePrimary
import com.snuggy.templeaddressbook.ui.theme.SearchBg
import com.snuggy.templeaddressbook.ui.theme.SuccessGreen
import kotlinx.coroutines.launch

@Composable
fun ManageTagsScreen(
    selectedLanguage: String,
    onLanguageChange: (String) -> Unit = {},
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    initialTagName: String? = null,
    onInitialTagConsumed: () -> Unit = {}
) {
    val context = LocalContext.current
    val repository = remember { ContactsRepository(context) }
    val scope = rememberCoroutineScope()
    var tags by remember { mutableStateOf<List<TagRecord>>(emptyList()) }
    var contacts by remember { mutableStateOf<List<ContactRecord>>(emptyList()) }
    var selectedTagId by rememberSaveable { mutableStateOf<Long?>(null) }
    var selectedContactId by rememberSaveable { mutableStateOf<Long?>(null) }
    var editingSelectedContact by rememberSaveable { mutableStateOf(false) }
    var addContactsMode by rememberSaveable { mutableStateOf(false) }
    var searchMode by rememberSaveable { mutableStateOf(false) }
    var query by rememberSaveable { mutableStateOf("") }
    var showCreateDialog by rememberSaveable { mutableStateOf(false) }
    var renamingTag by remember { mutableStateOf<TagRecord?>(null) }
    var deletingTag by remember { mutableStateOf<TagRecord?>(null) }
    var actionTag by remember { mutableStateOf<TagRecord?>(null) }

    fun t(en: String, ta: String): String = if (selectedLanguage == "TA") ta else en

    suspend fun refreshData() {
        contacts = repository.getContacts()
        tags = repository.getTags()
    }

    LaunchedEffect(Unit) { refreshData() }

    fun openTagByName(tagName: String) {
        val target = tags.firstOrNull { it.name.equals(tagName.trim(), ignoreCase = true) }
        if (target != null) {
            selectedContactId = null
            editingSelectedContact = false
            addContactsMode = false
            selectedTagId = target.id
            searchMode = false
            query = ""
        }
    }

    LaunchedEffect(initialTagName, tags) {
        val requestedTag = initialTagName?.trim().orEmpty()
        if (requestedTag.isNotBlank() && tags.isNotEmpty()) {
            openTagByName(requestedTag)
            onInitialTagConsumed()
        }
    }

    val selectedTag = tags.firstOrNull { it.id == selectedTagId }
    val selectedContact = contacts.firstOrNull { it.id == selectedContactId }

    BackHandler {
        when {
            selectedContactId != null && editingSelectedContact -> editingSelectedContact = false
            selectedContactId != null -> selectedContactId = null
            addContactsMode -> addContactsMode = false
            selectedTagId != null -> {
                selectedTagId = null
                searchMode = false
                query = ""
            }
            searchMode -> {
                searchMode = false
                query = ""
            }
            else -> onBack()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(AppBg)
    ) {
        if (selectedContact != null) {
            if (editingSelectedContact) {
                AddContactScreen(
                    selectedLanguage = selectedLanguage,
                    onLanguageChange = onLanguageChange,
                    onBack = { editingSelectedContact = false },
                    onSave = { draft ->
                        scope.launch {
                            repository.updateContact(selectedContact.id, draft)
                            refreshData()
                            editingSelectedContact = false
                        }
                    },
                    availableTags = tags.map { it.name },
                    relationshipContactOptions = contacts
                        .filter { it.id != selectedContact.id }
                        .map { it.fullName }
                        .filter { it.isNotBlank() },
                    editingContact = selectedContact
                )
            } else {
                ContactDetailsScreen(
                    contact = selectedContact,
                    selectedLanguage = selectedLanguage,
                    onBack = { selectedContactId = null },
                    onToggleFavorite = {
                        scope.launch {
                            repository.toggleFavorite(selectedContact.id, !selectedContact.isFavorite)
                            refreshData()
                        }
                    },
                    onEdit = { editingSelectedContact = true },
                    onOpenTag = { tagName -> openTagByName(tagName) }
                )
            }
        } else {
        Column(modifier = Modifier.fillMaxSize()) {
            TagsHeader(
                title = when {
                    addContactsMode -> t("Add Contacts", "தொடர்புகளைச் சேர்")
                    selectedTag != null -> selectedTag.name
                    else -> t("Manage Tags", "குறிச்சொற்கள் நிர்வாகம்")
                },
                searchMode = searchMode,
                searchPlaceholder = when {
                    addContactsMode -> t("Search contacts", "தொடர்புகளைத் தேடுக")
                    selectedTag != null -> t("Search contacts", "தொடர்புகளைத் தேடுக")
                    else -> t("Search tags", "குறிச்சொற்களைத் தேடுக")
                },
                query = query,
                onQueryChange = { query = it },
                onBack = {
                    when {
                        addContactsMode -> addContactsMode = false
                        selectedTagId != null -> {
                            selectedTagId = null
                            searchMode = false
                            query = ""
                        }
                        searchMode -> {
                            searchMode = false
                            query = ""
                        }
                        else -> onBack()
                    }
                },
                onSearchClick = { searchMode = true },
                onClearSearch = { query = "" }
            )

            when {
                addContactsMode && selectedTag != null -> AddContactsToTagContent(
                    tag = selectedTag,
                    contacts = contacts,
                    query = query,
                    selectedLanguage = selectedLanguage,
                    onSave = { ids ->
                        scope.launch {
                            repository.addContactsToTag(selectedTag.id, ids)
                            refreshData()
                            addContactsMode = false
                            query = ""
                        }
                    }
                )

                selectedTag != null -> TagDetailContent(
                    tag = selectedTag,
                    contacts = contacts,
                    query = query,
                    selectedLanguage = selectedLanguage,
                    onAddContacts = {
                        addContactsMode = true
                        searchMode = true
                        query = ""
                    },
                    onRemoveContact = { contactId ->
                        scope.launch {
                            repository.removeContactFromTag(contactId, selectedTag.id)
                            refreshData()
                        }
                    },
                    onOpenContact = { contactId ->
                        editingSelectedContact = false
                        selectedContactId = contactId
                    }
                )

                else -> ManageTagsListContent(
                    tags = tags.filter { query.isBlank() || it.name.contains(query, ignoreCase = true) },
                    selectedLanguage = selectedLanguage,
                    hasQuery = query.isNotBlank(),
                    onOpenTag = { selectedTagId = it.id },
                    onRename = { renamingTag = it },
                    onDelete = { deletingTag = it },
                    onMore = { actionTag = it }
                )
            }
        }
        }

        if (selectedContact == null && selectedTagId == null && !addContactsMode) {
            Surface(
                onClick = { showCreateDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(18.dp),
                shape = RoundedCornerShape(22.dp),
                color = SuccessGreen,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(7.dp)
                ) {
                    Icon(Icons.Outlined.Add, contentDescription = null, tint = Color.White)
                    Text(t("Create Tag", "உருவாக்கு"), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }

    if (showCreateDialog) {
        TagNameDialog(
            title = t("Create Tag", "குறிச்சொல் உருவாக்கு"),
            selectedLanguage = selectedLanguage,
            initialValue = "",
            existingNames = tags.map { it.name },
            onDismiss = { showCreateDialog = false },
            onSave = { name ->
                scope.launch {
                    repository.createTag(name)
                    refreshData()
                    showCreateDialog = false
                }
            }
        )
    }

    actionTag?.let { tag ->
        TagActionDialog(
            tag = tag,
            selectedLanguage = selectedLanguage,
            onDismiss = { actionTag = null },
            onRename = {
                actionTag = null
                renamingTag = tag
            },
            onDelete = {
                actionTag = null
                deletingTag = tag
            }
        )
    }

    renamingTag?.let { tag ->
        TagNameDialog(
            title = t("Rename Tag", "குறிச்சொல் பெயர் மாற்று"),
            selectedLanguage = selectedLanguage,
            initialValue = tag.name,
            existingNames = tags.filter { it.id != tag.id }.map { it.name },
            onDismiss = { renamingTag = null },
            onSave = { name ->
                scope.launch {
                    repository.renameTag(tag.id, name)
                    refreshData()
                    renamingTag = null
                }
            }
        )
    }

    deletingTag?.let { tag ->
        AlertDialog(
            onDismissRequest = { deletingTag = null },
            title = { Text(t("Delete Tag", "குறிச்சொல்லை நீக்கு"), fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    if (tag.usageCount > 0) {
                        t(
                            "This tag is used in ${tag.usageCount} contacts. Deleting it will remove this tag from those contacts. Contacts will not be deleted.",
                            "இந்த குறிச்சொல் ${tag.usageCount} தொடர்புகளில் உள்ளது. இதை நீக்கினால் அந்த தொடர்புகளில் இருந்து மட்டும் குறிச்சொல் நீக்கப்படும். தொடர்புகள் நீக்கப்படாது."
                        )
                    } else {
                        t("Delete this tag?", "இந்த குறிச்சொல்லை நீக்கவா?")
                    }
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        scope.launch {
                            repository.deleteTag(tag.id)
                            refreshData()
                            deletingTag = null
                        }
                    }
                ) { Text(t("Delete", "நீக்கு"), color = Color(0xFFC62828), fontWeight = FontWeight.Bold) }
            },
            dismissButton = { TextButton(onClick = { deletingTag = null }) { Text(t("Cancel", "ரத்து")) } }
        )
    }
}

@Composable
private fun TagsHeader(
    title: String,
    searchMode: Boolean,
    searchPlaceholder: String,
    query: String,
    onQueryChange: (String) -> Unit,
    onBack: () -> Unit,
    onSearchClick: () -> Unit,
    onClearSearch: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = OrangePrimary,
        shadowElevation = 2.dp
    ) {
        if (searchMode) {
            val focusRequester = remember { FocusRequester() }
            val keyboardController = LocalSoftwareKeyboardController.current

            LaunchedEffect(searchMode) {
                focusRequester.requestFocus()
                keyboardController?.show()
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.size(40.dp)) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                OutlinedTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(focusRequester),
                    placeholder = { Text(searchPlaceholder, color = Color.White.copy(alpha = 0.82f)) },
                    singleLine = true,
                    trailingIcon = {
                        if (query.isNotBlank()) {
                            IconButton(onClick = onClearSearch, modifier = Modifier.size(36.dp)) {
                                Icon(Icons.Outlined.Close, contentDescription = "Clear", tint = Color.White)
                            }
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White
                    )
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.size(40.dp)) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                val longTitle = title.length > 18
                Text(
                    text = title,
                    color = Color.White,
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = when {
                        title.length > 42 -> 13.sp
                        title.length > 30 -> 14.sp
                        longTitle -> 16.sp
                        else -> 22.sp
                    },
                    lineHeight = when {
                        title.length > 42 -> 16.sp
                        title.length > 30 -> 17.sp
                        longTitle -> 19.sp
                        else -> 26.sp
                    },
                    fontWeight = FontWeight.Bold,
                    maxLines = if (longTitle) 2 else 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onSearchClick, modifier = Modifier.size(40.dp)) {
                    Icon(Icons.Outlined.Search, contentDescription = "Search", tint = Color.White)
                }
            }
        }
    }
}

@Composable
private fun ManageTagsListContent(
    tags: List<TagRecord>,
    selectedLanguage: String,
    hasQuery: Boolean,
    onOpenTag: (TagRecord) -> Unit,
    onRename: (TagRecord) -> Unit,
    onDelete: (TagRecord) -> Unit,
    onMore: (TagRecord) -> Unit
) {
    fun t(en: String, ta: String): String = if (selectedLanguage == "TA") ta else en
    if (tags.isEmpty()) {
        EmptyTagsState(
            title = if (hasQuery) t("No matching tags", "பொருந்தும் குறிச்சொற்கள் இல்லை") else t("No tags yet", "குறிச்சொற்கள் இல்லை"),
            body = if (hasQuery) t("Try another search.", "வேறு தேடலை முயற்சிக்கவும்.") else t("Create tags to classify contacts.", "தொடர்புகளை வகைப்படுத்த குறிச்சொற்களை உருவாக்கவும்.")
        )
        return
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 12.dp, end = 12.dp, top = 4.dp, bottom = 82.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        items(tags, key = { it.id }) { tag ->
            TagListRow(
                tag = tag,
                contactsText = if (tag.usageCount == 1) t("1 contact", "1 தொடர்பு") else t("${tag.usageCount} contacts", "${tag.usageCount} தொடர்புகள்"),
                renameText = t("Rename Tag", "குறிச்சொல் பெயர் மாற்று"),
                deleteText = t("Delete Tag", "குறிச்சொல்லை நீக்கு"),
                onOpen = { onOpenTag(tag) },
                onRename = { onRename(tag) },
                onDelete = { onDelete(tag) },
                onMore = { onMore(tag) }
            )
        }
    }
}

@Composable
private fun TagListRow(
    tag: TagRecord,
    contactsText: String,
    renameText: String,
    deleteText: String,
    onOpen: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit,
    onMore: () -> Unit
) {
    Surface(
        onClick = onOpen,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
        color = CardWhite,
        border = BorderStroke(1.dp, CardBorder),
        shadowElevation = 1.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TagAvatar(tag.name)
            Spacer(Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tag.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    lineHeight = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = contactsText,
                    color = MutedText,
                    fontSize = 10.5.sp,
                    lineHeight = 13.sp,
                    modifier = Modifier.padding(top = 1.dp)
                )
            }
            IconButton(onClick = onMore, modifier = Modifier.size(32.dp)) {
                Icon(Icons.Outlined.MoreVert, contentDescription = "More", tint = MutedText, modifier = Modifier.size(20.dp))
            }
        }
    }
}


@Composable
private fun TagDetailContent(
    tag: TagRecord,
    contacts: List<ContactRecord>,
    query: String,
    selectedLanguage: String,
    onAddContacts: () -> Unit,
    onRemoveContact: (Long) -> Unit,
    onOpenContact: (Long) -> Unit
) {
    fun t(en: String, ta: String): String = if (selectedLanguage == "TA") ta else en
    val taggedContacts = contacts
        .filter { contact -> contact.tags.any { it.equals(tag.name, ignoreCase = true) } }
        .filter { query.isBlank() || it.fullName.contains(query, ignoreCase = true) || it.primaryPhone.contains(query, ignoreCase = true) }
        .sortedBy { it.fullName.lowercase() }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = if (taggedContacts.size == 1) t("1 contact", "1 தொடர்பு") else t("${taggedContacts.size} contacts", "${taggedContacts.size} தொடர்புகள்"),
            color = MutedText,
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.5.sp,
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 3.dp)
        )
        if (taggedContacts.isEmpty()) {
            EmptyTagsState(
                title = t("No contacts found", "தொடர்புகள் இல்லை"),
                body = t("Add contacts to this tag.", "இந்த குறிச்சொல்லில் தொடர்புகளைச் சேர்க்கவும்."),
                modifier = Modifier.weight(1f)
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 2.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(taggedContacts, key = { it.id }) { contact ->
                    TagContactRow(
                        contact = contact,
                        selectedLanguage = selectedLanguage,
                        actionText = t("Remove", "நீக்கு"),
                        onOpen = { onOpenContact(contact.id) },
                        onAction = { onRemoveContact(contact.id) }
                    )
                }
            }
        }
        Surface(
            onClick = onAddContacts,
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            shape = RoundedCornerShape(18.dp),
            color = SuccessGreen
        ) {
            Row(
                modifier = Modifier.padding(vertical = 12.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.Add, contentDescription = null, tint = Color.White)
                Spacer(Modifier.width(6.dp))
                Text(t("Add Contacts", "தொடர்புகளைச் சேர்"), color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun AddContactsToTagContent(
    tag: TagRecord,
    contacts: List<ContactRecord>,
    query: String,
    selectedLanguage: String,
    onSave: (List<Long>) -> Unit
) {
    fun t(en: String, ta: String): String = if (selectedLanguage == "TA") ta else en
    var selectedIds by remember(tag.id) { mutableStateOf<Set<Long>>(emptySet()) }
    val availableContacts = contacts
        .filterNot { contact -> contact.tags.any { it.equals(tag.name, ignoreCase = true) } }
        .filter { query.isBlank() || it.fullName.contains(query, ignoreCase = true) || it.primaryPhone.contains(query, ignoreCase = true) }
        .sortedBy { it.fullName.lowercase() }

    Column(modifier = Modifier.fillMaxSize()) {
        if (availableContacts.isEmpty()) {
            EmptyTagsState(
                title = t("No contacts found", "தொடர்புகள் இல்லை"),
                body = t("All matching contacts are already linked or no contacts match this search.", "பொருந்தும் தொடர்புகள் ஏற்கனவே சேர்க்கப்பட்டுள்ளன அல்லது தேடலுக்கு தொடர்புகள் இல்லை."),
                modifier = Modifier.weight(1f)
            )
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(availableContacts, key = { it.id }) { contact ->
                    val selected = contact.id in selectedIds
                    Surface(
                        onClick = {
                            selectedIds = if (selected) selectedIds - contact.id else selectedIds + contact.id
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        color = if (selected) SuccessGreen.copy(alpha = 0.10f) else CardWhite,
                        border = BorderStroke(1.dp, if (selected) SuccessGreen.copy(alpha = 0.32f) else CardBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ContactMiniAvatar(contact)
                            Spacer(Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(contact.fullName, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(contact.primaryPhone, color = MutedText, fontSize = 13.sp)
                            }
                            if (selected) Icon(Icons.Outlined.Check, contentDescription = null, tint = SuccessGreen)
                        }
                    }
                }
            }
        }
        Button(
            onClick = { onSave(selectedIds.toList()) },
            enabled = selectedIds.isNotEmpty(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            shape = RoundedCornerShape(18.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
        ) {
            Text(t("Add Selected", "தேர்ந்தவற்றைச் சேர்"), fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun TagContactRow(
    contact: ContactRecord,
    selectedLanguage: String,
    actionText: String,
    onOpen: () -> Unit,
    onAction: () -> Unit
) {
    Surface(
        onClick = onOpen,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(15.dp),
        color = CardWhite,
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ContactMiniAvatar(contact)
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(contact.fullName, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(
                    listOf(contact.primaryPhone, contact.locationLine).filter { it.isNotBlank() }.joinToString(" • "),
                    color = MutedText,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            TextButton(onClick = onAction) { Text(actionText, color = Color(0xFFC62828), fontWeight = FontWeight.SemiBold) }
        }
    }
}


@Composable
private fun TagActionDialog(
    tag: TagRecord,
    selectedLanguage: String,
    onDismiss: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit
) {
    fun t(en: String, ta: String): String = if (selectedLanguage == "TA") ta else en
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = CardWhite,
            shadowElevation = 12.dp,
            border = BorderStroke(1.dp, CardBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = tag.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    lineHeight = 19.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                PremiumTagActionRow(
                    label = t("Rename Tag", "குறிச்சொல் பெயர் மாற்று"),
                    icon = { Icon(Icons.Outlined.Edit, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(19.dp)) },
                    onClick = onRename
                )
                HorizontalDivider(color = CardBorder.copy(alpha = 0.7f))
                PremiumTagActionRow(
                    label = t("Delete Tag", "குறிச்சொல்லை நீக்கு"),
                    icon = { Icon(Icons.Outlined.DeleteOutline, contentDescription = null, tint = Color(0xFFC62828), modifier = Modifier.size(19.dp)) },
                    contentColor = Color(0xFFC62828),
                    onClick = onDelete
                )
            }
        }
    }
}

@Composable
private fun PremiumTagActionRow(
    label: String,
    icon: @Composable () -> Unit,
    contentColor: Color = Color(0xFF252525),
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = SearchBg.copy(alpha = 0.55f)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 13.dp, vertical = 11.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            icon()
            Text(label, color = contentColor, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun TagNameDialog(
    title: String,
    selectedLanguage: String,
    initialValue: String,
    existingNames: List<String>,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    fun t(en: String, ta: String): String = if (selectedLanguage == "TA") ta else en
    var value by rememberSaveable(initialValue) { mutableStateOf(initialValue) }
    val trimmed = value.trim()
    val duplicate = trimmed.isNotBlank() && existingNames.any { it.equals(trimmed, ignoreCase = true) }
    val canSave = trimmed.isNotBlank() && !duplicate
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = value,
                    onValueChange = { value = it },
                    label = { Text(t("Tag name", "குறிச்சொல் பெயர்")) },
                    singleLine = true,
                    isError = duplicate,
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = OrangePrimary,
                        cursorColor = OrangePrimary
                    )
                )
                if (duplicate) Text(t("Tag already exists", "குறிச்சொல் ஏற்கனவே உள்ளது"), color = Color(0xFFC62828), fontSize = 12.sp)
            }
        },
        confirmButton = {
            TextButton(enabled = canSave, onClick = { onSave(trimmed) }) { Text(t("Save", "சேமி"), fontWeight = FontWeight.Bold) }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(t("Cancel", "ரத்து")) } }
    )
}

@Composable
private fun EmptyTagsState(title: String, body: String, modifier: Modifier = Modifier.fillMaxSize()) {
    Box(modifier = modifier.fillMaxSize().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(shape = CircleShape, color = OrangePrimary.copy(alpha = 0.10f)) {
                Icon(Icons.Outlined.Search, contentDescription = null, tint = OrangePrimary, modifier = Modifier.padding(16.dp).size(30.dp))
            }
            Text(title, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(top = 12.dp), textAlign = TextAlign.Center)
            Text(body, color = MutedText, modifier = Modifier.padding(top = 5.dp), lineHeight = 18.sp, fontSize = 13.sp, textAlign = TextAlign.Center)
        }
    }
}

@Composable
private fun TagAvatar(name: String) {
    val colors = gradientForName(name)
    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(Brush.linearGradient(colors)),
        contentAlignment = Alignment.Center
    ) {
        Text(name.take(1).uppercase().ifBlank { "#" }, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

@Composable
private fun ContactMiniAvatar(contact: ContactRecord) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(Brush.linearGradient(gradientForName(contact.fullName))),
        contentAlignment = Alignment.Center
    ) {
        Text(contact.initials.take(2), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
    }
}

private fun gradientForName(name: String): List<Color> {
    val palettes = listOf(
        listOf(Color(0xFFA9C4FF), Color(0xFF7E96E8)),
        listOf(Color(0xFFD4B4FF), Color(0xFFB483EE)),
        listOf(Color(0xFF8BE1C5), Color(0xFF6CC7AE)),
        listOf(Color(0xFFFFC29C), Color(0xFFEE9A67)),
        listOf(Color(0xFFF8B6C4), Color(0xFFE58FA4))
    )
    val index = kotlin.math.abs(name.hashCode()) % palettes.size
    return palettes[index]
}
