# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Install debug build on connected device/emulator
./gradlew installDebug

# Run unit tests
./gradlew test

# Run a single unit test class
./gradlew test --tests "ru.diaries.mydiaries.ExampleUnitTest"

# Run instrumented tests (requires device/emulator)
./gradlew connectedAndroidTest

# Clean build
./gradlew clean

# Check for dependency updates
./gradlew dependencyUpdates
```

## Architecture

**Project Type:** Single-module Android application using Jetpack Compose

**Package:** `ru.diaries.mydiaries`

**Tech Stack:**
- UI: Jetpack Compose with Material Design 3
- Architecture: use MVI
- Use Coroutines for asynchronius
- Build: Gradle Kotlin DSL with version catalog (`gradle/libs.versions.toml`)
- Min SDK: 24 (Android 7.0), Target SDK: 36 (Android 15)
- Kotlin: 2.0.21 with Compose compiler plugin

**Current Structure:**
- `MainActivity.kt` - Single activity entry point using Compose
- `ui/theme/` - Material3 theming (Color.kt, Theme.kt, Type.kt)
- Dynamic color support for Android 12+

**Testing:**
- Unit tests: `app/src/test/` - JUnit 4
- Instrumented tests: `app/src/androidTest/` - AndroidJUnit4 + Espresso + Compose UI testing

## Key Configuration Files

- `gradle/libs.versions.toml` - Centralized dependency versions
- `app/build.gradle.kts` - App module build configuration
- `app/src/main/AndroidManifest.xml` - App manifest with backup rules configured
