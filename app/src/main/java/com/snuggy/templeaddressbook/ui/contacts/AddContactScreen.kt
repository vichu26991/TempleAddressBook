package com.snuggy.templeaddressbook.ui.contacts

import android.app.DatePickerDialog
import android.content.Context
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
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.window.Dialog
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
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
    onSave: (ContactDraft) -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()
    val imeVisible = WindowInsets.ime.getBottom(density) > 0

    var firstName by rememberSaveable { mutableStateOf("") }
    var lastName by rememberSaveable { mutableStateOf("") }
    var gender by rememberSaveable { mutableStateOf("") }
    var dobField by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue("")) }
    var rasi by rememberSaveable { mutableStateOf("") }
    var nakshatra by rememberSaveable { mutableStateOf("") }
    var villageTown by rememberSaveable { mutableStateOf("") }
    var district by rememberSaveable { mutableStateOf("") }
    var state by rememberSaveable { mutableStateOf("Tamil Nadu") }
    var country by rememberSaveable { mutableStateOf("India") }
    var doorNo by rememberSaveable { mutableStateOf("") }
    var buildingName by rememberSaveable { mutableStateOf("") }
    var streetName by rememberSaveable { mutableStateOf("") }
    var area by rememberSaveable { mutableStateOf("") }
    var postOffice by rememberSaveable { mutableStateOf("") }
    var taluk by rememberSaveable { mutableStateOf("") }
    var pinCode by rememberSaveable { mutableStateOf("") }
    var googleMapLink by rememberSaveable { mutableStateOf("") }
    var notes by rememberSaveable { mutableStateOf("") }
    var isFavorite by rememberSaveable { mutableStateOf(false) }
    var photoSpec by rememberSaveable { mutableStateOf<String?>(null) }
    var activeSection by rememberSaveable { mutableStateOf<AddSection?>(AddSection.BASIC_INFO) }
    var saveAttempted by rememberSaveable { mutableStateOf(false) }
    var showDiscardDialog by rememberSaveable { mutableStateOf(false) }
    var showPhotoActions by rememberSaveable { mutableStateOf(false) }
    var pendingPhotoSource by rememberSaveable { mutableStateOf<String?>(null) }
    var showPhotoAdjustDialog by rememberSaveable { mutableStateOf(false) }

    var photoScale by rememberSaveable { mutableStateOf(1f) }
    var photoOffsetX by rememberSaveable { mutableStateOf(0f) }
    var photoOffsetY by rememberSaveable { mutableStateOf(0f) }

    var rasiExpanded by rememberSaveable { mutableStateOf(false) }
    var nakshatraExpanded by rememberSaveable { mutableStateOf(false) }

    val phoneRows = remember {
        mutableStateListOf(
            PhoneRowInput(
                country = COUNTRY_OPTIONS.first(),
                localNumber = "",
                label = phoneLabels(selectedLanguage).first(),
                isPrimary = true,
                isWhatsApp = false
            )
        )
    }
    val emailRows = remember { mutableStateListOf(EmailRowInput(isPrimary = true, label = emailLabels(selectedLanguage).first())) }
    val relationshipRows = remember { mutableStateListOf(RelationshipRowInput()) }
    val selectedTags = remember { mutableStateListOf<String>() }
    var tagSearch by rememberSaveable { mutableStateOf("") }

    val quickTags = remember(selectedLanguage) {
        if (selectedLanguage == "TA") {
            listOf("தை அமாவாசை", "நிர்வாகம்", "கமிட்டி", "புதிய குடும்பம்", "தானதரர்")
        } else {
            listOf("Thai Amavasai", "Nirvaga", "Committee", "New Family", "Donor")
        }
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
    val atBottom = scrollState.maxValue == 0 || scrollState.value >= (scrollState.maxValue - 56)

    fun localizedLabel(en: String, ta: String): String = if (selectedLanguage == "TA") ta else en

    fun hasChanges(): Boolean = firstName.isNotBlank() ||
            lastName.isNotBlank() ||
            gender.isNotBlank() ||
            dobField.text.isNotBlank() ||
            rasi.isNotBlank() ||
            nakshatra.isNotBlank() ||
            villageTown.isNotBlank() ||
            district.isNotBlank() ||
            state != "Tamil Nadu" ||
            country != "India" ||
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
        val primaryPhoneRow = phoneRows.firstOrNull { it.localNumber.isNotBlank() && it.isPrimary }
            ?: phoneRows.firstOrNull { it.localNumber.isNotBlank() }
        val fullPhone = primaryPhoneRow?.let { "${it.country.code} ${it.localNumber.trim()}" } ?: ""
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
                photoUri = photoSpec
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
        PhotoAdjustDialog(
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
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBg)
    ) {
        AddContactHeader(
            selectedLanguage = selectedLanguage,
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
                    .padding(bottom = if (atBottom && !imeVisible) 18.dp else 90.dp),
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
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp),
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
                    LabeledCompactField(label = localizedLabel("Door No", "வீட்டு எண்"), value = doorNo, onValueChange = { doorNo = it })
                    LabeledCompactField(label = localizedLabel("Building Name", "கட்டிடம் பெயர்"), value = buildingName, onValueChange = { buildingName = it })
                    LabeledCompactField(label = localizedLabel("Street Name", "தெரு பெயர்"), value = streetName, onValueChange = { streetName = it })
                    LabeledCompactField(label = localizedLabel("Village / Area", "கிராமம் / பகுதி"), value = area, onValueChange = { area = it })
                    LabeledCompactField(label = localizedLabel("Post Office", "அஞ்சலகம்"), value = postOffice, onValueChange = { postOffice = it })
                    LabeledCompactField(label = localizedLabel("Taluk", "தாலுகா"), value = taluk, onValueChange = { taluk = it })
                    LabeledCompactField(label = localizedLabel("City / Town", "நகரம் / ஊர்"), value = villageTown, onValueChange = { villageTown = it })
                    LabeledCompactField(label = localizedLabel("District", "மாவட்டம்"), value = district, onValueChange = { district = it })
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        LabeledCompactField(
                            label = localizedLabel("State", "மாநிலம்"),
                            value = state,
                            onValueChange = { state = it },
                            modifier = Modifier.weight(1f)
                        )
                        LabeledCompactField(
                            label = localizedLabel("PIN Code", "அஞ்சல் குறியீடு"),
                            value = pinCode,
                            onValueChange = { pinCode = it.filter(Char::isDigit).take(6) },
                            keyboardType = KeyboardType.Number,
                            modifier = Modifier.weight(1f)
                        )
                    }
                    LabeledCompactField(label = localizedLabel("Country", "நாடு"), value = country, onValueChange = { country = it })
                    LabeledCompactField(label = localizedLabel("Google Map Link", "கூகுள் மேப் இணைப்பு"), value = googleMapLink, onValueChange = { googleMapLink = it })
                }

                SectionCard(
                    title = localizedLabel("Relationships", "உறவுகள்"),
                    expanded = activeSection == AddSection.RELATIONSHIPS,
                    onHeaderClick = {
                        activeSection = if (activeSection == AddSection.RELATIONSHIPS) null else AddSection.RELATIONSHIPS
                    }
                ) {
                    relationshipRows.forEachIndexed { index, row ->
                        RelationshipRowEditor(
                            selectedLanguage = selectedLanguage,
                            row = row,
                            relationshipOptions = relationshipOptions(selectedLanguage),
                            onUpdate = { updated -> relationshipRows[index] = updated },
                            onDelete = { if (relationshipRows.size > 1) relationshipRows.removeAt(index) }
                        )
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
                    title = localizedLabel("Tags", "டேக்குகள்"),
                    expanded = activeSection == AddSection.TAGS,
                    onHeaderClick = {
                        activeSection = if (activeSection == AddSection.TAGS) null else AddSection.TAGS
                    }
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        SmallSecondaryActionButton(localizedLabel("Manage Tags", "டேக்குகளை நிர்வகிக்க")) {}
                        SmallSecondaryActionButton(localizedLabel("+ New Tag", "+ புதிய டேக்")) {}
                    }
                    LabeledCompactField(
                        label = localizedLabel("Search Tags", "டேக்குகளை தேடு"),
                        value = tagSearch,
                        onValueChange = { tagSearch = it },
                        placeholder = localizedLabel("Type to filter tags", "டேக்குகளை வடிகட்ட தட்டச்சு செய்க")
                    )
                    TagChipsRow(
                        options = quickTags.filter { tagSearch.isBlank() || it.contains(tagSearch, ignoreCase = true) },
                        selectedTags = selectedTags,
                        onToggle = { tag ->
                            if (selectedTags.contains(tag)) selectedTags.remove(tag) else selectedTags.add(tag)
                        }
                    )
                    if (selectedTags.isNotEmpty()) {
                        Text(
                            text = localizedLabel("Selected Tags", "தேர்ந்தெடுத்த டேக்குகள்"),
                            color = MutedText,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                        TagChipsRow(options = selectedTags.toList(), selectedTags = selectedTags, onToggle = { tag -> selectedTags.remove(tag) })
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(localizedLabel("Mark as favorite", "பிடித்ததாக குறிக்க"), fontWeight = FontWeight.Medium)
                        Switch(
                            checked = isFavorite,
                            onCheckedChange = { isFavorite = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = SuccessGreen,
                                uncheckedBorderColor = CardBorder
                            )
                        )
                    }
                }
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = if (imeVisible) 18.dp else 76.dp)
            )
        }

        if (!imeVisible && atBottom) {
            SaveBottomBar(
                selectedLanguage = selectedLanguage,
                highlighted = canHighlightSave,
                onSave = { saveContact() }
            )
        }
    }
}

@Composable
private fun AddContactHeader(
    selectedLanguage: String,
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
                text = if (selectedLanguage == "TA") "தொடர்பை சேர்க்க" else "Add Contact",
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
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
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
                        .widthIn(min = 92.dp, max = 172.dp)
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
                onDismissRequest = onExpandedChange,
                modifier = Modifier.heightIn(max = 280.dp)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = option,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontWeight = if (option == value) FontWeight.SemiBold else FontWeight.Medium
                            )
                        },
                        onClick = { onSelected(option) },
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
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
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
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = onExpandedChange,
                modifier = Modifier.heightIn(max = 280.dp)
            ) {
                options.forEach { option ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = option,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontWeight = if (option == value) FontWeight.SemiBold else FontWeight.Medium
                            )
                        },
                        onClick = { onSelected(option) },
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

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
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

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.heightIn(max = 280.dp)
        ) {
            options.forEach { option ->
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
                        expanded = false
                        onSelected(option)
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
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.heightIn(max = 320.dp)
        ) {
            COUNTRY_OPTIONS.forEach { item ->
                val optionLabel = "${item.flag} ${if (selectedLanguage == "TA" && item.nameTa.isNotBlank()) item.nameTa else item.nameEn}  ${item.code}"
                DropdownMenuItem(
                    text = {
                        Text(
                            text = optionLabel,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontWeight = if (item.code == country.code && item.compactLabel == country.compactLabel) FontWeight.SemiBold else FontWeight.Medium
                        )
                    },
                    onClick = {
                        expanded = false
                        onSelected(item)
                    },
                    trailingIcon = if (item.code == country.code && item.compactLabel == country.compactLabel) {
                        { Icon(Icons.Outlined.Check, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp)) }
                    } else null,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
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

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
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
private fun RelationshipRowEditor(
    selectedLanguage: String,
    row: RelationshipRowInput,
    relationshipOptions: List<String>,
    onUpdate: (RelationshipRowInput) -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = SearchBg,
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            LabeledCompactField(
                label = if (selectedLanguage == "TA") "தொடர்பு பெயர்" else "Related Contact",
                value = row.relatedContact,
                onValueChange = { onUpdate(row.copy(relatedContact = it)) },
                placeholder = if (selectedLanguage == "TA") "பிறகு இருக்கும் தொடர்பை தேர்வு செய்யலாம்" else "Select existing contact later"
            )
            SmallSelectionField(
                label = if (selectedLanguage == "TA") "உறவு வகை" else "Relationship Type",
                value = row.type,
                options = relationshipOptions,
                onSelected = { onUpdate(row.copy(type = it)) }
            )
            LabeledCompactField(
                label = if (selectedLanguage == "TA") "உறவு பெயர்" else "Relationship Name",
                value = row.relatedName,
                onValueChange = { onUpdate(row.copy(relatedName = it)) },
                placeholder = if (selectedLanguage == "TA") "தொடர்புகளில் இல்லாவிட்டால் பயன்படுத்தவும்" else "Use if person is not yet in contacts"
            )
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(onClick = onDelete) { Text(if (selectedLanguage == "TA") "நீக்கு" else "Delete", color = MutedText) }
            }
        }
    }
}

@Composable
private fun SmallSelectionField(label: String, value: String, options: List<String>, onSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
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
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier.heightIn(max = 280.dp)
            ) {
                options.forEach { option ->
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
                            expanded = false
                            onSelected(option)
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
private fun TagChipsRow(options: List<String>, selectedTags: List<String>, onToggle: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        options.chunked(2).forEach { rowItems ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowItems.forEach { tag ->
                    FilterChip(
                        selected = selectedTags.contains(tag),
                        onClick = { onToggle(tag) },
                        label = { Text(tag) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = SuccessGreen.copy(alpha = 0.12f),
                            selectedLabelColor = SuccessGreen,
                            containerColor = SearchBg,
                            labelColor = Color(0xFF2A2A2A)
                        ),
                        border = BorderStroke(1.dp, if (selectedTags.contains(tag)) SuccessGreen.copy(alpha = 0.34f) else CardBorder)
                    )
                }
            }
        }
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
private fun PhotoAdjustDialog(
    source: String,
    selectedLanguage: String,
    initialScale: Float,
    initialOffsetX: Float,
    initialOffsetY: Float,
    onDismiss: () -> Unit,
    onConfirm: (String, Float, Float, Float) -> Unit
) {
    val context = LocalContext.current
    val bitmap = remember(source) { loadPhotoBitmap(context, encodePhotoSpec(source)) }
    val viewportSize = 220f
    var scale by rememberSaveable { mutableStateOf(initialScale.coerceAtLeast(1f)) }
    var offsetX by rememberSaveable { mutableStateOf(specOffsetToDialogPx(initialOffsetX, viewportSize)) }
    var offsetY by rememberSaveable { mutableStateOf(specOffsetToDialogPx(initialOffsetY, viewportSize)) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(28.dp), color = CardWhite) {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(if (selectedLanguage == "TA") "படத்தை சரிசெய்" else "Adjust Photo", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
                Text(
                    text = if (selectedLanguage == "TA") "இழுத்து நகர்த்தவும். பிஞ்ச் செய்து பெரிதாக்கவும் அல்லது சுருக்கவும்." else "Drag to position. Pinch to zoom in or out.",
                    color = MutedText,
                    fontSize = 12.sp
                )
                Surface(
                    modifier = Modifier.size(viewportSize.dp),
                    shape = CircleShape,
                    color = SearchBg,
                    border = BorderStroke(1.dp, CardBorder)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .pointerInput(source) {
                                detectTransformGestures(panZoomLock = true) { _, pan, zoom, _ ->
                                    val updatedScale = (scale * zoom).coerceIn(1f, 4f)
                                    val maxOffset = (viewportSize / 2f) * (updatedScale - 1f)
                                    scale = updatedScale
                                    offsetX = (offsetX + pan.x).coerceIn(-maxOffset, maxOffset)
                                    offsetY = (offsetY + pan.y).coerceIn(-maxOffset, maxOffset)
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (bitmap != null) {
                            Image(
                                bitmap = bitmap,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .graphicsLayer(
                                        scaleX = scale,
                                        scaleY = scale,
                                        translationX = offsetX,
                                        translationY = offsetY
                                    )
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = {
                        scale = 1f
                        offsetX = 0f
                        offsetY = 0f
                    }) {
                        Text(if (selectedLanguage == "TA") "ரீசெட்" else "Reset")
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextButton(onClick = onDismiss) {
                            Text(if (selectedLanguage == "TA") "ரத்து" else "Cancel")
                        }
                        TextButton(onClick = { onConfirm(source, scale, offsetX / viewportSize, offsetY / viewportSize) }) {
                            Text(if (selectedLanguage == "TA") "பயன்படுத்து" else "Apply")
                        }
                    }
                }
            }
        }
    }
}

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
    val uri = Uri.parse(spec.source)
    return runCatching {
        val bytes = if (uri.scheme == "file") {
            val path = uri.path ?: return@runCatching null
            File(path).takeIf { it.exists() }?.readBytes()
        } else {
            context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
        } ?: return@runCatching null

        val rawBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size) ?: return@runCatching null
        rotateBitmapIfRequired(rawBitmap, bytes).asImageBitmap()
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

private fun localizeMappedValue(value: String, selectedLanguage: String, mapping: List<Pair<String, String>>): String {
    if (value.isBlank()) return value
    val match = mapping.firstOrNull { it.first == value || it.second == value } ?: return value
    return if (selectedLanguage == "TA") match.second else match.first
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
    "Father" to "அப்பா", "Mother" to "அம்மா", "Son" to "மகன்", "Daughter" to "மகள்", "Husband" to "கணவர்",
    "Wife" to "மனைவி", "Elder Brother" to "அண்ணன்", "Younger Brother" to "தம்பி", "Elder Sister" to "அக்கா",
    "Younger Sister" to "தங்கை", "Grandfather" to "தாத்தா", "Grandmother" to "பாட்டி", "Grandson" to "பேரன்",
    "Granddaughter" to "பேத்தி", "Uncle" to "மாமா", "Aunt" to "அத்தை", "Chithappa" to "சித்தப்பா",
    "Chithi" to "சித்தி", "Periyappa" to "பெரியப்பா", "Periyamma" to "பெரியம்மா", "Son-in-law" to "மருமகன்",
    "Daughter-in-law" to "மருமகள்", "Father-in-law" to "மாமனார்", "Mother-in-law" to "மாமியார்",
    "Brother-in-law" to "மைத்துனன்", "Sister-in-law" to "மைத்துனி", "Relative" to "உறவினர்",
    "Family Friend" to "குடும்ப நண்பர்", "Driver" to "டிரைவர்", "Watchman" to "வாட்ச்மேன்",
    "House Maid" to "ஹவுஸ் மேய்ட்", "Cook" to "குக்", "Gardener" to "கார்டனர்", "Caretaker" to "கேர் டேக்கர்",
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
