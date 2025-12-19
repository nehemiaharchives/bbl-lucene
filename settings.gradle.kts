// settings.gradle.kts (root workspace)

rootProject.name = "bbl-lucene"

// This repository is an IntelliJ IDEA workspace container only.
// Actual builds are executed in the nested Gradle builds (e.g. bbl-kmp/, lucene-kmp/).
//
// Composite build substitution must be declared in the settings.gradle.kts of the build
// that you actually run (e.g. bbl-kmp/settings.gradle.kts). Keeping it here does not
// affect `./bbl-kmp/...` Gradle builds.
