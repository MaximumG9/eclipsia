plugins {
    kotlin("jvm") version "1.9.21"
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("io.papermc.paperweight.userdev") version "1.7.1"
    application
}

apply(plugin = "io.papermc.paperweight.userdev")

group = "dev.osmii"
version = "1.2"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.dmulloy2.net/repository/public/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://oss.sonatype.org/content/repositories/central")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://maven.enginehub.org/repo/")
    mavenLocal()
}

dependencies {
    paperweight.paperDevBundle("1.20.4-R0.1-SNAPSHOT")
    testImplementation(kotlin("test"))
    implementation(kotlin("stdlib"))
    compileOnly("com.comphenix.protocol:ProtocolLib:5.2.0-SNAPSHOT")

    compileOnly("com.sk89q.worldedit:worldedit-core:7.2.0-SNAPSHOT")
    compileOnly("com.sk89q.worldedit:worldedit-bukkit:7.2.9") { isTransitive = false }
    implementation(kotlin("reflect"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
    dependencies {
        implementation(kotlin("stdlib"))
    }
}

application {
    mainClass.set("dev.osmii.shadow.Shadow")
}

tasks {
    assemble {
        // dependsOn(reobfJar)
        dependsOn("shadowJar")
        dependsOn(reobfJar)
    }

    build {
    }

    shadowJar {
        archiveClassifier.set("")
        // destinationDirectory.set(layout.buildDirectory.dir("C:/Users/Osmii/Downloads"))
    }
    jar {
    }

    reobfJar {
        this.outputJar.set(layout.buildDirectory.file("C:/Users/Gamer/Desktop/Paper 1.20.4/plugins/Shadow-1.2.jar"))
    }
}