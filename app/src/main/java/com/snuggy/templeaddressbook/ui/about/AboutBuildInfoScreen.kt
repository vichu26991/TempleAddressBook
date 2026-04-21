package com.snuggy.templeaddressbook.ui.about

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.snuggy.templeaddressbook.BuildConfig
import com.snuggy.templeaddressbook.data.TempleDbHelper
import com.snuggy.templeaddressbook.ui.theme.AppBg
import com.snuggy.templeaddressbook.ui.theme.CardBorder
import com.snuggy.templeaddressbook.ui.theme.CardWhite
import com.snuggy.templeaddressbook.ui.theme.MutedText
import com.snuggy.templeaddressbook.ui.theme.OrangePrimary
import com.snuggy.templeaddressbook.ui.theme.SuccessGreen

@Composable
fun AboutBuildInfoScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler(onBack = onBack)

    val buildNotes = BuildConfig.BUILD_NOTES
        .split("\\n")
        .map { it.trim() }
        .filter { it.isNotBlank() }
    val changedFiles = BuildConfig.BUILD_CHANGED_FILES
        .split("\\n")
        .map { it.trim() }
        .filter { it.isNotBlank() }
    val testFocus = BuildConfig.BUILD_TEST_FOCUS
        .split("\\n")
        .map { it.trim() }
        .filter { it.isNotBlank() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppBg)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(OrangePrimary)
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White
                )
            }
            Text(
                text = "About • Build Info",
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 26.sp
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            BuildSectionCard(title = "App") {
                BuildInfoRow(label = "App Name", value = "Temple Address Book")
                BuildInfoRow(label = "Build Label", value = BuildConfig.BUILD_LABEL)
                BuildInfoRow(label = "Version", value = BuildConfig.VERSION_NAME)
                BuildInfoRow(label = "Build Code", value = BuildConfig.VERSION_CODE.toString())
                BuildInfoRow(label = "Build Date", value = BuildConfig.BUILD_DATE)
                BuildInfoRow(label = "Database Version", value = TempleDbHelper.SCHEMA_VERSION.toString())
            }

            BuildSectionCard(title = "What this build changed") {
                if (buildNotes.isEmpty()) {
                    MutedBody("No build notes added yet.")
                } else {
                    buildNotes.forEach { note -> BulletRow(note) }
                }
            }

            BuildSectionCard(title = "Files changed") {
                if (changedFiles.isEmpty()) {
                    MutedBody("No file list added yet.")
                } else {
                    changedFiles.forEach { path -> BulletRow(path) }
                }
            }

            BuildSectionCard(title = "Focus for testing") {
                if (testFocus.isEmpty()) {
                    MutedBody("No testing focus added yet.")
                } else {
                    testFocus.forEach { item -> BulletRow(item) }
                }
            }

            BuildSectionCard(title = "How to use this") {
                MutedBody(
                    "Use this screen to confirm the installed version before testing. " +
                        "Match the Build Label with the APK you installed locally."
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
private fun BuildSectionCard(
    title: String,
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = null,
                    tint = OrangePrimary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF1F1F25)
                )
            }
            HorizontalDivider(color = CardBorder)
            content()
        }
    }
}

@Composable
private fun BuildInfoRow(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            color = MutedText
        )
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (label == "Build Label") SuccessGreen else Color(0xFF1F1F25)
        )
    }
}

@Composable
private fun BulletRow(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = "•",
            color = OrangePrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(top = 1.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            color = Color(0xFF1F1F25),
            fontSize = 16.sp,
            lineHeight = 22.sp
        )
    }
}

@Composable
private fun MutedBody(text: String) {
    Text(
        text = text,
        color = MutedText,
        fontSize = 15.sp,
        lineHeight = 21.sp
    )
}
