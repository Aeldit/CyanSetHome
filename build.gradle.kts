plugins {
    id("java")
    id("maven-publish")
    id("fabric-loom") version "1.7-SNAPSHOT"
    id("com.modrinth.minotaur") version "2.+"
    id("me.modmuss50.mod-publish-plugin") version "0.5.+"
}

repositories {
    maven("https://api.modrinth.com/maven") {
        name = "Modrinth"
    }
    maven("https://maven.terraformersmc.com/releases/") {
        name = "TerraformersMC"
    }
}

object Constants {
    const val ARCHIVES_BASE_NAME: String = "cyansethome"
    const val MOD_VERSION: String = "0.1.9"
    const val LOADER_VERSION: String = "0.15.11"
    const val CYANLIB_VERSION: String = "0.4.14"
}

class ModData {
    val mcVersion = property("minecraft_version").toString()
    val javaVersion = property("java_version").toString()

    val fabricVersion = property("fabric_version").toString()
    val modmenuVersion = property("modmenu_version").toString()
    val cyanlibVersion = "${Constants.CYANLIB_VERSION}+${mcVersion}"

    val fullVersion = "${Constants.MOD_VERSION}+${mcVersion}"

    val isj21 = javaVersion == "1.21"
}

val mod = ModData()

// Sets the name of the output jar files
base {
    archivesName = "${Constants.ARCHIVES_BASE_NAME}-${mod.fullVersion}"
    group = "fr.aeldit.cyansh"
}

dependencies {
    minecraft("com.mojang:minecraft:${mod.mcVersion}")
    mappings("net.fabricmc:yarn:${property("yarn_mappings")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${Constants.LOADER_VERSION}")

    // Fabric API
    fun addFabricModule(name: String) {
        val module = fabricApi.module(name, mod.fabricVersion)
        modImplementation(module)
    }
    addFabricModule("fabric-resource-loader-v0")
    addFabricModule("fabric-command-api-v2")
    addFabricModule("fabric-lifecycle-events-v1")
    addFabricModule("fabric-networking-api-v1")
    addFabricModule("fabric-key-binding-api-v1")
    addFabricModule("fabric-screen-api-v1")

    // ModMenu
    modImplementation("com.terraformersmc:modmenu:${mod.modmenuVersion}")

    // CyanLib
    val debug = false
    if (debug) {
        modImplementation(files(projectDir.resolve("../../run/mods/cyanlib-0.4.12-3+1.21.jar")))
    } else {
        modImplementation("maven.modrinth:cyanlib:${mod.cyanlibVersion}")
        include("maven.modrinth:cyanlib:${mod.cyanlibVersion}")
    }

    // Gson
    implementation("com.google.code.gson:gson:2.10.1")
}

loom {
    runConfigs.all {
        ideConfigGenerated(true) // Run configurations are not created for subprojects by default
        runDir = "../../run" // Use a shared run folder and just create separate worlds
    }
}

java {
    sourceCompatibility = if (mod.isj21) JavaVersion.VERSION_21 else JavaVersion.VERSION_17
    targetCompatibility = if (mod.isj21) JavaVersion.VERSION_21 else JavaVersion.VERSION_17
}

val buildAndCollect = tasks.register<Copy>("buildAndCollect") {
    group = "build"
    from(tasks.remapJar.get().archiveFile)
    into(rootProject.layout.buildDirectory.file("libs/${Constants.ARCHIVES_BASE_NAME}-${mod.fullVersion}"))
    dependsOn("build")
}

if (stonecutter.current.isActive) {
    rootProject.tasks.register("buildActive") {
        group = "project"
        dependsOn(buildAndCollect)
    }
}

tasks {
    processResources {
        inputs.property("version", mod.fullVersion)
        inputs.property("loader_version", Constants.LOADER_VERSION)
        inputs.property("mc_version", mod.mcVersion)
        inputs.property("java_version", mod.javaVersion)

        filesMatching("fabric.mod.json") {
            expand(
                mapOf(
                    "version" to mod.fullVersion,
                    "loader_version" to Constants.LOADER_VERSION,
                    "mc_version" to mod.mcVersion,
                    "java_version" to mod.javaVersion
                )
            )
        }
    }

    jar {
        from("LICENSE")
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release = if (mod.isj21) 21 else 17
    }
}

publishMods {
    modrinth {
        accessToken = System.getenv("MODRINTH_TOKEN")

        projectId = "9auOqb3o"
        displayName = "[${mod.mcVersion}] CyanSetHome ${Constants.MOD_VERSION}"
        version = mod.fullVersion
        type = STABLE

        file = tasks.remapJar.get().archiveFile

        minecraftVersions.add(mod.mcVersion)
        modLoaders.add("fabric")

        requires("fabric-api", "modmenu")
        embeds("cyanlib")

        changelog = rootProject.file("changelogs/latest.md")
            .takeIf { it.exists() }
            ?.readText()
            ?: "No changelog provided."

        dryRun = false
    }
}

modrinth {
    token.set(System.getenv("MODRINTH_TOKEN"))

    projectId.set(Constants.ARCHIVES_BASE_NAME)
    if (rootProject.file("README.md").exists()) {
        syncBodyFrom.set(rootProject.file("README.md").readText())
    }

    debugMode = false
}