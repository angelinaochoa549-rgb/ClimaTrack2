# Walkthrough - OSMDroid HTTP 403 Fix

The HTTP 403 error in `osmdroid` was fixed by providing a proper `User-Agent` and correctly loading the configuration using `PreferenceManager`.

## Changes

### Build Configuration
- Added `androidx.preference:preference-ktx:1.2.1` to [app/build.gradle.kts](file:///C:/Users/Aprendiz/Documents/actividades_android/ClimaTrackProject/app/build.gradle.kts).

### UI - Activities
- Updated [UbicacionActivity.kt](file:///C:/Users/Aprendiz/Documents/actividades_android/ClimaTrackProject/app/src/main/java/com/example/climatrack/activities/UbicacionActivity.kt) to:
    - Include `androidx.preference.PreferenceManager` import.
    - Set a descriptive `userAgentValue` before `setContentView`.
    - Use `PreferenceManager.getDefaultSharedPreferences(applicationContext)` to load the configuration.

## Verification Results

### Automated Tests
- Gradle sync was successful.
- Semantic analysis of `UbicacionActivity.kt` confirms no compilation errors.

### Manual Verification
> [!IMPORTANT]
> Please deploy the app to your device and verify that the map tiles are now loading correctly.
