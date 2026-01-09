# AGENTS.md (parent repo: bbl-lucene)

## Continuity Ledger (compaction-safe)
Maintain a single Continuity Ledger for this workspace in `CONTINUITY.md`. The ledger is the canonical session briefing designed to survive context compaction; do not rely on earlier chat text unless it’s reflected in the ledger.

### How it works
- At the start of every assistant turn: read `CONTINUITY.md`, update it to reflect the latest goal/constraints/decisions/state, then proceed with the work.
- Update `CONTINUITY.md` again whenever any of these change: goal, constraints/assumptions, key decisions, progress state (Done/Now/Next), or important tool outcomes.
- Keep it short and stable: facts only, no transcripts. Prefer bullets. Mark uncertainty as `UNCONFIRMED` (never guess).
- If you notice missing recall or a compaction/summary event: refresh/rebuild the ledger from visible context, mark gaps `UNCONFIRMED`, ask up to 1–3 targeted questions, then continue.

### `functions.update_plan` vs the Ledger
- `functions.update_plan` is for short-term execution scaffolding while you work (a small 3–7 step plan with pending/in_progress/completed).
- `CONTINUITY.md` is for long-running continuity across compaction (the “what/why/current state”), not a step-by-step task list.
- Keep them consistent: when the plan or state changes, update the ledger at the intent/progress level (not every micro-step).

### In replies
- Begin with a brief “Ledger Snapshot” (Goal + Now/Next + Open Questions). Print the full ledger only when it materially changes or when the user asks.

### `CONTINUITY.md` format (keep headings)
- Goal (incl. success criteria):
- Constraints/Assumptions:
- Key decisions:
- State:
- Done:
- Now:
- Next:
- Open questions (UNCONFIRMED if needed):
- Working set (files/ids/commands):

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

4. `jdk24u` — *Upstream JDK 24 (read-only)*
    - Java Development Kit source, pinned to version 24
    - **Never modify this code.** It is used only as reference when porting JDK functionality which is used in Java Lucene but not found in Kotlin/Common portion of Kotlin Standard library
    - When you port, sometimes whole class/interface need to be ported and all method signature needs to be same or mirrored one to one.
    - all ported jdk classes will be ported into `lucene-kmp/core/src/commonMain/kotlin/org/gnit/lucenekmp/jdkport` no deeper package than that, all flat.
    - all ported jkd unit test classes will be ported into `lucene-kmp/core/src/commonTest/kotlin/org/gnit/lucenekmp/jdkport` no deeper package than that, all flat.
    - for example when the jdk class is `java.text.BreakIterator`, the ported class will be `org.gnit.lucenekmp.jdkport.BreakIterator` with annotation `@Ported(from = "java.text.BreakIterator")`

5. `morfologik-stemming` — *Upstream Morfologik (read-only)*
    - this library will be the bases of `lucene/analysis/morfologik/src/java/org/apache/lucene/analysis/morfologik/MorfologikAnalyzer.java` and `morfologik.stemming` package needed to be ported into `lucene-kmp`

6. `dict-uk` *project to generate POS tag dictionary for Ukrainian language*
    - the output data from this project will be fed to `morfologik` then becomee bases of `lucene/analysis/morfologik/src/java/org/apache/lucene/analysis/uk/UkrainianMorfologikAnalyzer.java`

7. `opensearch-analysis-vietnamese` — *Vietnamese analyzer from open search (read-only)*

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

### Porting parity & compile checks
- Do not add/remove functions (including private helpers) solely for cleanup; keep function signatures aligned with upstream Lucene for side-by-side comparison.
- After any code change, run JetBrains `get_file_problems` on the edited file and fix compilation errors immediately.

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

## Known IDE constraint (generated data)
IntelliJ code insight is disabled for files larger than **2.56 MB**. When embedding binary data as Kotlin source (to avoid resources), do **not** generate a single huge `.kt` file. Instead:

- Create a Gradle task that generates Kotlin source from the binary data.
- Split output into multiple part files, each under ~2.5 MB.
- Generate a small aggregator file that concatenates the parts into the final `ByteArray`.

This keeps IDE indexing working and avoids unresolved references during sync.

## Repo self-contained inputs

- Do not reference files outside this repo in build scripts or generators. If a build needs external data (e.g., JDK or Lucene resources), copy it into `gradle/<area>/...` and reference that in Gradle so CI remains self-contained.
- Lesson learned: CI runners won’t have sibling repos like `../lucene` or `../jdk24u`. Always vendor required inputs into this repo under `gradle/<area>/sourceFileForGeneratedData` (or similar) and point custom Gradle generator tasks to those in-repo paths.

## Where to find deeper module-specific rules

Each module contains additional `AGENTS.md` guidance:

- `bbl-kmp/AGENTS.md` — bbl-kmp workflow + test-running strategy
- `lucene-kmp/AGENTS.md` — lucene-kmp porting guidelines + KMP constraints

When working inside a module, follow its `AGENTS.md` first; this parent file is meant to explain the **big picture** and the **integration-first debugging loop**.
