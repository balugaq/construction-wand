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

val rebarVersion = project.properties["rebar.version"] as String
val pylonVersion = project.properties["pylon.version"] as String

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.8-R0.1-SNAPSHOT")
    compileOnly("io.github.pylonmc:rebar:$rebarVersion")
    compileOnly("io.github.pylonmc:pylon:$pylonVersion")
    implementation("org.metamechanists:DisplayModelLib:35")
    shadow("org.metamechanists:DisplayModelLib:35")
    compileOnly("org.projectlombok:lombok:1.18.46")
    annotationProcessor("org.projectlombok:lombok:1.18.46")
    testCompileOnly("org.projectlombok:lombok:1.18.46")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.46")
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
    minecraftVersion("1.21.10")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}