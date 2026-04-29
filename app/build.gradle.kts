val appVersionCode = 36
val appVersionName = "1.2.29-contact-details-edit-flow"
val appBuildDate = "2026-04-28"

val appBuildNotes = listOf(
    "Patch 1.4: Contact Details cleanup plus full Edit Contact update flow.",
    "Removed duplicate top favorite star from Contact Details; bottom Favorite action remains the single favorite control.",
    "Edit button now opens the Add/Edit Contact screen with existing saved values prefilled.",
    "Saving from Edit updates the existing contact instead of creating a duplicate.",
    "Added repository and SQLite update methods for existing contact records.",
    "No DB schema change; database remains schema 4.",
    "Tracking files updated to catch up Patch 1.1, Patch 1.2, Patch 1.3, and this Patch 1.4."
).joinToString("\n")

val appChangedFiles = listOf(
    "app/build.gradle.kts",
    "BUILD_HISTORY.md",
    "CHANGELOG.md",
    "README.md",
    "app/src/main/java/com/snuggy/templeaddressbook/data/TempleDbHelper.kt",
    "app/src/main/java/com/snuggy/templeaddressbook/ui/contacts/AddContactScreen.kt",
    "app/src/main/java/com/snuggy/templeaddressbook/ui/contacts/ContactDetailsScreen.kt",
    "app/src/main/java/com/snuggy/templeaddressbook/ui/contacts/ContactsRepository.kt",
    "app/src/main/java/com/snuggy/templeaddressbook/ui/contacts/ContactsScreen.kt"
).joinToString("\n")

val appTestFocus = listOf(
    "Open Contact Details and confirm the top favorite star is removed.",
    "Confirm bottom Favorite still toggles favorite state correctly.",
    "Open Contact Details, tap Edit, and verify saved values are prefilled.",
    "Change Basic Info, Address, Phone, Email, Notes, Tags, Photo, and Favorite where applicable.",
    "Save from Edit and verify the same contact is updated, not duplicated.",
    "Return to Contact Details and Contacts list and confirm refreshed values are visible.",
    "Confirm main bottom tabs stay hidden inside Contact Details and return on back to Contacts list.",
    "Regression retest Add Contact save flow, Contact Details bottom action bar, Share, More menu, Copy Address, and Open Map."
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
