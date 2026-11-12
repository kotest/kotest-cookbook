pluginManagement {
    repositories {
        providers.gradleProperty("targetPluginRepositoryUrl").orNull?.let { repositoryUrl ->
            maven {
                url = uri(repositoryUrl)
            }
        }
        gradlePluginPortal()
    }
}

rootProject.name = "kotest-cookbook"
