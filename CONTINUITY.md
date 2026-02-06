Goal (incl. success criteria):
- Clarify whether `TestLucene90LiveDocsFormat.testOverflow` OOM indicates a lucene-kmp bug vs expected Monster-test behavior, and define when heap increase is appropriate.

Constraints/Assumptions:
- Keep parity with upstream Lucene test semantics.
- Use JetBrains MCP workflow and avoid upstream edits.

Key decisions:
- Treat prior OOM as expected for Monster-scale input unless Monster mode is explicitly enabled.
- Keep default path gated via `tests.monster` (false) to avoid false failures in normal runs.

State:
- Completed for diagnostic question.

Done:
- Reproduced OOM on `TestLucene90LiveDocsFormat.testOverflow` when ungated.
- Confirmed payload scale is near Lucene max-doc stress level.
- Implemented `TEST_MONSTER` gate and verified default run config passes.
- Executed Monster-enabled run from IDE terminal:
  `cd lucene-kmp && ./gradlew :core:jvmTest --tests org.gnit.lucenekmp.codecs.lucene90.TestLucene90LiveDocsFormat.testOverflow -Dtests.monster=true --no-daemon --no-configuration-cache`
  Result: BUILD SUCCESSFUL.

Now:
- Report recommendation: skip in CI by default, optional local Monster verification run when needed.

Next:
- Optional follow-up: port proper assumption skip semantics in test framework.

Open questions (UNCONFIRMED if needed):
- UNCONFIRMED: whether to add a dedicated IDE run configuration for Monster tests with explicit flags.

Working set (files/ids/commands):
- `CONTINUITY.md`
- `lucene-kmp/test-framework/src/commonMain/kotlin/org/gnit/lucenekmp/tests/util/LuceneTestCase.kt`
- `lucene-kmp/test-framework/src/commonMain/kotlin/org/gnit/lucenekmp/tests/index/BaseLiveDocsFormatTestCase.kt`
- Command used for verification:
  `cd lucene-kmp && ./gradlew :core:jvmTest --tests org.gnit.lucenekmp.codecs.lucene90.TestLucene90LiveDocsFormat.testOverflow -Dtests.monster=true --no-daemon --no-configuration-cache`
