import io.gitlab.arturbosch.detekt.extensions.DetektExtension

// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.dagger.hilt.android) apply false
    alias(libs.plugins.detekt) apply false
}

allprojects.onEach { project ->
    project.afterEvaluate {
        project.plugins.apply(libs.plugins.detekt.get().pluginId)
        project.extensions.configure<DetektExtension> {
            config.setFrom(rootProject.files("default-detekt-config.yml"))
        }
        project.dependencies.add("detektPlugins", libs.detekt.formatting)
    }
}