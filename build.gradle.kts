buildscript {
    repositories {
        google()
        jcenter()
        maven {
            url = uri("https://kotlin.bintray.com/kotlinx/")
        }
        maven {
            url = uri("http://dl.bintray.com/kotlin/kotlin-eap")
        }
        maven {
            url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
        }
    }
    dependencies {
        classpath(Deps.GradlePlugin.android)
        classpath(Deps.GradlePlugin.kotlin)
        classpath(Deps.GradlePlugin.googleServices)
        classpath(Deps.GradlePlugin.firebaseCrashlyticsGradle)
    }
}

allprojects {
    repositories {
        google()
        jcenter()
        maven {
            url = uri("https://kotlin.bintray.com/kotlinx/")
        }
        maven {
            url = uri("http://dl.bintray.com/kotlin/kotlin-eap")
        }
        maven {
            url = uri("https://oss.sonatype.org/content/repositories/snapshots/")
        }
    }
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}