plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.cute.ad"
    compileSdk = 34

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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    //Anythink (Necessary)
    implementation("com.anythink.sdk:core-tpn:6.3.68")
    implementation ("com.anythink.sdk:nativead-tpn:6.3.68")
    implementation ("com.anythink.sdk:banner-tpn:6.3.68")
    implementation ("com.anythink.sdk:interstitial-tpn:6.3.68")
    implementation ("com.anythink.sdk:rewardedvideo-tpn:6.3.68")
    implementation ("com.anythink.sdk:splash-tpn:6.3.68")

    //Androidx (Necessary)
    implementation ("androidx.appcompat:appcompat:1.1.0")
    implementation ("androidx.browser:browser:1.4.0")

    //Vungle
    implementation ("com.anythink.sdk:adapter-tpn-vungle:6.3.68")
    implementation ("com.vungle:vungle-ads:7.3.2")
    implementation ("com.google.android.gms:play-services-basement:18.1.0")
    implementation ("com.google.android.gms:play-services-ads-identifier:18.0.1")

    //UnityAds
    implementation ("com.anythink.sdk:adapter-tpn-unityads:6.3.68")
    implementation ("com.unity3d.ads:unity-ads:4.9.3")

    //Ironsource
    implementation ("com.anythink.sdk:adapter-tpn-ironsource:6.3.68")
    implementation ("com.ironsource.sdk:mediationsdk:8.1.0")
    implementation ("com.google.android.gms:play-services-appset:16.0.2")
    implementation ("com.google.android.gms:play-services-ads-identifier:18.0.1")
    implementation ("com.google.android.gms:play-services-basement:18.1.0")

    //Pangle
    implementation ("com.anythink.sdk:adapter-tpn-pangle-nonchina:6.3.68.1")
    implementation ("com.pangle.global:ads-sdk:6.0.0.3")
    implementation ("com.google.android.gms:play-services-ads-identifier:18.0.1")

    //Facebook
    implementation ("com.anythink.sdk:adapter-tpn-facebook:6.3.68")
    implementation ("com.facebook.android:audience-network-sdk:6.17.0")
    implementation ("androidx.annotation:annotation:1.0.0")

    //Admob
    implementation ("com.anythink.sdk:adapter-tpn-admob:6.3.68")
    implementation ("com.google.android.gms:play-services-ads:23.1.0")

    //AppLovin
    implementation ("com.anythink.sdk:adapter-tpn-applovin:6.3.68")
    implementation ("com.applovin:applovin-sdk:12.5.0")

    //Mintegral
    implementation ("com.anythink.sdk:adapter-tpn-mintegral-nonchina:6.3.68")
    implementation ("com.mbridge.msdk.oversea:reward:16.7.51")
    implementation ("com.mbridge.msdk.oversea:newinterstitial:16.7.51")
    implementation ("com.mbridge.msdk.oversea:mbnative:16.7.51")
    implementation ("com.mbridge.msdk.oversea:mbnativeadvanced:16.7.51")
    implementation ("com.mbridge.msdk.oversea:mbsplash:16.7.51")
    implementation ("com.mbridge.msdk.oversea:mbbanner:16.7.51")
    implementation ("com.mbridge.msdk.oversea:mbbid:16.7.51")
    implementation ("androidx.recyclerview:recyclerview:1.1.0")

//Tramini
    implementation ("com.anythink.sdk:tramini-plugin-tpn:6.3.68")
}

