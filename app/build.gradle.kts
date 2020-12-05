import Config.Libs
import Config.Libs.kapts

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
    id("kotlin-android")
}

android {
    compileSdkVersion(Config.SdkVersions.compile)
    defaultConfig {
        applicationId = "com.example.foodvenueapp"
        minSdkVersion(Config.SdkVersions.min)
        targetSdkVersion(Config.SdkVersions.target)
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "com.example.foodvenueapp.ui.TestRunner"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

    sourceSets {
        val sharedTestDir = "src/sharedTest/kotlin"
        val test by getting
        test.java.srcDir(sharedTestDir)
        val androidTest by getting
        androidTest.java.srcDir(sharedTestDir)

        map {
            println(it)
            it.java.srcDir("src/${it.name}/kotlin")
        }
    }

    buildFeatures {
        viewBinding = true
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation("org.jetbrains.kotlin:kotlin-stdlib:${rootProject.extra["kotlin_version"]}")
    with(Libs.Kotlin) {
        implementation(coroutinesCore)
        implementation(coroutinesAndroid)
    }

    //region Jetpack
    with(Libs.Jetpack) {
        implementation(core)
        implementation(appCompat)
        implementation(fragment)
        implementation(rv)
        implementation(constraint)
        implementation(material)
        lifecycle.forEach { implementation(it) }
    }
    //endregion

    //region 3rd party libs
    with(Libs.Misc) {
        implementation(retrofit)
        implementation(retrofitGson)
        implementation(gson)
        implementation(okhttp)
        implementation(okhttp2)
        implementation(picasso)
        implementation(permissions)
        gms.forEach { implementation(it) }
    }
    //endregion

    //region Testing Libs
    // Unit
    Libs.unitTesting.forEach {
        testImplementation(it)
    }

    // JVM test
    Libs.jvmTesting.forEach {
        testImplementation(it)
    }
    debugImplementation(Libs.fragmentTesting) {
        // https://github.com/android/android-test/issues/731#issuecomment-687201783
        exclude("androidx.test", "monitor")
    }

    // AndroidX Test - Instrumented testing
    Libs.instrumentationTesting.forEach {
        androidTestImplementation(it)
    }
    //endregion

    // Annotation Processors
    kapt(kapts)
}