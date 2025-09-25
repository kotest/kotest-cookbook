plugins {
    kotlin("jvm") version "1.9.20"
    `maven-publish`
}

group = "io.kotest"
version = "1.0.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    
    testImplementation("io.kotest:kotest-runner-junit5:5.8.0")
    testImplementation("io.kotest:kotest-assertions-core:5.8.0")
    testImplementation("io.kotest:kotest-property:5.8.0")
}

kotlin {
    jvmToolchain(11)
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            
            pom {
                name.set("Kotest Cookbook")
                description.set("Practical recipes for Kotest users")
                url.set("https://github.com/kotest/kotest-cookbook")
                
                licenses {
                    license {
                        name.set("Apache License 2.0")
                        url.set("https://www.apache.org/licenses/LICENSE-2.0")
                    }
                }
                
                scm {
                    connection.set("scm:git:git://github.com/kotest/kotest-cookbook.git")
                    developerConnection.set("scm:git:ssh://github.com:kotest/kotest-cookbook.git")
                    url.set("https://github.com/kotest/kotest-cookbook")
                }
            }
        }
    }
}