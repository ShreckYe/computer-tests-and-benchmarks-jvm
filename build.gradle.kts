plugins {
    java
    kotlin("jvm") version "1.4.10"
    scala
    idea
}

group = "shreckye"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("script-runtime"))
    implementation("org.scala-lang:scala-library:2.13.3")

    testImplementation("junit", "junit", "4.12")
    testImplementation("org.scalatest:scalatest_2.11:3.0.0")
}
