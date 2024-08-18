plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("androidx.room")
}


android {
    namespace = "com.amigo.im"
    compileSdk = Version.compileSdk

    defaultConfig {
        minSdk = Version.targetSdk
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    room {
        schemaDirectory("$projectDir/schemas")
    }
}

dependencies {
    implementation(Version.Dependencies.ktx)
    implementation(Version.Dependencies.coroutines)
    implementation(Version.Dependencies.coroutinesAndroid)
    implementation(Version.Dependencies.rtm)
    implementation(Version.Dependencies.roomRuntime)
    kapt(Version.Dependencies.roomCompile)
}