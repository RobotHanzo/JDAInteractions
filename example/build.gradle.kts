plugins {
    java
    id("com.gradleup.shadow") version "9.3.1"
}

group = "dev.robothanzo.jda.interactions"
version = "0.0.10"

repositories {
    mavenCentral()
}

dependencies {
    implementation(rootProject)// replace this with the dependency provided in README.md
    implementation("net.dv8tion:JDA:6.3.0")
    // you don"t need to include this, but we have to do it here due to how gradle works
    implementation("org.reflections:reflections:0.10.2")
    implementation("io.github.cdimascio:dotenv-java:3.2.0")
    implementation("ch.qos.logback:logback-classic:1.5.21")
}

tasks {
    jar {
        manifest {
            attributes["Main-Class"] = "dev.robothanzo.jda.interactions.example.ExampleBot"
        }
    }

    compileJava {
        dependsOn(rootProject.tasks.shadowJar)
    }

    build {
        dependsOn(shadowJar)
    }
}
