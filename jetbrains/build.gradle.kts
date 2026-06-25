plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "2.0.21"
    id("org.jetbrains.intellij.platform") version "2.5.0"
}

group = "com.atref"
version = "0.2.1"

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        intellijIdeaCommunity("2024.1.7")
        // instrumentationTools() removed: deprecated in plugin 2.5+, only needed for
        // Java bytecode instrumentation (we're a Kotlin-only plugin without @NotNull
        // weaving etc.). Was the leading suspect for the CI BUILD FAILED.
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
                <li>0.2.1 — plugin icon (white @ on indigo), version aligned with the
                    VSCode extension. Build toolchain bumped: intellij-platform-gradle-plugin
                    2.2.1 -> 2.5.0, Kotlin 1.9.25 -> 2.0.21; instrumentationTools() removed
                    (deprecated and unneeded for a Kotlin-only plugin).</li>
                <li>0.2.0 — initial public release. AtRef brings the <code>@path:line</code> copy
                    workflow to all IntelliJ-based IDEs. Feature parity with the AtRef VSCode
                    extension: @path:line, Markdown link, with-code snippet (line numbers +
                    optional context), absolute path, multi-caret, configurable path style
                    + notification.</li>
            </ul>
        """.trimIndent()
    }
}
