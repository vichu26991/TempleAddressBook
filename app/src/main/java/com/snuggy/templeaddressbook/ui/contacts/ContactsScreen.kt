package com.snuggy.templeaddressbook.ui.contacts

import android.content.Intent
import android.widget.Toast
import android.net.Uri
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.FilterList
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material.icons.automirrored.rounded.Message
import androidx.compose.material.icons.rounded.Star
import androidx.activity.compose.BackHandler
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.snuggy.templeaddressbook.ui.theme.AppBg
import com.snuggy.templeaddressbook.ui.theme.CardBorder
import com.snuggy.templeaddressbook.ui.theme.CardWhite
import com.snuggy.templeaddressbook.ui.theme.FilterBg
import com.snuggy.templeaddressbook.ui.theme.MutedText
import com.snuggy.templeaddressbook.ui.theme.OrangePrimary
import com.snuggy.templeaddressbook.ui.theme.SearchBg
import com.snuggy.templeaddressbook.ui.theme.SuccessGreen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private sealed interface ContactListItem {
    data class Header(val letter: String) : ContactListItem
    data class Row(val contact: ContactRecord) : ContactListItem
}

@Composable
fun ContactsRoot(
    selectedLanguage: String,
    onLanguageChange: (String) -> Unit,
    onBottomBarVisibilityChange: (Boolean) -> Unit,
    onOpenMenu: () -> Unit
) {
    val context = LocalContext.current
    val repository = remember { ContactsRepository(context) }
    val scope = rememberCoroutineScope()

    var screen by rememberSaveable { mutableStateOf("list") }
    var selectedContactId by rememberSaveable { mutableStateOf(-1L) }
    var contacts by remember { mutableStateOf<List<ContactRecord>>(emptyList()) }
    var filterOptions by remember {
        mutableStateOf(ContactFilterOptions(emptyList(), emptyList(), emptyList(), emptyList(), emptyList()))
    }
    var savedListIndex by rememberSaveable { mutableStateOf(0) }
    var savedListOffset by rememberSaveable { mutableStateOf(0) }

    suspend fun refreshData() {
        contacts = repository.getContacts()
        filterOptions = repository.getFilterOptions()
    }

    LaunchedEffect(Unit) { refreshData() }

    when (screen) {
        "add" -> AddContactScreen(
            selectedLanguage = selectedLanguage,
            onLanguageChange = onLanguageChange,
            onBack = { screen = "list" },
            onSave = { draft ->
                scope.launch {
                    repository.addContact(draft)
                    refreshData()
                    screen = "list"
                }
            }
        )

        "edit" -> {
            val editing = contacts.firstOrNull { it.id == selectedContactId }
            if (editing == null) {
                LaunchedEffect(selectedContactId) { screen = "list" }
            } else {
                AddContactScreen(
                    selectedLanguage = selectedLanguage,
                    onLanguageChange = onLanguageChange,
                    onBack = { screen = "details" },
                    onSave = { draft ->
                        scope.launch {
                            repository.updateContact(editing.id, draft)
                            refreshData()
                            selectedContactId = editing.id
                            screen = "details"
                        }
                    },
                    editingContact = editing
                )
            }
        }

        "details" -> {
            val selected = contacts.firstOrNull { it.id == selectedContactId }
            if (selected == null) {
                LaunchedEffect(selectedContactId) { screen = "list" }
            } else {
                LaunchedEffect(selected.id) {
                    onBottomBarVisibilityChange(false)
                }

                ContactDetailsScreen(
                    contact = selected,
                    selectedLanguage = selectedLanguage,
                    onBack = {
                        onBottomBarVisibilityChange(true)
                        screen = "list"
                    },
                    onToggleFavorite = {
                        scope.launch {
                            repository.toggleFavorite(selected.id, !selected.isFavorite)
                            refreshData()
                        }
                    },
                    onEdit = {
                        onBottomBarVisibilityChange(false)
                        screen = "edit"
                    }
                )
            }
        }

        else -> ContactsScreen(
            selectedLanguage = selectedLanguage,
            onLanguageChange = onLanguageChange,
            contacts = contacts,
            filterOptions = filterOptions,
            onAddContact = { screen = "add" },
            onOpenDetails = { id ->
                onBottomBarVisibilityChange(false)
                selectedContactId = id
                screen = "details"
            },
            onToggleFavorite = { id, favorite ->
                scope.launch {
                    repository.toggleFavorite(id, favorite)
                    refreshData()
                }
            },
            onSaveSmartGroup = { name, filters ->
                scope.launch { repository.saveSmartGroup(name, filters) }
            },
            onBottomBarVisibilityChange = onBottomBarVisibilityChange,
            onOpenMenu = onOpenMenu,
            initialListIndex = savedListIndex,
            initialListOffset = savedListOffset,
            onListPositionChange = { index, _ ->
                savedListIndex = index
                savedListOffset = 0
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(
    selectedLanguage: String,
    onLanguageChange: (String) -> Unit,
    contacts: List<ContactRecord>,
    filterOptions: ContactFilterOptions,
    onAddContact: () -> Unit,
    onOpenDetails: (Long) -> Unit,
    onToggleFavorite: (Long, Boolean) -> Unit,
    onSaveSmartGroup: (String, AppliedContactFilters) -> Unit,
    onBottomBarVisibilityChange: (Boolean) -> Unit,
    onOpenMenu: () -> Unit,
    initialListIndex: Int,
    initialListOffset: Int,
    onListPositionChange: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = initialListIndex,
        initialFirstVisibleItemScrollOffset = initialListOffset
    )

    var searchMode by rememberSaveable { mutableStateOf(false) }
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var favoritesOnly by rememberSaveable { mutableStateOf(false) }
    var expandedContactId by rememberSaveable { mutableStateOf<Long?>(null) }
    var appliedFilters by remember { mutableStateOf(AppliedContactFilters()) }
    var draftFilters by remember { mutableStateOf(AppliedContactFilters()) }
    var showFilterSheet by rememberSaveable { mutableStateOf(false) }
    var showSaveDialog by rememberSaveable { mutableStateOf(false) }
    var smartGroupName by rememberSaveable { mutableStateOf("") }
    var filtersExpanded by rememberSaveable { mutableStateOf(false) }
    var activeRailLetter by remember { mutableStateOf<String?>(null) }
    var railInteracting by remember { mutableStateOf(false) }

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex to listState.firstVisibleItemScrollOffset }
            .collect { (index, offset) -> onListPositionChange(index, offset) }
    }

    val filteredContacts by remember(contacts, searchQuery, favoritesOnly, appliedFilters) {
        derivedStateOf {
            contacts.filter { contact ->
                val matchesSearch = searchQuery.isBlank() || listOf(
                    contact.fullName,
                    contact.primaryPhone,
                    contact.villageTown,
                    contact.district,
                    contact.state,
                    contact.country,
                    contact.tags.joinToString(" ")
                ).any { it.contains(searchQuery, ignoreCase = true) }

                val matchesFavorite = !favoritesOnly || contact.isFavorite
                val matchesFilters = contactMatchesFilters(contact, appliedFilters)

                matchesSearch && matchesFavorite && matchesFilters
            }.sortedBy { sortKeyOf(it.fullName) }
        }
    }

    val listItems = remember(filteredContacts) {
        filteredContacts.groupBy { sectionLetterOf(it.fullName) }.toSortedMap().flatMap { (letter, values) ->
            buildList {
                add(ContactListItem.Header(letter))
                values.forEach { add(ContactListItem.Row(it)) }
            }
        }
    }

    val sectionIndexMap = remember(listItems) {
        listItems.mapIndexedNotNull { index, item ->
            if (item is ContactListItem.Header) item.letter to index else null
        }.toMap()
    }

    val currentVisibleLetter by remember(listItems, listState.firstVisibleItemIndex) {
        derivedStateOf {
            when (val item = listItems.getOrNull(listState.firstVisibleItemIndex)) {
                is ContactListItem.Header -> item.letter
                is ContactListItem.Row -> sectionLetterOf(item.contact.fullName)
                null -> "A"
            }
        }
    }

    LaunchedEffect(listState.isScrollInProgress, currentVisibleLetter, railInteracting) {
        if (railInteracting) return@LaunchedEffect
        if (listState.isScrollInProgress) {
            activeRailLetter = currentVisibleLetter
        } else if (activeRailLetter == currentVisibleLetter) {
            delay(250)
            activeRailLetter = null
        }
    }

    LaunchedEffect(listState.isScrollInProgress, showFilterSheet, searchMode) {
        if (showFilterSheet || searchMode) {
            onBottomBarVisibilityChange(true)
        } else if (listState.isScrollInProgress) {
            onBottomBarVisibilityChange(false)
        } else {
            delay(900)
            onBottomBarVisibilityChange(true)
        }
    }

    val hasAppliedFilters = !appliedFilters.isEmpty()

    BackHandler(enabled = showFilterSheet || searchMode || favoritesOnly || hasAppliedFilters || expandedContactId != null) {
        when {
            showFilterSheet -> showFilterSheet = false
            searchMode -> {
                searchMode = false
                searchQuery = ""
            }
            expandedContactId != null -> expandedContactId = null
            favoritesOnly -> favoritesOnly = false
            hasAppliedFilters -> {
                appliedFilters = AppliedContactFilters()
                draftFilters = AppliedContactFilters()
                filtersExpanded = false
            }
        }
    }

    fun dial(number: String) {
        context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number")))
    }

    fun sms(number: String) {
        context.startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$number")))
    }

    fun whatsapp(number: String) {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/${number.filter(Char::isDigit)}")))
    }

    if (showFilterSheet) {
        val filterSheetState = androidx.compose.material3.rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showFilterSheet = false },
            containerColor = CardWhite,
            dragHandle = null,
            sheetState = filterSheetState
        ) {
            ContactFiltersSheet(
                initialFilters = draftFilters,
                contacts = contacts,
                onDismiss = { showFilterSheet = false },
                onApply = {
                    appliedFilters = it
                    draftFilters = it
                    filtersExpanded = false
                    expandedContactId = null
                    showFilterSheet = false
                }
            )
        }
    }

    if (showSaveDialog) {
        AlertDialog(
            onDismissRequest = { showSaveDialog = false },
            title = { Text("Save as Smart Group") },
            text = {
                OutlinedTextField(
                    value = smartGroupName,
                    onValueChange = { smartGroupName = it },
                    label = { Text("Smart group name") },
                    singleLine = true,
                    shape = RoundedCornerShape(18.dp)
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onSaveSmartGroup(smartGroupName.ifBlank { "Filtered Contacts" }, appliedFilters)
                    Toast.makeText(context, "Smart group saved. Listing will appear in the Groups phase.", Toast.LENGTH_SHORT).show()
                    smartGroupName = ""
                    showSaveDialog = false
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showSaveDialog = false }) { Text("Cancel") }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppBg)
    ) {
        ContactsHeader(
            selectedLanguage = selectedLanguage,
            onLanguageChange = onLanguageChange,
            searchMode = searchMode,
            searchQuery = searchQuery,
            onSearchModeChange = {
                searchMode = it
                if (!it) searchQuery = ""
            },
            onQueryChange = { searchQuery = it },
            onClearQuery = { searchQuery = "" },
            onOpenMenu = onOpenMenu
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 4.dp, vertical = 2.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                ControlsRow(
                    hasFiltersApplied = hasAppliedFilters,
                    favoritesOnly = favoritesOnly,
                    onFilterClick = {
                        draftFilters = appliedFilters
                        showFilterSheet = true
                        onBottomBarVisibilityChange(true)
                    },
                    onFavoritesClick = {
                        favoritesOnly = !favoritesOnly
                        expandedContactId = null
                        onBottomBarVisibilityChange(true)
                    },
                    onAddContact = {
                        onBottomBarVisibilityChange(true)
                        onAddContact()
                    },
                    onSaveAsSmartGroup = {
                        onBottomBarVisibilityChange(true)
                        showSaveDialog = true
                    }
                )

                androidx.compose.animation.AnimatedVisibility(visible = hasAppliedFilters) {
                    AppliedFiltersSummary(
                        filters = appliedFilters,
                        expanded = filtersExpanded,
                        onToggle = {
                            filtersExpanded = !filtersExpanded
                            onBottomBarVisibilityChange(true)
                        }
                    )
                }

                Text(
                    text = if (filteredContacts.size == 1) "1 contact" else "${filteredContacts.size} contacts",
                    color = MutedText,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(start = 4.dp, top = 0.dp, bottom = 1.dp)
                )

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    shape = RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp, bottomStart = 26.dp, bottomEnd = 26.dp),
                    color = CardWhite,
                    border = BorderStroke(1.dp, CardBorder)
                ) {
                    if (filteredContacts.isEmpty()) {
                        EmptyContactsState(hasQuery = searchQuery.isNotBlank() || hasAppliedFilters || favoritesOnly)
                    } else {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(top = 2.dp, bottom = 78.dp, end = 18.dp)
                        ) {
                            itemsIndexed(
                                items = listItems,
                                key = { _, item ->
                                    when (item) {
                                        is ContactListItem.Header -> "header_${item.letter}"
                                        is ContactListItem.Row -> "row_${item.contact.id}"
                                    }
                                }
                            ) { index, item ->
                                when (item) {
                                    is ContactListItem.Header -> ContactLetterHeader(item.letter)
                                    is ContactListItem.Row -> ContactRow(
                                        contact = item.contact,
                                        expanded = expandedContactId == item.contact.id,
                                        onClick = {
                                            onBottomBarVisibilityChange(true)
                                            expandedContactId = if (expandedContactId == item.contact.id) null else item.contact.id
                                        },
                                        onFavoriteClick = {
                                            onBottomBarVisibilityChange(true)
                                            onToggleFavorite(item.contact.id, !item.contact.isFavorite)
                                        },
                                        onCallClick = {
                                            onBottomBarVisibilityChange(true)
                                            dial(item.contact.primaryPhone)
                                        },
                                        onSmsClick = {
                                            onBottomBarVisibilityChange(true)
                                            sms(item.contact.primaryPhone)
                                        },
                                        onWhatsAppClick = {
                                            onBottomBarVisibilityChange(true)
                                            whatsapp(item.contact.primaryPhone)
                                        },
                                        onInfoClick = {
                                            onBottomBarVisibilityChange(true)
                                            onOpenDetails(item.contact.id)
                                        },
                                        showDivider = index < listItems.lastIndex
                                    )
                                }
                            }
                        }
                    }
                }
            }

            AlphabetRail(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .fillMaxSize()
                    .padding(top = 64.dp, bottom = 2.dp, end = 0.dp),
                listState = listState,
                sectionIndexMap = sectionIndexMap,
                activeLetter = activeRailLetter,
                onLetterSelected = { letter ->
                    railInteracting = true
                    activeRailLetter = letter
                    scope.launch {
                        findClosestSectionIndex(letter, sectionIndexMap)?.let { listState.scrollToItem(it) }
                        delay(400)
                        railInteracting = false
                        activeRailLetter = null
                    }
                }
            )

            androidx.compose.animation.AnimatedVisibility(
                visible = activeRailLetter != null,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = Modifier.align(Alignment.Center)
            ) {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = Color(0xFF5A5A5A).copy(alpha = 0.88f)
                ) {
                    Text(
                        text = activeRailLetter.orEmpty(),
                        modifier = Modifier.padding(horizontal = 30.dp, vertical = 16.dp),
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }

}

@Composable
private fun ContactsHeader(
    selectedLanguage: String,
    onLanguageChange: (String) -> Unit,
    searchMode: Boolean,
    searchQuery: String,
    onSearchModeChange: (Boolean) -> Unit,
    onQueryChange: (String) -> Unit,
    onClearQuery: () -> Unit,
    onOpenMenu: () -> Unit
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
                if (searchMode) {
                    focusRequester.requestFocus()
                    keyboardController?.show()
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onSearchModeChange(false) }, modifier = Modifier.size(40.dp)) {
                    Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onQueryChange,
                    modifier = Modifier.weight(1f).focusRequester(focusRequester),
                    placeholder = { Text("Search contacts", color = Color.White.copy(alpha = 0.82f)) },
                    singleLine = true,
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = onClearQuery, modifier = Modifier.size(36.dp)) {
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
                IconButton(onClick = onOpenMenu, modifier = Modifier.size(40.dp)) {
                    Icon(Icons.Outlined.Menu, contentDescription = "Menu", tint = Color.White)
                }
                Text(
                    text = "Contacts",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                LanguageToggle(modifier = Modifier.padding(vertical = 1.dp), selectedLanguage = selectedLanguage, onLanguageChange = onLanguageChange)
                Spacer(modifier = Modifier.width(2.dp))
                IconButton(onClick = { onSearchModeChange(true) }, modifier = Modifier.size(40.dp)) {
                    Icon(Icons.Outlined.Search, contentDescription = "Search", tint = Color.White)
                }
            }
        }
    }
}

@Composable
private fun LanguageToggle(modifier: Modifier = Modifier, selectedLanguage: String, onLanguageChange: (String) -> Unit) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.14f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.22f))
    ) {
        Row(modifier = Modifier.padding(horizontal = 2.dp, vertical = 3.dp)) {
            LanguagePill("EN", selectedLanguage == "EN") { onLanguageChange("EN") }
            LanguagePill("TA", selectedLanguage == "TA") { onLanguageChange("TA") }
        }
    }
}

@Composable
private fun LanguagePill(text: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (selected) Color.White else Color.Transparent
    ) {
        Box(modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp)) {
            Text(
                text = text,
                color = if (selected) OrangePrimary else Color.White,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
private fun ControlsRow(
    hasFiltersApplied: Boolean,
    favoritesOnly: Boolean,
    onFilterClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onAddContact: () -> Unit,
    onSaveAsSmartGroup: () -> Unit
) {
    if (hasFiltersApplied) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ControlChip(
                modifier = Modifier.weight(0.82f),
                label = "Filter",
                icon = Icons.Outlined.FilterList,
                selected = true,
                onClick = onFilterClick
            )
            ControlChip(
                modifier = Modifier.weight(2.18f),
                label = "Save as Smart Group",
                icon = Icons.Outlined.Check,
                selected = true,
                filled = true,
                filledColor = OrangePrimary,
                onClick = onSaveAsSmartGroup
            )
        }
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ControlChip(
                modifier = Modifier.weight(0.9f),
                label = "Filter",
                icon = Icons.Outlined.FilterList,
                selected = false,
                onClick = onFilterClick
            )
            ControlChip(
                modifier = Modifier.weight(1.0f),
                label = "Favorites",
                icon = if (favoritesOnly) Icons.Rounded.Star else Icons.Outlined.Star,
                selected = favoritesOnly,
                onClick = onFavoritesClick
            )
            ControlChip(
                modifier = Modifier.weight(1.18f),
                label = "Add Contact",
                icon = Icons.Outlined.Add,
                selected = true,
                filled = true,
                filledColor = SuccessGreen,
                onClick = onAddContact
            )
        }
    }
}

@Composable
private fun ControlChip(
    modifier: Modifier = Modifier,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    filled: Boolean = false,
    filledColor: Color = OrangePrimary
) {
    val bg = when {
        filled -> filledColor
        selected -> FilterBg
        else -> CardWhite
    }
    val border = when {
        filled -> filledColor
        selected -> OrangePrimary.copy(alpha = 0.30f)
        else -> CardBorder
    }
    val tint = if (filled) Color.White else OrangePrimary
    val textColor = if (filled) Color.White else Color(0xFF262626)

    Surface(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = bg,
        border = BorderStroke(1.dp, border)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                color = textColor,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun AppliedFiltersSummary(
    filters: AppliedContactFilters,
    expanded: Boolean,
    onToggle: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
        shape = RoundedCornerShape(20.dp),
        color = CardWhite,
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onToggle)
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Filters applied (${filters.selectedCount()})",
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF262626)
                )
                Icon(
                    imageVector = if (expanded) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MutedText
                )
            }
            if (expanded) {
                Column(
                    modifier = Modifier.padding(start = 14.dp, end = 14.dp, bottom = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    FilterSummaryLine("Country", filters.countries)
                    FilterSummaryLine("State", filters.states)
                    FilterSummaryLine("District", filters.districts)
                    FilterSummaryLine("Village / Town", filters.villageTowns)
                    FilterSummaryLine("Tag", filters.tags)
                }
            }
        }
    }
}

@Composable
private fun FilterSummaryLine(label: String, values: Set<String>) {
    if (values.isEmpty()) return
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(label, color = MutedText, fontWeight = FontWeight.Medium)
        Text(values.joinToString(", "), color = Color(0xFF2D2D2D), fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun ContactLetterHeader(letter: String) {
    Text(
        text = letter,
        modifier = Modifier.padding(start = 18.dp, top = 6.dp, bottom = 4.dp),
        color = MutedText,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp
    )
}

@Composable
private fun ContactRow(
    contact: ContactRecord,
    expanded: Boolean,
    onClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onCallClick: () -> Unit,
    onSmsClick: () -> Unit,
    onWhatsAppClick: () -> Unit,
    onInfoClick: () -> Unit,
    showDivider: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick, indication = null, interactionSource = remember { MutableInteractionSource() })
            .padding(horizontal = 14.dp, vertical = 1.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            ContactAvatar(contact = contact, onClick = onInfoClick)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = contact.fullName,
                    color = Color(0xFF171717),
                    fontSize = 17.sp,
                    lineHeight = 21.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = contact.primaryPhone,
                    color = MutedText,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 1.dp)
                )
            }
            IconButton(onClick = onFavoriteClick, modifier = Modifier.size(34.dp)) {
                Icon(
                    imageVector = if (contact.isFavorite) Icons.Rounded.Star else Icons.Outlined.Star,
                    contentDescription = "Favorite",
                    tint = if (contact.isFavorite) Color(0xFFF3B122) else Color(0xFFCCC4BA)
                )
            }
        }

        if (expanded) {
            Column(modifier = Modifier.padding(start = 52.dp, top = 0.dp, end = 8.dp)) {
                Text(
                    text = contact.locationLine.ifBlank { "Location not added" },
                    color = MutedText,
                    fontSize = 12.sp
                )
                if (contact.tags.isNotEmpty()) {
                    Row(
                        modifier = Modifier
                            .padding(top = 3.dp)
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        contact.tags.forEach { tag ->
                            AssistChip(
                                onClick = {},
                                label = { Text(tag) },
                                colors = AssistChipDefaults.assistChipColors(
                                    containerColor = SearchBg,
                                    labelColor = Color(0xFF2E2E2E)
                                ),
                                border = BorderStroke(1.dp, CardBorder)
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier.padding(top = 3.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    QuickActionIcon("Call", Icons.Outlined.Call, onCallClick)
                    QuickActionIcon("SMS", Icons.AutoMirrored.Rounded.Message, onSmsClick)
                    QuickActionIcon("WhatsApp", Icons.AutoMirrored.Rounded.Message, onWhatsAppClick, accent = SuccessGreen)
                    QuickActionIcon("Info", Icons.Outlined.Info, onInfoClick, accent = Color(0xFF64748B))
                }
            }
        }

        if (showDivider) {
            HorizontalDivider(modifier = Modifier.padding(top = 4.dp), color = CardBorder)
        }
    }
}

@Composable
private fun QuickActionIcon(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit,
    accent: Color = OrangePrimary
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Surface(
            onClick = onClick,
            shape = CircleShape,
            color = accent.copy(alpha = 0.10f),
            border = BorderStroke(1.dp, accent.copy(alpha = 0.18f))
        ) {
            Box(modifier = Modifier.size(38.dp), contentAlignment = Alignment.Center) {
                Icon(icon, contentDescription = label, tint = accent, modifier = Modifier.size(18.dp))
            }
        }
        Text(label, color = MutedText, fontSize = 11.sp, modifier = Modifier.padding(top = 3.dp))
    }
}

@Composable
private fun ContactAvatar(contact: ContactRecord, onClick: () -> Unit) {
    val context = LocalContext.current
    val imageBitmap = remember(contact.photoUri) { loadContactPhotoBitmap(context, contact.photoUri) }
    val spec = decodePhotoSpec(contact.photoUri)
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(Brush.linearGradient(colors = gradientForName(contact.fullName)))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        if (imageBitmap != null && spec != null) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val sizePx = with(LocalDensity.current) { maxWidth.toPx() }
                Image(
                    bitmap = imageBitmap,
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(
                            scaleX = spec.scale,
                            scaleY = spec.scale,
                            translationX = photoTranslationForSize(spec.offsetX, sizePx),
                            translationY = photoTranslationForSize(spec.offsetY, sizePx)
                        )
                )
            }
        } else {
            Text(contact.initials, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
        }
    }
}

@Composable
private fun AlphabetRail(
    modifier: Modifier,
    listState: LazyListState,
    sectionIndexMap: Map<String, Int>,
    activeLetter: String?,
    onLetterSelected: (String) -> Unit
) {
    val letters = remember { ('A'..'Z').map { it.toString() } }
    val alpha by animateFloatAsState(if (listState.isScrollInProgress || activeLetter != null) 1f else 0.86f, label = "railAlpha")
    var railSize by remember { mutableStateOf(IntSize.Zero) }

    fun selectAtY(y: Float) {
        if (railSize.height <= 0) return
        val perLetter = railSize.height / letters.size.toFloat()
        val index = (y / perLetter).toInt().coerceIn(0, letters.lastIndex)
        onLetterSelected(letters[index])
    }

    Box(modifier = modifier, contentAlignment = Alignment.CenterEnd) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .onSizeChanged { railSize = it }
                .pointerInput(letters) {
                    detectVerticalDragGestures(
                        onDragStart = { offset -> selectAtY(offset.y) },
                        onVerticalDrag = { change, _ -> selectAtY(change.position.y) }
                    )
                }
                .padding(horizontal = 2.dp, vertical = 2.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            letters.forEach { letter ->
                val isActive = letter == activeLetter
                val enabled = sectionIndexMap.containsKey(letter)
                Text(
                    text = letter,
                    color = when {
                        isActive -> OrangePrimary
                        enabled -> MutedText
                        else -> MutedText.copy(alpha = 0.28f)
                    },
                    fontSize = if (isActive) 10.sp else 8.sp,
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                    modifier = Modifier
                        .alpha(alpha)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onLetterSelected(letter) }
                        .padding(horizontal = 3.dp, vertical = 0.dp)
                )
            }
        }
    }
}

@Composable
private fun EmptyContactsState(hasQuery: Boolean) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = if (hasQuery) "No contacts found" else "No contacts yet",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2E2E2E)
            )
            Text(
                text = if (hasQuery) "Try another search or filter." else "Add a contact to get started.",
                color = MutedText,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }
}

@Composable
private fun ContactFiltersSheet(
    initialFilters: AppliedContactFilters,
    contacts: List<ContactRecord>,
    onDismiss: () -> Unit,
    onApply: (AppliedContactFilters) -> Unit
) {
    var countries by remember(initialFilters) { mutableStateOf(initialFilters.countries) }
    var states by remember(initialFilters) { mutableStateOf(initialFilters.states) }
    var districts by remember(initialFilters) { mutableStateOf(initialFilters.districts) }
    var villageTowns by remember(initialFilters) { mutableStateOf(initialFilters.villageTowns) }
    var tags by remember(initialFilters) { mutableStateOf(initialFilters.tags) }
    var expandedSection by rememberSaveable { mutableStateOf<String?>(null) }

    val selectedCountry = countries.firstOrNull()
    val selectedState = states.firstOrNull()
    val selectedDistrict = districts.firstOrNull()
    val selectedVillageTown = villageTowns.firstOrNull()

    val dynamicOptions = remember(contacts, selectedCountry, selectedState, selectedDistrict, selectedVillageTown) {
        deriveDynamicFilterOptions(
            contacts = contacts,
            country = selectedCountry,
            state = selectedState,
            district = selectedDistrict,
            villageTown = selectedVillageTown
        )
    }

    fun setCountry(value: String?) {
        countries = value?.let { setOf(it) } ?: emptySet()
        states = emptySet()
        districts = emptySet()
        villageTowns = emptySet()
    }

    fun setState(value: String?) {
        states = value?.let { setOf(it) } ?: emptySet()
        districts = emptySet()
        villageTowns = emptySet()
    }

    fun setDistrict(value: String?) {
        districts = value?.let { setOf(it) } ?: emptySet()
        villageTowns = emptySet()
    }

    fun setVillageTown(value: String?) {
        villageTowns = value?.let { setOf(it) } ?: emptySet()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.82f)
            .padding(horizontal = 12.dp, vertical = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Filters",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            TextButton(onClick = {
                countries = emptySet()
                states = emptySet()
                districts = emptySet()
                villageTowns = emptySet()
                tags = emptySet()
                expandedSection = null
            }) { Text("Clear") }
            TextButton(onClick = onDismiss) { Text("Close") }
        }

        Column(
            modifier = Modifier
                .weight(1f, fill = true)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            SingleSelectFilterCard(
                title = "Country",
                options = dynamicOptions.countries,
                selected = selectedCountry,
                expanded = expandedSection == "Country",
                onExpandedChange = { expandedSection = if (it) "Country" else null },
                onSelectedChange = ::setCountry
            )
            SingleSelectFilterCard(
                title = "State",
                options = dynamicOptions.states,
                selected = selectedState,
                expanded = expandedSection == "State",
                onExpandedChange = { expandedSection = if (it) "State" else null },
                onSelectedChange = ::setState
            )
            SingleSelectFilterCard(
                title = "District",
                options = dynamicOptions.districts,
                selected = selectedDistrict,
                expanded = expandedSection == "District",
                onExpandedChange = { expandedSection = if (it) "District" else null },
                onSelectedChange = ::setDistrict
            )
            SingleSelectFilterCard(
                title = "Village / Town",
                options = dynamicOptions.villageTowns,
                selected = selectedVillageTown,
                expanded = expandedSection == "Village / Town",
                onExpandedChange = { expandedSection = if (it) "Village / Town" else null },
                onSelectedChange = ::setVillageTown
            )
            MultiSelectFilterCard(
                title = "Tag",
                options = dynamicOptions.tags,
                selected = tags,
                expanded = expandedSection == "Tag",
                onExpandedChange = { expandedSection = if (it) "Tag" else null },
                onSelectedChange = { tags = it.intersect(dynamicOptions.tags.toSet()) }
            )
        }

        Surface(
            onClick = {
                onApply(
                    AppliedContactFilters(
                        countries = countries,
                        states = states,
                        districts = districts,
                        villageTowns = villageTowns,
                        tags = tags
                    )
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            shape = RoundedCornerShape(18.dp),
            color = OrangePrimary
        ) {
            Box(modifier = Modifier.padding(vertical = 12.dp), contentAlignment = Alignment.Center) {
                Text("Apply", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun MultiSelectFilterCard(
    title: String,
    options: List<String>,
    selected: Set<String>,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onSelectedChange: (Set<String>) -> Unit
) {
    var query by rememberSaveable(title) { mutableStateOf("") }
    val filteredOptions = remember(options, query) { options.filter { it.contains(query, ignoreCase = true) } }
    val summary = when {
        selected.isEmpty() -> "Any"
        selected.size <= 2 -> selected.joinToString(", ")
        else -> "${selected.size} selected"
    }

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = SearchBg,
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onExpandedChange(!expanded) }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text(
                    text = summary,
                    color = MutedText,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Icon(
                    imageVector = if (expanded) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MutedText
                )
            }

            if (expanded) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 0.dp)) {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 46.dp),
                        placeholder = { Text("Search $title") },
                        singleLine = true,
                        trailingIcon = {
                            if (query.isNotBlank()) {
                                IconButton(onClick = { query = "" }) {
                                    Icon(Icons.Outlined.Close, contentDescription = "Clear")
                                }
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = CardWhite,
                            unfocusedContainerColor = CardWhite,
                            focusedBorderColor = OrangePrimary.copy(alpha = 0.35f),
                            unfocusedBorderColor = CardBorder,
                            cursorColor = OrangePrimary
                        )
                    )
                    Column(
                        modifier = Modifier
                            .heightIn(max = 130.dp)
                            .verticalScroll(rememberScrollState())
                            .padding(top = 2.dp, bottom = 3.dp),
                        verticalArrangement = Arrangement.spacedBy(1.dp)
                    ) {
                        filteredOptions.forEach { option ->
                            val isSelected = option in selected
                            Surface(
                                onClick = { onSelectedChange(if (isSelected) selected - option else selected + option) },
                                color = if (isSelected) FilterBg else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        option,
                                        modifier = Modifier.weight(1f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        color = Color(0xFF232323),
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                    )
                                    if (isSelected) {
                                        Icon(Icons.Outlined.Check, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SingleSelectFilterCard(
    title: String,
    options: List<String>,
    selected: String?,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onSelectedChange: (String?) -> Unit
) {
    var query by rememberSaveable(title) { mutableStateOf("") }
    val filteredOptions = remember(options, query) { options.filter { it.contains(query, ignoreCase = true) } }
    val summary = selected ?: "Any"

    Surface(
        shape = RoundedCornerShape(18.dp),
        color = SearchBg,
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onExpandedChange(!expanded) }
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text(
                    text = summary,
                    color = MutedText,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Icon(
                    imageVector = if (expanded) Icons.Rounded.KeyboardArrowUp else Icons.Rounded.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MutedText
                )
            }

            if (expanded) {
                Column(modifier = Modifier.padding(horizontal = 12.dp, vertical = 0.dp)) {
                    OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 46.dp),
                        placeholder = { Text("Search $title") },
                        singleLine = true,
                        trailingIcon = {
                            if (query.isNotBlank()) {
                                IconButton(onClick = { query = "" }) {
                                    Icon(Icons.Outlined.Close, contentDescription = "Clear")
                                }
                            }
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = CardWhite,
                            unfocusedContainerColor = CardWhite,
                            focusedBorderColor = OrangePrimary.copy(alpha = 0.35f),
                            unfocusedBorderColor = CardBorder,
                            cursorColor = OrangePrimary
                        )
                    )
                    Column(
                        modifier = Modifier
                            .heightIn(max = 130.dp)
                            .verticalScroll(rememberScrollState())
                            .padding(top = 2.dp, bottom = 3.dp),
                        verticalArrangement = Arrangement.spacedBy(1.dp)
                    ) {
                        Surface(
                            onClick = { onSelectedChange(null) },
                            color = if (selected == null) FilterBg else Color.Transparent,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Any", modifier = Modifier.weight(1f), color = Color(0xFF232323))
                                if (selected == null) {
                                    Icon(Icons.Outlined.Check, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(18.dp))
                                }
                            }
                        }
                        filteredOptions.forEach { option ->
                            val isSelected = option == selected
                            Surface(
                                onClick = { onSelectedChange(option) },
                                color = if (isSelected) FilterBg else Color.Transparent,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        option,
                                        modifier = Modifier.weight(1f),
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        color = Color(0xFF232323),
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                    )
                                    if (isSelected) {
                                        Icon(Icons.Outlined.Check, contentDescription = null, tint = OrangePrimary, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun findClosestSectionIndex(letter: String, sectionIndexMap: Map<String, Int>): Int? {
    if (sectionIndexMap.isEmpty()) return null
    sectionIndexMap[letter]?.let { return it }

    val keys = sectionIndexMap.keys
        .mapNotNull { key -> key.firstOrNull()?.takeIf { it in 'A'..'Z' } }
        .sorted()
    val target = letter.firstOrNull()?.takeIf { it in 'A'..'Z' } ?: return sectionIndexMap.values.minOrNull()

    val next = keys.firstOrNull { it >= target }?.toString()
    if (next != null) return sectionIndexMap[next]

    val previous = keys.lastOrNull()?.toString()
    return previous?.let { sectionIndexMap[it] } ?: sectionIndexMap.values.minOrNull()
}

private fun contactMatchesFilters(contact: ContactRecord, filters: AppliedContactFilters): Boolean {
    val countryMatch = filters.countries.isEmpty() || filters.countries.first() == contact.country
    val stateMatch = filters.states.isEmpty() || filters.states.first() == contact.state
    val districtMatch = filters.districts.isEmpty() || filters.districts.first() == contact.district
    val villageMatch = filters.villageTowns.isEmpty() || filters.villageTowns.first() == contact.villageTown
    val tagMatch = filters.tags.isEmpty() || filters.tags.all { selected -> contact.tags.any { it.equals(selected, ignoreCase = true) } }
    return countryMatch && stateMatch && districtMatch && villageMatch && tagMatch
}

private fun deriveDynamicFilterOptions(
    contacts: List<ContactRecord>,
    country: String?,
    state: String?,
    district: String?,
    villageTown: String?
): ContactFilterOptions {
    fun List<ContactRecord>.valuesOf(selector: (ContactRecord) -> String) =
        map(selector).filter { it.isNotBlank() }.distinct().sorted()

    val countries = contacts.valuesOf { it.country }
    val stateScoped = if (country == null) contacts else contacts.filter { it.country == country }
    val states = stateScoped.valuesOf { it.state }
    val districtScoped = if (state == null) stateScoped else stateScoped.filter { it.state == state }
    val districts = districtScoped.valuesOf { it.district }
    val villageScoped = if (district == null) districtScoped else districtScoped.filter { it.district == district }
    val villageTowns = villageScoped.valuesOf { it.villageTown }
    val tags = contacts.flatMap { it.tags }.filter { it.isNotBlank() }.distinct().sorted()

    return ContactFilterOptions(
        countries = countries,
        states = states,
        districts = districts,
        villageTowns = villageTowns,
        tags = tags
    )
}

private fun gradientForName(name: String): List<Color> {
    val palettes = listOf(
        listOf(Color(0xFFA9C4FF), Color(0xFF7E96E8)),
        listOf(Color(0xFFD4B4FF), Color(0xFFB483EE)),
        listOf(Color(0xFF8BE1C5), Color(0xFF6CC7AE)),
        listOf(Color(0xFFFFC29C), Color(0xFFEE9A67)),
        listOf(Color(0xFFF8B6C4), Color(0xFFE58FA4))
    )
    return palettes[kotlin.math.abs(name.hashCode()) % palettes.size]
}

private fun sortKeyOf(name: String): String = name.replace(".", " ").trim().lowercase()

private fun sectionLetterOf(name: String): String {
    val first = sortKeyOf(name).firstOrNull()?.uppercaseChar() ?: '#'
    return if (first in 'A'..'Z') first.toString() else "#"
}
