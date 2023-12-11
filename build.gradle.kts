plugins {
    id("java")
    id("org.openjfx.javafxplugin") version "0.1.0"
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

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.21")
    implementation("org.openjfx:javafx-controls:19.0.2.1")
    implementation("org.openjfx:javafx-fxml:19.0.2.1")
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}