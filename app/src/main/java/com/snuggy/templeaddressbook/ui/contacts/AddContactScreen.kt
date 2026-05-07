package com.snuggy.templeaddressbook.ui.contacts

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.net.Uri
import android.util.Patterns
import java.io.ByteArrayInputStream
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.ExpandLess
import androidx.compose.material.icons.outlined.ExpandMore
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.MyLocation
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.unit.Dp
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.material3.LocalMinimumInteractiveComponentSize
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.window.PopupProperties
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.snuggy.templeaddressbook.R
import com.snuggy.templeaddressbook.ui.theme.AppBg
import com.snuggy.templeaddressbook.ui.theme.CardBorder
import com.snuggy.templeaddressbook.ui.theme.CardWhite
import com.snuggy.templeaddressbook.ui.theme.MutedText
import com.snuggy.templeaddressbook.ui.theme.OrangePrimary
import com.snuggy.templeaddressbook.ui.theme.SearchBg
import com.snuggy.templeaddressbook.ui.theme.SuccessGreen
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.abs

@Composable
fun AddContactScreen(
    selectedLanguage: String,
    onLanguageChange: (String) -> Unit,
    onBack: () -> Unit,
    onSave: (ContactDraft) -> Unit,
    availableTags: List<String> = emptyList(),
    relationshipContactOptions: List<String> = emptyList(),
    editingContact: ContactRecord? = null
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()
    val imeVisible = WindowInsets.ime.getBottom(density) > 0

    var firstName by rememberSaveable(editingContact?.id) { mutableStateOf(editingContact?.firstName.orEmpty()) }
    var lastName by rememberSaveable(editingContact?.id) { mutableStateOf(editingContact?.lastName.orEmpty()) }
    var gender by rememberSaveable(editingContact?.id) { mutableStateOf(editingContact?.gender.orEmpty()) }
    var dobField by rememberSaveable(editingContact?.id, stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue(editingContact?.dob.orEmpty())) }
    var rasi by rememberSaveable(editingContact?.id) { mutableStateOf(editingContact?.rasi.orEmpty()) }
    var nakshatra by rememberSaveable(editingContact?.id) { mutableStateOf(editingContact?.nakshatra.orEmpty()) }
    var villageTown by rememberSaveable(editingContact?.id) { mutableStateOf(editingContact?.villageTown.orEmpty()) }
    var district by rememberSaveable(editingContact?.id) { mutableStateOf(editingContact?.district.orEmpty()) }
    var state by rememberSaveable(editingContact?.id) { mutableStateOf(editingContact?.state.orEmpty()) }
    var country by rememberSaveable(editingContact?.id) { mutableStateOf(editingContact?.country.orEmpty()) }
    var doorNo by rememberSaveable(editingContact?.id) { mutableStateOf(editingContact?.doorNo.orEmpty()) }
    var buildingName by rememberSaveable(editingContact?.id) { mutableStateOf(editingContact?.buildingName.orEmpty()) }
    var streetName by rememberSaveable(editingContact?.id) { mutableStateOf(editingContact?.streetName.orEmpty()) }
    var area by rememberSaveable(editingContact?.id) { mutableStateOf(editingContact?.area.orEmpty()) }
    var postOffice by rememberSaveable(editingContact?.id) { mutableStateOf(editingContact?.postOffice.orEmpty()) }
    var taluk by rememberSaveable(editingContact?.id) { mutableStateOf(editingContact?.taluk.orEmpty()) }
    var pinCode by rememberSaveable(editingContact?.id) { mutableStateOf(editingContact?.pinCode.orEmpty()) }
    var googleMapLink by rememberSaveable(editingContact?.id) { mutableStateOf(editingContact?.googleMapLink.orEmpty()) }
    var notes by rememberSaveable(editingContact?.id) { mutableStateOf(editingContact?.notes.orEmpty()) }
    var isFavorite by rememberSaveable(editingContact?.id) { mutableStateOf(editingContact?.isFavorite ?: false) }
    var photoSpec by rememberSaveable(editingContact?.id) { mutableStateOf<String?>(editingContact?.photoUri) }
    var activeSection by rememberSaveable { mutableStateOf<AddSection?>(AddSection.BASIC_INFO) }
    var saveAttempted by rememberSaveable { mutableStateOf(false) }
    var showDiscardDialog by rememberSaveable { mutableStateOf(false) }
    var showPhotoActions by rememberSaveable { mutableStateOf(false) }
    var pendingPhotoSource by rememberSaveable { mutableStateOf<String?>(null) }
    var showPhotoAdjustDialog by rememberSaveable { mutableStateOf(false) }

    val initialPhotoSpec = remember(editingContact?.id) { decodePhotoSpec(editingContact?.photoUri) }
    var photoScale by rememberSaveable(editingContact?.id) { mutableStateOf(initialPhotoSpec?.scale ?: 1f) }
    var photoOffsetX by rememberSaveable(editingContact?.id) { mutableStateOf(initialPhotoSpec?.offsetX ?: 0f) }
    var photoOffsetY by rememberSaveable(editingContact?.id) { mutableStateOf(initialPhotoSpec?.offsetY ?: 0f) }

    var rasiExpanded by rememberSaveable { mutableStateOf(false) }
    var nakshatraExpanded by rememberSaveable { mutableStateOf(false) }

    var districtExpanded by rememberSaveable { mutableStateOf(false) }
    var stateExpanded by rememberSaveable { mutableStateOf(false) }
    var countryExpanded by rememberSaveable { mutableStateOf(false) }

    var districtSearchQuery by rememberSaveable { mutableStateOf("") }
    var stateSearchQuery by rememberSaveable { mutableStateOf("") }
    var countrySearchQuery by rememberSaveable { mutableStateOf("") }

    var addressStatePinned by rememberSaveable { mutableStateOf(false) }
    var addressCountryPinned by rememberSaveable { mutableStateOf(false) }

    val phoneRows = remember(editingContact?.id) {
        val existingPhones = editingContact?.phonesForDisplay.orEmpty()
            .filter { it.displayNumber.isNotBlank() || it.localNumber.isNotBlank() }
        val initialRows = if (existingPhones.isNotEmpty()) {
            existingPhones.mapIndexed { index, phone ->
                val savedCountry = countryOptionForSavedPhone(phone)
                PhoneRowInput(
                    country = savedCountry,
                    localNumber = phone.localNumber.ifBlank { localNumberFromDisplayNumber(phone.displayNumber, savedCountry.code) },
                    label = localizePhoneLabel(phone.label, selectedLanguage).ifBlank { phoneLabels(selectedLanguage).first() },
                    isPrimary = phone.isPrimary || (index == 0 && existingPhones.none { it.isPrimary }),
                    isWhatsApp = phone.isWhatsApp
                )
            }
        } else {
            listOf(
                PhoneRowInput(
                    country = COUNTRY_OPTIONS.first(),
                    localNumber = "",
                    label = phoneLabels(selectedLanguage).first(),
                    isPrimary = true,
                    isWhatsApp = false
                )
            )
        }
        mutableStateListOf<PhoneRowInput>().apply { addAll(initialRows) }
    }
    val emailRows = remember(editingContact?.id) {
        val existingEmails = editingContact?.emailsForDisplay.orEmpty()
        val initialRows = if (existingEmails.isNotEmpty()) {
            existingEmails.mapIndexed { index, email ->
                EmailRowInput(
                    email = email.email,
                    label = localizeEmailLabel(email.label, selectedLanguage).ifBlank { emailLabels(selectedLanguage).first() },
                    isPrimary = email.isPrimary || (index == 0 && existingEmails.none { it.isPrimary })
                )
            }
        } else {
            listOf(EmailRowInput(isPrimary = true, label = emailLabels(selectedLanguage).first()))
        }
        mutableStateListOf<EmailRowInput>().apply { addAll(initialRows) }
    }
    val relationshipRows = remember(editingContact?.id) {
        mutableStateListOf<RelationshipRowInput>().apply {
            editingContact?.relationships
                .orEmpty()
                .filter { !it.isReverse }
                .forEach { relationship ->
                    add(
                        RelationshipRowInput(
                            relatedContact = relationship.relatedContactName,
                            type = localizeRelationshipValue(relationship.relationshipType, selectedLanguage),
                            relatedName = relationship.referenceName
                        )
                    )
                }
        }
    }
    val selectedTags = remember(editingContact?.id) { mutableStateListOf<String>().apply { addAll(editingContact?.tags.orEmpty()) } }
    var tagSearch by rememberSaveable { mutableStateOf("") }
    var showAllSelectedTags by rememberSaveable { mutableStateOf(false) }

    val selectedTagSnapshot = selectedTags.toList()
    val tagOptions = remember(availableTags, selectedTagSnapshot) {
        (availableTags + selectedTagSnapshot)
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinctBy { it.lowercase() }
            .sortedWith(String.CASE_INSENSITIVE_ORDER)
    }
    val addressDistrictHistory = remember { mutableStateListOf<String>().apply { addAll(loadAddressSuggestionHistory(context, ADDRESS_HISTORY_DISTRICT)) } }
    val addressStateHistory = remember { mutableStateListOf<String>().apply { addAll(loadAddressSuggestionHistory(context, ADDRESS_HISTORY_STATE)) } }
    val addressCountryHistory = remember { mutableStateListOf<String>().apply { addAll(loadAddressSuggestionHistory(context, ADDRESS_HISTORY_COUNTRY)) } }

    var loadedRelationshipContactOptions by remember { mutableStateOf<List<String>>(emptyList()) }
    LaunchedEffect(Unit) {
        loadedRelationshipContactOptions = ContactsRepository(context)
            .getContacts()
            .map { it.fullName.trim() }
            .filter { it.isNotBlank() }
            .distinctBy { it.lowercase() }
            .sortedBy { it.lowercase() }
    }
    val relationshipContactSuggestions = remember(relationshipContactOptions, loadedRelationshipContactOptions) {
        val source = if (relationshipContactOptions.isNotEmpty()) relationshipContactOptions else loadedRelationshipContactOptions
        source
            .map { displayNameOnly(it) }
            .filter { it.isNotBlank() }
            .distinctBy { it.lowercase() }
            .sortedBy { it.lowercase() }
    }

    LaunchedEffect(selectedLanguage) {
        gender = localizeGenderValue(gender, selectedLanguage)
        rasi = localizeRasiValue(rasi, selectedLanguage)
        nakshatra = localizeNakshatraValue(nakshatra, selectedLanguage)
        phoneRows.indices.forEach { index ->
            phoneRows[index] = phoneRows[index].copy(label = localizePhoneLabel(phoneRows[index].label, selectedLanguage))
        }
        emailRows.indices.forEach { index ->
            emailRows[index] = emailRows[index].copy(label = localizeEmailLabel(emailRows[index].label, selectedLanguage))
        }
        relationshipRows.indices.forEach { index ->
            relationshipRows[index] = relationshipRows[index].copy(type = localizeRelationshipValue(relationshipRows[index].type, selectedLanguage))
        }
    }

    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (_: SecurityException) {
            }
            pendingPhotoSource = uri.toString()
            photoScale = 1f
            photoOffsetX = 0f
            photoOffsetY = 0f
            showPhotoAdjustDialog = true
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
        if (bitmap != null) {
            val uriString = saveBitmapToCache(context, bitmap)
            if (uriString != null) {
                pendingPhotoSource = uriString
                photoScale = 1f
                photoOffsetX = 0f
                photoOffsetY = 0f
                showPhotoAdjustDialog = true
            }
        }
    }

    val firstNameMissing = saveAttempted && firstName.isBlank()
    val phoneMissing = saveAttempted && phoneRows.none { it.localNumber.isNotBlank() }
    val canHighlightSave = firstName.isNotBlank() && phoneRows.any { it.localNumber.isNotBlank() }

    fun localizedLabel(en: String, ta: String): String = if (selectedLanguage == "TA") ta else en

    val addressCountryOptionsForUi = remember(selectedLanguage, addressCountryHistory.toList()) {
        mergeAddressSuggestionOptions(
            builtIn = addressCountryOptions(selectedLanguage),
            history = addressCountryHistory
        )
    }
    val addressStateOptionsForUi = remember(country, selectedLanguage, addressCountryPinned, addressStateHistory.toList()) {
        mergeAddressSuggestionOptions(
            builtIn = addressStateOptions(
                country = country,
                selectedLanguage = selectedLanguage,
                filterByCountry = addressCountryPinned
            ),
            history = addressStateHistory
        )
    }
    val addressDistrictOptionsForUi = remember(
        state,
        country,
        selectedLanguage,
        addressStatePinned,
        addressCountryPinned,
        addressDistrictHistory.toList()
    ) {
        mergeAddressSuggestionOptions(
            builtIn = addressDistrictOptions(
                country = country,
                state = state,
                selectedLanguage = selectedLanguage,
                filterByCountry = addressCountryPinned,
                filterByState = addressStatePinned
            ),
            history = addressDistrictHistory
        )
    }

    fun postalCodePlaceholder(): String =
        if (isIndiaCountry(country)) {
            localizedLabel("6 digits", "6 எண்கள்")
        } else {
            "A1B 2C3"
        }

    fun addressQueryText(): String =
        listOf(
            doorNo,
            buildingName,
            streetName,
            area,
            postOffice,
            taluk,
            villageTown,
            district,
            state,
            pinCode,
            country
        ).map { it.trim() }
            .filter { it.isNotBlank() }
            .joinToString(", ")

    fun openMapChooser() {
        val rawTarget = googleMapLink.trim().ifBlank { addressQueryText() }

        val targetUri = if (rawTarget.startsWith("http://", ignoreCase = true) || rawTarget.startsWith("https://", ignoreCase = true)) {
            Uri.parse(rawTarget)
        } else {
            Uri.parse("geo:0,0?q=${Uri.encode(rawTarget)}")
        }

        val chooser = Intent.createChooser(
            Intent(Intent.ACTION_VIEW, targetUri).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            },
            localizedLabel("Open with", "இதன் மூலம் திற")
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        runCatching { context.startActivity(chooser) }
            .onFailure {
                scope.launch {
                    snackbarHostState.showSnackbar(
                        localizedLabel("No map app available", "மேப் பயன்பாடு கிடைக்கவில்லை")
                    )
                }
            }
    }

    fun hasChanges(): Boolean = firstName.isNotBlank() ||
            lastName.isNotBlank() ||
            gender.isNotBlank() ||
            dobField.text.isNotBlank() ||
            rasi.isNotBlank() ||
            nakshatra.isNotBlank() ||
            villageTown.isNotBlank() ||
            district.isNotBlank() ||
            state.isNotBlank() ||
            country.isNotBlank() ||
            doorNo.isNotBlank() ||
            buildingName.isNotBlank() ||
            streetName.isNotBlank() ||
            area.isNotBlank() ||
            postOffice.isNotBlank() ||
            taluk.isNotBlank() ||
            pinCode.isNotBlank() ||
            googleMapLink.isNotBlank() ||
            notes.isNotBlank() ||
            photoSpec != null ||
            isFavorite ||
            phoneRows.anyIndexed { index, row ->
                row.localNumber.isNotBlank() ||
                        row.label != phoneLabels(selectedLanguage).first() ||
                        row.isWhatsApp ||
                        row.country != COUNTRY_OPTIONS.first() ||
                        (index == 0 && !row.isPrimary) || (index > 0 && row.isPrimary)
            } ||
            emailRows.any { it.email.isNotBlank() } ||
            relationshipRows.any { it.relatedName.isNotBlank() || it.type.isNotBlank() || it.relatedContact.isNotBlank() } ||
            selectedTags.isNotEmpty() ||
            tagSearch.isNotBlank()

    fun dismissTransientUi(): Boolean {
        return when {
            rasiExpanded -> {
                rasiExpanded = false
                true
            }
            nakshatraExpanded -> {
                nakshatraExpanded = false
                true
            }
            showPhotoActions -> {
                showPhotoActions = false
                true
            }
            showPhotoAdjustDialog -> {
                showPhotoAdjustDialog = false
                pendingPhotoSource = null
                true
            }
            else -> false
        }
    }

    fun handleBack() {
        when {
            dismissTransientUi() -> Unit
            hasChanges() -> showDiscardDialog = true
            else -> onBack()
        }
    }

    fun persistAddressSuggestions() {
        addAddressSuggestion(addressDistrictHistory, district)
        addAddressSuggestion(addressStateHistory, state)
        addAddressSuggestion(addressCountryHistory, country)
        persistAddressSuggestionHistory(context, ADDRESS_HISTORY_DISTRICT, addressDistrictHistory)
        persistAddressSuggestionHistory(context, ADDRESS_HISTORY_STATE, addressStateHistory)
        persistAddressSuggestionHistory(context, ADDRESS_HISTORY_COUNTRY, addressCountryHistory)
    }

    fun saveContact() {
        saveAttempted = true
        if (firstName.isBlank() || phoneRows.none { it.localNumber.isNotBlank() }) {
            activeSection = if (firstName.isBlank()) AddSection.BASIC_INFO else AddSection.PHONE
            scope.launch {
                snackbarHostState.showSnackbar(localizedLabel("Enter First Name and Phone Number", "முதல் பெயரும் தொலைபேசி எண்ணும் உள்ளிடவும்"))
            }
            return
        }
        val invalidPhoneRow = phoneRows.firstOrNull { it.localNumber.isNotBlank() && !isPhoneLengthValid(it) }
        if (invalidPhoneRow != null) {
            activeSection = AddSection.PHONE
            scope.launch {
                snackbarHostState.showSnackbar(invalidPhoneErrorMessage(invalidPhoneRow, selectedLanguage))
            }
            return
        }
        val blankEmailIndexes = emailRows.mapIndexedNotNull { index, row -> if (row.email.isBlank()) index else null }
        blankEmailIndexes.asReversed().forEach { index ->
            if (emailRows.size > 1) emailRows.removeAt(index) else emailRows[index] = emailRows[index].copy(email = "")
        }
        val invalidEmailRow = emailRows.firstOrNull { it.email.isNotBlank() && !isValidEmailAddress(it.email) }
        if (invalidEmailRow != null) {
            activeSection = AddSection.EMAIL
            scope.launch {
                snackbarHostState.showSnackbar(if (selectedLanguage == "TA") "செல்லுபடியாகும் மின்னஞ்சல் முகவரியை உள்ளிடவும்" else "Enter a valid email address")
            }
            return
        }
        val filledEmailIndexes = emailRows.mapIndexedNotNull { index, row -> if (row.email.isNotBlank()) index else null }
        if (filledEmailIndexes.isNotEmpty() && filledEmailIndexes.none { emailRows[it].isPrimary }) {
            val firstIndex = filledEmailIndexes.first()
            emailRows.indices.forEach { i -> emailRows[i] = emailRows[i].copy(isPrimary = i == firstIndex) }
        }
        val blankRelationshipIndexes = relationshipRows.mapIndexedNotNull { index, row ->
            if (isBlankRelationshipRow(row)) index else null
        }
        blankRelationshipIndexes.asReversed().forEach { index -> relationshipRows.removeAt(index) }
        val invalidRelationshipRow = relationshipRows.firstOrNull { !isValidRelationshipRow(it) }
        if (invalidRelationshipRow != null) {
            activeSection = AddSection.RELATIONSHIPS
            scope.launch {
                snackbarHostState.showSnackbar(relationshipValidationMessage(invalidRelationshipRow, selectedLanguage))
            }
            return
        }
        val primaryPhoneIndex = phoneRows.indexOfFirst { it.localNumber.isNotBlank() && it.isPrimary }
            .takeIf { it >= 0 }
            ?: phoneRows.indexOfFirst { it.localNumber.isNotBlank() }
        val primaryPhoneRow = phoneRows.getOrNull(primaryPhoneIndex)
        val savedPhoneRows = phoneRows
            .mapIndexedNotNull { index, row ->
                if (row.localNumber.isBlank()) return@mapIndexedNotNull null
                val localNumber = row.localNumber.trim()
                ContactPhoneRecord(
                    countryName = row.country.nameEn,
                    countryCode = row.country.code,
                    countryFlag = row.country.flag,
                    countryCompactLabel = row.country.compactLabel,
                    localNumber = localNumber,
                    fullNumber = "${row.country.code} $localNumber",
                    label = row.label.trim(),
                    isPrimary = index == primaryPhoneIndex,
                    isWhatsApp = row.isWhatsApp
                )
            }
        val savedEmailRows = emailRows
            .filter { it.email.isNotBlank() }
            .map { row ->
                ContactEmailRecord(
                    email = row.email.trim(),
                    label = row.label.trim(),
                    isPrimary = row.isPrimary
                )
            }
        val fullPhone = primaryPhoneRow?.let { "${it.country.code} ${it.localNumber.trim()}" } ?: ""
        persistAddressSuggestions()
        onSave(
            ContactDraft(
                firstName = firstName.trim(),
                lastName = lastName.trim(),
                primaryPhone = fullPhone,
                phoneLabel = primaryPhoneRow?.label.orEmpty(),
                villageTown = villageTown.trim(),
                district = district.trim(),
                state = state.trim(),
                country = country.trim(),
                tags = selectedTags.toList(),
                isFavorite = isFavorite,
                photoUri = photoSpec,
                gender = gender.trim(),
                dob = dobField.text.trim(),
                rasi = rasi.trim(),
                nakshatra = nakshatra.trim(),
                doorNo = doorNo.trim(),
                buildingName = buildingName.trim(),
                streetName = streetName.trim(),
                area = area.trim(),
                postOffice = postOffice.trim(),
                taluk = taluk.trim(),
                pinCode = pinCode.trim(),
                googleMapLink = googleMapLink.trim(),
                notes = notes.trim(),
                phoneNumbers = savedPhoneRows,
                emailAddresses = savedEmailRows,
                relationships = relationshipRows
                    .filter { !isBlankRelationshipRow(it) && isValidRelationshipRow(it) }
                    .map { row ->
                        ContactRelationshipDraft(
                            relationshipType = localizeRelationshipValue(row.type, "EN").trim(),
                            relatedContactName = row.relatedContact.trim(),
                            referenceName = row.relatedName.trim()
                        )
                    }
            )
        )
    }

    BackHandler(onBack = { handleBack() })

    if (showDiscardDialog) {
        AlertDialog(
            onDismissRequest = { showDiscardDialog = false },
            title = { Text(localizedLabel("Discard changes?", "மாற்றங்களை நீக்கவா?")) },
            text = { Text(localizedLabel("Your unsaved contact details will be lost.", "சேமிக்கப்படாத தொடர்பு விவரங்கள் இழக்கப்படும்.")) },
            confirmButton = {
                TextButton(onClick = {
                    showDiscardDialog = false
                    onBack()
                }) {
                    Text(localizedLabel("Discard", "நீக்கு"))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDiscardDialog = false }) {
                    Text(localizedLabel("Keep Editing", "திருத்தத்தை தொடரவும்"))
                }
            }
        )
    }

    if (showPhotoActions) {
        AlertDialog(
            onDismissRequest = { showPhotoActions = false },
            title = { Text(localizedLabel("Contact Photo", "தொடர்பு படம்")) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    ActionRowButton(localizedLabel("Take Photo", "புகைப்படம் எடுக்க")) {
                        showPhotoActions = false
                        cameraLauncher.launch(null)
                    }
                    ActionRowButton(localizedLabel("Choose from Gallery", "காட்சியகத்தில் இருந்து தேர்வு")) {
                        showPhotoActions = false
                        photoPicker.launch(arrayOf("image/*"))
                    }
                    if (photoSpec != null) {
                        ActionRowButton(localizedLabel("Remove Photo", "படத்தை நீக்கு"), danger = true) {
                            showPhotoActions = false
                            photoSpec = null
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showPhotoActions = false }) {
                    Text(localizedLabel("Close", "மூடு"))
                }
            }
        )
    }

    if (showPhotoAdjustDialog && pendingPhotoSource != null) {
        PhotoAdjustScreen(
            source = pendingPhotoSource!!,
            selectedLanguage = selectedLanguage,
            initialScale = photoScale,
            initialOffsetX = photoOffsetX,
            initialOffsetY = photoOffsetY,
            onDismiss = {
                showPhotoAdjustDialog = false
                pendingPhotoSource = null
            },
            onConfirm = { source, scale, offsetX, offsetY ->
                photoSpec = encodePhotoSpec(source, scale, offsetX, offsetY)
                showPhotoAdjustDialog = false
                pendingPhotoSource = null
                photoScale = scale
                photoOffsetX = offsetX
                photoOffsetY = offsetY
            }
        )
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBg)
    ) {
        AddContactHeader(
            selectedLanguage = selectedLanguage,
            isEditMode = editingContact != null,
            onLanguageChange = onLanguageChange,
            onBack = { handleBack() },
            saveEnabledHighlight = canHighlightSave,
            onSave = { saveContact() }
        )

        Box(modifier = Modifier.weight(1f)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(horizontal = 12.dp, vertical = 10.dp)
                    .padding(bottom = 18.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SectionCard(
                    title = localizedLabel("Basic Info", "அடிப்படை தகவல்"),
                    expanded = activeSection == AddSection.BASIC_INFO,
                    onHeaderClick = {
                        activeSection = if (activeSection == AddSection.BASIC_INFO) null else AddSection.BASIC_INFO
                    }
                ) {
                    PhotoPickerRow(
                        photoSpec = photoSpec,
                        selectedLanguage = selectedLanguage,
                        onOpenActions = { showPhotoActions = true }
                    )
                    LabeledCompactField(
                        label = localizedLabel("First Name", "முதல் பெயர்"),
                        value = firstName,
                        onValueChange = { firstName = it },
                        required = true,
                        error = if (firstNameMissing) localizedLabel("First Name is required", "முதல் பெயர் தேவை") else null,
                        capitalization = KeyboardCapitalization.Words
                    )
                    LabeledCompactField(
                        label = localizedLabel("Last Name", "கடைசி பெயர்"),
                        value = lastName,
                        onValueChange = { lastName = it },
                        capitalization = KeyboardCapitalization.Words
                    )
                    GenderRow(selectedLanguage = selectedLanguage, selected = gender, onSelected = { gender = it })
                    DobField(
                        selectedLanguage = selectedLanguage,
                        value = dobField,
                        onValueChange = { dobField = formatDateFieldValue(it) },
                        onOpenPicker = { showDatePicker(context, dobField.text) { dobField = TextFieldValue(it, TextRange(it.length)) } }
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        CompactPremiumDropdownField(
                            label = localizedLabel("Rasi", "ராசி"),
                            value = rasi,
                            placeholder = localizedLabel("Select", "தேர்வு செய்க"),
                            expanded = rasiExpanded,
                            onExpandedChange = {
                                nakshatraExpanded = false
                                rasiExpanded = !rasiExpanded
                            },
                            options = rasiOptions(selectedLanguage),
                            onSelected = {
                                rasi = it
                                rasiExpanded = false
                            },
                            modifier = Modifier.weight(1f)
                        )
                        CompactPremiumDropdownField(
                            label = localizedLabel("Nakshatra", "நட்சத்திரம்"),
                            value = nakshatra,
                            placeholder = localizedLabel("Select", "தேர்வு செய்க"),
                            expanded = nakshatraExpanded,
                            onExpandedChange = {
                                rasiExpanded = false
                                nakshatraExpanded = !nakshatraExpanded
                            },
                            options = nakshatraOptions(selectedLanguage),
                            onSelected = {
                                nakshatra = it
                                nakshatraExpanded = false
                            },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                SectionCard(
                    title = localizedLabel("Phone Numbers", "தொலைபேசி எண்கள்"),
                    expanded = activeSection == AddSection.PHONE,
                    onHeaderClick = {
                        activeSection = if (activeSection == AddSection.PHONE) null else AddSection.PHONE
                    }
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        color = SearchBg,
                        border = BorderStroke(1.dp, CardBorder)
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)) {
                            phoneRows.forEachIndexed { index, row ->
                                PhoneRowEditor(
                                    selectedLanguage = selectedLanguage,
                                    row = row,
                                    labels = phoneLabels(selectedLanguage),
                                    showError = phoneMissing && row.isPrimary,
                                    isPrimaryRow = row.isPrimary,
                                    onUpdate = { updated -> phoneRows[index] = updated },
                                    onMakePrimary = {
                                        phoneRows.indices.forEach { i ->
                                            phoneRows[i] = phoneRows[i].copy(isPrimary = i == index)
                                        }
                                    },
                                    onDelete = {
                                        if (phoneRows.size > 1) {
                                            val wasPrimary = phoneRows[index].isPrimary
                                            phoneRows.removeAt(index)
                                            if (wasPrimary && phoneRows.none { it.isPrimary }) {
                                                phoneRows[0] = phoneRows[0].copy(isPrimary = true)
                                            }
                                        }
                                    },
                                    showDivider = index != phoneRows.lastIndex
                                )
                            }
                        }
                    }
                    AddInlineAction(label = localizedLabel("+ Add another number", "+ மற்றொரு எண்ணை சேர்க்க")) {
                        phoneRows.add(
                            PhoneRowInput(
                                country = COUNTRY_OPTIONS.first(),
                                label = phoneLabels(selectedLanguage).first(),
                                isPrimary = phoneRows.isEmpty()
                            )
                        )
                        if (phoneRows.none { it.isPrimary } && phoneRows.isNotEmpty()) {
                            phoneRows[0] = phoneRows[0].copy(isPrimary = true)
                        }
                    }
                }

                SectionCard(
                    title = localizedLabel("Email Address", "மின்னஞ்சல் முகவரி"),
                    expanded = activeSection == AddSection.EMAIL,
                    onHeaderClick = {
                        activeSection = if (activeSection == AddSection.EMAIL) null else AddSection.EMAIL
                    }
                ) {
                    val emailCounts = emailRows
                        .map { normalizedEmail(it.email) }
                        .filter { it.isNotBlank() }
                        .groupingBy { it }
                        .eachCount()
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        color = SearchBg,
                        border = BorderStroke(1.dp, CardBorder)
                    ) {
                        Column(modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)) {
                            emailRows.forEachIndexed { index, row ->
                                val normalized = normalizedEmail(row.email)
                                val duplicateWarning = normalized.isNotBlank() && (emailCounts[normalized] ?: 0) > 1
                                val invalidError = row.email.isNotBlank() && !isValidEmailAddress(row.email)
                                EmailRowEditor(
                                    selectedLanguage = selectedLanguage,
                                    row = row,
                                    labels = emailLabels(selectedLanguage),
                                    showInvalidError = invalidError,
                                    showDuplicateWarning = duplicateWarning,
                                    onUpdate = { updated -> emailRows[index] = updated },
                                    onSetPrimary = {
                                        emailRows.indices.forEach { i ->
                                            emailRows[i] = emailRows[i].copy(isPrimary = i == index)
                                        }
                                    },
                                    onDelete = {
                                        if (emailRows.size > 1) {
                                            val wasPrimary = emailRows[index].isPrimary
                                            emailRows.removeAt(index)
                                            if (wasPrimary) {
                                                val firstFilled = emailRows.indexOfFirst { it.email.isNotBlank() }
                                                val targetIndex = if (firstFilled >= 0) firstFilled else 0
                                                emailRows.indices.forEach { i -> emailRows[i] = emailRows[i].copy(isPrimary = i == targetIndex) }
                                            }
                                        }
                                    },
                                    showDivider = index != emailRows.lastIndex
                                )
                            }
                        }
                    }
                    AddInlineAction(label = localizedLabel("+ Add another email", "+ மற்றொரு மின்னஞ்சலை சேர்க்க")) {
                        emailRows.add(EmailRowInput(label = emailLabels(selectedLanguage).first(), isPrimary = false))
                    }
                }

                SectionCard(
                    title = localizedLabel("Address", "முகவரி"),
                    expanded = activeSection == AddSection.ADDRESS,
                    onHeaderClick = {
                        activeSection = if (activeSection == AddSection.ADDRESS) null else AddSection.ADDRESS
                    }
                ) {
                    val isIndia = isIndiaCountry(country)

                    LabeledCompactField(
                        label = addressFieldText("doorNo", isIndia, selectedLanguage).label,
                        value = doorNo,
                        onValueChange = { doorNo = it },
                        placeholder = addressFieldText("doorNo", isIndia, selectedLanguage).placeholder
                    )
                    LabeledCompactField(
                        label = addressFieldText("buildingName", isIndia, selectedLanguage).label,
                        value = buildingName,
                        onValueChange = { buildingName = it },
                        placeholder = addressFieldText("buildingName", isIndia, selectedLanguage).placeholder
                    )
                    LabeledCompactField(
                        label = addressFieldText("streetName", isIndia, selectedLanguage).label,
                        value = streetName,
                        onValueChange = { streetName = it },
                        placeholder = addressFieldText("streetName", isIndia, selectedLanguage).placeholder
                    )
                    LabeledCompactField(
                        label = addressFieldText("area", isIndia, selectedLanguage).label,
                        value = area,
                        onValueChange = { area = it },
                        placeholder = addressFieldText("area", isIndia, selectedLanguage).placeholder
                    )
                    LabeledCompactField(
                        label = addressFieldText("postOffice", isIndia, selectedLanguage).label,
                        value = postOffice,
                        onValueChange = { postOffice = it },
                        placeholder = addressFieldText("postOffice", isIndia, selectedLanguage).placeholder
                    )
                    LabeledCompactField(
                        label = addressFieldText("taluk", isIndia, selectedLanguage).label,
                        value = taluk,
                        onValueChange = { taluk = it },
                        placeholder = addressFieldText("taluk", isIndia, selectedLanguage).placeholder
                    )
                    LabeledCompactField(
                        label = localizedLabel("City / Town", "நகரம் / ஊர்"),
                        value = villageTown,
                        onValueChange = { villageTown = it },
                        placeholder = localizedLabel("City / Town", "நகரம் / ஊர்")
                    )

                    LabeledCompactField(
                        label = addressFieldText("district", isIndia, selectedLanguage).label,
                        value = district,
                        onValueChange = { district = it },
                        placeholder = addressFieldText("district", isIndia, selectedLanguage).placeholder
                    )

                    LabeledCompactField(
                        label = addressFieldText("state", isIndia, selectedLanguage).label,
                        value = state,
                        onValueChange = { state = it },
                        placeholder = addressFieldText("state", isIndia, selectedLanguage).placeholder
                    )

                    LabeledCompactField(
                        label = localizedLabel("Country", "நாடு"),
                        value = country,
                        onValueChange = { country = it },
                        placeholder = localizedLabel("Country", "நாடு")
                    )

                    LabeledCompactField(
                        label = if (isIndia) {
                            localizedLabel("PIN Code", "அஞ்சல் குறியீடு")
                        } else {
                            localizedLabel("PIN / Postal / ZIP Code", "அஞ்சல் / ZIP குறியீடு")
                        },
                        value = pinCode,
                        onValueChange = { pinCode = sanitizePostalCodeInput(country, it) },
                        placeholder = postalCodePlaceholder(),
                        keyboardType = if (isIndia) KeyboardType.Number else KeyboardType.Text,
                        error = postalCodeError(pinCode, country, selectedLanguage)
                    )

                    LabeledCompactField(
                        label = localizedLabel("Google Map Link", "கூகுள் மேப் இணைப்பு"),
                        value = googleMapLink,
                        onValueChange = { googleMapLink = it },
                        placeholder = localizedLabel("Map link", "மேப் இணைப்பு"),
                        keyboardType = KeyboardType.Uri,
                        trailing = {
                            CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
                                Surface(
                                    onClick = { openMapChooser() },
                                    shape = CircleShape,
                                    color = CardWhite,
                                    border = BorderStroke(1.dp, CardBorder),
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        Icon(
                                            Icons.Outlined.MyLocation,
                                            contentDescription = "Map",
                                            tint = OrangePrimary,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    )
                }

                SectionCard(
                    title = localizedLabel("Relationships", "உறவுகள்"),
                    expanded = activeSection == AddSection.RELATIONSHIPS,
                    onHeaderClick = {
                        activeSection = if (activeSection == AddSection.RELATIONSHIPS) null else AddSection.RELATIONSHIPS
                    }
                ) {
                    if (relationshipRows.isNotEmpty()) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(18.dp),
                            color = SearchBg,
                            border = BorderStroke(1.dp, CardBorder)
                        ) {
                            Column(modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp)) {
                                relationshipRows.forEachIndexed { index, row ->
                                    RelationshipRowEditor(
                                        selectedLanguage = selectedLanguage,
                                        row = row,
                                        relationshipOptions = relationshipOptions(selectedLanguage),
                                        contactOptions = relationshipContactSuggestions,
                                        onUpdate = { updated -> relationshipRows[index] = updated },
                                        onDelete = { relationshipRows.removeAt(index) },
                                        showDivider = index != relationshipRows.lastIndex
                                    )
                                }
                            }
                        }
                    }

                    AddInlineAction(label = localizedLabel("+ Add Relationship", "+ உறவை சேர்க்க")) {
                        relationshipRows.add(RelationshipRowInput())
                    }
                }
                SectionCard(
                    title = localizedLabel("Notes", "குறிப்புகள்"),
                    expanded = activeSection == AddSection.NOTES,
                    onHeaderClick = {
                        activeSection = if (activeSection == AddSection.NOTES) null else AddSection.NOTES
                    }
                ) {
                    LabeledCompactField(
                        label = localizedLabel("Notes", "குறிப்புகள்"),
                        value = notes,
                        onValueChange = { notes = it },
                        singleLine = false,
                        minLines = 3,
                        maxLines = 6
                    )
                }

                SectionCard(
                    title = localizedLabel("Tags", "குறிச்சொற்கள்"),
                    expanded = activeSection == AddSection.TAGS,
                    onHeaderClick = {
                        activeSection = if (activeSection == AddSection.TAGS) null else AddSection.TAGS
                    }
                ) {
                    if (tagOptions.isEmpty()) {
                        DetailNoteText(
                            localizedLabel(
                                "No tags available. Create tags from Manage Tags.",
                                "குறிச்சொற்கள் இல்லை. குறிச்சொற்கள் நிர்வாகத்தில் உருவாக்கவும்."
                            )
                        )
                    } else {
                        if (selectedTags.isNotEmpty()) {
                            Text(
                                text = localizedLabel("Selected Tags", "தேர்ந்தெடுத்த குறிச்சொற்கள்"),
                                color = MutedText,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 10.5.sp,
                                modifier = Modifier.padding(top = 1.dp)
                            )
                            SelectedTagsTextSummary(
                                tags = selectedTags.toList().sortedWith(String.CASE_INSENSITIVE_ORDER),
                                expanded = showAllSelectedTags,
                                selectedLanguage = selectedLanguage,
                                onToggleExpanded = { showAllSelectedTags = !showAllSelectedTags }
                            )
                        }
                        CompactTagSearchField(
                            value = tagSearch,
                            onValueChange = { tagSearch = it },
                            placeholder = localizedLabel("Search tags", "குறிச்சொற்களைத் தேடுக")
                        )
                        AvailableTagRows(
                            options = tagOptions.filter { tagSearch.isBlank() || it.contains(tagSearch, ignoreCase = true) },
                            selectedTags = selectedTags,
                            selectedLanguage = selectedLanguage,
                            onToggle = { tag ->
                                if (selectedTags.any { it.equals(tag, ignoreCase = true) }) {
                                    selectedTags.removeAll { it.equals(tag, ignoreCase = true) }
                                } else {
                                    selectedTags.add(tag)
                                }
                            }
                        )
                    }

                }
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 18.dp)
            )
        }
    }
}

@Composable
private fun AddContactHeader(
    selectedLanguage: String,
    isEditMode: Boolean,
    onLanguageChange: (String) -> Unit,
    onBack: () -> Unit,
    saveEnabledHighlight: Boolean,
    onSave: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = OrangePrimary,
        shadowElevation = 2.dp
    ) {
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
            Text(
                text = when {
                    isEditMode && selectedLanguage == "TA" -> "தொடர்பை திருத்து"
                    isEditMode -> "Edit Contact"
                    selectedLanguage == "TA" -> "தொடர்பை சேர்க்க"
                    else -> "Add Contact"
                },
                modifier = Modifier.weight(1f),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            AddLanguageToggle(selectedLanguage = selectedLanguage, onLanguageChange = onLanguageChange)
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = onSave,
                modifier = Modifier.defaultMinSize(minHeight = 36.dp),
                shape = RoundedCornerShape(14.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (saveEnabledHighlight) SuccessGreen else SuccessGreen.copy(alpha = 0.55f),
                    contentColor = Color.White
                )
            ) {
                Text(if (selectedLanguage == "TA") "சேமி" else "Save", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun AddLanguageToggle(selectedLanguage: String, onLanguageChange: (String) -> Unit) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.14f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.22f))
    ) {
        Row(modifier = Modifier.padding(horizontal = 2.dp, vertical = 3.dp)) {
            AddLanguagePill(text = "EN", selected = selectedLanguage == "EN") { onLanguageChange("EN") }
            AddLanguagePill(text = "TA", selected = selectedLanguage == "TA") { onLanguageChange("TA") }
        }
    }
}

@Composable
private fun AddLanguagePill(text: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = if (selected) Color.White else Color.Transparent
    ) {
        Box(modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp)) {
            Text(text = text, color = if (selected) OrangePrimary else Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun SectionCard(
    title: String,
    expanded: Boolean,
    onHeaderClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = CardWhite,
        tonalElevation = 0.dp,
        shadowElevation = 1.dp,
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = onHeaderClick),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(title, fontWeight = FontWeight.Bold, color = Color(0xFF242424))
                }
                Icon(
                    imageVector = if (expanded) Icons.Outlined.ExpandLess else Icons.Outlined.ExpandMore,
                    contentDescription = null,
                    tint = MutedText
                )
            }
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                content()
            }
        }
    }
}

@Composable
private fun PhotoPickerRow(
    photoSpec: String?,
    selectedLanguage: String,
    onOpenActions: () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier.size(108.dp), contentAlignment = Alignment.BottomEnd) {
            PhotoAvatar(specString = photoSpec, modifier = Modifier.size(96.dp))
            Surface(
                onClick = onOpenActions,
                shape = CircleShape,
                color = Color(0xFF2F3A44),
                shadowElevation = 2.dp
            ) {
                Box(modifier = Modifier.padding(8.dp), contentAlignment = Alignment.Center) {
                    Icon(Icons.Outlined.Edit, contentDescription = "Edit photo", tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }
        }
        TextButton(onClick = onOpenActions) {
            Text(
                if (photoSpec == null) {
                    if (selectedLanguage == "TA") "படத்தை சேர்க்க" else "Add Photo"
                } else {
                    if (selectedLanguage == "TA") "படத்தை மாற்ற" else "Change Photo"
                },
                color = SuccessGreen,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun PhotoAvatar(specString: String?, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val imageBitmap = remember(specString) { loadPhotoBitmap(context, specString) }
    val spec = decodePhotoSpec(specString)
    Surface(
        modifier = modifier,
        shape = CircleShape,
        color = SearchBg,
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (imageBitmap != null && spec != null) {
                BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                    val sizePx = with(LocalDensity.current) { maxWidth.toPx() }
                    Image(
                        bitmap = imageBitmap,
                        contentDescription = "Contact photo",
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
                Icon(Icons.Outlined.Person, contentDescription = null, tint = MutedText, modifier = Modifier.size(40.dp))
            }
        }
    }
}

@Composable
private fun LabeledCompactField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    required: Boolean = false,
    error: String? = null,
    placeholder: String = "",
    keyboardType: KeyboardType = KeyboardType.Text,
    capitalization: KeyboardCapitalization = KeyboardCapitalization.None,
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = 6,
    readOnly: Boolean = false,
    trailing: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    val borderColor = when {
        !error.isNullOrBlank() -> MaterialTheme.colorScheme.error
        isFocused -> OrangePrimary.copy(alpha = 0.5f)
        else -> CardBorder
    }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                color = Color(0xFF444444),
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            )
            if (required) {
                Text(
                    text = "*",
                    color = MaterialTheme.colorScheme.error,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .let { if (onClick != null) it.clickable { onClick() } else it },
            shape = RoundedCornerShape(14.dp),
            color = SearchBg,
            border = BorderStroke(1.dp, borderColor)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (singleLine) Modifier.height(44.dp)
                        else Modifier.heightIn(min = 96.dp)
                    )
                    .padding(
                        horizontal = 14.dp,
                        vertical = if (singleLine) 0.dp else 12.dp
                    ),
                verticalAlignment = if (singleLine) Alignment.CenterVertically else Alignment.Top
            ) {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.weight(1f),
                    singleLine = singleLine,
                    maxLines = if (singleLine) 1 else maxLines,
                    minLines = if (singleLine) 1 else minLines,
                    readOnly = readOnly,
                    interactionSource = interactionSource,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF202020),
                        fontWeight = FontWeight.Medium,
                        fontSize = 15.sp
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = keyboardType,
                        capitalization = capitalization
                    ),
                    cursorBrush = SolidColor(OrangePrimary),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = if (singleLine) Alignment.CenterStart else Alignment.TopStart
                        ) {
                            if (value.isBlank() && placeholder.isNotBlank()) {
                                Text(
                                    text = placeholder,
                                    color = MutedText.copy(alpha = 0.7f),
                                    fontSize = 15.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            innerTextField()
                        }
                    }
                )
                if (trailing != null) {
                    Box(
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        trailing()
                    }
                }
            }
        }

        if (!error.isNullOrBlank()) {
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                fontSize = 11.sp,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
    }
}

@Composable
private fun GenderRow(selectedLanguage: String, selected: String, onSelected: (String) -> Unit) {
    val title = if (selectedLanguage == "TA") "பாலினம்" else "Gender"
    val labels = if (selectedLanguage == "TA") listOf("ஆண்", "பெண்", "மற்றவை") else listOf("Male", "Female", "Other")
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(title, color = Color(0xFF333333), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            labels.forEach { label ->
                FilterChip(
                    selected = selected == label,
                    onClick = { onSelected(label) },
                    label = { Text(label) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SuccessGreen.copy(alpha = 0.14f),
                        selectedLabelColor = SuccessGreen,
                        containerColor = SearchBg,
                        labelColor = Color(0xFF2A2A2A)
                    ),
                    border = BorderStroke(1.dp, if (selected == label) SuccessGreen.copy(alpha = 0.34f) else CardBorder)
                )
            }
        }
    }
}

@Composable
private fun DobField(
    selectedLanguage: String,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    onOpenPicker: () -> Unit
) {
    val label = if (selectedLanguage == "TA") "பிறந்த தேதி" else "Date of Birth"
    val placeholder = "DD/MM/YYYY"

    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Text(
            text = label,
            color = Color(0xFF444444),
            fontWeight = FontWeight.SemiBold,
            fontSize = 12.sp
        )

        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            color = SearchBg,
            border = BorderStroke(
                1.dp,
                if (isFocused) OrangePrimary.copy(alpha = 0.5f) else CardBorder
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 14.dp),
                    singleLine = true,
                    interactionSource = interactionSource,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF202020),
                        fontWeight = FontWeight.Medium,
                        fontSize = 15.sp
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    cursorBrush = SolidColor(OrangePrimary),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (value.text.isBlank()) {
                                Text(
                                    text = placeholder,
                                    color = MutedText.copy(alpha = 0.7f),
                                    fontSize = 15.sp
                                )
                            }
                            innerTextField()
                        }
                    }
                )

                CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clickable { onOpenPicker() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.CalendarMonth,
                            contentDescription = null,
                            tint = MutedText,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun PremiumDropdownMenuContainer(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    maxHeight: Dp = 280.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest,
        modifier = modifier
            .widthIn(min = 160.dp, max = 320.dp)
            .heightIn(max = maxHeight)
            .padding(vertical = 4.dp),
        shape = RoundedCornerShape(16.dp),
        containerColor = CardWhite,
        tonalElevation = 0.dp,
        shadowElevation = 6.dp,
        border = BorderStroke(1.dp, CardBorder.copy(alpha = 0.9f)),
        content = content
    )
}

@Composable
private fun PremiumDropdownOptionRow(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp, vertical = 3.dp)
            .then(if (enabled) Modifier.clickable(onClick = onClick) else Modifier),
        shape = RoundedCornerShape(10.dp),
        color = if (selected) SuccessGreen.copy(alpha = 0.10f) else CardWhite,
        border = BorderStroke(
            1.dp,
            if (selected) SuccessGreen.copy(alpha = 0.34f) else CardBorder.copy(alpha = 0.70f)
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = text,
                modifier = Modifier.weight(1f),
                color = if (enabled) Color(0xFF202020) else MutedText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                fontSize = 13.sp
            )
            if (selected) {
                Icon(
                    Icons.Outlined.Check,
                    contentDescription = null,
                    tint = SuccessGreen,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
private fun CompactPremiumDropdownField(
    label: String,
    value: String,
    placeholder: String,
    expanded: Boolean,
    onExpandedChange: () -> Unit,
    options: List<String>,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(label, color = Color(0xFF333333), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        Box(modifier = Modifier.wrapContentWidth()) {
            Surface(
                modifier = Modifier
                    .wrapContentWidth()
                    .clickable(onClick = onExpandedChange),
                shape = RoundedCornerShape(10.dp),
                color = CardWhite,
                border = BorderStroke(1.dp, if (expanded) SuccessGreen.copy(alpha = 0.34f) else CardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .defaultMinSize(minHeight = 26.dp)
                        .widthIn(min = 66.dp, max = 220.dp)
                        .padding(horizontal = 10.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = value.ifBlank { placeholder },
                        color = if (value.isBlank()) MutedText.copy(alpha = 0.85f) else Color(0xFF202020),
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Icon(
                        Icons.Outlined.KeyboardArrowDown,
                        contentDescription = null,
                        tint = MutedText,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
            PremiumDropdownMenuContainer(
                expanded = expanded,
                onDismissRequest = onExpandedChange,
                maxHeight = 280.dp
            ) {
                options.forEach { option ->
                    PremiumDropdownOptionRow(
                        text = option,
                        selected = option == value,
                        onClick = { onSelected(option) }
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchableCompactPremiumDropdownField(
    label: String,
    value: String,
    placeholder: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    options: List<String>,
    searchPlaceholder: String,
    noResultsText: String,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val filteredOptions = remember(searchQuery, options) {
        if (searchQuery.isBlank()) options else options.filter { it.contains(searchQuery, ignoreCase = true) }
    }

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(label, color = Color(0xFF333333), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        Box(modifier = Modifier.wrapContentWidth()) {
            Surface(
                modifier = Modifier
                    .wrapContentWidth()
                    .clickable { onExpandedChange(true) },
                shape = RoundedCornerShape(10.dp),
                color = CardWhite,
                border = BorderStroke(1.dp, if (expanded) SuccessGreen.copy(alpha = 0.34f) else CardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .defaultMinSize(minHeight = 26.dp)
                        .widthIn(min = 66.dp, max = 240.dp)
                        .padding(horizontal = 10.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = value.ifBlank { placeholder },
                        color = if (value.isBlank()) MutedText.copy(alpha = 0.85f) else Color(0xFF202020),
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Icon(
                        Icons.Outlined.KeyboardArrowDown,
                        contentDescription = null,
                        tint = MutedText,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpandedChange(false) },
                modifier = Modifier.heightIn(max = 320.dp)
            ) {
                Box(
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                        .widthIn(min = 220.dp, max = 320.dp)
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        color = SearchBg,
                        border = BorderStroke(1.dp, CardBorder)
                    ) {
                        BasicTextField(
                            value = searchQuery,
                            onValueChange = onSearchQueryChange,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyMedium.copy(
                                color = Color(0xFF202020),
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp
                            ),
                            cursorBrush = SolidColor(OrangePrimary),
                            decorationBox = { innerTextField ->
                                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
                                    if (searchQuery.isBlank()) {
                                        Text(
                                            text = searchPlaceholder,
                                            color = MutedText.copy(alpha = 0.75f),
                                            fontSize = 14.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    innerTextField()
                                }
                            }
                        )
                    }
                }

                if (filteredOptions.isEmpty()) {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = noResultsText,
                                color = MutedText,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        onClick = {},
                        enabled = false
                    )
                } else {
                    filteredOptions.forEach { option ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = option,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    fontWeight = if (option == value) FontWeight.SemiBold else FontWeight.Medium
                                )
                            },
                            onClick = {
                                onSelected(option)
                                onExpandedChange(false)
                            },
                            trailingIcon = if (option == value) {
                                { Icon(Icons.Outlined.Check, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp)) }
                            } else null,
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EditableSearchableAddressField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    options: List<String>,
    noResultsText: String,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val filteredOptions = remember(value, options) {
        val query = value.trim()
        if (query.isBlank()) {
            options
        } else {
            options.filter { it.contains(query, ignoreCase = true) }
        }
    }

    Box(modifier = modifier.fillMaxWidth()) {
        LabeledCompactField(
            label = label,
            value = value,
            onValueChange = {
                onValueChange(it)
                onExpandedChange(true)
            },
            placeholder = placeholder,
            trailing = {
                CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clickable { onExpandedChange(!expanded) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.KeyboardArrowDown,
                            contentDescription = null,
                            tint = MutedText,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            },
            onClick = { onExpandedChange(true) }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) },
            modifier = Modifier.heightIn(max = 320.dp)
        ) {
            if (filteredOptions.isEmpty()) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = noResultsText,
                            color = MutedText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    onClick = {},
                    enabled = false
                )
            } else {
                filteredOptions.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = option,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontWeight = if (option == value) FontWeight.SemiBold else FontWeight.Medium
                            )
                        },
                        onClick = {
                            onSelected(option)
                            onExpandedChange(false)
                        },
                        trailingIcon = if (option == value) {
                            { Icon(Icons.Outlined.Check, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp)) }
                        } else null,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}


@Composable
private fun SelectionField(
    label: String,
    value: String,
    placeholder: String,
    expanded: Boolean,
    onExpandedChange: () -> Unit,
    options: List<String>,
    onSelected: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(label, color = Color(0xFF333333), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        Box {
            Surface(
                modifier = Modifier.fillMaxWidth().clickable(onClick = onExpandedChange),
                shape = RoundedCornerShape(16.dp),
                color = CardWhite,
                border = BorderStroke(1.dp, if (expanded) SuccessGreen.copy(alpha = 0.40f) else CardBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 11.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = value.ifBlank { placeholder },
                        color = if (value.isBlank()) MutedText.copy(alpha = 0.85f) else Color(0xFF202020),
                        fontWeight = if (value.isBlank()) FontWeight.Medium else FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Icon(Icons.Outlined.KeyboardArrowDown, contentDescription = null, tint = MutedText, modifier = Modifier.size(16.dp))
                }
            }
            PremiumDropdownMenuContainer(
                expanded = expanded,
                onDismissRequest = onExpandedChange,
                maxHeight = 280.dp
            ) {
                options.forEach { option ->
                    PremiumDropdownOptionRow(
                        text = option,
                        selected = option == value,
                        onClick = { onSelected(option) }
                    )
                }
            }
        }
    }
}

@Composable
private fun PhoneRowEditor(
    selectedLanguage: String,
    row: PhoneRowInput,
    labels: List<String>,
    showError: Boolean,
    isPrimaryRow: Boolean,
    onUpdate: (PhoneRowInput) -> Unit,
    onMakePrimary: () -> Unit,
    onDelete: () -> Unit,
    showDivider: Boolean
) {
    val typeLabel = if (selectedLanguage == "TA") "வகை" else "Type"
    val whatsappAllowed = isWhatsAppAllowed(row.label)

    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, bottom = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = typeLabel,
                color = Color(0xFF333333),
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                maxLines = 1,
                modifier = Modifier.defaultMinSize(minWidth = 30.dp)
            )
            CompactSelectionPicker(
                title = typeLabel,
                value = row.label,
                options = labels,
                modifier = Modifier.wrapContentWidth().widthIn(max = 140.dp),
                onSelected = {
                    val normalized = localizePhoneLabel(it, selectedLanguage)
                    val disableWhatsapp = !isWhatsAppAllowed(normalized)
                    onUpdate(row.copy(label = normalized, isWhatsApp = if (disableWhatsapp) false else row.isWhatsApp))
                }
            )
        }

        PhoneInlineField(
            selectedLanguage = selectedLanguage,
            country = row.country,
            value = row.localNumber,
            error = if (showError) {
                if (selectedLanguage == "TA") "தொலைபேசி எண் தேவை" else "Phone Number is required"
            } else null,
            isPrimaryRow = isPrimaryRow,
            whatsappEnabled = row.isWhatsApp && whatsappAllowed,
            whatsappAllowed = whatsappAllowed,
            onMakePrimary = onMakePrimary,
            onCountrySelected = { onUpdate(row.copy(country = it)) },
            onValueChange = { digits -> onUpdate(row.copy(localNumber = digits)) },
            onWhatsAppToggle = { if (whatsappAllowed) onUpdate(row.copy(isWhatsApp = !row.isWhatsApp)) },
            onDelete = onDelete
        )

        if (showDivider) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 3.dp)
                    .height(1.dp)
                    .background(CardBorder.copy(alpha = 0.75f))
            )
        }
    }
}

@Composable
private fun CompactSelectionPicker(
    title: String,
    value: String,
    options: List<String>,
    modifier: Modifier = Modifier,
    onSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        Surface(
            modifier = Modifier.clickable { expanded = true },
            shape = RoundedCornerShape(10.dp),
            color = CardWhite,
            border = BorderStroke(1.dp, if (expanded) SuccessGreen.copy(alpha = 0.34f) else CardBorder)
        ) {
            Row(
                modifier = Modifier
                    .defaultMinSize(minHeight = 26.dp)
                    .padding(horizontal = 8.dp, vertical = 2.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = value.ifBlank { localizedSelectionPlaceholder(title) },
                    modifier = Modifier.weight(1f, fill = false),
                    color = if (value.isBlank()) MutedText.copy(alpha = 0.85f) else Color(0xFF202020),
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(
                    Icons.Outlined.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MutedText,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        PremiumDropdownMenuContainer(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            maxHeight = 280.dp
        ) {
            options.forEach { option ->
                PremiumDropdownOptionRow(
                    text = option,
                    selected = option == value,
                    onClick = {
                        expanded = false
                        onSelected(option)
                    }
                )
            }
        }
    }
}

@Composable
private fun PhoneInlineField(
    selectedLanguage: String,
    country: CountryOption,
    value: String,
    error: String?,
    isPrimaryRow: Boolean,
    whatsappEnabled: Boolean,
    whatsappAllowed: Boolean,
    onMakePrimary: () -> Unit,
    onCountrySelected: (CountryOption) -> Unit,
    onValueChange: (String) -> Unit,
    onWhatsAppToggle: () -> Unit,
    onDelete: () -> Unit
) {
    val rule = phoneRuleFor(country)
    val placeholder = if (rule.minDigits == rule.maxDigits) {
        if (selectedLanguage == "TA") "${rule.maxDigits} எண்கள்" else "${rule.maxDigits} digits"
    } else {
        if (selectedLanguage == "TA") "${rule.minDigits}-${rule.maxDigits} எண்கள்" else "${rule.minDigits}-${rule.maxDigits} digits"
    }
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            PrimaryTickButton(selected = isPrimaryRow, onClick = onMakePrimary)
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                color = CardWhite,
                border = BorderStroke(
                    1.dp,
                    if (error.isNullOrBlank()) CardBorder else MaterialTheme.colorScheme.error
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    CountryCodePicker(
                        selectedLanguage = selectedLanguage,
                        country = country,
                        onSelected = onCountrySelected,
                        modifier = Modifier.wrapContentWidth(),
                        embedded = true
                    )
                    Box(
                        modifier = Modifier
                            .width(1.dp)
                            .height(20.dp)
                            .background(CardBorder)
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 28.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        BasicTextField(
                            value = value,
                            onValueChange = { raw ->
                                onValueChange(raw.filter(Char::isDigit).take(rule.maxDigits))
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(end = 4.dp),
                            singleLine = true,
                            textStyle = MaterialTheme.typography.bodyMedium.copy(
                                color = Color(0xFF202020),
                                fontWeight = FontWeight.Medium,
                                fontSize = 15.sp
                            ),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            cursorBrush = SolidColor(OrangePrimary),
                            decorationBox = { innerTextField ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .heightIn(min = 28.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    if (value.isBlank()) {
                                        Text(
                                            text = placeholder,
                                            color = MutedText.copy(alpha = 0.78f),
                                            fontSize = 14.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    innerTextField()
                                }
                            }
                        )
                    }
                }
            }
            WhatsAppIconToggle(
                enabled = whatsappEnabled,
                allowed = whatsappAllowed,
                onToggle = onWhatsAppToggle
            )
            DeleteIconButton(onClick = onDelete)
        }
        if (!error.isNullOrBlank()) {
            Text(
                error,
                color = MaterialTheme.colorScheme.error,
                fontSize = 11.sp,
                modifier = Modifier.padding(start = 30.dp)
            )
        }
    }
}

@Composable
private fun PrimaryTickButton(selected: Boolean, onClick: () -> Unit) {
    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
        Surface(
            onClick = onClick,
            shape = CircleShape,
            color = if (selected) SuccessGreen else CardWhite,
            border = BorderStroke(1.dp, if (selected) SuccessGreen else CardBorder),
            modifier = Modifier.size(26.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                if (selected) {
                    Icon(
                        Icons.Outlined.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(13.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(MutedText.copy(alpha = 0.24f))
                    )
                }
            }
        }
    }
}

@Composable
private fun CountryCodePicker(
    selectedLanguage: String,
    country: CountryOption,
    onSelected: (CountryOption) -> Unit,
    modifier: Modifier = Modifier,
    embedded: Boolean = false
) {
    var expanded by remember { mutableStateOf(false) }
    Box(modifier = modifier) {
        Surface(
            modifier = Modifier.clickable { expanded = true },
            shape = RoundedCornerShape(if (embedded) 10.dp else 16.dp),
            color = if (embedded) SearchBg else CardWhite,
            border = BorderStroke(1.dp, if (expanded) SuccessGreen.copy(alpha = 0.34f) else CardBorder)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = if (embedded) 6.dp else 14.dp, vertical = if (embedded) 5.dp else 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = if (embedded) country.code else "${country.code} ${country.compactLabel}",
                    color = Color(0xFF202020),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = if (embedded) 12.sp else 9.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(Icons.Outlined.KeyboardArrowDown, contentDescription = null, tint = MutedText, modifier = Modifier.size(9.dp))
            }
        }
        PremiumDropdownMenuContainer(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            maxHeight = 320.dp,
            modifier = Modifier.widthIn(min = 240.dp, max = 340.dp)
        ) {
            COUNTRY_OPTIONS.forEach { item ->
                val selected = item.code == country.code && item.compactLabel == country.compactLabel
                val optionLabel = "${item.flag} ${if (selectedLanguage == "TA" && item.nameTa.isNotBlank()) item.nameTa else item.nameEn}  ${item.code}"
                PremiumDropdownOptionRow(
                    text = optionLabel,
                    selected = selected,
                    onClick = {
                        expanded = false
                        onSelected(item)
                    }
                )
            }
        }
    }
}

@Composable
private fun WhatsAppIconToggle(enabled: Boolean, allowed: Boolean, onToggle: () -> Unit) {
    val offRed = Color(0xFFD95C5C)
    val tint = when {
        !allowed -> MutedText.copy(alpha = 0.38f)
        enabled -> SuccessGreen
        else -> offRed
    }
    val borderColor = when {
        !allowed -> CardBorder
        enabled -> SuccessGreen
        else -> offRed.copy(alpha = 0.75f)
    }
    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
        Surface(
            onClick = { if (allowed) onToggle() },
            shape = CircleShape,
            color = CardWhite,
            border = BorderStroke(1.dp, borderColor),
            modifier = Modifier.size(26.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_whatsapp),
                    contentDescription = "WhatsApp",
                    tint = tint,
                    modifier = Modifier.size(13.dp)
                )
            }
        }
    }
}

@Composable
private fun DeleteIconButton(onClick: () -> Unit) {
    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
        Surface(
            onClick = onClick,
            shape = CircleShape,
            color = CardWhite,
            border = BorderStroke(1.dp, CardBorder),
            modifier = Modifier.size(26.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Icon(
                    Icons.Outlined.DeleteOutline,
                    contentDescription = "Delete",
                    tint = MutedText,
                    modifier = Modifier.size(13.dp)
                )
            }
        }
    }
}

@Composable
private fun EmailRowEditor(
    selectedLanguage: String,
    row: EmailRowInput,
    labels: List<String>,
    showInvalidError: Boolean,
    showDuplicateWarning: Boolean,
    onUpdate: (EmailRowInput) -> Unit,
    onSetPrimary: () -> Unit,
    onDelete: () -> Unit,
    showDivider: Boolean
) {
    val typeLabel = if (selectedLanguage == "TA") "வகை" else "Type"

    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        if (showDuplicateWarning) {
            Text(
                text = if (selectedLanguage == "TA") "நகல் மின்னஞ்சல் பதிவு" else "Duplicate email entry",
                color = Color(0xFFD95C5C),
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(start = 30.dp, bottom = 2.dp)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, bottom = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = typeLabel,
                color = Color(0xFF333333),
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                maxLines = 1,
                modifier = Modifier.defaultMinSize(minWidth = 30.dp)
            )
            CompactSelectionPicker(
                title = typeLabel,
                value = row.label,
                options = labels,
                modifier = Modifier.wrapContentWidth().widthIn(max = 140.dp),
                onSelected = { onUpdate(row.copy(label = localizeEmailLabel(it, selectedLanguage))) }
            )
        }

        EmailInlineField(
            selectedLanguage = selectedLanguage,
            value = row.email,
            showInvalidError = showInvalidError,
            isPrimaryRow = row.isPrimary,
            onMakePrimary = onSetPrimary,
            onValueChange = { onUpdate(row.copy(email = it)) },
            onDelete = onDelete
        )

        if (showDivider) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 3.dp)
                    .height(1.dp)
                    .background(CardBorder.copy(alpha = 0.75f))
            )
        }
    }
}

@Composable
private fun EmailInlineField(
    selectedLanguage: String,
    value: String,
    showInvalidError: Boolean,
    isPrimaryRow: Boolean,
    onMakePrimary: () -> Unit,
    onValueChange: (String) -> Unit,
    onDelete: () -> Unit
) {
    val placeholder = "name@example.com"
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            PrimaryTickButton(selected = isPrimaryRow, onClick = onMakePrimary)
            Surface(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp),
                color = CardWhite,
                border = BorderStroke(
                    1.dp,
                    if (showInvalidError) MaterialTheme.colorScheme.error else CardBorder
                )
            ) {
                BasicTextField(
                    value = value,
                    onValueChange = { onValueChange(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 11.dp),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFF202020),
                        fontWeight = FontWeight.Medium,
                        fontSize = 15.sp
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    cursorBrush = SolidColor(OrangePrimary),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 28.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            if (value.isBlank()) {
                                Text(
                                    text = placeholder,
                                    color = MutedText.copy(alpha = 0.78f),
                                    fontSize = 14.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            innerTextField()
                        }
                    }
                )
            }
            DeleteIconButton(onClick = onDelete)
        }
        if (showInvalidError) {
            Text(
                text = if (selectedLanguage == "TA") "செல்லுபடியாகும் மின்னஞ்சல் முகவரியை உள்ளிடவும்" else "Enter a valid email address",
                color = MaterialTheme.colorScheme.error,
                fontSize = 11.sp,
                modifier = Modifier.padding(start = 30.dp)
            )
        }
    }
}

@Composable
private fun SearchableRelatedContactField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    options: List<String>,
    placeholder: String,
    noResultsText: String,
    clearContentDescription: String,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val filteredOptions = remember(value, options) {
        val query = value.trim()
        if (query.isBlank()) {
            options
        } else {
            options.filter { it.contains(query, ignoreCase = true) }
        }
    }

    Box(modifier = modifier.fillMaxWidth()) {
        LabeledCompactField(
            label = label,
            value = value,
            onValueChange = { text ->
                onValueChange(text)
                expanded = true
            },
            placeholder = placeholder,
            capitalization = KeyboardCapitalization.Words,
            trailing = {
                if (value.isNotBlank()) {
                    CompositionLocalProvider(LocalMinimumInteractiveComponentSize provides Dp.Unspecified) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clickable {
                                    onValueChange("")
                                    expanded = false
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Outlined.Close,
                                contentDescription = clearContentDescription,
                                tint = MutedText,
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    }
                }
            },
            onClick = { expanded = true }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.heightIn(max = 300.dp),
            properties = PopupProperties(focusable = false)
        ) {
            if (filteredOptions.isEmpty()) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = noResultsText,
                            color = MutedText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    onClick = {},
                    enabled = false,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                )
            } else {
                filteredOptions.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = option,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontWeight = if (option == value) FontWeight.SemiBold else FontWeight.Medium
                            )
                        },
                        onClick = {
                            onValueChange(option)
                            expanded = false
                        },
                        trailingIcon = if (option == value) {
                            { Icon(Icons.Outlined.Check, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp)) }
                        } else null,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun RelationshipRowEditor(
    selectedLanguage: String,
    row: RelationshipRowInput,
    relationshipOptions: List<String>,
    contactOptions: List<String>,
    onUpdate: (RelationshipRowInput) -> Unit,
    onDelete: () -> Unit,
    showDivider: Boolean
) {
    val typeLabel = if (selectedLanguage == "TA") "உறவு வகை" else "Relationship Type"
    val relatedContactLabel = if (selectedLanguage == "TA") "தொடர்பில் உள்ளவர்" else "Related Contact"
    val referenceNameLabel = if (selectedLanguage == "TA") "குறிப்பு பெயர்" else "Reference Contact Name"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 4.dp, bottom = 2.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = typeLabel,
                color = Color(0xFF333333),
                fontWeight = FontWeight.SemiBold,
                fontSize = 11.sp,
                maxLines = 1,
                modifier = Modifier.defaultMinSize(minWidth = 74.dp)
            )
            CompactSelectionPicker(
                title = typeLabel,
                value = row.type,
                options = relationshipOptions,
                modifier = Modifier.wrapContentWidth().widthIn(max = 190.dp),
                onSelected = { selected ->
                    onUpdate(row.copy(type = localizeRelationshipValue(selected, selectedLanguage)))
                }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            SearchableRelatedContactField(
                label = relatedContactLabel,
                value = row.relatedContact,
                onValueChange = { onUpdate(row.copy(relatedContact = it)) },
                options = contactOptions,
                placeholder = if (selectedLanguage == "TA") "சேமித்த தொடர்பை தேடுக" else "Search existing contact",
                noResultsText = if (selectedLanguage == "TA") "பொருந்தும் தொடர்புகள் இல்லை" else "No matching contacts",
                clearContentDescription = if (selectedLanguage == "TA") "தொடர்பை அழி" else "Clear contact",
                modifier = Modifier.weight(1f)
            )
            Box(
                modifier = Modifier
                    .padding(bottom = 1.dp)
                    .height(44.dp),
                contentAlignment = Alignment.Center
            ) {
                DeleteIconButton(onClick = onDelete)
            }
        }

        LabeledCompactField(
            label = referenceNameLabel,
            value = row.relatedName,
            onValueChange = { onUpdate(row.copy(relatedName = it)) },
            placeholder = if (selectedLanguage == "TA") {
                "சேமிக்கப்படாத நபரின் பெயர்"
            } else {
                "Name if contact is not saved"
            },
            capitalization = KeyboardCapitalization.Words
        )

        if (showDivider) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp, bottom = 3.dp)
                    .height(1.dp)
                    .background(CardBorder.copy(alpha = 0.75f))
            )
        }
    }
}
@Composable
private fun SmallSelectionField(label: String, value: String, options: List<String>, onSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
        Text(label, color = Color(0xFF333333), fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        Box {
            Surface(
                modifier = Modifier.fillMaxWidth().clickable { expanded = true },
                shape = RoundedCornerShape(16.dp),
                color = CardWhite,
                border = BorderStroke(1.dp, if (expanded) SuccessGreen.copy(alpha = 0.40f) else CardBorder)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 11.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        value.ifBlank { localizedSelectionPlaceholder(label) },
                        color = if (value.isBlank()) MutedText.copy(alpha = 0.85f) else Color(0xFF202020),
                        fontWeight = if (value.isBlank()) FontWeight.Medium else FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Icon(Icons.Outlined.KeyboardArrowDown, contentDescription = null, tint = MutedText, modifier = Modifier.size(16.dp))
                }
            }
            PremiumDropdownMenuContainer(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                maxHeight = 280.dp
            ) {
                options.forEach { option ->
                    PremiumDropdownOptionRow(
                        text = option,
                        selected = option == value,
                        onClick = {
                            expanded = false
                            onSelected(option)
                        }
                    )
                }
            }
        }
    }
}


private fun displayNameOnly(value: String): String {
    val cleaned = value.trim()
    if (cleaned.isBlank()) return cleaned
    return cleaned
        .substringBefore("•")
        .substringBefore("|")
        .substringBefore(" - ")
        .trim()
}

private fun localizedSelectionPlaceholder(label: String): String = when (label) {
    "வகை", "Relationship Type", "உறவு வகை", "Type" -> if (label.contains("வகை")) "தேர்வு செய்க" else "Select"
    else -> "Select"
}

@Composable
private fun OptionPickerDialog(
    title: String,
    options: List<Any>,
    selectedLabel: String,
    labelForOption: (Any) -> String,
    onDismiss: () -> Unit,
    onSelected: (Any) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 320.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                options.forEach { option ->
                    val label = labelForOption(option)
                    val isSelected = label == selectedLabel || (selectedLabel.isNotBlank() && label.endsWith(selectedLabel))
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) SuccessGreen.copy(alpha = 0.10f) else SearchBg,
                        border = BorderStroke(1.dp, if (isSelected) SuccessGreen.copy(alpha = 0.30f) else CardBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onSelected(option); onDismiss() }
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(label, color = Color(0xFF202020), fontWeight = FontWeight.Medium)
                            if (isSelected) {
                                Icon(Icons.Outlined.Check, contentDescription = null, tint = SuccessGreen)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

@Composable
private fun ActionRowButton(label: String, danger: Boolean = false, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = if (danger) Color(0xFFFFF1F0) else SearchBg,
        border = BorderStroke(1.dp, if (danger) Color(0xFFFFCCC7) else CardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(label, color = if (danger) Color(0xFFB42318) else Color(0xFF2D2D2D), fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun DetailNoteText(text: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = SearchBg,
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Text(
            text = text,
            color = MutedText,
            fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp)
        )
    }
}

@Composable
private fun AvailableTagRows(
    options: List<String>,
    selectedTags: List<String>,
    selectedLanguage: String,
    onToggle: (String) -> Unit
) {
    if (options.isEmpty()) {
        DetailNoteText(if (selectedLanguage == "TA") "பொருந்தும் குறிச்சொற்கள் இல்லை" else "No matching tags")
        return
    }
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 190.dp),
        shape = RoundedCornerShape(12.dp),
        color = SearchBg,
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {
            options.forEachIndexed { index, tag ->
                val selected = selectedTags.any { it.equals(tag, ignoreCase = true) }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onToggle(tag) }
                        .padding(horizontal = 10.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = tag,
                        color = Color(0xFF232323),
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                        fontSize = 12.8.sp,
                        lineHeight = 16.sp,
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (selected) {
                        Icon(Icons.Outlined.Check, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(15.dp))
                    }
                }
                if (index != options.lastIndex) {
                    HorizontalDivider(color = CardBorder.copy(alpha = 0.60f))
                }
            }
        }
    }
}


@Composable
private fun AddInlineAction(label: String, onClick: () -> Unit) {
    TextButton(onClick = onClick, contentPadding = PaddingValues(horizontal = 0.dp, vertical = 0.dp)) {
        Text(label, color = SuccessGreen, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun SmallSecondaryActionButton(label: String, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        color = SearchBg,
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Box(modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)) {
            Text(label, color = Color(0xFF2E2E2E), fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun SelectedTagsTextSummary(
    tags: List<String>,
    expanded: Boolean,
    selectedLanguage: String,
    onToggleExpanded: () -> Unit
) {
    val visible = if (expanded) tags else tags.take(3)
    val hiddenCount = (tags.size - visible.size).coerceAtLeast(0)
    val canToggle = tags.size > 3
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 3.dp, bottom = 7.dp),
        shape = RoundedCornerShape(12.dp),
        color = SearchBg,
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            visible.forEachIndexed { index, tag ->
                Text(
                    text = tag,
                    color = Color(0xFF2E2E2E),
                    fontSize = 12.5.sp,
                    lineHeight = 16.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                )
                if (index != visible.lastIndex || canToggle) {
                    HorizontalDivider(color = CardBorder.copy(alpha = 0.55f))
                }
            }
            if (canToggle) {
                Text(
                    text = if (expanded) {
                        if (selectedLanguage == "TA") "குறைவாக காட்டு" else "Show less"
                    } else {
                        if (selectedLanguage == "TA") "+$hiddenCount மேலும்" else "+$hiddenCount more"
                    },
                    color = SuccessGreen,
                    fontSize = 12.5.sp,
                    lineHeight = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onToggleExpanded)
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}


@Composable
private fun CompactTagSearchField(value: String, onValueChange: (String) -> Unit, placeholder: String) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(36.dp),
        shape = RoundedCornerShape(12.dp),
        color = SearchBg,
        border = BorderStroke(1.dp, CardBorder)
    ) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            textStyle = androidx.compose.ui.text.TextStyle(
                color = Color(0xFF232323),
                fontSize = 13.sp,
                lineHeight = 16.sp
            ),
            cursorBrush = SolidColor(OrangePrimary),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            decorationBox = { innerTextField ->
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.CenterStart) {
                    if (value.isBlank()) {
                        Text(
                            text = placeholder,
                            color = MutedText.copy(alpha = 0.65f),
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}


@Composable
private fun SaveBottomBar(selectedLanguage: String, highlighted: Boolean, onSave: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        shadowElevation = 8.dp,
        color = CardWhite,
        border = BorderStroke(1.dp, CardBorder.copy(alpha = 0.7f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = onSave,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (highlighted) SuccessGreen else SuccessGreen.copy(alpha = 0.55f),
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(horizontal = 22.dp, vertical = 12.dp)
            ) {
                Text(if (selectedLanguage == "TA") "சேமி" else "Save", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun PhotoAdjustScreen(
    source: String,
    selectedLanguage: String,
    initialScale: Float,
    initialOffsetX: Float,
    initialOffsetY: Float,
    onDismiss: () -> Unit,
    onConfirm: (String, Float, Float, Float) -> Unit
) {
    val context = LocalContext.current
    val sourceBitmap = remember(source) { loadPhotoAndroidBitmap(context, source) }
    val imageBitmap = remember(sourceBitmap) { sourceBitmap?.asImageBitmap() }

    var scale by rememberSaveable(source) { mutableStateOf(initialScale.coerceIn(1f, 5f)) }
    var offsetX by rememberSaveable(source) { mutableStateOf(initialOffsetX) }
    var offsetY by rememberSaveable(source) { mutableStateOf(initialOffsetY) }
    var cropSizeFactor by rememberSaveable(source) { mutableStateOf(0.72f) }
    var viewportWidthPx by remember { mutableStateOf(0f) }
    var viewportHeightPx by remember { mutableStateOf(0f) }

    fun commitCrop() {
        val bitmap = sourceBitmap
        val cropDiameterPx = (minOf(viewportWidthPx, viewportHeightPx) * cropSizeFactor).coerceAtLeast(80f)
        val croppedSource = if (bitmap != null && viewportWidthPx > 0f && viewportHeightPx > 0f) {
            cropVisiblePhotoToCache(
                context = context,
                bitmap = bitmap,
                source = source,
                viewportWidthPx = viewportWidthPx,
                viewportHeightPx = viewportHeightPx,
                cropCenterXPx = viewportWidthPx / 2f,
                cropCenterYPx = viewportHeightPx / 2f,
                cropDiameterPx = cropDiameterPx,
                scale = scale,
                offsetX = offsetX,
                offsetY = offsetY
            )
        } else null
        onConfirm(croppedSource ?: source, 1f, 0f, 0f)
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(Color.Black)
                .pointerInput(source) {
                    detectTransformGestures(panZoomLock = false) { _, pan, zoom, _ ->
                        scale = (scale * zoom).coerceIn(1f, 5f)
                        offsetX += pan.x
                        offsetY += pan.y
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            val density = LocalDensity.current
            viewportWidthPx = with(density) { maxWidth.toPx() }
            viewportHeightPx = with(density) { maxHeight.toPx() }
            val cropDiameter = minOf(maxWidth, maxHeight) * cropSizeFactor

            if (imageBitmap != null) {
                Image(
                    bitmap = imageBitmap,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(
                            scaleX = scale,
                            scaleY = scale,
                            translationX = offsetX,
                            translationY = offsetY
                        )
                )
            } else {
                Text(
                    text = if (selectedLanguage == "TA") "படத்தை திறக்க முடியவில்லை" else "Unable to open photo",
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(18.dp)
                )
            }

            Canvas(
                modifier = Modifier
                    .matchParentSize()
                    .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
            ) {
                val radius = cropDiameter.toPx() / 2f
                val center = Offset(size.width / 2f, size.height / 2f)
                drawRect(Color.Black.copy(alpha = 0.48f))
                drawCircle(Color.Transparent, radius = radius, center = center, blendMode = BlendMode.Clear)
                drawCircle(
                    color = Color.White.copy(alpha = 0.30f),
                    radius = radius,
                    center = center,
                    style = Stroke(width = 1.2.dp.toPx())
                )
                drawCircle(
                    color = Color.White.copy(alpha = 0.78f),
                    radius = radius,
                    center = center,
                    style = Stroke(width = 1.6.dp.toPx())
                )

                val left = center.x - radius
                val right = center.x + radius
                val top = center.y - radius
                val bottom = center.y + radius
                val bracket = 42.dp.toPx()
                val stroke = 3.dp.toPx()
                val bracketColor = Color.White.copy(alpha = 0.96f)
                drawLine(bracketColor, Offset(left, top), Offset(left + bracket, top), stroke)
                drawLine(bracketColor, Offset(left, top), Offset(left, top + bracket), stroke)
                drawLine(bracketColor, Offset(right, top), Offset(right - bracket, top), stroke)
                drawLine(bracketColor, Offset(right, top), Offset(right, top + bracket), stroke)
                drawLine(bracketColor, Offset(left, bottom), Offset(left + bracket, bottom), stroke)
                drawLine(bracketColor, Offset(left, bottom), Offset(left, bottom - bracket), stroke)
                drawLine(bracketColor, Offset(right, bottom), Offset(right - bracket, bottom), stroke)
                drawLine(bracketColor, Offset(right, bottom), Offset(right, bottom - bracket), stroke)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(104.dp)
                .background(Color.Black)
                .padding(horizontal = 44.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = onDismiss) {
                Text(
                    text = if (selectedLanguage == "TA") "ரத்து" else "Cancel",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            TextButton(onClick = { commitCrop() }) {
                Text(
                    text = if (selectedLanguage == "TA") "முடிந்தது" else "Done",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

private fun cropVisiblePhotoToCache(
    context: Context,
    bitmap: Bitmap,
    source: String,
    viewportWidthPx: Float,
    viewportHeightPx: Float,
    cropCenterXPx: Float,
    cropCenterYPx: Float,
    cropDiameterPx: Float,
    scale: Float,
    offsetX: Float,
    offsetY: Float
): String? = runCatching {
    val baseFitScale = minOf(viewportWidthPx / bitmap.width.toFloat(), viewportHeightPx / bitmap.height.toFloat())
    val displayedWidth = bitmap.width * baseFitScale * scale
    val displayedHeight = bitmap.height * baseFitScale * scale
    val displayedLeft = (viewportWidthPx - displayedWidth) / 2f + offsetX
    val displayedTop = (viewportHeightPx - displayedHeight) / 2f + offsetY
    val sourceScale = baseFitScale * scale

    val cropLeftInViewport = cropCenterXPx - cropDiameterPx / 2f
    val cropTopInViewport = cropCenterYPx - cropDiameterPx / 2f
    val sourceLeft = ((cropLeftInViewport - displayedLeft) / sourceScale).coerceIn(0f, bitmap.width.toFloat())
    val sourceTop = ((cropTopInViewport - displayedTop) / sourceScale).coerceIn(0f, bitmap.height.toFloat())
    val sourceRight = ((cropLeftInViewport + cropDiameterPx - displayedLeft) / sourceScale).coerceIn(0f, bitmap.width.toFloat())
    val sourceBottom = ((cropTopInViewport + cropDiameterPx - displayedTop) / sourceScale).coerceIn(0f, bitmap.height.toFloat())

    val sourceSize = minOf(sourceRight - sourceLeft, sourceBottom - sourceTop).coerceAtLeast(1f)
    val adjustedLeft = sourceLeft.coerceAtMost(bitmap.width - sourceSize)
    val adjustedTop = sourceTop.coerceAtMost(bitmap.height - sourceSize)

    val outputSize = 512
    val output = Bitmap.createBitmap(outputSize, outputSize, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(output)
    val paint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG or android.graphics.Paint.FILTER_BITMAP_FLAG)
    canvas.drawColor(android.graphics.Color.TRANSPARENT)
    canvas.drawBitmap(
        bitmap,
        android.graphics.Rect(
            adjustedLeft.toInt(),
            adjustedTop.toInt(),
            (adjustedLeft + sourceSize).toInt().coerceAtMost(bitmap.width),
            (adjustedTop + sourceSize).toInt().coerceAtMost(bitmap.height)
        ),
        android.graphics.Rect(0, 0, outputSize, outputSize),
        paint
    )
    saveBitmapToCache(context, output) ?: source
}.getOrNull()

private fun specOffsetToDialogPx(value: Float, viewportSize: Float): Float = if (abs(value) > 3f) value else value * viewportSize

private fun formatDateFieldValue(input: TextFieldValue): TextFieldValue {
    val digits = input.text.filter(Char::isDigit).take(8)
    val formatted = buildString {
        digits.forEachIndexed { index, ch ->
            append(ch)
            if ((index == 1 || index == 3) && index != digits.lastIndex) append('/')
        }
    }
    return TextFieldValue(
        text = formatted,
        selection = TextRange(formatted.length)
    )
}

private fun showDatePicker(context: Context, currentValue: String, onSelected: (String) -> Unit) {
    val calendar = Calendar.getInstance()
    val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    runCatching { formatter.parse(currentValue) }.getOrNull()?.let { parsed -> calendar.time = parsed }

    DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            onSelected(String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year))
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    ).show()
}

private fun saveBitmapToCache(context: Context, bitmap: Bitmap): String? = runCatching {
    val file = File(context.cacheDir, "contact_${System.currentTimeMillis()}.jpg")
    FileOutputStream(file).use { out -> bitmap.compress(Bitmap.CompressFormat.JPEG, 92, out) }
    Uri.fromFile(file).toString()
}.getOrNull()

private fun loadPhotoBitmap(context: Context, specString: String?): ImageBitmap? {
    val spec = decodePhotoSpec(specString) ?: return null
    return loadPhotoAndroidBitmap(context, spec.source)?.asImageBitmap()
}

private fun loadPhotoAndroidBitmap(context: Context, source: String): Bitmap? {
    val uri = Uri.parse(source)
    return runCatching {
        val bytes = if (uri.scheme == "file") {
            val path = uri.path ?: return@runCatching null
            File(path).takeIf { it.exists() }?.readBytes()
        } else {
            context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
        } ?: return@runCatching null

        val rawBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size) ?: return@runCatching null
        rotateBitmapIfRequired(rawBitmap, bytes)
    }.getOrNull()
}

private fun rotateBitmapIfRequired(bitmap: Bitmap, bytes: ByteArray): Bitmap {
    val orientation = runCatching {
        ExifInterface(ByteArrayInputStream(bytes)).getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )
    }.getOrDefault(ExifInterface.ORIENTATION_NORMAL)

    val matrix = Matrix().apply {
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> postRotate(270f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> preScale(-1f, 1f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> preScale(1f, -1f)
        }
    }
    if (matrix.isIdentity) return bitmap
    return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
}

private fun localizeGenderValue(value: String, selectedLanguage: String): String =
    localizeMappedValue(value, selectedLanguage, listOf("Male" to "ஆண்", "Female" to "பெண்", "Other" to "மற்றவை"))

private fun localizePhoneLabel(value: String, selectedLanguage: String): String =
    localizeMappedValue(value, selectedLanguage, PHONE_LABELS)

private fun localizeEmailLabel(value: String, selectedLanguage: String): String =
    localizeMappedValue(value, selectedLanguage, EMAIL_LABELS)

private fun localizeRasiValue(value: String, selectedLanguage: String): String =
    localizeMappedValue(value, selectedLanguage, RASI_LABELS)

private fun localizeNakshatraValue(value: String, selectedLanguage: String): String =
    localizeMappedValue(value, selectedLanguage, NAKSHATRA_LABELS)

private fun localizeRelationshipValue(value: String, selectedLanguage: String): String =
    localizeMappedValue(value, selectedLanguage, RELATIONSHIP_LABELS)

private fun countryOptionForSavedPhone(phone: ContactPhoneRecord): CountryOption {
    val trimmedNumber = phone.displayNumber.trim()
    return COUNTRY_OPTIONS.firstOrNull { option ->
        option.code == phone.countryCode && (phone.countryCompactLabel.isBlank() || option.compactLabel == phone.countryCompactLabel)
    } ?: COUNTRY_OPTIONS.firstOrNull { option ->
        option.code == phone.countryCode && option.nameEn.equals(phone.countryName, ignoreCase = true)
    } ?: COUNTRY_OPTIONS.firstOrNull { option ->
        option.code == phone.countryCode
    } ?: COUNTRY_OPTIONS
        .sortedByDescending { it.code.length }
        .firstOrNull { option -> trimmedNumber.startsWith(option.code) }
    ?: COUNTRY_OPTIONS.firstOrNull { option ->
        option.nameEn.equals(phone.countryName, ignoreCase = true) || option.compactLabel.equals(phone.countryCompactLabel, ignoreCase = true)
    } ?: COUNTRY_OPTIONS.first()
}

private fun localNumberFromDisplayNumber(displayNumber: String, countryCode: String): String {
    val trimmed = displayNumber.trim()
    return when {
        trimmed.isBlank() -> ""
        countryCode.isNotBlank() && trimmed.startsWith(countryCode) -> trimmed.removePrefix(countryCode).trim()
        else -> trimmed
    }
}

private fun localizeMappedValue(value: String, selectedLanguage: String, mapping: List<Pair<String, String>>): String {
    if (value.isBlank()) return value
    val match = mapping.firstOrNull { it.first == value || it.second == value } ?: return value
    return if (selectedLanguage == "TA") match.second else match.first
}

private fun isBlankRelationshipRow(row: RelationshipRowInput): Boolean =
    row.type.isBlank() && row.relatedContact.isBlank() && row.relatedName.isBlank()

private fun isValidRelationshipRow(row: RelationshipRowInput): Boolean {
    if (isBlankRelationshipRow(row)) return true
    val hasType = row.type.isNotBlank()
    val hasContactOrName = row.relatedContact.isNotBlank() || row.relatedName.isNotBlank()
    return hasType && hasContactOrName
}

private fun relationshipValidationMessage(row: RelationshipRowInput, selectedLanguage: String): String {
    val hasType = row.type.isNotBlank()
    val hasContactOrName = row.relatedContact.isNotBlank() || row.relatedName.isNotBlank()
    return when {
        !hasType && hasContactOrName -> {
            if (selectedLanguage == "TA") "உறவு வகையை தேர்வு செய்யவும்" else "Select relationship type"
        }
        hasType && !hasContactOrName -> {
            if (selectedLanguage == "TA") "தொடர்பை தேர்வு செய்யவும் அல்லது பெயரை உள்ளிடவும்" else "Select contact or enter reference name"
        }
        else -> {
            if (selectedLanguage == "TA") "உறவு விவரத்தை சரிபார்க்கவும்" else "Check relationship details"
        }
    }
}
private data class AddressFieldText(
    val label: String,
    val placeholder: String
)

private fun addressFieldText(
    field: String,
    isIndia: Boolean,
    selectedLanguage: String
): AddressFieldText {
    val ta = selectedLanguage == "TA"

    return when (field) {
        "doorNo" -> if (isIndia) {
            AddressFieldText(
                label = if (ta) "வீட்டு எண்" else "Door No",
                placeholder = if (ta) "கதவு எண்" else "Door No"
            )
        } else {
            AddressFieldText(
                label = if (ta) "வீட்டு எண் / யூனிட் / அப்ட் எண்" else "Door No / Unit / Apt No",
                placeholder = if (ta) "கதவு எண் / யூனிட்" else "Door No / Unit"
            )
        }

        "buildingName" -> if (isIndia) {
            AddressFieldText(
                label = if (ta) "கட்டிடம் பெயர்" else "Building Name",
                placeholder = if (ta) "கட்டிடம்" else "Building"
            )
        } else {
            AddressFieldText(
                label = if (ta) "கட்டிடம் / டவர்" else "Building Name / Tower",
                placeholder = if (ta) "கட்டிடம் / டவர்" else "Building / Tower"
            )
        }

        "streetName" -> if (isIndia) {
            AddressFieldText(
                label = if (ta) "தெரு பெயர்" else "Street Name",
                placeholder = if (ta) "தெரு" else "Street"
            )
        } else {
            AddressFieldText(
                label = if (ta) "தெரு பெயர் / தெரு / அவென்யூ" else "Street Name / Street / Avenue",
                placeholder = if (ta) "தெரு / அவென்யூ" else "Street / Avenue"
            )
        }

        "area" -> if (isIndia) {
            AddressFieldText(
                label = if (ta) "கிராமம் / பகுதி" else "Village / Area",
                placeholder = if (ta) "பகுதி" else "Area"
            )
        } else {
            AddressFieldText(
                label = if (ta) "கிராமம் / பகுதி / இருப்பிடம்" else "Village / Area / Locality",
                placeholder = if (ta) "பகுதி / இருப்பிடம்" else "Area / Locality"
            )
        }

        "postOffice" -> if (isIndia) {
            AddressFieldText(
                label = if (ta) "அஞ்சலகம்" else "Post Office",
                placeholder = if (ta) "அஞ்சலகம்" else "Post Office"
            )
        } else {
            AddressFieldText(
                label = if (ta) "அஞ்சலகம் / மெயில் பகுதி" else "Post Office / Mail Area",
                placeholder = if (ta) "மெயில் பகுதி" else "Mail Area"
            )
        }

        "taluk" -> if (isIndia) {
            AddressFieldText(
                label = if (ta) "தாலுகா" else "Taluk",
                placeholder = if (ta) "தாலுகா" else "Taluk"
            )
        } else {
            AddressFieldText(
                label = if (ta) "தாலுகா / கவுண்டி" else "Taluk / County",
                placeholder = if (ta) "கவுண்டி" else "County"
            )
        }

        "district" -> if (isIndia) {
            AddressFieldText(
                label = if (ta) "மாவட்டம்" else "District",
                placeholder = if (ta) "மாவட்டம்" else "District"
            )
        } else {
            AddressFieldText(
                label = if (ta) "மாவட்டம் / கவுண்டி" else "District / County",
                placeholder = if (ta) "மாவட்டம் / கவுண்டி" else "District / County"
            )
        }

        "state" -> if (isIndia) {
            AddressFieldText(
                label = if (ta) "மாநிலம்" else "State",
                placeholder = if (ta) "மாநிலம்" else "State"
            )
        } else {
            AddressFieldText(
                label = if (ta) "மாநிலம் / மாகாணம் / பிராந்தியம்" else "State / Province / Region",
                placeholder = if (ta) "மாநிலம் / மாகாணம்" else "State / Province"
            )
        }

        else -> AddressFieldText(label = field, placeholder = "")
    }
}

private data class PhoneDigitRule(val minDigits: Int, val maxDigits: Int)

private fun phoneRuleFor(country: CountryOption): PhoneDigitRule = when (country.code) {
    "+1" -> PhoneDigitRule(10, 10)
    "+44" -> PhoneDigitRule(10, 10)
    "+65" -> PhoneDigitRule(8, 8)
    "+852" -> PhoneDigitRule(8, 8)
    "+86" -> PhoneDigitRule(11, 11)
    "+91" -> PhoneDigitRule(10, 10)
    "+971" -> PhoneDigitRule(8, 9)
    "+62" -> PhoneDigitRule(9, 12)
    "+60" -> PhoneDigitRule(9, 10)
    "+61" -> PhoneDigitRule(9, 9)
    "+94" -> PhoneDigitRule(9, 9)
    else -> PhoneDigitRule(8, 12)
}

private fun isPhoneLengthValid(row: PhoneRowInput): Boolean {
    if (row.localNumber.isBlank()) return false
    val rule = phoneRuleFor(row.country)
    val digits = row.localDigits()
    return digits.length in rule.minDigits..rule.maxDigits
}

private fun invalidPhoneErrorMessage(row: PhoneRowInput, selectedLanguage: String): String {
    val rule = phoneRuleFor(row.country)
    return if (rule.minDigits == rule.maxDigits) {
        if (selectedLanguage == "TA") "${row.country.code} க்கு ${rule.maxDigits} இலக்கங்கள் தேவை" else "${row.country.code} needs ${rule.maxDigits} digits"
    } else {
        if (selectedLanguage == "TA") "${row.country.code} க்கு ${rule.minDigits}-${rule.maxDigits} இலக்கங்கள் தேவை" else "${row.country.code} needs ${rule.minDigits}-${rule.maxDigits} digits"
    }
}


private fun isValidEmailAddress(value: String): Boolean =
    Patterns.EMAIL_ADDRESS.matcher(value.trim()).matches()

private fun normalizedEmail(value: String): String = value.trim().lowercase()

private fun isWhatsAppAllowed(label: String): Boolean {
    val normalized = PHONE_LABELS.firstOrNull { it.first == label || it.second == label }?.first ?: label
    return normalized != "Landline" && normalized != "Fax"
}

private inline fun <T> Iterable<T>.anyIndexed(predicate: (Int, T) -> Boolean): Boolean {
    forEachIndexed { index, item -> if (predicate(index, item)) return true }
    return false
}

private fun PhoneRowInput.localDigits(): String = localNumber.filter(Char::isDigit)



private data class AddressDistrictPath(
    val district: String,
    val state: String,
    val country: String
)

private val INDIA_STATE_NAMES = listOf(
    "Andhra Pradesh",
    "Arunachal Pradesh",
    "Assam",
    "Bihar",
    "Chhattisgarh",
    "Goa",
    "Gujarat",
    "Haryana",
    "Himachal Pradesh",
    "Jharkhand",
    "Karnataka",
    "Kerala",
    "Madhya Pradesh",
    "Maharashtra",
    "Manipur",
    "Meghalaya",
    "Mizoram",
    "Nagaland",
    "Odisha",
    "Punjab",
    "Rajasthan",
    "Sikkim",
    "Tamil Nadu",
    "Telangana",
    "Tripura",
    "Uttar Pradesh",
    "Uttarakhand",
    "West Bengal",
    "Andaman and Nicobar Islands",
    "Chandigarh",
    "Dadra and Nagar Haveli and Daman and Diu",
    "Delhi",
    "Jammu and Kashmir",
    "Ladakh",
    "Lakshadweep",
    "Puducherry"
)

private val OTHER_STATE_MASTER = mapOf(
    "United States" to listOf("Alabama", "Alaska", "Arizona", "Arkansas", "California", "Colorado", "Connecticut", "Delaware", "Florida", "Georgia", "Hawaii", "Idaho", "Illinois", "Indiana", "Iowa", "Kansas", "Kentucky", "Louisiana", "Maine", "Maryland", "Massachusetts", "Michigan", "Minnesota", "Mississippi", "Missouri", "Montana", "Nebraska", "Nevada", "New Hampshire", "New Jersey", "New Mexico", "New York", "North Carolina", "North Dakota", "Ohio", "Oklahoma", "Oregon", "Pennsylvania", "Rhode Island", "South Carolina", "South Dakota", "Tennessee", "Texas", "Utah", "Vermont", "Virginia", "Washington", "West Virginia", "Wisconsin", "Wyoming", "District of Columbia"),
    "Singapore" to listOf("Singapore"),
    "United Arab Emirates" to listOf("Abu Dhabi", "Ajman", "Dubai", "Fujairah", "Ras Al Khaimah", "Sharjah", "Umm Al Quwain"),
    "Malaysia" to listOf("Johor", "Kedah", "Kelantan", "Kuala Lumpur", "Labuan", "Malacca", "Negeri Sembilan", "Pahang", "Penang", "Perak", "Perlis", "Putrajaya", "Sabah", "Sarawak", "Selangor", "Terengganu"),
    "Sri Lanka" to listOf("Central", "Eastern", "North Central", "Northern", "North Western", "Sabaragamuwa", "Southern", "Uva", "Western")
)

private val ADDRESS_DISTRICT_PATHS = listOf(
    // Tamil Nadu
    AddressDistrictPath("Ariyalur", "Tamil Nadu", "India"),
    AddressDistrictPath("Chengalpattu", "Tamil Nadu", "India"),
    AddressDistrictPath("Chennai", "Tamil Nadu", "India"),
    AddressDistrictPath("Coimbatore", "Tamil Nadu", "India"),
    AddressDistrictPath("Cuddalore", "Tamil Nadu", "India"),
    AddressDistrictPath("Dharmapuri", "Tamil Nadu", "India"),
    AddressDistrictPath("Dindigul", "Tamil Nadu", "India"),
    AddressDistrictPath("Erode", "Tamil Nadu", "India"),
    AddressDistrictPath("Kallakurichi", "Tamil Nadu", "India"),
    AddressDistrictPath("Kancheepuram", "Tamil Nadu", "India"),
    AddressDistrictPath("Kanyakumari", "Tamil Nadu", "India"),
    AddressDistrictPath("Karur", "Tamil Nadu", "India"),
    AddressDistrictPath("Krishnagiri", "Tamil Nadu", "India"),
    AddressDistrictPath("Madurai", "Tamil Nadu", "India"),
    AddressDistrictPath("Mayiladuthurai", "Tamil Nadu", "India"),
    AddressDistrictPath("Nagapattinam", "Tamil Nadu", "India"),
    AddressDistrictPath("Namakkal", "Tamil Nadu", "India"),
    AddressDistrictPath("Nilgiris", "Tamil Nadu", "India"),
    AddressDistrictPath("Perambalur", "Tamil Nadu", "India"),
    AddressDistrictPath("Pudukkottai", "Tamil Nadu", "India"),
    AddressDistrictPath("Ramanathapuram", "Tamil Nadu", "India"),
    AddressDistrictPath("Ranipet", "Tamil Nadu", "India"),
    AddressDistrictPath("Salem", "Tamil Nadu", "India"),
    AddressDistrictPath("Sivaganga", "Tamil Nadu", "India"),
    AddressDistrictPath("Tenkasi", "Tamil Nadu", "India"),
    AddressDistrictPath("Thanjavur", "Tamil Nadu", "India"),
    AddressDistrictPath("Theni", "Tamil Nadu", "India"),
    AddressDistrictPath("Thoothukudi", "Tamil Nadu", "India"),
    AddressDistrictPath("Tiruchirappalli", "Tamil Nadu", "India"),
    AddressDistrictPath("Tirunelveli", "Tamil Nadu", "India"),
    AddressDistrictPath("Tirupathur", "Tamil Nadu", "India"),
    AddressDistrictPath("Tiruppur", "Tamil Nadu", "India"),
    AddressDistrictPath("Tiruvallur", "Tamil Nadu", "India"),
    AddressDistrictPath("Tiruvannamalai", "Tamil Nadu", "India"),
    AddressDistrictPath("Tiruvarur", "Tamil Nadu", "India"),
    AddressDistrictPath("Vellore", "Tamil Nadu", "India"),
    AddressDistrictPath("Viluppuram", "Tamil Nadu", "India"),
    AddressDistrictPath("Virudhunagar", "Tamil Nadu", "India"),

    // Karnataka
    AddressDistrictPath("Bagalkote", "Karnataka", "India"),
    AddressDistrictPath("Ballari", "Karnataka", "India"),
    AddressDistrictPath("Belagavi", "Karnataka", "India"),
    AddressDistrictPath("Bengaluru Rural", "Karnataka", "India"),
    AddressDistrictPath("Bengaluru Urban", "Karnataka", "India"),
    AddressDistrictPath("Bidar", "Karnataka", "India"),
    AddressDistrictPath("Chamarajanagar", "Karnataka", "India"),
    AddressDistrictPath("Chikballapur", "Karnataka", "India"),
    AddressDistrictPath("Chikkamagaluru", "Karnataka", "India"),
    AddressDistrictPath("Chitradurga", "Karnataka", "India"),
    AddressDistrictPath("Dakshina Kannada", "Karnataka", "India"),
    AddressDistrictPath("Davanagere", "Karnataka", "India"),
    AddressDistrictPath("Dharwad", "Karnataka", "India"),
    AddressDistrictPath("Gadag", "Karnataka", "India"),
    AddressDistrictPath("Hassan", "Karnataka", "India"),
    AddressDistrictPath("Haveri", "Karnataka", "India"),
    AddressDistrictPath("Kalaburagi", "Karnataka", "India"),
    AddressDistrictPath("Kodagu", "Karnataka", "India"),
    AddressDistrictPath("Kolar", "Karnataka", "India"),
    AddressDistrictPath("Koppal", "Karnataka", "India"),
    AddressDistrictPath("Mandya", "Karnataka", "India"),
    AddressDistrictPath("Mysuru", "Karnataka", "India"),
    AddressDistrictPath("Raichur", "Karnataka", "India"),
    AddressDistrictPath("Ramanagara", "Karnataka", "India"),
    AddressDistrictPath("Shivamogga", "Karnataka", "India"),
    AddressDistrictPath("Tumakuru", "Karnataka", "India"),
    AddressDistrictPath("Udupi", "Karnataka", "India"),
    AddressDistrictPath("Uttara Kannada", "Karnataka", "India"),
    AddressDistrictPath("Vijayapura", "Karnataka", "India"),
    AddressDistrictPath("Vijayanagara", "Karnataka", "India"),
    AddressDistrictPath("Yadgir", "Karnataka", "India"),

    // Kerala
    AddressDistrictPath("Alappuzha", "Kerala", "India"),
    AddressDistrictPath("Ernakulam", "Kerala", "India"),
    AddressDistrictPath("Idukki", "Kerala", "India"),
    AddressDistrictPath("Kannur", "Kerala", "India"),
    AddressDistrictPath("Kasaragod", "Kerala", "India"),
    AddressDistrictPath("Kollam", "Kerala", "India"),
    AddressDistrictPath("Kottayam", "Kerala", "India"),
    AddressDistrictPath("Kozhikode", "Kerala", "India"),
    AddressDistrictPath("Malappuram", "Kerala", "India"),
    AddressDistrictPath("Palakkad", "Kerala", "India"),
    AddressDistrictPath("Pathanamthitta", "Kerala", "India"),
    AddressDistrictPath("Thiruvananthapuram", "Kerala", "India"),
    AddressDistrictPath("Thrissur", "Kerala", "India"),
    AddressDistrictPath("Wayanad", "Kerala", "India"),

    // Andhra Pradesh
    AddressDistrictPath("Alluri Sitharama Raju", "Andhra Pradesh", "India"),
    AddressDistrictPath("Anakapalli", "Andhra Pradesh", "India"),
    AddressDistrictPath("Ananthapuramu", "Andhra Pradesh", "India"),
    AddressDistrictPath("Annamayya", "Andhra Pradesh", "India"),
    AddressDistrictPath("Bapatla", "Andhra Pradesh", "India"),
    AddressDistrictPath("Chittoor", "Andhra Pradesh", "India"),
    AddressDistrictPath("Dr. B.R. Ambedkar Konaseema", "Andhra Pradesh", "India"),
    AddressDistrictPath("East Godavari", "Andhra Pradesh", "India"),
    AddressDistrictPath("Eluru", "Andhra Pradesh", "India"),
    AddressDistrictPath("Guntur", "Andhra Pradesh", "India"),
    AddressDistrictPath("Kakinada", "Andhra Pradesh", "India"),
    AddressDistrictPath("Krishna", "Andhra Pradesh", "India"),
    AddressDistrictPath("Kurnool", "Andhra Pradesh", "India"),
    AddressDistrictPath("Nandyal", "Andhra Pradesh", "India"),
    AddressDistrictPath("NTR", "Andhra Pradesh", "India"),
    AddressDistrictPath("Palnadu", "Andhra Pradesh", "India"),
    AddressDistrictPath("Parvathipuram Manyam", "Andhra Pradesh", "India"),
    AddressDistrictPath("Prakasam", "Andhra Pradesh", "India"),
    AddressDistrictPath("Sri Potti Sriramulu Nellore", "Andhra Pradesh", "India"),
    AddressDistrictPath("Sri Sathya Sai", "Andhra Pradesh", "India"),
    AddressDistrictPath("Srikakulam", "Andhra Pradesh", "India"),
    AddressDistrictPath("Tirupati", "Andhra Pradesh", "India"),
    AddressDistrictPath("Visakhapatnam", "Andhra Pradesh", "India"),
    AddressDistrictPath("Vizianagaram", "Andhra Pradesh", "India"),
    AddressDistrictPath("West Godavari", "Andhra Pradesh", "India"),
    AddressDistrictPath("YSR Kadapa", "Andhra Pradesh", "India"),

    // Telangana
    AddressDistrictPath("Adilabad", "Telangana", "India"),
    AddressDistrictPath("Bhadradri Kothagudem", "Telangana", "India"),
    AddressDistrictPath("Hanamkonda", "Telangana", "India"),
    AddressDistrictPath("Hyderabad", "Telangana", "India"),
    AddressDistrictPath("Jagtial", "Telangana", "India"),
    AddressDistrictPath("Jangaon", "Telangana", "India"),
    AddressDistrictPath("Jayashankar Bhupalpally", "Telangana", "India"),
    AddressDistrictPath("Jogulamba Gadwal", "Telangana", "India"),
    AddressDistrictPath("Kamareddy", "Telangana", "India"),
    AddressDistrictPath("Karimnagar", "Telangana", "India"),
    AddressDistrictPath("Khammam", "Telangana", "India"),
    AddressDistrictPath("Komaram Bheem Asifabad", "Telangana", "India"),
    AddressDistrictPath("Mahabubabad", "Telangana", "India"),
    AddressDistrictPath("Mahabubnagar", "Telangana", "India"),
    AddressDistrictPath("Mancherial", "Telangana", "India"),
    AddressDistrictPath("Medak", "Telangana", "India"),
    AddressDistrictPath("Medchal-Malkajgiri", "Telangana", "India"),
    AddressDistrictPath("Mulugu", "Telangana", "India"),
    AddressDistrictPath("Nagarkurnool", "Telangana", "India"),
    AddressDistrictPath("Nalgonda", "Telangana", "India"),
    AddressDistrictPath("Narayanpet", "Telangana", "India"),
    AddressDistrictPath("Nirmal", "Telangana", "India"),
    AddressDistrictPath("Nizamabad", "Telangana", "India"),
    AddressDistrictPath("Peddapalli", "Telangana", "India"),
    AddressDistrictPath("Rajanna Sircilla", "Telangana", "India"),
    AddressDistrictPath("Ranga Reddy", "Telangana", "India"),
    AddressDistrictPath("Sangareddy", "Telangana", "India"),
    AddressDistrictPath("Siddipet", "Telangana", "India"),
    AddressDistrictPath("Suryapet", "Telangana", "India"),
    AddressDistrictPath("Vikarabad", "Telangana", "India"),
    AddressDistrictPath("Wanaparthy", "Telangana", "India"),
    AddressDistrictPath("Warangal", "Telangana", "India"),
    AddressDistrictPath("Yadadri Bhuvanagiri", "Telangana", "India"),

    // Maharashtra
    AddressDistrictPath("Ahmednagar", "Maharashtra", "India"),
    AddressDistrictPath("Akola", "Maharashtra", "India"),
    AddressDistrictPath("Amravati", "Maharashtra", "India"),
    AddressDistrictPath("Aurangabad", "Maharashtra", "India"),
    AddressDistrictPath("Beed", "Maharashtra", "India"),
    AddressDistrictPath("Bhandara", "Maharashtra", "India"),
    AddressDistrictPath("Buldhana", "Maharashtra", "India"),
    AddressDistrictPath("Chandrapur", "Maharashtra", "India"),
    AddressDistrictPath("Dhule", "Maharashtra", "India"),
    AddressDistrictPath("Gadchiroli", "Maharashtra", "India"),
    AddressDistrictPath("Gondia", "Maharashtra", "India"),
    AddressDistrictPath("Hingoli", "Maharashtra", "India"),
    AddressDistrictPath("Jalgaon", "Maharashtra", "India"),
    AddressDistrictPath("Jalna", "Maharashtra", "India"),
    AddressDistrictPath("Kolhapur", "Maharashtra", "India"),
    AddressDistrictPath("Latur", "Maharashtra", "India"),
    AddressDistrictPath("Mumbai City", "Maharashtra", "India"),
    AddressDistrictPath("Mumbai Suburban", "Maharashtra", "India"),
    AddressDistrictPath("Nagpur", "Maharashtra", "India"),
    AddressDistrictPath("Nanded", "Maharashtra", "India"),
    AddressDistrictPath("Nandurbar", "Maharashtra", "India"),
    AddressDistrictPath("Nashik", "Maharashtra", "India"),
    AddressDistrictPath("Osmanabad", "Maharashtra", "India"),
    AddressDistrictPath("Palghar", "Maharashtra", "India"),
    AddressDistrictPath("Parbhani", "Maharashtra", "India"),
    AddressDistrictPath("Pune", "Maharashtra", "India"),
    AddressDistrictPath("Raigad", "Maharashtra", "India"),
    AddressDistrictPath("Ratnagiri", "Maharashtra", "India"),
    AddressDistrictPath("Sangli", "Maharashtra", "India"),
    AddressDistrictPath("Satara", "Maharashtra", "India"),
    AddressDistrictPath("Sindhudurg", "Maharashtra", "India"),
    AddressDistrictPath("Solapur", "Maharashtra", "India"),
    AddressDistrictPath("Thane", "Maharashtra", "India"),
    AddressDistrictPath("Wardha", "Maharashtra", "India"),
    AddressDistrictPath("Washim", "Maharashtra", "India"),
    AddressDistrictPath("Yavatmal", "Maharashtra", "India"),

    // Other useful India paths
    AddressDistrictPath("New Delhi", "Delhi", "India"),
    AddressDistrictPath("Central Delhi", "Delhi", "India"),
    AddressDistrictPath("North Delhi", "Delhi", "India"),
    AddressDistrictPath("South Delhi", "Delhi", "India"),
    AddressDistrictPath("Howrah", "West Bengal", "India"),
    AddressDistrictPath("Kolkata", "West Bengal", "India"),
    AddressDistrictPath("Darjeeling", "West Bengal", "India"),
    AddressDistrictPath("Lucknow", "Uttar Pradesh", "India"),
    AddressDistrictPath("Varanasi", "Uttar Pradesh", "India"),
    AddressDistrictPath("Prayagraj", "Uttar Pradesh", "India"),
    AddressDistrictPath("Kanpur Nagar", "Uttar Pradesh", "India"),
    AddressDistrictPath("Agra", "Uttar Pradesh", "India"),
    AddressDistrictPath("Ahmedabad", "Gujarat", "India"),
    AddressDistrictPath("Surat", "Gujarat", "India"),
    AddressDistrictPath("Vadodara", "Gujarat", "India"),
    AddressDistrictPath("Rajkot", "Gujarat", "India"),

    // International examples for ambiguity handling
    AddressDistrictPath("Queens County", "New York", "United States"),
    AddressDistrictPath("Los Angeles County", "California", "United States"),
    AddressDistrictPath("San Diego County", "California", "United States"),
    AddressDistrictPath("Harris County", "Texas", "United States"),
    AddressDistrictPath("Dallas County", "Texas", "United States"),
    AddressDistrictPath("Travis County", "Texas", "United States"),
    AddressDistrictPath("Dubai", "Dubai", "United Arab Emirates"),
    AddressDistrictPath("Abu Dhabi", "Abu Dhabi", "United Arab Emirates"),
    AddressDistrictPath("Sharjah", "Sharjah", "United Arab Emirates"),
    AddressDistrictPath("Petaling", "Selangor", "Malaysia"),
    AddressDistrictPath("Klang", "Selangor", "Malaysia"),
    AddressDistrictPath("Gombak", "Selangor", "Malaysia"),
    AddressDistrictPath("Colombo", "Western", "Sri Lanka"),
    AddressDistrictPath("Gampaha", "Western", "Sri Lanka"),
    AddressDistrictPath("Kalutara", "Western", "Sri Lanka"),
    AddressDistrictPath("Kandy", "Central", "Sri Lanka"),
    AddressDistrictPath("Matale", "Central", "Sri Lanka"),
    AddressDistrictPath("Nuwara Eliya", "Central", "Sri Lanka")
)

private fun addressCountryOptions(selectedLanguage: String): List<String> {
    val locale = if (selectedLanguage == "TA") Locale("ta") else Locale.ENGLISH
    return Locale.getISOCountries()
        .map { countryCode ->
            Locale("", countryCode).getDisplayCountry(locale).ifBlank {
                Locale("", countryCode).getDisplayCountry(Locale.ENGLISH)
            }
        }
        .distinct()
        .sorted()
}

private fun normalizeCountryName(value: String): String {
    if (value.isBlank()) return value
    val lowered = value.trim().lowercase()
    return Locale.getISOCountries()
        .map { code -> Locale("", code) }
        .firstOrNull { locale ->
            locale.getDisplayCountry(Locale.ENGLISH).lowercase() == lowered ||
                    locale.getDisplayCountry(Locale("ta")).lowercase() == lowered
        }
        ?.getDisplayCountry(Locale.ENGLISH)
        ?: COUNTRY_OPTIONS.firstOrNull {
            it.nameEn.equals(value, ignoreCase = true) || it.nameTa == value
        }?.nameEn
        ?: value
}

private fun localizeAddressCountryValue(value: String, selectedLanguage: String): String {
    if (value.isBlank()) return value
    val english = normalizeCountryName(value)
    return if (selectedLanguage == "TA") {
        Locale.getISOCountries()
            .map { code -> Locale("", code) }
            .firstOrNull { it.getDisplayCountry(Locale.ENGLISH).equals(english, ignoreCase = true) }
            ?.getDisplayCountry(Locale("ta"))
            ?.takeIf { it.isNotBlank() }
            ?: english
    } else {
        english
    }
}

private fun isIndiaCountry(country: String): Boolean =
    normalizeCountryName(country).equals("India", ignoreCase = true)

private fun sanitizePostalCodeInput(country: String, raw: String): String {
    return if (isIndiaCountry(country)) {
        raw.filter(Char::isDigit).take(6)
    } else {
        raw.uppercase()
            .filter { it.isLetterOrDigit() || it == ' ' || it == '-' }
            .take(12)
    }
}

private fun postalCodeError(value: String, country: String, selectedLanguage: String): String? {
    if (value.isBlank()) return null
    return if (isIndiaCountry(country)) {
        if (value.length == 6) null
        else if (selectedLanguage == "TA") "இந்தியாவிற்கு 6 இலக்க PIN தேவை" else "India requires 6 digit PIN"
    } else {
        if (value.length in 3..12) null
        else if (selectedLanguage == "TA") "செல்லுபடியாகும் Postal Code உள்ளிடவும்" else "Enter a valid postal code"
    }
}

private fun addressStateOptions(
    country: String,
    selectedLanguage: String,
    filterByCountry: Boolean
): List<String> {
    val englishValues = if (filterByCountry) {
        val normalizedCountry = normalizeCountryName(country)
        if (normalizedCountry == "India") {
            INDIA_STATE_NAMES
        } else {
            OTHER_STATE_MASTER[normalizedCountry].orEmpty()
        }
    } else {
        (INDIA_STATE_NAMES + OTHER_STATE_MASTER.values.flatten()).distinct().sorted()
    }
    return if (selectedLanguage == "TA") englishValues.map(::localizeStateValue) else englishValues
}

private fun addressDistrictPaths(
    district: String,
    state: String = "",
    country: String = ""
): List<AddressDistrictPath> {
    val englishDistrict = deLocalizeDistrictValue(district)
    val englishState = deLocalizeStateValue(state)
    val englishCountry = normalizeCountryName(country)

    return ADDRESS_DISTRICT_PATHS.filter { path ->
        path.district.equals(englishDistrict, ignoreCase = true) &&
                (englishState.isBlank() || path.state.equals(englishState, ignoreCase = true)) &&
                (englishCountry.isBlank() || path.country.equals(englishCountry, ignoreCase = true))
    }
}

private fun countriesForState(state: String): List<String> {
    val englishState = deLocalizeStateValue(state)
    val allPaths = ADDRESS_DISTRICT_PATHS.filter { it.state.equals(englishState, ignoreCase = true) }
        .map { it.country }
    val explicitStates = OTHER_STATE_MASTER.entries
        .filter { (_, states) -> states.any { it.equals(englishState, ignoreCase = true) } }
        .map { it.key }
    val indiaMatch = if (INDIA_STATE_NAMES.any { it.equals(englishState, ignoreCase = true) }) listOf("India") else emptyList()
    return (allPaths + explicitStates + indiaMatch).distinct()
}

private fun addressDistrictOptions(
    country: String,
    state: String,
    selectedLanguage: String,
    filterByCountry: Boolean,
    filterByState: Boolean
): List<String> {
    val englishCountry = normalizeCountryName(country)
    val englishState = deLocalizeStateValue(state)

    val filtered = ADDRESS_DISTRICT_PATHS.filter { path ->
        when {
            filterByState -> path.state.equals(englishState, ignoreCase = true) &&
                    (!filterByCountry || path.country.equals(englishCountry, ignoreCase = true))
            filterByCountry -> path.country.equals(englishCountry, ignoreCase = true)
            else -> true
        }
    }

    val englishDistricts = filtered.map { it.district }.distinct().sorted()
    return if (selectedLanguage == "TA") englishDistricts.map(::localizeDistrictValue) else englishDistricts
}

private fun localizeAddressStateValue(value: String, selectedLanguage: String): String {
    if (value.isBlank()) return value
    val english = deLocalizeStateValue(value)
    return if (selectedLanguage == "TA") localizeStateValue(english) else english
}

private fun localizeAddressDistrictValue(value: String, selectedLanguage: String): String {
    if (value.isBlank()) return value
    val english = deLocalizeDistrictValue(value)
    return if (selectedLanguage == "TA") localizeDistrictValue(english) else english
}

private fun localizeStateValue(value: String): String = when (value) {
    "Andhra Pradesh" -> "ஆந்திரப் பிரதேசம்"
    "Arunachal Pradesh" -> "அருணாச்சலப் பிரதேசம்"
    "Assam" -> "அசாம்"
    "Bihar" -> "பீகார்"
    "Chhattisgarh" -> "சத்தீஸ்கர்"
    "Goa" -> "கோவா"
    "Gujarat" -> "குஜராத்"
    "Haryana" -> "ஹரியானா"
    "Himachal Pradesh" -> "ஹிமாச்சலப் பிரதேசம்"
    "Jharkhand" -> "ஜார்கண்ட்"
    "Karnataka" -> "கர்நாடகா"
    "Kerala" -> "கேரளா"
    "Madhya Pradesh" -> "மத்தியப் பிரதேசம்"
    "Maharashtra" -> "மகாராஷ்டிரா"
    "Manipur" -> "மணிப்பூர்"
    "Meghalaya" -> "மேகாலயா"
    "Mizoram" -> "மிசோரம்"
    "Nagaland" -> "நாகாலாந்து"
    "Odisha" -> "ஒடிஷா"
    "Punjab" -> "பஞ்சாப்"
    "Rajasthan" -> "ராஜஸ்தான்"
    "Sikkim" -> "சிக்கிம்"
    "Tamil Nadu" -> "தமிழ்நாடு"
    "Telangana" -> "தெலுங்கானா"
    "Tripura" -> "திரிபுரா"
    "Uttar Pradesh" -> "உத்தரப் பிரதேசம்"
    "Uttarakhand" -> "உத்தரகாண்ட்"
    "West Bengal" -> "மேற்கு வங்காளம்"
    "Andaman and Nicobar Islands" -> "அந்தமான் மற்றும் நிக்கோபார் தீவுகள்"
    "Chandigarh" -> "சண்டிகர்"
    "Dadra and Nagar Haveli and Daman and Diu" -> "தாத்ரா மற்றும் நகர் ஹவேலி மற்றும் தாமன் மற்றும் தீவு"
    "Delhi" -> "டெல்லி"
    "Jammu and Kashmir" -> "ஜம்மு மற்றும் காஷ்மீர்"
    "Ladakh" -> "லடாக்"
    "Lakshadweep" -> "லட்சத்தீவு"
    "Puducherry" -> "புதுச்சேரி"
    "Alabama" -> "அலபாமா"
    "Alaska" -> "அலாஸ்கா"
    "Arizona" -> "அரிசோனா"
    "Arkansas" -> "ஆர்கன்சாஸ்"
    "California" -> "காலிஃபோர்னியா"
    "Colorado" -> "கொலராடோ"
    "Connecticut" -> "கனெக்டிகட்"
    "Delaware" -> "டெலாவேர்"
    "Florida" -> "ஃப்ளோரிடா"
    "Georgia" -> "ஜார்ஜியா"
    "Hawaii" -> "ஹவாய்"
    "Idaho" -> "ஐடஹோ"
    "Illinois" -> "இல்லினாய்"
    "Indiana" -> "இந்தியானா"
    "Iowa" -> "அயோவா"
    "Kansas" -> "கான்சஸ்"
    "Kentucky" -> "கென்டக்கி"
    "Louisiana" -> "லூயிசியானா"
    "Maine" -> "மேயின்"
    "Maryland" -> "மேரிலாண்ட்"
    "Massachusetts" -> "மாசசூசெட்ஸ்"
    "Michigan" -> "மிச்சிகன்"
    "Minnesota" -> "மின்னசோட்டா"
    "Mississippi" -> "மிசிசிப்பி"
    "Missouri" -> "மிசூரி"
    "Montana" -> "மொண்டானா"
    "Nebraska" -> "நெப்ராஸ்கா"
    "Nevada" -> "நெவாடா"
    "New Hampshire" -> "நியூ ஹாம்ஷயர்"
    "New Jersey" -> "நியூ ஜெர்சி"
    "New Mexico" -> "நியூ மெக்ஸிகோ"
    "New York" -> "நியூயார்க்"
    "North Carolina" -> "வட கரோலினா"
    "North Dakota" -> "வட டகோட்டா"
    "Ohio" -> "ஓஹியோ"
    "Oklahoma" -> "ஒக்லஹோமா"
    "Oregon" -> "ஒரிகன்"
    "Pennsylvania" -> "பென்சில்வேனியா"
    "Rhode Island" -> "ரோட் ஐலந்து"
    "South Carolina" -> "தென் கரோலினா"
    "South Dakota" -> "தென் டகோட்டா"
    "Tennessee" -> "டென்னசி"
    "Texas" -> "டெக்சாஸ்"
    "Utah" -> "யூட்டா"
    "Vermont" -> "வர்மாண்ட்"
    "Virginia" -> "விர்ஜீனியா"
    "Washington" -> "வாஷிங்டன்"
    "West Virginia" -> "மேற்கு விர்ஜீனியா"
    "Wisconsin" -> "விஸ்கான்சின்"
    "Wyoming" -> "வையோமிங்"
    "District of Columbia" -> "கொலம்பியா மாவட்டம்"
    "Singapore" -> "சிங்கப்பூர்"
    "Abu Dhabi" -> "அபுதாபி"
    "Ajman" -> "அஜ்மான்"
    "Dubai" -> "துபாய்"
    "Fujairah" -> "ஃபுஜைரா"
    "Ras Al Khaimah" -> "ராஸ் அல் கைமா"
    "Sharjah" -> "ஷார்ஜா"
    "Umm Al Quwain" -> "உம் அல் குவைன்"
    "Johor" -> "ஜொகூர்"
    "Kedah" -> "கெடா"
    "Kelantan" -> "கெலந்தான்"
    "Kuala Lumpur" -> "கோலாலம்பூர்"
    "Labuan" -> "லபுவான்"
    "Malacca" -> "மலாக்கா"
    "Negeri Sembilan" -> "நெகிரி செம்பிலான்"
    "Pahang" -> "பஹாங்"
    "Penang" -> "பினாங்கு"
    "Perak" -> "பெராக்"
    "Perlis" -> "பெர்லிஸ்"
    "Putrajaya" -> "புத்ராஜெயா"
    "Sabah" -> "சபா"
    "Sarawak" -> "சரவாக்"
    "Selangor" -> "செலாங்கூர்"
    "Terengganu" -> "தெரெங்கானு"
    "Central" -> "மத்திய"
    "Eastern" -> "கிழக்கு"
    "North Central" -> "வட மத்திய"
    "Northern" -> "வடக்கு"
    "North Western" -> "வடமேற்கு"
    "Sabaragamuwa" -> "சபரகமுவா"
    "Southern" -> "தெற்கு"
    "Uva" -> "உவா"
    "Western" -> "மேற்கு"
    else -> value
}

private fun deLocalizeStateValue(value: String): String = when (value) {
    "ஆந்திரப் பிரதேசம்" -> "Andhra Pradesh"
    "அருணாச்சலப் பிரதேசம்" -> "Arunachal Pradesh"
    "அசாம்" -> "Assam"
    "பீகார்" -> "Bihar"
    "சத்தீஸ்கர்" -> "Chhattisgarh"
    "கோவா" -> "Goa"
    "குஜராத்" -> "Gujarat"
    "ஹரியானா" -> "Haryana"
    "ஹிமாச்சலப் பிரதேசம்" -> "Himachal Pradesh"
    "ஜார்கண்ட்" -> "Jharkhand"
    "கர்நாடகா" -> "Karnataka"
    "கேரளா" -> "Kerala"
    "மத்தியப் பிரதேசம்" -> "Madhya Pradesh"
    "மகாராஷ்டிரா" -> "Maharashtra"
    "மணிப்பூர்" -> "Manipur"
    "மேகாலயா" -> "Meghalaya"
    "மிசோரம்" -> "Mizoram"
    "நாகாலாந்து" -> "Nagaland"
    "ஒடிஷா" -> "Odisha"
    "பஞ்சாப்" -> "Punjab"
    "ராஜஸ்தான்" -> "Rajasthan"
    "சிக்கிம்" -> "Sikkim"
    "தமிழ்நாடு" -> "Tamil Nadu"
    "தெலுங்கானா" -> "Telangana"
    "திரிபுரா" -> "Tripura"
    "உத்தரப் பிரதேசம்" -> "Uttar Pradesh"
    "உத்தரகாண்ட்" -> "Uttarakhand"
    "மேற்கு வங்காளம்" -> "West Bengal"
    "அந்தமான் மற்றும் நிக்கோபார் தீவுகள்" -> "Andaman and Nicobar Islands"
    "சண்டிகர்" -> "Chandigarh"
    "தாத்ரா மற்றும் நகர் ஹவேலி மற்றும் தாமன் மற்றும் தீவு" -> "Dadra and Nagar Haveli and Daman and Diu"
    "டெல்லி" -> "Delhi"
    "ஜம்மு மற்றும் காஷ்மீர்" -> "Jammu and Kashmir"
    "லடாக்" -> "Ladakh"
    "லட்சத்தீவு" -> "Lakshadweep"
    "புதுச்சேரி" -> "Puducherry"
    "காலிஃபோர்னியா" -> "California"
    "நியூ ஜெர்சி" -> "New Jersey"
    "நியூயார்க்" -> "New York"
    "டெக்சாஸ்" -> "Texas"
    "ஃப்ளோரிடா" -> "Florida"
    "இல்லினாய்" -> "Illinois"
    "மாசசூசெட்ஸ்" -> "Massachusetts"
    "ஒரிகன்" -> "Oregon"
    "சிங்கப்பூர்" -> "Singapore"
    "அபுதாபி" -> "Abu Dhabi"
    "அஜ்மான்" -> "Ajman"
    "துபாய்" -> "Dubai"
    "ஃபுஜைரா" -> "Fujairah"
    "ராஸ் அல் கைமா" -> "Ras Al Khaimah"
    "ஷார்ஜா" -> "Sharjah"
    "உம் அல் குவைன்" -> "Umm Al Quwain"
    "ஜொகூர்" -> "Johor"
    "கோலாலம்பூர்" -> "Kuala Lumpur"
    "பினாங்கு" -> "Penang"
    "செலாங்கூர்" -> "Selangor"
    "மத்திய" -> "Central"
    "கிழக்கு" -> "Eastern"
    "வட மத்திய" -> "North Central"
    "வடக்கு" -> "Northern"
    "வடமேற்கு" -> "North Western"
    "சபரகமுவா" -> "Sabaragamuwa"
    "தெற்கு" -> "Southern"
    "உவா" -> "Uva"
    "மேற்கு" -> "Western"
    else -> value
}

private fun localizeDistrictValue(value: String): String = when (value) {
    "Ariyalur" -> "அரியலூர்"
    "Chengalpattu" -> "செங்கல்பட்டு"
    "Chennai" -> "சென்னை"
    "Coimbatore" -> "கோயம்புத்தூர்"
    "Cuddalore" -> "கடலூர்"
    "Dharmapuri" -> "தர்மபுரி"
    "Dindigul" -> "திண்டுக்கல்"
    "Erode" -> "ஈரோடு"
    "Kallakurichi" -> "கள்ளக்குறிச்சி"
    "Kancheepuram" -> "காஞ்சிபுரம்"
    "Kanyakumari" -> "கன்னியாகுமரி"
    "Karur" -> "கரூர்"
    "Krishnagiri" -> "கிருஷ்ணகிரி"
    "Madurai" -> "மதுரை"
    "Mayiladuthurai" -> "மயிலாடுதுறை"
    "Nagapattinam" -> "நாகப்பட்டினம்"
    "Namakkal" -> "நாமக்கல்"
    "Nilgiris" -> "நீலகிரி"
    "Perambalur" -> "பெரம்பலூர்"
    "Pudukkottai" -> "புதுக்கோட்டை"
    "Ramanathapuram" -> "ராமநாதபுரம்"
    "Ranipet" -> "ராணிப்பேட்டை"
    "Salem" -> "சேலம்"
    "Sivaganga" -> "சிவகங்கை"
    "Tenkasi" -> "தென்காசி"
    "Thanjavur" -> "தஞ்சாவூர்"
    "Theni" -> "தேனி"
    "Thoothukudi" -> "தூத்துக்குடி"
    "Tiruchirappalli" -> "திருச்சிராப்பள்ளி"
    "Tirunelveli" -> "திருநெல்வேலி"
    "Tirupathur" -> "திருப்பத்தூர்"
    "Tiruppur" -> "திருப்பூர்"
    "Tiruvallur" -> "திருவள்ளூர்"
    "Tiruvannamalai" -> "திருவண்ணாமலை"
    "Tiruvarur" -> "திருவாரூர்"
    "Vellore" -> "வேலூர்"
    "Viluppuram" -> "விழுப்புரம்"
    "Virudhunagar" -> "விருதுநகர்"
    "Bengaluru Urban" -> "பெங்களூரு அர்பன்"
    "Mysuru" -> "மைசூரு"
    "Mangaluru" -> "மங்களூரு"
    "Thiruvananthapuram" -> "திருவனந்தபுரம்"
    "Ernakulam" -> "எറണாகுளம்"
    "Kozhikode" -> "கொழிக்கோடு"
    "Tirupati" -> "திருப்பதி"
    "Nellore" -> "நெல்லூர்"
    "Chittoor" -> "சித்தூர்"
    "Hyderabad" -> "ஹைதராபாத்"
    "Warangal" -> "வரங்கல்"
    "Karimnagar" -> "கரீம்நகர்"
    "Mumbai" -> "மும்பை"
    "Pune" -> "புனே"
    "Nagpur" -> "நாக்பூர்"
    "New Delhi" -> "நியூ டெல்லி"
    "Howrah" -> "ஹாவ்ரா"
    "Kolkata" -> "கொல்கத்தா"
    "Lucknow" -> "லக்னோ"
    "Varanasi" -> "வாரணாசி"
    "Prayagraj" -> "பிரயாக்ராஜ்"
    "Kanpur Nagar" -> "கான்பூர் நகர்"
    "Agra" -> "ஆக்ரா"
    "Ahmedabad" -> "அகமதாபாத்"
    "Surat" -> "சூரத்"
    "Vadodara" -> "வடோதரா"
    "Rajkot" -> "ராஜ்கோட்"
    "Queens County" -> "க்வீன்ஸ் கவுண்டி"
    "Los Angeles County" -> "லாஸ் ஏஞ்சல்ஸ் கவுண்டி"
    "San Diego County" -> "சான் டியாகோ கவுண்டி"
    "Harris County" -> "ஹாரிஸ் கவுண்டி"
    "Dallas County" -> "டல்லஸ் கவுண்டி"
    "Travis County" -> "ட்ராவிஸ் கவுண்டி"
    "Petaling" -> "பெட்டாலிங்"
    "Klang" -> "கிளாங்"
    "Gombak" -> "கொம்பாக்"
    "Colombo" -> "கொழும்பு"
    "Gampaha" -> "கம்பஹா"
    "Kalutara" -> "கலுத்துறை"
    "Kandy" -> "கண்டி"
    "Matale" -> "மட்டளை"
    "Nuwara Eliya" -> "நுவரெலியா"
    else -> value
}

private fun deLocalizeDistrictValue(value: String): String = when (value) {
    "அரியலூர்" -> "Ariyalur"
    "செங்கல்பட்டு" -> "Chengalpattu"
    "சென்னை" -> "Chennai"
    "கோயம்புத்தூர்" -> "Coimbatore"
    "கடலூர்" -> "Cuddalore"
    "தர்மபுரி" -> "Dharmapuri"
    "திண்டுக்கல்" -> "Dindigul"
    "ஈரோடு" -> "Erode"
    "கள்ளக்குறிச்சி" -> "Kallakurichi"
    "காஞ்சிபுரம்" -> "Kancheepuram"
    "கன்னியாகுமரி" -> "Kanyakumari"
    "கரூர்" -> "Karur"
    "கிருஷ்ணகிரி" -> "Krishnagiri"
    "மதுரை" -> "Madurai"
    "மயிலாடுதுறை" -> "Mayiladuthurai"
    "நாகப்பட்டினம்" -> "Nagapattinam"
    "நாமக்கல்" -> "Namakkal"
    "நீலகிரி" -> "Nilgiris"
    "பெரம்பலூர்" -> "Perambalur"
    "புதுக்கோட்டை" -> "Pudukkottai"
    "ராமநாதபுரம்" -> "Ramanathapuram"
    "ராணிப்பேட்டை" -> "Ranipet"
    "சேலம்" -> "Salem"
    "சிவகங்கை" -> "Sivaganga"
    "தென்காசி" -> "Tenkasi"
    "தஞ்சாவூர்" -> "Thanjavur"
    "தேனி" -> "Theni"
    "தூத்துக்குடி" -> "Thoothukudi"
    "திருச்சிராப்பள்ளி" -> "Tiruchirappalli"
    "திருநெல்வேலி" -> "Tirunelveli"
    "திருப்பத்தூர்" -> "Tirupathur"
    "திருப்பூர்" -> "Tiruppur"
    "திருவள்ளூர்" -> "Tiruvallur"
    "திருவண்ணாமலை" -> "Tiruvannamalai"
    "திருவாரூர்" -> "Tiruvarur"
    "வேலூர்" -> "Vellore"
    "விழுப்புரம்" -> "Viluppuram"
    "விருதுநகர்" -> "Virudhunagar"
    "பெங்களூரு அர்பன்" -> "Bengaluru Urban"
    "மைசூரு" -> "Mysuru"
    "மங்களூரு" -> "Mangaluru"
    "திருவனந்தபுரம்" -> "Thiruvananthapuram"
    "எറണாகுளம்" -> "Ernakulam"
    "கொழிக்கோடு" -> "Kozhikode"
    "திருப்பதி" -> "Tirupati"
    "நெல்லூர்" -> "Nellore"
    "சித்தூர்" -> "Chittoor"
    "ஹைதராபாத்" -> "Hyderabad"
    "வரங்கல்" -> "Warangal"
    "கரீம்நகர்" -> "Karimnagar"
    "மும்பை" -> "Mumbai"
    "புனே" -> "Pune"
    "நாக்பூர்" -> "Nagpur"
    "நியூ டெல்லி" -> "New Delhi"
    "ஹாவ்ரா" -> "Howrah"
    "கொல்கத்தா" -> "Kolkata"
    "லக்னோ" -> "Lucknow"
    "வாரணாசி" -> "Varanasi"
    "பிரயாக்ராஜ்" -> "Prayagraj"
    "கான்பூர் நகர்" -> "Kanpur Nagar"
    "ஆக்ரா" -> "Agra"
    "அகமதாபாத்" -> "Ahmedabad"
    "சூரத்" -> "Surat"
    "வடோதரா" -> "Vadodara"
    "ராஜ்கோட்" -> "Rajkot"
    "க்வீன்ஸ் கவுண்டி" -> "Queens County"
    "லாஸ் ஏஞ்சல்ஸ் கவுண்டி" -> "Los Angeles County"
    "சான் டியாகோ கவுண்டி" -> "San Diego County"
    "ஹாரிஸ் கவுண்டி" -> "Harris County"
    "டல்லஸ் கவுண்டி" -> "Dallas County"
    "ட்ராவிஸ் கவுண்டி" -> "Travis County"
    "பெட்டாலிங்" -> "Petaling"
    "கிளாங்" -> "Klang"
    "கொம்பாக்" -> "Gombak"
    "கொழும்பு" -> "Colombo"
    "கம்பஹா" -> "Gampaha"
    "கலுத்துறை" -> "Kalutara"
    "கண்டி" -> "Kandy"
    "மட்டளை" -> "Matale"
    "நுவரெலியா" -> "Nuwara Eliya"
    else -> value
}

private const val ADDRESS_HISTORY_PREFS = "address_history_prefs"
private const val ADDRESS_HISTORY_DISTRICT = "district"
private const val ADDRESS_HISTORY_STATE = "state"
private const val ADDRESS_HISTORY_COUNTRY = "country"

private fun loadAddressSuggestionHistory(context: Context, key: String): List<String> {
    return context.getSharedPreferences(ADDRESS_HISTORY_PREFS, Context.MODE_PRIVATE)
        .getStringSet(key, emptySet())
        .orEmpty()
        .filter { it.isNotBlank() }
        .sorted()
}

private fun persistAddressSuggestionHistory(context: Context, key: String, values: List<String>) {
    context.getSharedPreferences(ADDRESS_HISTORY_PREFS, Context.MODE_PRIVATE)
        .edit()
        .putStringSet(key, values.filter { it.isNotBlank() }.toSet())
        .apply()
}

private fun addAddressSuggestion(target: MutableList<String>, value: String) {
    val cleaned = value.trim()
    if (cleaned.isBlank()) return
    if (target.none { it.equals(cleaned, ignoreCase = true) }) {
        target.add(cleaned)
        target.sortBy { it.lowercase() }
    }
}

private fun mergeAddressSuggestionOptions(builtIn: List<String>, history: List<String>): List<String> {
    val merged = mutableListOf<String>()
    history.filter { it.isNotBlank() }.forEach { item ->
        if (merged.none { it.equals(item, ignoreCase = true) }) merged.add(item)
    }
    builtIn.filter { it.isNotBlank() }.forEach { item ->
        if (merged.none { it.equals(item, ignoreCase = true) }) merged.add(item)
    }
    return merged
}

private val PHONE_LABELS = listOf(
    "Personal" to "தனிப்பட்ட",
    "Office" to "அலுவலகம்",
    "Home" to "வீடு",
    "Landline" to "லேண்ட்லைன்",
    "Fax" to "ஃபாக்ஸ்",
    "Other" to "மற்றவை"
)

private val EMAIL_LABELS = listOf(
    "Personal" to "தனிப்பட்ட",
    "Official" to "அலுவலகம்",
    "Family" to "குடும்பம்",
    "Other" to "மற்றவை"
)

private val RASI_LABELS = listOf(
    "Mesham" to "மேஷம்", "Rishabam" to "ரிஷபம்", "Mithunam" to "மிதுனம்", "Kadagam" to "கடகம்",
    "Simmam" to "சிம்மம்", "Kanni" to "கன்னி", "Thulam" to "துலாம்", "Viruchigam" to "விருச்சிகம்",
    "Dhanusu" to "தனுசு", "Magaram" to "மகரம்", "Kumbam" to "கும்பம்", "Meenam" to "மீனம்"
)

private val NAKSHATRA_LABELS = listOf(
    "Ashwini" to "அஸ்வினி", "Bharani" to "பரணி", "Karthigai" to "கார்த்திகை", "Rohini" to "ரோகிணி",
    "Mirugasiridam" to "மிருகசீரிடம்", "Thiruvathirai" to "திருவாதிரை", "Punarpoosam" to "புனர்பூசம்", "Poosam" to "பூசம்",
    "Ayilyam" to "ஆயில்யம்", "Magam" to "மகம்", "Pooram" to "பூரம்", "Uthiram" to "உத்திரம்",
    "Hastham" to "ஹஸ்தம்", "Chithirai" to "சித்திரை", "Swathi" to "சுவாதி", "Visakam" to "விசாகம்",
    "Anusham" to "அனுஷம்", "Kettai" to "கேட்டை", "Moolam" to "மூலம்", "Pooradam" to "பூராடம்",
    "Uthiradam" to "உத்திராடம்", "Thiruvonam" to "திருவோணம்", "Avittam" to "அவிட்டம்", "Sadayam" to "சதயம்",
    "Poorattadhi" to "பூரட்டாதி", "Uthirattadhi" to "உத்திரட்டாதி", "Revathi" to "ரேவதி"
)

private val RELATIONSHIP_LABELS = listOf(
    "Husband" to "கணவர்",
    "Wife" to "மனைவி",
    "Son" to "மகன்",
    "Daughter" to "மகள்",
    "Father" to "அப்பா",
    "Mother" to "அம்மா",
    "Brother" to "சகோதரர்",
    "Sister" to "சகோதரி",
    "Elder Brother" to "அண்ணன்",
    "Younger Brother" to "தம்பி",
    "Elder Sister" to "அக்கா",
    "Younger Sister" to "தங்கை",
    "Grandfather" to "தாத்தா",
    "Grandmother" to "பாட்டி",
    "Grandson" to "பேரன்",
    "Granddaughter" to "பேத்தி",
    "Uncle" to "மாமா",
    "Aunt" to "அத்தை",
    "Chithappa" to "சித்தப்பா",
    "Chithi" to "சித்தி",
    "Periyappa" to "பெரியப்பா",
    "Periyamma" to "பெரியம்மா",
    "Father-in-law" to "மாமனார்",
    "Mother-in-law" to "மாமியார்",
    "Son-in-law" to "மருமகன்",
    "Daughter-in-law" to "மருமகள்",
    "Brother-in-law" to "மைத்துனன்",
    "Sister-in-law" to "மைத்துனி",
    "Relative" to "உறவினர்",
    "PA" to "பி.ஏ",
    "Manager" to "மேனேஜர்",
    "Watchman" to "வாட்ச்மேன்",
    "Security" to "செக்யூரிட்டி",
    "House Maid" to "ஹவுஸ் மேய்ட்",
    "Driver" to "டிரைவர்",
    "Cook" to "குக்",
    "Gardener" to "தோட்டக்காரர்",
    "Caretaker" to "கேர் டேக்கர்",
    "Neighbor" to "பக்கத்து வீட்டுக்காரர்",
    "Friend" to "நண்பர்",
    "Family Friend" to "குடும்ப நண்பர்",
    "Other" to "மற்றவை"
)
private fun phoneLabels(selectedLanguage: String): List<String> =
    PHONE_LABELS.map { if (selectedLanguage == "TA") it.second else it.first }

private fun emailLabels(selectedLanguage: String): List<String> =
    EMAIL_LABELS.map { if (selectedLanguage == "TA") it.second else it.first }

private fun rasiOptions(selectedLanguage: String): List<String> =
    RASI_LABELS.map { if (selectedLanguage == "TA") it.second else it.first }

private fun nakshatraOptions(selectedLanguage: String): List<String> =
    NAKSHATRA_LABELS.map { if (selectedLanguage == "TA") it.second else it.first }

private fun relationshipOptions(selectedLanguage: String): List<String> =
    RELATIONSHIP_LABELS.map { if (selectedLanguage == "TA") it.second else it.first }

private enum class AddSection {
    BASIC_INFO,
    PHONE,
    EMAIL,
    ADDRESS,
    RELATIONSHIPS,
    NOTES,
    TAGS
}

private data class PhoneRowInput(
    val country: CountryOption,
    val localNumber: String = "",
    val label: String = "",
    val isPrimary: Boolean = false,
    val isWhatsApp: Boolean = false
)

private data class EmailRowInput(
    val email: String = "",
    val label: String = "Personal",
    val isPrimary: Boolean = false
)

private data class RelationshipRowInput(
    val relatedContact: String = "",
    val type: String = "",
    val relatedName: String = ""
)

private data class CountryOption(
    val nameEn: String,
    val nameTa: String,
    val code: String,
    val flag: String,
    val compactLabel: String
)

private val COUNTRY_OPTIONS = listOf(
    CountryOption("India", "இந்தியா", "+91", "🇮🇳", "IN"),
    CountryOption("Singapore", "சிங்கப்பூர்", "+65", "🇸🇬", "SG"),
    CountryOption("United States", "அமெரிக்கா", "+1", "🇺🇸", "US"),
    CountryOption("China", "சீனா", "+86", "🇨🇳", "CN"),
    CountryOption("Hong Kong", "ஹாங்காங்", "+852", "🇭🇰", "HK"),
    CountryOption("United Kingdom", "இங்கிலாந்து", "+44", "🇬🇧", "UK"),
    CountryOption("United Arab Emirates", "ஐக்கிய அரபு அமீரகம்", "+971", "🇦🇪", "UAE"),
    CountryOption("Malaysia", "மலேசியா", "+60", "🇲🇾", "MY"),
    CountryOption("Canada", "கனடா", "+1", "🇨🇦", "CA"),
    CountryOption("Australia", "ஆஸ்திரேலியா", "+61", "🇦🇺", "AUS"),
    CountryOption("Sri Lanka", "இலங்கை", "+94", "🇱🇰", "LK"),
    CountryOption("Germany", "ஜெர்மனி", "+49", "🇩🇪", "DE"),
    CountryOption("France", "பிரான்ஸ்", "+33", "🇫🇷", "FR"),
    CountryOption("Japan", "ஜப்பான்", "+81", "🇯🇵", "JP"),
    CountryOption("Saudi Arabia", "சவுதி அரேபியா", "+966", "🇸🇦", "KSA"),
    CountryOption("Qatar", "கத்தார்", "+974", "🇶🇦", "QA"),
    CountryOption("Kuwait", "குவைத்", "+965", "🇰🇼", "KW"),
    CountryOption("Oman", "ஓமன்", "+968", "🇴🇲", "OM"),
    CountryOption("South Africa", "தென் ஆப்ரிக்கா", "+27", "🇿🇦", "ZA"),
    CountryOption("New Zealand", "நியூசிலாந்து", "+64", "🇳🇿", "NZ"),
    CountryOption("Thailand", "தாய்லாந்து", "+66", "🇹🇭", "TH"),
    CountryOption("Indonesia", "இந்தோனேசியா", "+62", "🇮🇩", "ID")
)
