import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { localProperties.load(it) }
}

fun quotedProperty(name: String, fallback: String): String {
    val value = localProperties.getProperty(name) ?: fallback
    return "\"${value.replace("\\", "\\\\").replace("\"", "\\\"")}\""
}

android {
    namespace = "com.example.fittrack"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.fittrack"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String",
            "SUPABASE_URL",
            quotedProperty(
                "SUPABASE_URL",
                "https://dpwelxavdsrswunzkfct.supabase.co",
            ),
        )
        buildConfigField(
            "String",
            "SUPABASE_ANON_KEY",
            quotedProperty(
                "SUPABASE_ANON_KEY",
                "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImRwd2VseGF2ZHNyc3d1bnprZmN0Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjkxNDY2MDQsImV4cCI6MjA4NDcyMjYwNH0.XENUvdTasSEUTfMyGePhADsHjVrFS_cFyPQddqL0L6Q",
            ),
        )
    }

    buildTypes {
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    implementation(project(":shared"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.drawerlayout)
    implementation(libs.androidx.fragment)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.play.services.auth)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
