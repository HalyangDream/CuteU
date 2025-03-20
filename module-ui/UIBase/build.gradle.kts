plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}

android {
    namespace = "com.amigo.uibase"
    compileSdk = 35

    defaultConfig {
        minSdk = 21

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

    buildFeatures {
        viewBinding = true
    }
}

kapt {
    arguments {
        arg("AROUTER_MODULE_NAME", project.name)
    }
}

dependencies {
    implementation("com.alibaba:arouter-api:1.5.2")
    kapt("com.alibaba:arouter-compiler:1.5.2")
    api(project(":module-logic:DataAnalysis"))
    api(project(":module-logic:BaseLogic"))
    api(project(":module-logic:Http"))
    api(project(":module-logic:Message"))
    api(project(":module-base:Basic"))
    api(project(":module-base:IM"))
    api(project(":module-base:Rtc"))
    api(project(":module-base:Http"))
    api(project(":module-base:Ad"))
    api(project(":module-public:Tool"))
    api(project(":module-public:Storage"))
    api(project(":module-public:Picture"))
    api(project(":module-public:Pay"))
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    api("androidx.appcompat:appcompat:1.6.1")
    api("androidx.recyclerview:recyclerview:1.3.2")
    api("com.google.android.material:material:1.9.0")
    api("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.0")
    api("androidx.lifecycle:lifecycle-runtime-ktx:2.4.0")
    api("androidx.lifecycle:lifecycle-livedata-ktx:2.4.0")
    api("io.github.scwang90:refresh-layout-kernel:2.1.0")
    api("io.github.scwang90:refresh-header-material:2.1.0")
    api("io.github.scwang90:refresh-footer-classics:2.1.0")
    api("com.github.getActivity:ShapeView:8.5")
    api("androidx.media3:media3-exoplayer:1.3.1")
    api("androidx.media3:media3-exoplayer-hls:1.3.1")
    api("androidx.media3:media3-exoplayer-dash:1.3.1")
    api("androidx.media3:media3-exoplayer-smoothstreaming:1.3.1")
    api("androidx.media3:media3-exoplayer-rtsp:1.3.1")
    api("androidx.media3:media3-ui:1.3.1")
    api("com.airbnb.android:lottie:6.4.0")
    api("com.google.code.gson:gson:2.10.1")
    api("com.google.android.play:app-update:2.1.0")
    api("com.google.android.play:app-update-ktx:2.1.0")
    api("io.github.youth5201314:banner:2.2.3")
}