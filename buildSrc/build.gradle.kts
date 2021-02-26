plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    val postgreSQLVersion = "42.2.10"

    compile("org.postgresql", "postgresql", postgreSQLVersion)
    compile("org.jetbrains.exposed", "exposed", "0.17.7")
}