import org.apache.tools.ant.filters.ReplaceTokens

plugins {
    id("java")
}

group = "net.azisaba"
version = "1.0.0-SNAPSHOT"

java.toolchain.languageVersion.set(JavaLanguageVersion.of(8))

repositories {
    mavenCentral()
    maven {
        name = "papermc-repo"
        url = uri("https://papermc.io/repo/repository/maven-public/")
    }
    maven {
        name = "sonatype"
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }
    maven {
        name = "jitpack"
        url = uri("https://jitpack.io")
    }
    maven {
        name = "azisaba-repo"
        url = uri("https://repo.azisaba.net/repository/maven-public/")
    }
    if (properties["azisabaNmsUsername"] != null && properties["azisabaNmsPassword"] != null) {
        maven {
            name = "azisabaNms"
            credentials(PasswordCredentials::class)
            url = uri("https://repo.azisaba.net/repository/nms/")
        }
    }
    mavenLocal()
}

dependencies {
    compileOnly("org.jetbrains:annotations:24.0.1")
    compileOnly("net.azisaba.loreeditor:common:1.0.0-SNAPSHOT:all")
    compileOnly("com.destroystokyo.paper:paper-api:1.15.2-R0.1-SNAPSHOT")
    compileOnly("org.spigotmc:spigot:1.15.2-R0.1-SNAPSHOT")
}

tasks {
    compileJava {
        options.encoding = "UTF-8"
    }

    processResources {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE

        from(sourceSets.main.get().resources.srcDirs) {
            filter(ReplaceTokens::class, mapOf("tokens" to mapOf("version" to project.version.toString())))
            filteringCharset = "UTF-8"
        }
    }
}
