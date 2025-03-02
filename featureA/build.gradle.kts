import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("maven-publish")
}

// Función para cargar propiedades desde local.properties
fun getLocalProperty(key: String, project: Project): String {
    val propertiesFile = project.rootProject.file("local.properties")
    if (!propertiesFile.exists()) {
        throw GradleException("local.properties file not found!")
    }

    val properties = Properties().apply {
        load(FileInputStream(propertiesFile))
    }

    return properties.getProperty(key) ?: throw GradleException("Property $key not found in local.properties")
}

val githubToken: String = getLocalProperty("GITHUB_TOKEN", project)
val githubUser: String = getLocalProperty("GITHUB_USER", project)
val githubRepo: String = getLocalProperty("GITHUB_REPO", project)

android {
    namespace = "com.javacktom.featurea"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

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

publishing {
    publications {
        create<MavenPublication>("release") {
            groupId = "com.javacktom"
            artifactId = "featurea"  // O cambia a "feature" según la librería
            version = "1.0.4"

            afterEvaluate {
                from(components["release"])
            }
        }
    }

    repositories {
        maven {
            name = "GithubPackage"
            url = uri(System.getenv(githubRepo) ?: githubRepo)
            isAllowInsecureProtocol = true
            credentials {
                username = System.getenv("GITHUB_USER") ?: githubUser
                password = System.getenv("GITHUB_TOKEN") ?: githubToken
            }
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}