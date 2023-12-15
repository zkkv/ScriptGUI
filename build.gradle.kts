plugins {
    id("java")
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

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.9.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.2")

    implementation("org.openjfx:javafx-controls:19.0.2.1")
    implementation("org.openjfx:javafx-fxml:19.0.2.1")


    //implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.21")
    //implementation("org.jetbrains.kotlin:kotlin-scripting-compiler-embeddable:1.9.21")
    //runtimeOnly("org.jetbrains.kotlin:kotlin-scripting-jsr223:1.9.21")
    //implementation("org.jetbrains.kotlin:kotlin-script-runtime:1.7.10")
    //implementation(kotlin("script-runtime"))

    /*implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation(kotlin("script-runtime"))
    implementation(kotlin("compiler-embeddable"))
    implementation(kotlin("script-util"))
    runtimeOnly(kotlin("scripting-compiler-embeddable"))*/

    /*implementation("org.jetbrains.kotlin:kotlin-compiler:1.7.10")
    implementation("org.jetbrains.kotlin:kotlin-runtime:1.7.10")
    implementation("org.jetbrains.kotlin:kotlin-util:1.7.10")
    implementation("org.jetbrains.kotlin:compiler-embeddable:1.7.10")*/

    implementation(kotlin("scripting-jsr223"))
    implementation(kotlin("script-runtime"))
}

tasks.getByName<Test>("test") {
    useJUnitPlatform()
}