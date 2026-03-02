Goal (incl. success criteria):
- Add `earlyoom` safety instructions to both root and `lucene-kmp` `AGENTS.md` so Gradle runs are protected from full-system freezes under low memory.
- Success criteria: explicit guidance added for `./gradlew` usage with regex that targets Gradle/Java processes for OOM kill preference.

Constraints/Assumptions:
- Work in `lucene-kmp` test/code paths only; keep instrumentation scoped and reversible.
- Use compile-first, then timed test runs with requested timeout policy when running targeted tests.
- User requested increasing linuxX64 timeout and continuing autonomously.

Key decisions:
- `testThreads2` overrides in lucene90 test classes all delegate to `super.testThreads2()`, so hotspot tracing must start at the shared superclass implementation.
- Applied minimal hot-loop optimization in `testThreads2`: cache `storedFields.document(j)` once per doc and reuse for all stored-field accesses, removing repeated document materializations in the threaded inner loop.

State:
- In progress (active implementation).

Done:
- Confirmed `testThreads2` override locations:
  - `lucene-kmp/core/src/commonTest/kotlin/org/gnit/lucenekmp/codecs/lucene90/TestLucene90DocValuesFormat.kt`
  - `lucene-kmp/core/src/commonTest/kotlin/org/gnit/lucenekmp/codecs/lucene90/TestLucene90DocValuesFormatMergeInstance.kt`
  - `lucene-kmp/core/src/commonTest/kotlin/org/gnit/lucenekmp/codecs/lucene90/TestLucene90DocValuesFormatVariableSkipInterval.kt`

Now:
- Report the AGENTS updates and wait for next action.

Next:
- Optionally run a small safe test command using the new `earlyoom` guard pattern if requested.

Open questions (UNCONFIRMED if needed):
- None.

Working set (files/ids/commands):
- `lucene-kmp/core/src/commonTest/kotlin/org/gnit/lucenekmp/codecs/lucene90/TestLucene90DocValuesFormat.kt`
- `lucene-kmp/core/src/commonTest/kotlin/org/gnit/lucenekmp/codecs/lucene90/TestLucene90DocValuesFormatMergeInstance.kt`
- `lucene-kmp/core/src/commonTest/kotlin/org/gnit/lucenekmp/codecs/lucene90/TestLucene90DocValuesFormatVariableSkipInterval.kt`
- `lucene-kmp/test-framework/src/commonMain/kotlin/org/gnit/lucenekmp/tests/index/LegacyBaseDocValuesFormatTestCase.kt`
- commands: targeted `:core:jvmTest`/`:core:linuxX64Test` for `testThreads2`
- Implemented optimization in:
  - `lucene-kmp/test-framework/src/commonMain/kotlin/org/gnit/lucenekmp/tests/index/LegacyBaseDocValuesFormatTestCase.kt`
    - `testThreads2` now uses `val storedDoc = storedFields.document(j)` once per doc.
- IDE checks:
  - `open_file_in_editor` success on modified file.
  - `get_file_problems` reports no errors (warnings only).
- Compile-first via IDE run configs:
  - `lucene-kmp [compileTestKotlinJvm]` passed.
  - `lucene-kmp [compileTestKotlinLinuxX64]` passed.
- JVM targeted run (no timeout):
  - `:core:jvmTest --tests org.gnit...TestLucene90DocValuesFormat.testThreads2` passed; suite time `2.7s`.
- Timed runs attempted:
  - `timeout 1s` JVM and `timeout 10s` linuxX64 both terminated before test execution due Gradle startup/task graph overhead.
- linuxX64 targeted run (no timeout) is currently blocked in this environment by repeated timeout during `linkDebugTestLinuxX64` in terminal tool.
- Added OOM-protection instructions to:
  - `AGENTS.md`
  - `lucene-kmp/AGENTS.md`
  - Both now require starting `/usr/bin/earlyoom` with Gradle/Java prefer-regex before terminal `./gradlew` runs.
