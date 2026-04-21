import org.gradle.api.tasks.Copy

val appVersionCode = 28
val appVersionName = "1.2.23-rasi-nakshatra-inline-premium"
val appBuildDate = "2026-04-20"

val appBuildNotes = listOf(
    "Moved Rasi and Nakshatra onto the same line in Basic Info.",
    "Changed Rasi and Nakshatra to compact dropdown style closer to Phone/Email Type dropdown family.",
    "Replaced older picker feel for Rasi/Nakshatra with faster inline dropdown menu.",
    "Kept Phone Numbers section untouched.",
    "Kept Email Address section untouched.",
    "About build info updated for this build."
).joinToString("\n")

val appChangedFiles = listOf(
    "app/build.gradle.kts",
    "ui/contacts/AddContactScreen.kt"
).joinToString("\n")

val appTestFocus = listOf(
    "Rasi and Nakshatra should appear on the same line in Basic Info.",
    "Both compact dropdowns should visually match the Type dropdown family more closely.",
    "Tapping Rasi/Nakshatra should open a faster inline dropdown menu instead of the older picker feel.",
    "Selected values should remain visible in both EN and TA without truncating too early.",
    "Phone section should remain unchanged.",
    "Email section should remain unchanged."
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