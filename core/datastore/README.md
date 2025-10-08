# GBR Datastore Module

This module provides user preferences management using Android DataStore.

## Features

- **Dark Theme Configuration**: Manage app's dark theme preference with 3 options:
  - `FOLLOW_SYSTEM` - Follow system's dark theme setting
  - `LIGHT` - Always use light theme
  - `DARK` - Always use dark theme

- **Last Used Gitabase**: Remember and restore the last used Gitabase database:
  - Save `GitabaseID` objects with type and language
  - Reactive Flow-based observation
  - Automatic serialization/deserialization

## Usage

### 1. Inject the DataSource

```kotlin
class MyRepository @Inject constructor(
    private val gbrPreferencesDataSource: GbrPreferencesDataSource
) {
    // Use the data source
}
```

### 2. Observe User Preferences

```kotlin
// Collect user data changes
gbrPreferencesDataSource.userData.collect { userData ->
    when (userData.darkThemeConfig) {
        DarkThemeConfig.FOLLOW_SYSTEM -> {
            // Apply system theme
        }
        DarkThemeConfig.LIGHT -> {
            // Apply light theme
        }
        DarkThemeConfig.DARK -> {
            // Apply dark theme
        }
    }
}
```

### 3. Update Preferences

```kotlin
// Set dark theme to light
gbrPreferencesDataSource.setDarkThemeConfig(DarkThemeConfig.LIGHT)

// Set dark theme to dark
gbrPreferencesDataSource.setDarkThemeConfig(DarkThemeConfig.DARK)

// Set dark theme to follow system
gbrPreferencesDataSource.setDarkThemeConfig(DarkThemeConfig.FOLLOW_SYSTEM)
```

### 4. Last Used Gitabase

```kotlin
// Observe last used Gitabase changes
gbrPreferencesDataSource.lastUsedGitabase.collect { gitabaseId ->
    if (gitabaseId != null) {
        // Handle last used Gitabase
        println("Last used: ${gitabaseId.type.value}_${gitabaseId.lang.value}")
    }
}

// Set last used Gitabase
val gitabaseId = GitabaseID(
    type = GitabaseType.HELP,
    lang = GitabaseLang.ENG
)
gbrPreferencesDataSource.setLastUsedGitabase(gitabaseId)

// Get last used Gitabase
val lastUsed = gbrPreferencesDataSource.getLastUsedGitabase()
if (lastUsed != null) {
    println("Retrieved: ${lastUsed.key}")
}
```

## Architecture

- **GbrPreferencesDataSource**: Main data source class
- **UserData**: Data class containing user preferences
- **DarkThemeConfig**: Enum with theme options
- **DataStore**: Uses Android DataStore for persistence

## Dependencies

- Android DataStore Preferences
- Hilt for dependency injection
- Kotlin Coroutines for reactive programming
