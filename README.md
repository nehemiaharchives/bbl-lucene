# bbl\-lucene

## Overview

\`bbl\-lucene\` is a **parent workspace** repository used to develop and validate a Kotlin Multiplatform port of Apache Lucene by integrating it into a real consumer application.

It is intentionally organized for a fast loop:

- change code in \`lucene-kmp/\`
- verify behavior immediately through \`bbl-kmp/\` integration (especially the CLI search test)

This repo primarily provides workspace glue (Gradle wiring), shared docs, and a pinned upstream reference checkout.

## Background

Apache Lucene is a mature Java search library with extensive behavior encoded across its implementation and tests. Porting Lucene to **platform-agnostic Kotlin common code** (Kotlin Multiplatform) requires preserving subtle semantics while avoiding JVM-only APIs.

This workspace exists because correctness cannot be proven by unit tests alone during a port: the most valuable signal is end-to-end indexing and searching in a real application. \`bbl-kmp\` serves as that consumer so that failures surface quickly and fixes in \`lucene-kmp\` can be validated without publishing artifacts.

The upstream Java Lucene source is kept as a read-only reference pinned to a known commit during the initial porting phase; the Kotlin port iterates toward parity.

## Repository layout

This workspace expects three directories:

1. \`bbl-kmp/\` \-\- Bible reading & search apps (Kotlin Multiplatform)
   - \`cli/\`: CLI used for rapid integration testing
   - \`composeApp/\`: Compose Multiplatform UI app
   - \`shared/\`: shared domain code (e.g. search integration)

2. \`lucene-kmp/\` \-\- Kotlin Multiplatform port of Lucene
   - Focus on \`commonMain\` first (JVM \+ Kotlin/Native)
   - Behavior is validated by both unit tests and real integration usage via \`bbl-kmp\`

3. \`lucene/\` \-\- Upstream Apache Lucene (Java, read-only reference)
   - Pinned to commit \`ec75fcad5a4208c7b9e35e870229d9b703cda8f3\`
   - Used only for reading/copying/analyzing when porting logic into \`lucene-kmp\`

## Current integration signal (important)

The key integration test is:

- \`bbl-kmp/cli/src/commonTest/kotlin/org/gnit/bible/cli/CliBibleTest.kt\`
  - \`searchJesusChristInWebus()\`

This calls into:

- \`bbl-kmp/shared/src/commonMain/kotlin/org/gnit/bible/SearchEngine.kt\`

If this test fails, the most likely root cause is a \`lucene-kmp\` behavior/porting issue rather than app logic.

## Invariants / rules

- Never modify anything under \`lucene/\` (read-only upstream reference).
- Prefer Kotlin Multiplatform \`commonMain\` code; avoid JVM-only APIs in common code.
- Do not use \`String.toByteArray()\`; use \`String.encodeToByteArray()\`.
- Avoid \`String.format\`; use Kotlin string interpolation.

## Docs

- Parent rules: \`AGENTS.md\`
- Porting notes: \`LUCENE_PORT.md\`
- Module-specific rules:
  - \`bbl-kmp/AGENTS.md\`
  - \`lucene-kmp/AGENTS.md\`