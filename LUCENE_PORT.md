# Lucene KMP Porting Project

## Overview

This repository is dedicated to porting Apache Lucene from Java to Kotlin Multiplatform (KMP) to create a cross-platform library called lucene-kmp. The project aims to make Lucene's powerful search capabilities available across multiple platforms through Kotlin Multiplatform technology.

## Repository Structure

This repository contains two git submodules:

1. **lucene** - The original Apache Lucene Java codebase (read-only)
   - Fixed at commit: `ec75fcad5a4208c7b9e35e870229d9b703cda8f3`
   - URL: https://github.com/apache/lucene.git

2. **lucene-kmp** - The Kotlin Multiplatform port (work-in-progress)
   - URL: https://github.com/nehemiaharchives/lucene-kmp.git

## Porting Workflow

The porting process follows these guidelines:

- The Java version of Apache Lucene is **read-only** and should never be modified.
- The Java version is fixed at commit `ec75fcad5a4208c7b9e35e870229d9b703cda8f3` or (`ec75fca` in short) until the initial porting is complete.
- Do not run `git pull` or `git fetch` in the Java lucene submodule as the porting is based on the specific commit.
- The porting work involves converting Java code to Kotlin Common code to create a Kotlin Multiplatform library.

## Project Purpose

This repository structure is specifically designed to facilitate AI agents in the porting work by providing access to both the original Java code and the Kotlin port in a single repository. This setup allows for easier comparison and reference between the two codebases during the porting process.

## Phase Breakdown

The porting and development of lucene-kmp is organized into several distinct phases:

### **Phase 1: Minimum Indexing Support**

- **Goal:**  
  Port the core Lucene classes necessary to write (create) an index.
- **Includes:**  
  Index writing classes (e.g., `IndexWriter`, `IndexWriterConfig`), required document and field types, and storage components such as `FSDirectory`.
- **Outcome:**  
  lucene-kmp can write indices on supported platforms.

### **Phase 2: Minimum Search Support**

- **Goal:**  
  Port essential classes to enable searching of indices.
- **Includes:**  
  Index reading classes (e.g., `DirectoryReader`, `StandardDirectoryReader`), query parsing and execution components, and required storage support.
- **Outcome:**  
  lucene-kmp can run basic search queries over indices.

### **Phase 3: Testing and Benchmarking**

- **Goal:**  
  Ensure functional correctness and performance.
- **Includes:**  
  Porting/writing test suites, CI integration across platforms, and benchmarking against the original Java implementation.

### **Phase 4: Ongoing Upstream Sync**

- **Goal:**  
  Maintain currency with the upstream Apache Lucene project.
- **Includes:**  
  Porting subsequent commits from upstream on an ongoing basis (potentially using automation), with periodic updates to the Kotlin Multiplatform port.


## Future Plans

After completing the initial port of all components and tests from the fixed Java version:

1. The project will proceed with commit-by-commit updates, following the subsequent commits in the Java Lucene repository.
2. Each change in the Java version will be ported to the lucene-kmp project to keep it up-to-date with the latest Lucene features and improvements.

## Supported Platforms

The lucene-kmp library targets the following platforms:
- JVM
- Android
- iOS (iosX64, iosArm64, iosSimulatorArm64)

## Progress

For detailed progress on the porting effort, please refer to the [PROGRESS.md](lucene-kmp/PROGRESS.md) file in the lucene-kmp submodule.

## Contributing

If you're interested in contributing to the porting effort, please follow these steps:

1. Understand the original Java implementation in the lucene submodule
2. Port the code to Kotlin following Kotlin Multiplatform best practices
3. Ensure all tests pass for the ported code
4. Submit your changes to the lucene-kmp repository

Remember that the Java version should remain untouched, and all development work should happen in the lucene-kmp submodule.
