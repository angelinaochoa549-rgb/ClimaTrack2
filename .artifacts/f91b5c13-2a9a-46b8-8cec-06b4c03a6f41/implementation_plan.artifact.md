# Fix OSMDroid HTTP 403 Error

This plan addresses the HTTP 403 (Access Blocked) error in osmdroid by correctly configuring the User-Agent and loading the configuration using `PreferenceManager`.

## Proposed Changes

### [Component: Build Configuration]

#### [MODIFY] [app/build.gradle.kts](file:///C:/Users/Aprendiz/Documents/actividades_android/ClimaTrackProject/app/build.gradle.kts)
- Add `androidx.preference:preference-ktx:1.2.1` dependency to support `PreferenceManager`.

### [Component: UI - Activities]

#### [MODIFY] [UbicacionActivity.kt](file:///C:/Users/Aprendiz/Documents/actividades_android/ClimaTrackProject/app/src/main/java/com/example/climatrack/activities/UbicacionActivity.kt)
- Add necessary imports for `androidx.preference.PreferenceManager`.
- Update `onCreate` to set the `userAgentValue` and load the configuration before `setContentView`.
- Verify `onResume` and `onPause` lifecycle methods for `MapView`.

## Verification Plan

### Automated Tests
- Run `gradlew :app:assembleDebug` to ensure the project builds with the new dependency.

### Manual Verification
- Deploy the app to a device/emulator and verify that the map tiles load without HTTP 403 errors.
