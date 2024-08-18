plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.amigo.picture"
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
}

dependencies {
    implementation(Version.Dependencies.appcompat)
    implementation(Version.Dependencies.material)
    implementation(Version.Dependencies.coil)
    implementation(Version.Dependencies.coilGif)
    implementation(Version.Dependencies.coilVideo)
    api(Version.Dependencies.pictureSelector)
    implementation(Version.Dependencies.pictureCompress)
    implementation(Version.Dependencies.pictureUcrop)
    implementation(Version.Dependencies.cameraXCore)
    implementation(Version.Dependencies.cameraXCamera2)
    implementation(Version.Dependencies.cameraXLifecycle)
    implementation(Version.Dependencies.cameraXVideo)
    implementation(Version.Dependencies.cameraXView)
    implementation(Version.Dependencies.cameraXExtensions)

}