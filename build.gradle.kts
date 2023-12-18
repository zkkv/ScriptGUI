plugins {
    id("java")
    id("application")
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("org.jetbrains.kotlin.jvm") version "1.7.10"
    id("checkstyle")
}

group = "com.github.zkkv"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

javafx {
    version = "19.0.2.1"
    modules("javafx.controls", "javafx.fxml")
}

application {
    mainClass.set("com.github.zkkv.Main")
}

checkstyle {
    toolVersion = "9.2.1"
    configFile = file("$rootDir/sun_checks.xml")
}

dependencies {
    implementation("org.openjfx:javafx-controls:19.0.2.1")
    implementation("org.openjfx:javafx-fxml:19.0.2.1")

    implementation(kotlin("scripting-jsr223"))
    implementation(kotlin("script-runtime"))

    implementation("org.fxmisc.richtext:richtextfx:0.11.2")
}

sourceSets {
    main {
        java {
            exclude("**/*.kts")
        }
    }
}

