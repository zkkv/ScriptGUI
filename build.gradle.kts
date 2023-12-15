plugins {
    id("java")
    id("application")
    id("org.openjfx.javafxplugin") version "0.1.0"
    id("org.jetbrains.kotlin.jvm") version "1.7.10"
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

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")

    implementation("org.openjfx:javafx-controls:19.0.2.1")
    implementation("org.openjfx:javafx-fxml:19.0.2.1")

    implementation(kotlin("scripting-jsr223"))
    implementation(kotlin("script-runtime"))
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}