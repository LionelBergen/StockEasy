import dad.business.tasks.DbLoadTestDataLocal

// TODO: create variables for versions that need to be the same. I think we need to use settings.gradle to have it only declared once.
plugins {
    id("org.jetbrains.kotlin.jvm") version "1.3.10"
    id("org.jetbrains.kotlin.plugin.spring") version "1.3.21"
    id("org.springframework.boot") version "2.1.2.RELEASE"
    id("io.spring.dependency-management") version "1.0.6.RELEASE"
}

sourceSets["main"].withConvention(org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet::class) {
    kotlin.srcDir("src/main/kotlin")
}

group = "dad"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

buildscript {
    val kotlinVersion = "1.3.10"
    val springBootVersion = "2.1.2.RELEASE"

    repositories {
        jcenter()
        gradlePluginPortal()
        maven("https://kotlin.bintray.com/kotlinx")
    }

    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion")
        classpath("org.jetbrains.kotlin:kotlin-allopen:$kotlinVersion")
        classpath("org.springframework.boot:spring-boot-gradle-plugin:$springBootVersion")
    }
}

val junitVersion = "5.3.2"

tasks {
    withType<Jar> {
        baseName = "gs-rest-service"
        version = "0.1.0"
    }

    // config JVM target to 1.8 for kotlin compilation tasks
    withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions.jvmTarget = "1.8"
    }
}

val compileTestKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks

compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    val kotlinVersion = "1.3.10"
    compile("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    compile("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")

    val postgreSQLVersion = "42.2.10"
    compile("org.postgresql", "postgresql", postgreSQLVersion)
    implementation("org.jetbrains.kotlin:kotlin-script-runtime:1.3.10")

    compile(kotlin("stdlib-jdk8"))

    compile("org.jetbrains.exposed", "exposed", "0.17.7")

    implementation("com.google.code.gson:gson:2.8.6")

    compileOnly("org.springframework.boot:spring-boot-devtools")

    // required for mailgun
    compile("org.glassfish.jersey.inject", "jersey-hk2", "2.30.1")
    compile("net.sargue:mailgun:1.9.0")

    testCompile("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.junit.jupiter:junit-jupiter:5.6.0")
    // selenium
    testCompile("io.github.bonigarcia:webdrivermanager:3.8.1")
    testCompile("org.seleniumhq.selenium",  "selenium-chrome-driver", "3.141.59")
    testCompile("org.seleniumhq.selenium",  "selenium-support", "3.141.59")
}

tasks.test {
    useJUnitPlatform()

    testLogging {
        events("passed", "skipped", "failed")
    }

    environment("MAILGUN_DOMAIN", "sandbox6c370c68eeb54b2696ae5a20753054c1.mailgun.org")
    environment("MAILGUN_KEY", "838ddd36e7095f825bbe36dd8a126ee4-c322068c-374983ff")
    environment("MAILGUN_FROM_NAME", "Test Account")
    environment("MAILGUN_FROM_EMAIL", "postmaster@sandbox6c370c68eeb54b2696ae5a20753054c1.mailgun.org")

    environment("DATABASE_URL", "postgres://dad:business@localhost:5432/dad_business_to_business")
}

tasks.register("testFullSuite") {
    dependsOn("dbTestDataLoader")
    // TODO: run in background? Exec("BootRun")

    finalizedBy("test")

    doLast {
        println("Task complete.")
    }
}

tasks {
    register<DbLoadTestDataLocal>("loadTestDatabase") {
        description = "loads a Local database data for testing"
    }

    register<dad.business.tasks.DbLoadKomodoLoco>("loadKomodoLoco") {
        description = "loads the database full of KomodoLoco data"
    }
}

tasks.register("dbTestDataLoader") {
    println("Running db test data")

    // starts postgresql service
    dependsOn("startPostgres")
    dependsOn("loadTestDatabase")

    doLast {
        println("Finished loading test data")
    }
}

tasks.bootRun {
    environment("MAILGUN_DOMAIN", "sandbox6c370c68eeb54b2696ae5a20753054c1.mailgun.org")
    environment("MAILGUN_KEY", "838ddd36e7095f825bbe36dd8a126ee4-c322068c-374983ff")
    environment("MAILGUN_FROM_NAME", "Test Account")
    environment("MAILGUN_FROM_EMAIL", "postmaster@sandbox6c370c68eeb54b2696ae5a20753054c1.mailgun.org")

    environment("DATABASE_URL", "postgres://dad:business@localhost:5432/dad_business_to_business")
}