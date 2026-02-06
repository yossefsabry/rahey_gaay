import java.util.Properties

val envProps = Properties().apply {
    val envFile = rootDir.resolve(".env")
    if (envFile.exists()) {
        envFile.inputStream().use { load(it) }
    }
}
val mapboxDownloadsToken = envProps.getProperty("MAPBOX_DOWNLOADS_TOKEN")
    ?: envProps.getProperty("MAPBOX_TOKEN")
    ?: System.getenv("MAPBOX_DOWNLOADS_TOKEN")
    ?: System.getenv("MAPBOX_TOKEN")

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        if (!mapboxDownloadsToken.isNullOrBlank()) {
            maven {
                url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
                credentials {
                    username = "mapbox"
                    password = mapboxDownloadsToken
                }
            }
        }
    }
}

rootProject.name = "RaheyGaay"
include(":app")
