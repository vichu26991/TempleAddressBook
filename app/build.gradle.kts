val appVersionCode = 43
val appVersionName = "1.2.36-tags-link-text-ui-polish"
val appBuildDate = "2026-04-29"

val appBuildNotes = listOf(
    "Patch 1.5.6: Tags UI polish for Contact Details and Add/Edit Contact.",
    "Contact Details Tags section now uses clean link-style tag text rows without left icons, right arrows, or boxed submenu rows.",
    "Tapping a tag text row from Contact Details still opens that tag detail page in Manage Tags.",
    "Add/Edit Contact selected tags now use a compact read-only list panel instead of plain loose text.",
    "Add/Edit Contact available tags now use compact filter-style list rows with right-side ticks instead of large green boxed rows.",
    "Carries forward Patch 1.5.5, Patch 1.5.4, and Patch 1.5.3 real DB-backed tag integration."
).joinToString("\n")

val appChangedFiles = listOf(
    "app/build.gradle.kts",
    "BUILD_HISTORY.md",
    "CHANGELOG.md",
    "README.md",
    "app/src/main/java/com/snuggy/templeaddressbook/data/TempleDbHelper.kt",
    "app/src/main/java/com/snuggy/templeaddressbook/ui/TempleAddressBookApp.kt",
    "app/src/main/java/com/snuggy/templeaddressbook/ui/contacts/AddContactScreen.kt",
    "app/src/main/java/com/snuggy/templeaddressbook/ui/contacts/ContactDetailsScreen.kt",
    "app/src/main/java/com/snuggy/templeaddressbook/ui/contacts/ContactsModels.kt",
    "app/src/main/java/com/snuggy/templeaddressbook/ui/contacts/ContactsRepository.kt",
    "app/src/main/java/com/snuggy/templeaddressbook/ui/contacts/ContactsScreen.kt",
    "app/src/main/java/com/snuggy/templeaddressbook/ui/tags/ManageTagsScreen.kt"
).joinToString("\n")

val appTestFocus = listOf(
    "Install directly over the current local build; previous Patch 1.5.3/1.5.4/1.5.5 do not need to be installed separately if skipped.",
    "Open Contact Details for a contact with tags and verify the Tags section shows link-style tag text, not card rows.",
    "Tap a tag text row in Contact Details and verify the matching Manage Tags detail page opens.",
    "Edit a contact with more than 3 selected tags and verify the selected tag summary is compact, vertical, and expandable with +N more / Show less.",
    "Verify available tag rows in Add/Edit Contact are compact list rows with right-side ticks, not large green boxes.",
    "Verify tag assignment, rename, delete, usage count, and Contacts Tag filter remain DB-backed and synchronized."
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
