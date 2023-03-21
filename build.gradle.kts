import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    java
    `maven-publish`
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "dev.robothanzo"
version = "0.1.1"

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.dv8tion:JDA:5.0.0-beta.6")
    implementation("org.reflections:reflections:0.10.2")

    compileOnly("org.projectlombok:lombok:1.18.24")
    annotationProcessor("org.projectlombok:lombok:1.18.24")
}

tasks {
    java {
        withSourcesJar()
    }

    named<ShadowJar>("shadowJar") {
        dependencies {
            include(dependency("org.reflections:reflections:0.10.2"))
            include(dependency("net.dv8tion:JDA:5.0.0-beta.6"))
        }
        archiveClassifier.set("")
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }

    build {
        dependsOn("shadowJar")
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

