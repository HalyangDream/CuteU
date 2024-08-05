import com.android.build.gradle.internal.api.BaseVariantOutputImpl
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}


android {
    namespace = "com.cute.app"
    compileSdk = Version.compileSdk

    defaultConfig {
        applicationId = "com.amor.app"
        minSdk = Version.targetSdk
        targetSdk = Version.compileSdk
        versionCode = 1
        versionName = "1.0"
        flavorDimensionList.add("product")
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        ndk {
            abiFilters.addAll(listOf("armeabi-v7a", "arm64-v8a"))
        }
    }



    signingConfigs {
        getByName("debug") {
            storeFile = file("../keystore/cuteu.jks")
            storePassword = "cuteu0731"
            keyAlias = "cuteu"
            keyPassword = "cuteu0731"
            enableV1Signing = true
            enableV2Signing = true
        }

        create("Vibro") {
            storeFile = file("../keystore/cuteu.jks")
            storePassword = "cuteu0731"
            keyAlias = "cuteu"
            keyPassword = "cuteu0731"
            enableV1Signing = true
            enableV2Signing = true
        }
    }

    productFlavors {

        create("piya") {
            applicationId = "com.piya.app"
            versionCode = 10000
            versionName = "1.0.0"
            resValue("string", "app_name", "Piya")
            resValue(
                "string",
                "google_server_id",
                "980280066942-06l9gr3h87ikp7r4r9888g2g16ba003d.apps.googleusercontent.com"
            )
            resValue("string", "admob_id", "ca-app-pub-3081270146300138~4612580463")
            manifestPlaceholders["app_icon"] = "@mipmap/ic_launcher"
            buildConfigField("int", "APP_ID", "1")
            buildConfigField("String", "DT_APP_ID", "\"dt_8d891c8f9c02a310\"")
            buildConfigField("String", "DT_SERVER_URL", "\"https://report.roiquery.com\"")
            buildConfigField("String", "APP_URL", "\"https://www.amormeetapp.com\"")
            buildConfigField(
                "String",
                "PRIVACY_AGREEMENT",
                "\"https://www.amormeetapp.com/privacy.html\""
            )
            buildConfigField("String", "USER_AGREEMENT", "\"https://www.amormeetapp.com/terms.html\"")
            buildConfigField("String", "TOP_ON_ID", "\"h6689368ab49e1\"")
            buildConfigField("String", "TOP_ON_KEY", "\"e9dbebeb490ac0b0324a3b8f93baa865\"")
            signingConfig = signingConfigs.findByName("Vibro")!!
        }
        create("Vibro") {
            applicationId = "com.market.vibro.app"
            versionCode = 10000
            versionName = "1.0.0"
            resValue("string", "app_name", "Vibro")
            resValue(
                "string",
                "google_server_id",
                "980280066942-06l9gr3h87ikp7r4r9888g2g16ba003d.apps.googleusercontent.com"
            )
            resValue("string", "admob_id", "ca-app-pub-3081270146300138~4612580463")
            manifestPlaceholders["app_icon"] = "@mipmap/ic_launcher"
            buildConfigField("int", "APP_ID", "5")
            buildConfigField("String", "DT_APP_ID", "\"dt_eed0a0646e06c1dc\"")
            buildConfigField("String", "DT_SERVER_URL", "\"https://report.roiquery.com\"")
            buildConfigField("String", "APP_URL", "\"https://www.vibrometa.com\"")
            buildConfigField(
                "String",
                "PRIVACY_AGREEMENT",
                "\"https://www.vibrometa.com/vibro/privacy.html\""
            )
            buildConfigField("String", "USER_AGREEMENT", "\"https://www.vibrometa.com/vibro/terms.html\"")
            buildConfigField("String", "TOP_ON_ID", "\"h66948e19d2314\"")
            buildConfigField("String", "TOP_ON_KEY", "\"e9dbebeb490ac0b0324a3b8f93baa865\"")
            signingConfig = signingConfigs.findByName("Vibro")!!
        }
    }

    buildTypes {
        debug {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    applicationVariants.all {
        if (buildType.name == "release") {
            outputs.all {
                this as BaseVariantOutputImpl
                val outName = this.outputFileName
                val endFormat = if (outName.lowercase().contains("apk")) "apk" else "aab"
                outputFileName = "${flavorName}_${buildType.name}_v${versionName}.${endFormat}"
            }
        }
    }


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildFeatures {
        buildConfig = true
    }
    bundle {
        language {
            enableSplit = false
        }
    }

}

dependencies {

    implementation(project(":module-base:IM"))
    implementation(project(":module-public:Storage"))
    implementation(project(":module-public:Tool"))
    implementation(project(":module-logic:Http"))
    implementation(project(":module-logic:BaseLogic"))
    implementation(project(":module-logic:DataAnalysis"))
    implementation(project(":module-ui:Main"))
    implementation(project(":module-ui:UIBase"))
    implementation(project(":module-ui:Login"))
    implementation(project(":module-ui:Home"))
    implementation(project(":module-ui:Chat"))
    implementation(project(":module-ui:Mine"))
    implementation(project(":module-ui:Call"))
    implementation(project(":module-ui:Store"))
    implementation(Version.Dependencies.adjust)
    api(Version.Dependencies.retrofit)
}
