pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven("https://jitpack.io")
        //Anythink(Core)
        maven("https://jfrog.anythinktech.com/artifactory/overseas_sdk")

        //Ironsource
        maven("https://android-sdk.is.com/")

        //Pangle
        maven("https://artifact.bytedance.com/repository/pangle")

        //Mintegral
        maven("https://dl-maven-android.mintegral.com/repository/mbridge_android_sdk_oversea")
    }

}

rootProject.name = "CuteU"

include(":app")

include(":module-base")
include(":module-base:Basic")
include(":module-base:Http")
include(":module-base:IM")
include(":module-base:Rtc")
include(":module-base:Ad")

include(":module-public")
include(":module-public:Picture")
include(":module-public:Storage")
include(":module-public:Tool")
include(":module-public:Pay")

include(":module-logic")
include(":module-logic:BaseLogic")
include(":module-logic:Http")
include(":module-logic:Message")
include(":module-logic:DataAnalysis")

include(":module-ui")
include(":module-ui:UIBase")
include(":module-ui:Main")
include(":module-ui:Home")
include(":module-ui:Login")
include(":module-ui:Chat")
include(":module-ui:Mine")
include(":module-ui:Call")
include(":module-ui:Store")
