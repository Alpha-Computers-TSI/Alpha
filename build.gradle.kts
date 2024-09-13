plugins {
    id("com.android.application") version "8.0.0" apply false
    id("org.jetbrains.kotlin.android") version "2.0.0" apply false
}

buildscript {
    dependencies {
        classpath("com.android.tools.build:gradle:8.0.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.0")
    }
}

allprojects {
    // Não adicione repositórios aqui
}