val appVersionCode = 45
val appVersionName = "1.2.38-dropdown-ui-polish"
val appBuildDate = "2026-05-02"

val appBuildNotes = listOf(
    "Patch 1.6.1: Dropdown visual polish and Relationship detail icon cleanup.",
    "Removed the tag/label-style icon from Contact Details relationship rows.",
    "Updated compact dropdown menu presentation for Rasi, Nakshatra, phone label, email label, relationship type, and country selector.",
    "Dropdown options now use cleaner boxed rows with selected tick styling to match the Tags visual language.",
    "No relationship logic, tag logic, phone/email save logic, or DB schema changes were made."
).joinToString("\n")

val appChangedFiles = listOf(
    "app/build.gradle.kts",
    "BUILD_HISTORY.md",
    "CHANGELOG.md",
    "README.md",
    "app/src/main/java/com/snuggy/templeaddressbook/ui/contacts/AddContactScreen.kt",
    "app/src/main/java/com/snuggy/templeaddressbook/ui/contacts/ContactDetailsScreen.kt"
).joinToString("\n")

val appTestFocus = listOf(
    "Install directly over versionCode 44; do not uninstall.",
    "Open Contact Details and verify relationship rows no longer show the tag/label icon.",
    "Open Add/Edit Contact and verify Rasi/Nakshatra dropdown menus use the cleaner boxed option style.",
    "Verify phone label, email label, and relationship type dropdown menus use the same visual style.",
    "Verify selection values, save behavior, relationships, tags, phone, and email remain unchanged."
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
