package com.snuggy.templeaddressbook.ui.contacts

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Label
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Print
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.VolunteerActivism
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.snuggy.templeaddressbook.ui.theme.AppBg
import com.snuggy.templeaddressbook.ui.theme.CardBorder
import com.snuggy.templeaddressbook.ui.theme.CardWhite
import com.snuggy.templeaddressbook.ui.theme.MutedText
import com.snuggy.templeaddressbook.ui.theme.OrangePrimary
import com.snuggy.templeaddressbook.ui.theme.SearchBg
import com.snuggy.templeaddressbook.ui.theme.SuccessGreen

@Composable
fun ContactDetailsScreen(
    contact: ContactRecord,
    onBack: () -> Unit,
    onToggleFavorite: () -> Unit,
    selectedLanguage: String = "EN",
    onEdit: (() -> Unit)? = null,
    onOpenTag: ((String) -> Unit)? = null
) {
    BackHandler(onBack = onBack)

    val context = LocalContext.current
    val primaryPhone = contact.primaryPhoneForDisplay
    val whatsAppPhone = contact.whatsAppPhoneForDisplay
    val primaryEmail = contact.primaryEmailForDisplay
    val sortedTags = remember(contact.tags) {
        contact.tags
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinctBy { it.lowercase() }
            .sortedWith(String.CASE_INSENSITIVE_ORDER)
    }
    fun t(en: String, ta: String): String = if (selectedLanguage == "TA") ta else en

    fun openChooser(intent: Intent, title: String) {
        runCatching {
            context.startActivity(Intent.createChooser(intent, title))
        }.recoverCatching {
            context.startActivity(intent)
        }
    }

    fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun shareText(title: String, text: String) {
        if (text.isBlank()) {
            showToast(t("Nothing to share", "பகிர தகவல் இல்லை"))
            return
        }
        openChooser(
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
            },
            title
        )
    }

    fun copyText(label: String, text: String) {
        if (text.isBlank()) {
            showToast(t("Nothing to copy", "நகலெடுக்க தகவல் இல்லை"))
            return
        }
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText(label, text))
        showToast(t("Copied", "நகலெடுக்கப்பட்டது"))
    }

    fun contactShareText(): String = buildString {
        appendLine(contact.fullName)
        primaryPhone?.displayNumber?.takeIf { it.isNotBlank() }?.let { appendLine("Phone: $it") }
        primaryEmail?.email?.takeIf { it.isNotBlank() }?.let { appendLine("Email: $it") }
        contact.fullPostalAddress.takeIf { it.isNotBlank() }?.let { appendLine("Address: $it") }
    }.trim()

    fun dialNumber() {
        val number = primaryPhone?.displayNumber.orEmpty()
        if (number.isNotBlank()) {
            context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${number.filter { !it.isWhitespace() }}")))
        }
    }

    fun sendSms() {
        val number = primaryPhone?.displayNumber.orEmpty()
        if (number.isNotBlank()) {
            context.startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:${number.filter { !it.isWhitespace() }}")))
        }
    }

    fun openWhatsApp() {
        val number = whatsAppPhone?.displayNumber.orEmpty().filter(Char::isDigit)
        if (number.isNotBlank()) {
            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$number")))
        }
    }

    fun sendEmail() {
        val email = primaryEmail?.email.orEmpty()
        if (email.isNotBlank()) {
            context.startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse("mailto:$email")))
        }
    }

    fun openMap() {
        val rawTarget = contact.googleMapLink.trim().ifBlank { contact.fullPostalAddress }
        val uri = if (rawTarget.startsWith("http://", ignoreCase = true) || rawTarget.startsWith("https://", ignoreCase = true)) {
            Uri.parse(rawTarget)
        } else {
            Uri.parse("geo:0,0?q=${Uri.encode(rawTarget)}")
        }
        try {
            openChooser(
                Intent(Intent.ACTION_VIEW, uri).apply { addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) },
                t("Open map with", "மேப்பை திற")
            )
        } catch (_: ActivityNotFoundException) {
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBg)
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = OrangePrimary,
            shadowElevation = 3.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.width(40.dp).height(40.dp)) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text(
                    text = t("Contact Details", "தொடர்பு விவரம்"),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                color = CardWhite,
                border = BorderStroke(1.dp, CardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        DetailsAvatar(contact)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(contact.fullName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineSmall)
                            Text(
                                primaryPhone?.displayNumber.orEmpty().ifBlank { "—" },
                                fontSize = 17.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF1E1E1E),
                                modifier = Modifier.padding(top = 3.dp)
                            )
                            Text(
                                listOf(contact.villageTown, contact.district, contact.state, contact.country)
                                    .filter { it.isNotBlank() }
                                    .joinToString(", "),
                                color = MutedText,
                                modifier = Modifier.padding(top = 3.dp)
                            )
                        }
                    }

                    if (sortedTags.isNotEmpty()) {
                        HeaderTagSummary(
                            tags = sortedTags,
                            modifier = Modifier.padding(top = 10.dp)
                        )
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp), color = CardBorder)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        QuickActionButton(
                            label = t("Call", "அழை"),
                            onClick = ::dialNumber,
                            modifier = Modifier.weight(1f),
                            enabled = primaryPhone != null
                        )
                        QuickActionButton(
                            label = t("SMS", "SMS"),
                            onClick = ::sendSms,
                            modifier = Modifier.weight(1f),
                            enabled = primaryPhone != null
                        )
                        QuickActionButton(
                            label = t("WhatsApp", "வாட்ஸ்அப்"),
                            onClick = ::openWhatsApp,
                            modifier = Modifier.weight(1f),
                            enabled = whatsAppPhone != null,
                            accent = SuccessGreen
                        )
                    }
                    if (primaryEmail != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        QuickActionButton(
                            label = t("Email", "மின்னஞ்சல்"),
                            onClick = ::sendEmail,
                            modifier = Modifier.fillMaxWidth(),
                            enabled = true,
                            accent = Color(0xFF3367D6)
                        )
                    }
                }
            }

            DetailInfoCard(title = t("Basic Info", "அடிப்படை தகவல்")) {
                DetailLine(icon = Icons.Outlined.Info, label = t("First Name", "முதல் பெயர்"), value = contact.firstName)
                DetailLine(icon = Icons.Outlined.Info, label = t("Last Name", "கடைசி பெயர்"), value = contact.lastName)
                DetailLine(icon = Icons.Outlined.Info, label = t("Gender", "பாலினம்"), value = contact.gender)
                DetailLine(icon = Icons.Outlined.Info, label = t("Date of Birth", "பிறந்த தேதி"), value = contact.dob)
                DetailLine(icon = Icons.Outlined.Info, label = t("Rasi", "ராசி"), value = contact.rasi)
                DetailLine(icon = Icons.Outlined.Info, label = t("Nakshatra", "நட்சத்திரம்"), value = contact.nakshatra)
            }

            DetailInfoCard(title = t("Tags", "குறிச்சொற்கள்")) {
                if (sortedTags.isEmpty()) {
                    DetailEmptyText(t("No tags saved", "குறிச்சொற்கள் இல்லை"))
                } else {
                    DetailTagList(tags = sortedTags, onTagClick = onOpenTag)
                }
            }

            DetailInfoCard(title = t("Phone Numbers", "தொலைபேசி எண்கள்")) {
                contact.phonesForDisplay.forEach { phone ->
                    PhoneDetailLine(phone = phone, selectedLanguage = selectedLanguage)
                }
            }

            DetailInfoCard(title = t("Email Address", "மின்னஞ்சல் முகவரி")) {
                if (contact.emailsForDisplay.isEmpty()) {
                    DetailEmptyText(t("No email saved", "மின்னஞ்சல் சேமிக்கப்படவில்லை"))
                } else {
                    contact.emailsForDisplay.forEach { email ->
                        EmailDetailLine(email = email, selectedLanguage = selectedLanguage)
                    }
                }
            }

            DetailInfoCard(title = t("Address", "முகவரி")) {
                DetailLine(icon = Icons.Outlined.LocationOn, label = t("Door No / Unit / Apt No", "வீட்டு எண் / யூனிட்"), value = contact.doorNo)
                DetailLine(icon = Icons.Outlined.LocationOn, label = t("Building Name / Tower", "கட்டிடம் / டவர்"), value = contact.buildingName)
                DetailLine(icon = Icons.Outlined.LocationOn, label = t("Street Name / Street / Avenue", "தெரு / அவென்யூ"), value = contact.streetName)
                DetailLine(icon = Icons.Outlined.LocationOn, label = t("Village / Area / Locality", "பகுதி / இருப்பிடம்"), value = contact.area.ifBlank { contact.villageTown })
                DetailLine(icon = Icons.Outlined.LocationOn, label = t("Post Office / Mail Area", "அஞ்சலகம்"), value = contact.postOffice)
                DetailLine(icon = Icons.Outlined.LocationOn, label = t("Taluk / County", "தாலுகா / கவுண்டி"), value = contact.taluk)
                DetailLine(icon = Icons.Outlined.LocationOn, label = t("City / Town", "நகரம் / ஊர்"), value = contact.villageTown)
                DetailLine(icon = Icons.Outlined.LocationOn, label = t("District / County", "மாவட்டம் / கவுண்டி"), value = contact.district)
                DetailLine(icon = Icons.Outlined.LocationOn, label = t("State / Province / Region", "மாநிலம் / மாகாணம்"), value = contact.state)
                DetailLine(icon = Icons.Outlined.LocationOn, label = t("PIN / Postal / ZIP Code", "அஞ்சல் / ZIP குறியீடு"), value = contact.pinCode)
                DetailLine(icon = Icons.Outlined.LocationOn, label = t("Country", "நாடு"), value = contact.country)

                if (contact.fullPostalAddress.isNotBlank() || contact.googleMapLink.isNotBlank()) {
                    QuickActionButton(
                        label = t("Open Map", "மேப் திற"),
                        onClick = ::openMap,
                        modifier = Modifier.fillMaxWidth(),
                        accent = SuccessGreen
                    )
                }
            }

            DetailInfoCard(title = t("Notes", "குறிப்புகள்")) {
                if (contact.notes.isBlank()) {
                    DetailEmptyText(t("No notes saved", "குறிப்புகள் இல்லை"))
                } else {
                    Text(
                        text = contact.notes,
                        color = Color(0xFF222222),
                        fontSize = 16.sp,
                        lineHeight = 22.sp
                    )
                }
            }

            DetailInfoCard(title = t("Donation Summary", "நன்கொடை சுருக்கம்")) {
                DetailEmptyText(t("Donation module is not implemented yet.", "நன்கொடை பகுதி இன்னும் செயல்படுத்தப்படவில்லை."))
            }
        }

        ContactDetailsBottomActionBar(
            isFavorite = contact.isFavorite,
            selectedLanguage = selectedLanguage,
            onFavorite = onToggleFavorite,
            onEdit = {
                if (onEdit != null) {
                    onEdit()
                } else {
                    showToast(t("Edit contact will be added in the edit-flow patch", "தொடர்பு திருத்தம் அடுத்த பாச்சில் சேர்க்கப்படும்"))
                }
            },
            onShare = { shareText(t("Share contact", "தொடர்பை பகிர்"), contactShareText()) },
            onAddToGroup = { showToast(t("Groups module is not implemented yet", "குழுக்கள் பகுதி இன்னும் செயல்படுத்தப்படவில்லை")) },
            onManageTags = { showToast(t("Tag management is not implemented yet", "குறிச்சொல் மேலாண்மை இன்னும் செயல்படுத்தப்படவில்லை")) },
            onAddDonation = { showToast(t("Donation entry is not implemented yet", "நன்கொடை பதிவு இன்னும் செயல்படுத்தப்படவில்லை")) },
            onShareAddress = { shareText(t("Share address", "முகவரியை பகிர்"), contact.fullPostalAddress) },
            onCopyAddress = { copyText("Address", contact.fullPostalAddress) },
            onOpenMap = ::openMap,
            onDuplicate = { showToast(t("Duplicate contact will be added later", "நகல் தொடர்பு பின்னர் சேர்க்கப்படும்")) },
            onDelete = { showToast(t("Delete contact will be added in the edit-flow patch", "தொடர்பு நீக்கம் திருத்த பாச்சில் சேர்க்கப்படும்")) }
        )
    }
}

@Composable
private fun ContactDetailsBottomActionBar(
    isFavorite: Boolean,
    selectedLanguage: String,
    onFavorite: () -> Unit,
    onEdit: () -> Unit,
    onShare: () -> Unit,
    onAddToGroup: () -> Unit,
    onManageTags: () -> Unit,
    onAddDonation: () -> Unit,
    onShareAddress: () -> Unit,
    onCopyAddress: () -> Unit,
    onOpenMap: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit
) {
    fun t(en: String, ta: String): String = if (selectedLanguage == "TA") ta else en
    var moreExpanded by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = CardWhite,
        shadowElevation = 10.dp,
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomActionItem(
                label = t("Favorite", "பிடித்தவை"),
                icon = if (isFavorite) Icons.Rounded.Star else Icons.Outlined.Star,
                tint = if (isFavorite) Color(0xFFFFC107) else MutedText,
                onClick = onFavorite
            )
            BottomActionItem(
                label = t("Edit", "திருத்து"),
                icon = Icons.Outlined.Edit,
                tint = OrangePrimary,
                onClick = onEdit
            )
            BottomActionItem(
                label = t("Share", "பகிர்"),
                icon = Icons.Outlined.Share,
                tint = MutedText,
                onClick = onShare
            )

            Box(contentAlignment = Alignment.TopEnd) {
                BottomActionItem(
                    label = t("More", "மேலும்"),
                    icon = Icons.Outlined.MoreVert,
                    tint = MutedText,
                    onClick = { moreExpanded = true }
                )
                DropdownMenu(
                    expanded = moreExpanded,
                    onDismissRequest = { moreExpanded = false }
                ) {
                    ContactMoreMenuItem(t("Add to Group", "குழுவில் சேர்"), Icons.Outlined.Info) { moreExpanded = false; onAddToGroup() }
                    ContactMoreMenuItem(t("Manage Tags", "குறிச்சொற்கள்"), Icons.Outlined.Label) { moreExpanded = false; onManageTags() }
                    ContactMoreMenuItem(t("Add Donation Entry", "நன்கொடை பதிவு"), Icons.Outlined.VolunteerActivism) { moreExpanded = false; onAddDonation() }
                    ContactMoreMenuItem(t("Print / Share Address", "முகவரி பகிர்"), Icons.Outlined.Print) { moreExpanded = false; onShareAddress() }
                    ContactMoreMenuItem(t("Copy Address", "முகவரி நகல்"), Icons.Outlined.ContentCopy) { moreExpanded = false; onCopyAddress() }
                    ContactMoreMenuItem(t("Open Map", "மேப் திற"), Icons.Outlined.LocationOn) { moreExpanded = false; onOpenMap() }
                    ContactMoreMenuItem(t("Duplicate Contact", "நகல் தொடர்பு"), Icons.Outlined.Info) { moreExpanded = false; onDuplicate() }
                    ContactMoreMenuItem(t("Delete Contact", "தொடர்பு நீக்கு"), Icons.Outlined.DeleteOutline) { moreExpanded = false; onDelete() }
                }
            }
        }
    }
}

@Composable
private fun BottomActionItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick, modifier = Modifier.width(78.dp).height(52.dp)) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = label, tint = tint, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(3.dp))
            Text(label, color = tint, fontSize = 11.sp, fontWeight = FontWeight.Medium, maxLines = 1)
        }
    }
}

@Composable
private fun ContactMoreMenuItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    DropdownMenuItem(
        text = { Text(label, fontWeight = FontWeight.Medium) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = OrangePrimary) },
        onClick = onClick
    )
}

@Composable
private fun QuickActionButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accent: Color = OrangePrimary,
    enabled: Boolean = true
) {
    FilledTonalButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = accent.copy(alpha = 0.12f),
            contentColor = accent
        ),
        contentPadding = PaddingValues(vertical = 12.dp)
    ) {
        Text(label, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun DetailInfoCard(title: String, content: @Composable () -> Unit) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        color = CardWhite,
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            content()
        }
    }
}

@Composable
private fun DetailLine(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Icon(icon, contentDescription = null, tint = OrangePrimary, modifier = Modifier.padding(top = 2.dp))
        Column {
            Text(label, color = MutedText, fontWeight = FontWeight.Medium)
            Text(value.ifBlank { "—" }, color = Color(0xFF222222), fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun PhoneDetailLine(phone: ContactPhoneRecord, selectedLanguage: String) {
    val tPrimary = if (selectedLanguage == "TA") "முதன்மை" else "Primary"
    val tWhatsApp = if (selectedLanguage == "TA") "வாட்ஸ்அப்" else "WhatsApp"
    val meta = buildList {
        if (phone.label.isNotBlank()) add(phone.label)
        if (phone.isPrimary) add(tPrimary)
        if (phone.isWhatsApp) add(tWhatsApp)
    }.joinToString(" • ")

    Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Icon(Icons.Outlined.Call, contentDescription = null, tint = OrangePrimary, modifier = Modifier.padding(top = 2.dp))
        Column {
            Text(meta.ifBlank { if (selectedLanguage == "TA") "தொலைபேசி" else "Phone" }, color = MutedText, fontWeight = FontWeight.Medium)
            Text(
                text = listOf(phone.countryFlag, phone.displayNumber).filter { it.isNotBlank() }.joinToString(" "),
                color = Color(0xFF222222),
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun EmailDetailLine(email: ContactEmailRecord, selectedLanguage: String) {
    val tPrimary = if (selectedLanguage == "TA") "முதன்மை" else "Primary"
    val meta = buildList {
        if (email.label.isNotBlank()) add(email.label)
        if (email.isPrimary) add(tPrimary)
    }.joinToString(" • ")

    Row(verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Icon(Icons.Outlined.Email, contentDescription = null, tint = OrangePrimary, modifier = Modifier.padding(top = 2.dp))
        Column {
            Text(meta.ifBlank { if (selectedLanguage == "TA") "மின்னஞ்சல்" else "Email" }, color = MutedText, fontWeight = FontWeight.Medium)
            Text(email.email.ifBlank { "—" }, color = Color(0xFF222222), fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun DetailEmptyText(text: String) {
    Text(text = text, color = MutedText, fontSize = 15.sp)
}

@Composable
private fun DetailTagList(tags: List<String>, onTagClick: ((String) -> Unit)?) {
    Column(modifier = Modifier.fillMaxWidth()) {
        tags.forEachIndexed { index, tag ->
            val rowModifier = if (onTagClick != null) {
                Modifier
                    .fillMaxWidth()
                    .clickable { onTagClick(tag) }
            } else {
                Modifier.fillMaxWidth()
            }
            Text(
                text = tag,
                color = OrangePrimary,
                fontSize = 13.5.sp,
                lineHeight = 18.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
                modifier = rowModifier.padding(vertical = 7.dp)
            )
            if (index != tags.lastIndex) {
                HorizontalDivider(color = CardBorder.copy(alpha = 0.65f))
            }
        }
    }
}


@Composable
private fun HeaderTagSummary(
    tags: List<String>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        tags.forEach { tag ->
            HeaderTagChip(label = tag)
        }
    }
}

@Composable
private fun HeaderTagChip(label: String) {
    Surface(
        shape = RoundedCornerShape(9.dp),
        color = SearchBg,
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Text(
            text = label,
            color = Color(0xFF363636),
            fontWeight = FontWeight.Medium,
            fontSize = 11.sp,
            lineHeight = 14.sp,
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
        )
    }
}


@Composable
private fun DetailTagChip(label: String) {
    Surface(
        shape = RoundedCornerShape(9.dp),
        color = SearchBg,
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Text(
            text = label,
            color = Color(0xFF363636),
            fontWeight = FontWeight.Medium,
            fontSize = 10.5.sp,
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
        )
    }
}

@Composable
private fun DetailsAvatar(contact: ContactRecord) {
    val context = LocalContext.current
    val imageBitmap = remember(contact.photoUri) { loadContactPhotoBitmap(context, contact.photoUri) }
    val spec = decodePhotoSpec(contact.photoUri)
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .background(Brush.linearGradient(listOf(Color(0xFFD8C4AA), Color(0xFF9C744D))))
            .width(62.dp)
            .height(62.dp),
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
            Text(contact.initials, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }
    }
}
