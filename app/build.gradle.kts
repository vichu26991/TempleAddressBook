import org.gradle.api.tasks.Copy

val appVersionCode = 31
val appVersionName = "1.2.24-address-labels-map-chooser-fix"
val appBuildDate = "2026-04-25"

val appBuildNotes = listOf(
    "Kept District, State, and Country as textbox fields in Add Contact.",
    "Retained the updated Address labels/placeholders already present in the latest AddContactScreen source.",
    "Fixed only openMapChooser() so chooser behavior now follows the locked 3-use-case rule.",
    "Map link opens chooser when present.",
    "Address query opens chooser when map link is empty but address exists.",
    "When both address and map link are empty, chooser still opens with a generic geo intent and no snackbar.",
    "Phone section, Email section, and the Google Map Link field UI block were kept unchanged.",
    "About build info updated for this build."
).joinToString("\\n")

val appChangedFiles = listOf(
    "app/build.gradle.kts",
    "app/src/main/java/com/snuggy/templeaddressbook/ui/contacts/AddContactScreen.kt",
    "CHANGELOG.md",
    "BUILD_HISTORY.md",
    "README.md"
).joinToString("\\n")

val appTestFocus = listOf(
    "Tap map icon with map link filled -> chooser should open with the link.",
    "Tap map icon with address filled and map link empty -> chooser should open with address query.",
    "Tap map icon with both address and map link empty -> chooser should still open and no snackbar should appear.",
    "Address labels/placeholders should remain as in the latest stable source.",
    "District, State, and Country should remain textbox fields.",
    "Phone and Email sections should remain visually and functionally unchanged."
).joinToString("\\n")

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
