# AGENTS.md (parent repo: bbl-lucene)

## Project Overview

`bbl-lucene` is a **multi-module Gradle workspace** built to *dogfood* and rapidly debug a Kotlin Multiplatform port of Apache Lucene.

It contains three main modules:

1. `bbl-kmp/` — *Bible reading & search apps*
   - Kotlin Multiplatform.
   - Apps:
     - `cli/`: Kotlin/Native CLI (Linux/macOS)
     - `composeApp/`: Compose Multiplatform GUI (Android/iOS/Desktop JVM)
   - Shared domain code lives under `shared/`.
   - Depends on `lucene-kmp/`.

2. `lucene-kmp/` — *Lucene Kotlin Multiplatform (work-in-progress)*
   - Kotlin Multiplatform port of Lucene.
   - Goal: platform-agnostic **common** code first (JVM + Kotlin/Native).
   - Tested but still incomplete; bugs are expected.
   - This repo intentionally keeps `lucene-kmp` as a **local dependency** of `bbl-kmp` so fixes can be verified immediately without publishing to Maven.

3. `lucene/` — *Upstream Apache Lucene (read-only)*
   - Java Lucene source, pinned to a specific commit during the initial porting phase.
   - **Never modify this code.** It is used only as reference when porting/fixing `lucene-kmp`.

## Why this parent repo exists

`lucene-kmp` can pass many unit tests, but the real proof is whether it works in a *real* consumer.

`bbl-kmp` (especially the CLI search) acts as that consumer so we can:

- build/load indices,
- run actual searches,
- validate search results,
- and catch real integration bugs that unit tests miss.

## Current integration signal (important)

The key integration test is:

- `bbl-kmp/cli/src/commonTest/kotlin/org/gnit/bible/cli/CliBibleTest.kt`
  - `@Test override fun searchJesusChristInWebus()`

This calls into `bbl-kmp/shared`:

- `bbl-kmp/shared/src/commonMain/kotlin/org/gnit/bible/SearchEngine.kt`

`SearchEngine` uses `lucene-kmp` APIs such as `StandardDirectoryReader`, `IndexSearcher`, and `QueryParser`.
If this test fails, assume the root cause is likely a **lucene-kmp behavior/porting bug**, not (only) app logic.

## Invariants / rules for agents

### Do not touch upstream Lucene
- **Never edit any files under `lucene/`.**
- You may read/copy/analyze it to port logic and unit tests into `lucene-kmp`.

### Kotlin Multiplatform constraints
Applies to `bbl-kmp` and especially `lucene-kmp` common code:

- Prefer Kotlin **common** code over `expect/actual`.
- Do not accidentally introduce JVM-only APIs into `commonMain` / `commonTest`.
- **Do not use** `String.toByteArray()`; use `String.encodeToByteArray()`.
- Avoid `String.format`; use Kotlin string interpolation.

### Logging convention
Use Kotlin Logging:

- `import io.github.oshai.kotlinlogging.KotlinLogging`
- `private val logger = KotlinLogging.logger {}`
- `logger.debug { "message" }`

## Fast dev workflow (how to debug lucene-kmp via bbl-kmp)

This repo is set up so you can fix `lucene-kmp` and verify the fix immediately by running `CliBibleTest.searchJesusChristInWebus()`.

Workflow:

1. Reproduce the failure by running **only** `CliBibleTest.searchJesusChristInWebus()`.
2. Use the failure stacktrace to identify whether the bug is in:
   - `bbl-kmp` integration code (`SearchEngine`, `BibleResourcesReader`, etc.), or
   - `lucene-kmp` internals (most likely for search/index correctness).
3. If the bug is in `lucene-kmp`, implement the fix there.
4. Re-run **the same single failing test**.
5. Only after the single test is green, run broader test scopes.
6. Then if it pass, we will add more search related unit tests, and add more Analyzers for new languages repeating 1-5 workflow for those new codes.

## Tooling / execution policy

### Priority 1: JetBrains IDE runner (required when available)
Use the JetBrains MCP server to:

- inspect compile errors using IDE inspections,
- find run configurations,
- run a **single** test method/class,
- read test results.

### Priority 2: Command line (avoid)
Avoid using Gradle from the terminal in this repo when MCP-based run configurations are available.

## Where to find deeper module-specific rules

Each module contains additional `AGENTS.md` guidance:

- `bbl-kmp/AGENTS.md` — bbl-kmp workflow + test-running strategy
- `lucene-kmp/AGENTS.md` — lucene-kmp porting guidelines + KMP constraints

When working inside a module, follow its `AGENTS.md` first; this parent file is meant to explain the **big picture** and the **integration-first debugging loop**.
