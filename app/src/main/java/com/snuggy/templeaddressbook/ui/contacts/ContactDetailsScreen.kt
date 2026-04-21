package com.snuggy.templeaddressbook.ui.contacts

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
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
    onToggleFavorite: () -> Unit
) {
    BackHandler(onBack = onBack)

    val context = LocalContext.current

    fun openUri(uri: String) {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(uri)))
    }

    fun dialNumber() {
        context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:${contact.primaryPhone}")))
    }

    fun sendSms() {
        context.startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:${contact.primaryPhone}")))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppBg)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding(),
            color = OrangePrimary,
            shadowElevation = 3.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack, modifier = Modifier.width(40.dp).height(40.dp)) {
                    Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back", tint = Color.White)
                }
                Text(
                    text = "Contact Details",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onToggleFavorite, modifier = Modifier.width(40.dp).height(40.dp)) {
                    Icon(
                        imageVector = if (contact.isFavorite) Icons.Rounded.Star else Icons.Outlined.Star,
                        contentDescription = "Favorite",
                        tint = Color(0xFFFFD14A)
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = CardWhite,
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        DetailsAvatar(contact)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(contact.fullName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.headlineSmall)
                            Text(
                                contact.primaryPhone,
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

                    if (contact.tags.isNotEmpty()) {
                        Row(
                            modifier = Modifier
                                .padding(top = 12.dp)
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            contact.tags.forEach { tag ->
                                AssistChip(
                                    onClick = {},
                                    label = { Text(tag) },
                                    colors = AssistChipDefaults.assistChipColors(
                                        containerColor = SearchBg,
                                        labelColor = Color(0xFF363636)
                                    ),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
                                )
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp), color = CardBorder)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        QuickActionButton(label = "Call", onClick = ::dialNumber, modifier = Modifier.weight(1f))
                        QuickActionButton(label = "SMS", onClick = ::sendSms, modifier = Modifier.weight(1f))
                        QuickActionButton(
                            label = "WhatsApp",
                            onClick = { openUri("https://wa.me/${contact.primaryPhone.filter(Char::isDigit)}") },
                            modifier = Modifier.weight(1f),
                            accent = SuccessGreen
                        )
                    }
                }
            }

            DetailInfoCard(
                title = "Primary details",
                content = {
                    DetailLine(icon = Icons.Outlined.Call, label = "Phone", value = "${contact.primaryPhone} (${contact.phoneLabel})")
                    DetailLine(icon = Icons.Outlined.LocationOn, label = "Village / Town", value = contact.villageTown)
                    DetailLine(icon = Icons.Outlined.Info, label = "District", value = contact.district)
                }
            )
        }
    }
}

@Composable
private fun QuickActionButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    accent: Color = OrangePrimary
) {
    FilledTonalButton(
        onClick = onClick,
        modifier = modifier,
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
        shape = RoundedCornerShape(24.dp),
        color = CardWhite,
        border = androidx.compose.foundation.BorderStroke(1.dp, CardBorder)
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
private fun DetailsAvatar(contact: ContactRecord) {
    val context = LocalContext.current
    val imageBitmap = androidx.compose.runtime.remember(contact.photoUri) { loadContactPhotoBitmap(context, contact.photoUri) }
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
            androidx.compose.foundation.layout.BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
                val sizePx = with(androidx.compose.ui.platform.LocalDensity.current) { maxWidth.toPx() }
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

