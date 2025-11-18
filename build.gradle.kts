// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.ktlint) apply false
}

// Ensure Detekt plugin is included in dependencies
buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
    dependencies {
        classpath("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:1.23.0")
    }
}

// Apply Detekt plugin to all subprojects
subprojects {
    apply(plugin = "io.gitlab.arturbosch.detekt")

    tasks.withType<io.gitlab.arturbosch.detekt.Detekt> {
        jvmTarget = "17"
        reports {
            html.required.set(true)
            xml.required.set(false)
            txt.required.set(false)
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}