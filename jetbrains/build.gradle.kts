plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.25"
    id("org.jetbrains.intellij.platform") version "2.2.1"
}

group = "com.atref"
version = "0.1.0"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        intellijIdeaCommunity("2024.1.7")
        instrumentationTools()
    }
}

kotlin {
    jvmToolchain(17)
}

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild = "241"
            untilBuild = provider { null }
        }
        changeNotes = """
            <ul>
                <li>0.1.0 — initial release. AtRef brings the <code>@path:line</code> copy
                    workflow to all IntelliJ-based IDEs. Feature parity with the AtRef VSCode
                    extension: @path:line, Markdown link, with-code snippet (line numbers +
                    optional context), absolute path, multi-caret, configurable path style
                    + notification.</li>
            </ul>
        """.trimIndent()
    }
}
