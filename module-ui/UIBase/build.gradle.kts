plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}

android {
    namespace = "com.cute.uibase"
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
    implementation(Version.Dependencies.arouter)
    kapt(Version.Dependencies.arouterCompiler)
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
    api(Version.Dependencies.coroutines)
    api(Version.Dependencies.coroutinesAndroid)
    api(Version.Dependencies.appcompat)
    api(Version.Dependencies.recyclerview)
    api(Version.Dependencies.material)
    api(Version.Dependencies.lifecycleKtx)
    api(Version.Dependencies.livedataKtx)
    api(Version.Dependencies.viewModelKtx)
    api(Version.Dependencies.refresh)
    api(Version.Dependencies.refreshHeader)
    api(Version.Dependencies.refreshFooter)
    api(Version.Dependencies.shapeView)
    api(Version.Dependencies.exoplayer)
    api(Version.Dependencies.exoplayerHls)
    api(Version.Dependencies.exoplayerDash)
    api(Version.Dependencies.exoplayerSs)
    api(Version.Dependencies.exoplayerRtsp)
    api(Version.Dependencies.exoplayerUi)
    api(Version.Dependencies.lottie)
    api(Version.Dependencies.gson)
    api(Version.Dependencies.appUpdate)
    api(Version.Dependencies.appUpdateKtx)
    api(Version.Dependencies.banner)
}