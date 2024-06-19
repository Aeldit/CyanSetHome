plugins {
    id("java")
    id("fabric-loom") version "1.7-SNAPSHOT"
    id("maven-publish")
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

val archivesBaseName = property("archives_base_name").toString()
val modVersion = property("mod_version").toString()

val mcVersion = property("minecraft_version").toString()
val loaderVersion = property("loader_version").toString()
val javaVersion = property("java_version").toString()

val fabricVersion = property("fabric_version").toString()
val modmenuVersion = property("modmenu_version").toString()
val cyanlibVersion = "${property("cyanlib_version")}+${mcVersion}"

val fullVersion = "${modVersion}+${mcVersion}"

val isj21 = javaVersion == "1.21"

// Sets the name of the output jar files
base {
    archivesName.set("${property("archives_base_name")}-${fullVersion}")
}

dependencies {
    minecraft("com.mojang:minecraft:${mcVersion}")
    mappings("net.fabricmc:yarn:${property("yarn_mappings")}:v2")
    modImplementation("net.fabricmc:fabric-loader:${loaderVersion}")

    // Fabric API
    fun addFabricModule(name: String) {
        val module = fabricApi.module(name, fabricVersion)
        modImplementation(module)
    }
    addFabricModule("fabric-resource-loader-v0")
    addFabricModule("fabric-command-api-v2")
    addFabricModule("fabric-lifecycle-events-v1")
    addFabricModule("fabric-networking-api-v1")
    addFabricModule("fabric-key-binding-api-v1")
    addFabricModule("fabric-screen-api-v1")

    // ModMenu
    modImplementation("com.terraformersmc:modmenu:${modmenuVersion}")

    // CyanLib
    val debug = false
    if (debug) {
        modImplementation(files(projectDir.resolve("../../run/mods/cyanlib-0.4.11-2+1.21.jar")))
    } else {
        modImplementation("maven.modrinth:cyanlib:${cyanlibVersion}")
        include("maven.modrinth:cyanlib:${cyanlibVersion}")
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
    sourceCompatibility = if (isj21) JavaVersion.VERSION_21 else JavaVersion.VERSION_17
    targetCompatibility = if (isj21) JavaVersion.VERSION_21 else JavaVersion.VERSION_17
}

val buildAndCollect = tasks.register<Copy>("buildAndCollect") {
    group = "build"
    from(tasks.remapJar.get().archiveFile)
    into(rootProject.layout.buildDirectory.file("libs/${archivesBaseName}-${fullVersion}"))
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
        inputs.property("version", fullVersion)
        inputs.property("loader_version", loaderVersion)
        inputs.property("mc_version", mcVersion)
        inputs.property("java_version", javaVersion)

        filesMatching("fabric.mod.json") {
            expand(
                mapOf(
                    "version" to fullVersion,
                    "loader_version" to loaderVersion,
                    "mc_version" to mcVersion,
                    "java_version" to javaVersion
                )
            )
        }
    }

    jar {
        from("LICENSE")
    }

    withType<JavaCompile> {
        options.encoding = "UTF-8"
        options.release = if (isj21) 21 else 17
    }
}

publishMods {
    modrinth {
        accessToken = System.getenv("MODRINTH_TOKEN")

        projectId = "9auOqb3o"
        displayName = "[${mcVersion}] CyanSetHome $modVersion"
        version = fullVersion
        type = STABLE

        file = tasks.remapJar.get().archiveFile

        minecraftVersions.add(mcVersion)
        modLoaders.add("fabric")

        requires("fabric-api", "modmenu")
        embeds("cyanlib")

        changelog = rootProject.file("changelogs/latest.md")
            .takeIf { it.exists() }
            ?.readText()
            ?: "No changelog provided."

        dryRun = true
    }
}

modrinth {
    token.set(System.getenv("MODRINTH_TOKEN"))

    projectId.set("${property("archives_base_name")}")
    if (rootProject.file("README.md").exists()) {
        syncBodyFrom.set(rootProject.file("README.md").readText())
    }

    debugMode = false
}