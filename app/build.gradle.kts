@file:Suppress("UnstableApiUsage")

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin)
    publishing
}

android {
    namespace = "app.revanced.integrations"
    // noinspection GradleDependency
    compileSdk = 33

    applicationVariants.all {
        outputs.all {
            this as com.android.build.gradle.internal.api.ApkVariantOutputImpl

            outputFileName = "${rootProject.name}-$versionName.apk"
        }
    }

    defaultConfig {
        applicationId = "app.revanced.integrations"
        minSdk = 24
        // noinspection EditedTargetSdkVersion, ExpiredTargetSdkVersion
        targetSdk = 31
        multiDexEnabled = false
        versionName = version as String

        buildConfigField("String", "VERSION_NAME", "\"${versionName}\"")
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

dependencies {
    compileOnly(libs.annotation)
    implementation(libs.lang3)

    compileOnly(project(":stub"))
}

tasks {
    // Because the signing plugin doesn't support signing APKs, do it manually.
    register("sign") {
        group = "signing"

        dependsOn(build)

        doLast {
            val outputDirectory = layout.buildDirectory.dir("outputs/apk/release").get().asFile
            val integrationsApk = outputDirectory.resolve("${rootProject.name}-$version.apk")

            org.gradle.security.internal.gnupg.GnupgSignatoryFactory().createSignatory(project).sign(
                integrationsApk.inputStream(),
                outputDirectory.resolve("${integrationsApk.name}.asc").outputStream(),
            )
        }
    }

    // Needed by gradle-semantic-release-plugin.
    // Tracking: https://github.com/KengoTODA/gradle-semantic-release-plugin/issues/435
    publish {
        dependsOn(build)
        dependsOn("sign")
    }
}
