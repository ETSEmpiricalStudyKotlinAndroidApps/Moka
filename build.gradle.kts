import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
        maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven(url = "https://maven.pkg.jetbrains.space/public/p/ktor/eap")
    }
    dependencies {
        classpath(Deps.GradlePlugin.android)
        classpath(Deps.GradlePlugin.kotlin)
        classpath(Deps.GradlePlugin.googleServices)
        classpath(Deps.GradlePlugin.firebaseCrashlyticsGradle)
        classpath(Deps.GradlePlugin.serialization)
        classpath(Deps.GradlePlugin.compose)
        classpath(Deps.GradlePlugin.buildKonfig)
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots/")
        maven(url = "https://maven.pkg.jetbrains.space/public/p/compose/dev")
        maven(url = "https://maven.pkg.jetbrains.space/public/p/ktor/eap")
    }
    configurations.all {
        resolutionStrategy {
            force(Deps.Kotlin.coroutinesCore)
        }
    }
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xopt-in=kotlin.RequiresOptIn")
    }
}