import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    `maven-publish`
    id("com.gradleup.shadow") version "9.2.2"
}

group = "dev.robothanzo"
version = "0.1.3"

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.dv8tion:JDA:6.1.2")
    implementation("org.reflections:reflections:0.10.2")

    compileOnly("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")
}

tasks {
    java {
        withSourcesJar()
    }

    named<ShadowJar>("shadowJar") {
        dependencies {
            include(dependency("org.reflections:reflections:0.10.2"))
        }
        archiveClassifier.set("")
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }

    build {
        dependsOn(shadowJar)
    }
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "dev.robothanzo"
            artifactId = "JDAInteractions"
            version = version

            from(components["java"])
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
        }
    }
}

