import org.jetbrains.kotlin.gradle.internal.Kapt3GradleSubplugin.Companion.isUseK2

plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.25"
    id("org.jetbrains.intellij") version "1.17.4"
}

sourceSets {
    main {
        resources {
            srcDir("src/main/resources")
            srcDir("src/main/java")

            include("**/*")
            exclude("**/*.kt")
            exclude("**/*.java")
            exclude("**/*.form")
        }
    }
}



group = "io.github.iceofsummer"
version = "1.0-SNAPSHOT"

repositories {
    maven{ url=uri("https://maven.aliyun.com/repository/public") }
    maven{ url=uri("https://maven.aliyun.com/repository/google") }
//    mavenCentral()
}

// Configure Gradle IntelliJ Plugin
// Read more: https://plugins.jetbrains.com/docs/intellij/tools-gradle-intellij-plugin.html
intellij {
    version.set("2023.2.6")
    type.set("IC") // Target IDE Platform

    plugins.set(listOf("com.intellij.java", "Git4Idea", "Subversion"))
}

dependencies {
    implementation("org.xerial:sqlite-jdbc:3.46.1.0") {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }
    implementation("com.zaxxer:HikariCP:6.0.0") {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }
    implementation("org.mybatis:mybatis:3.5.16") {
        exclude(group = "org.slf4j", module = "slf4j-api")
    }
    implementation("com.google.code.gson:gson:2.11.0")
}

tasks {
    // Set the JVM compatibility versions
    withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
        kotlinOptions.jvmTarget = "17"
    }

    patchPluginXml {
        sinceBuild.set("232")
        untilBuild.set("242.*")
    }

    signPlugin {
        certificateChain.set(System.getenv("CERTIFICATE_CHAIN"))
        privateKey.set(System.getenv("PRIVATE_KEY"))
        password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
    }

    publishPlugin {
        token.set(System.getenv("PUBLISH_TOKEN"))
    }

    processResources {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        includeEmptyDirs = false
    }
}
