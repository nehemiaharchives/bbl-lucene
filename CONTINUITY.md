- Goal (Big Goal): Make CharArrayIterator and its dependencies (jdk ported classes) in analysis/common work with unit tests TestCharArrayIterator and unit tests for jkd ported classes.
- TestCharArrayIterator: Status; not ported from java lucene yet. we will port after all dependent jdk classes passed unit tests.
- Status of JDK classes ported into lucene-kmp to enable CharArrayIterator:
  core/src/commonMain/kotlin/org/gnit/lucenekmp/jdkport/BreakDictionary.kt (Status: Passees Unit Tests)
  core/src/commonMain/kotlin/org/gnit/lucenekmp/jdkport/BreakIterator.kt (Status: Passes Unit Tests)

  core/src/commonMain/kotlin/org/gnit/lucenekmp/jdkport/BreakIteratorProvider.kt (Status: Compiles but No Unit Tests)
  core/src/commonMain/kotlin/org/gnit/lucenekmp/jdkport/BreakIteratorProviderImpl.kt (Status: Compiles but No Unit Tests)
  core/src/commonMain/kotlin/org/gnit/lucenekmp/jdkport/CharacterIterator.kt (Status: Compiles but No Unit Tests)
  core/src/commonMain/kotlin/org/gnit/lucenekmp/jdkport/DictionaryBasedBreakIterator.kt (Status: Compiles but No Unit Tests)
  core/src/commonMain/kotlin/org/gnit/lucenekmp/jdkport/Grapheme.kt (Status: Compiles but No Unit Tests)
  core/src/commonMain/kotlin/org/gnit/lucenekmp/jdkport/IndicConjunctBreak.kt (Status: Compiles but No Unit Tests)
  core/src/commonMain/kotlin/org/gnit/lucenekmp/jdkport/JRELocaleProviderAdapter.kt (Status: Compiles but No Unit Tests)
  core/src/commonMain/kotlin/org/gnit/lucenekmp/jdkport/LocaleProviderAdapter.kt (Status: Compiles but No Unit Tests)
  core/src/commonMain/kotlin/org/gnit/lucenekmp/jdkport/LocaleServiceProvider.kt (Status: Compiles but No Unit Tests)
  core/src/commonMain/kotlin/org/gnit/lucenekmp/jdkport/RuleBasedBreakIterator.kt (Status: Compiles but No Unit Tests)
  core/src/commonMain/kotlin/org/gnit/lucenekmp/jdkport/StringCharacterIterator.kt (Status: Compiles but No Unit Tests)

- Goal (incl. success criteria): Create Unit tests for BreakIteratorProvider.kt
- Constraints/Assumptions: Follow AGENTS.md; do not modify lucene/ or jdk24u/; KMP common code only; avoid String.toByteArray() and String.format; @Ignore in tests has no args; use KotlinLogging if logging.
- Key decisions: Treat 0xFF in charCategoryTable as IGNORE; implement BreakIterator.availableLocales via JRE adapter; GraphemeBreakTest should read from classpath test resources.
- State:
- Done: LABEL check enabled; supportedVersion=1; charCategoryTable expanded; lookupCategory handles IGNORE. Implemented supplementary character data support and codepoint string conversion; BreakIteratorTest passes. GraphemeBreakTest.txt copied to commonTest resources.
- Now: Await confirmation or next test to port/run.
- Next: Rerun BreakIteratorTest if requested.
- Open questions (UNCONFIRMED if needed): None.
- Working set (files/ids/commands): /home/joel/code/bbl-lucene/lucene-kmp/core/src/commonTest/kotlin/org/gnit/lucenekmp/jdkport/BreakIteratorTest.kt, /home/joel/code/bbl-lucene/lucene-kmp/core/src/commonTest/resources/GraphemeBreakTest.txt, /home/joel/code/bbl-lucene/CONTINUITY.md
