buildscript {
    val kotlin_version by extra("1.4.10")
    repositories.deps()
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
        with(Config.Plugins) {
            classpath(android)
            classpath(kotlin)
        }
    }
}

allprojects {
    repositories.deps()
}