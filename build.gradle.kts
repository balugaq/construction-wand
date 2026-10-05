import net.minecrell.pluginyml.bukkit.BukkitPluginDescription

plugins {
    java
    idea
    id("com.gradleup.shadow") version "9.0.0"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
    id("xyz.jpenilla.run-paper") version "2.3.0"
}

group = project.properties["group"]!!

repositories {
    mavenCentral()
    maven("https://central.sonatype.com/repository/maven-snapshots/") {
        name = "sonatype"
    }
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc"
    }
    maven("https://repo.xenondevs.xyz/releases") {
        name = "InvUI"
    }
    maven("https://repo.metamechanists.org/releases") {
        name = "MetaMechanists Repository"
    }
    maven("https://jitpack.io") {
        name = "JitPack"
    }
}

// 版本统一由 gradle/libs.versions.toml 管理
val rebarVersion = libs.versions.rebar.get()

dependencies {
    compileOnly(libs.paper.api)
    compileOnly(libs.rebar)
    compileOnly(libs.pylon)
    implementation(libs.display.model.lib)
    shadow(libs.display.model.lib)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    testCompileOnly(libs.lombok)
    testAnnotationProcessor(libs.lombok)
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true
    }
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
}

tasks.shadowJar {
    relocate("org.metamechanists", "${project.group}.shaded.org.metamechanists")

    // I have no idea about why these files are being included, so excludes them.
    exclude("META-INF/maven/**")
    exclude("edu/")
    exclude("javax/")
    exclude("net/")
    exclude("org/intellij/")
    exclude("org/jetbrains/")
    exclude {
        val isRootPluginYml = it.relativePath.pathString == "plugin.yml"
        val isFromDisplayModelLib = isRootPluginYml && it.size < 150
        isFromDisplayModelLib
    }

    mergeServiceFiles()

    archiveBaseName = project.name
    archiveClassifier = null
}

bukkit {
    name = project.properties["name"] as String
    main = project.properties["main-class"] as String
    version = project.version.toString()
    apiVersion = "1.21"
    depend = listOf("Rebar")
    load = BukkitPluginDescription.PluginLoadOrder.STARTUP
}

tasks.runServer {
    downloadPlugins {
        github("pylonmc", "rebar", rebarVersion, "rebar-$rebarVersion.jar")
    }
    maxHeapSize = "4G"
    minecraftVersion("1.21.11")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}