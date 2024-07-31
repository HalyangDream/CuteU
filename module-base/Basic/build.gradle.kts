plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.cute.basic"
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

    buildFeatures {
        viewBinding = true
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(Version.Dependencies.ktx)
    implementation(Version.Dependencies.viewModelKtx)
    implementation(Version.Dependencies.livedataKtx)
    implementation(Version.Dependencies.lifecycleKtx)
    implementation(Version.Dependencies.appcompat)
    implementation(Version.Dependencies.recyclerview)
    implementation(Version.Dependencies.material)
    api(Version.Dependencies.brvahRecyclerView)
}