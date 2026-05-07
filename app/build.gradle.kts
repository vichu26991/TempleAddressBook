val appVersionCode = 50
val appVersionName = "1.2.43-gesture-photo-crop-editor"
val appBuildDate = "2026-05-02"

val appBuildNotes = listOf(
    "Patch 1.6.6: Modernized the Add/Edit Contact photo crop editor by removing outdated on-screen +/- controls.",
    "Camera/Gallery selection in both Add Contact and Edit Contact opens the full-photo crop editor with a circular crop overlay.",
    "Users now adjust the photo using natural touch gestures: drag to reposition and pinch to zoom.",
    "The crop UI keeps only Cancel and Done actions, matching modern contact-photo crop behavior.",
    "Done creates and stores a real cropped contact-photo image from the circular crop area.",
    "No DB schema change."
).joinToString("\n")

val appChangedFiles = listOf(
    "app/build.gradle.kts",
    "BUILD_HISTORY.md",
    "CHANGELOG.md",
    "README.md",
    "app/src/main/java/com/snuggy/templeaddressbook/ui/contacts/AddContactScreen.kt"
).joinToString("\n")

val appTestFocus = listOf(
    "Install directly over versionCode 49; do not uninstall.",
    "Add Contact → Add/Change Photo → Choose from Gallery and verify full image opens with circular crop overlay.",
    "Add Contact → Camera and verify the same editor opens immediately after capture.",
    "Edit existing contact → Change Photo and verify the same editor works in edit mode.",
    "Verify the editor has no outdated +/- or circle +/- buttons; only Cancel and Done should be shown.",
    "Test drag to reposition and pinch to zoom the image inside the crop area.",
    "After Done, verify the cropped contact photo preview appears correctly, then Save and check Contacts list and Contact Details."
).joinToString("\n")

fun buildConfigString(value: String): String = buildString {
    append("\"")
    append(
        value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
    )
    append("\"")
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.snuggy.templeaddressbook"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.snuggy.templeaddressbook"
        minSdk = 24
        targetSdk = 36
        versionCode = appVersionCode
        versionName = appVersionName

        buildConfigField("String", "BUILD_LABEL", buildConfigString(appVersionName))
        buildConfigField("String", "BUILD_DATE", buildConfigString(appBuildDate))
        buildConfigField("String", "BUILD_NOTES", buildConfigString(appBuildNotes))
        buildConfigField("String", "BUILD_CHANGED_FILES", buildConfigString(appChangedFiles))
        buildConfigField("String", "BUILD_TEST_FOCUS", buildConfigString(appTestFocus))

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
        }
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

tasks.register<Copy>("copyVersionedDebugApk") {
    from(layout.buildDirectory.file("outputs/apk/debug/app-debug.apk"))
    into(layout.buildDirectory.dir("outputs/versioned-apk/debug"))
    rename { "TempleAddressBook-v$appVersionName($appVersionCode)-debug.apk" }
}

tasks.matching { it.name == "assembleDebug" }.configureEach {
    finalizedBy("copyVersionedDebugApk")
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
