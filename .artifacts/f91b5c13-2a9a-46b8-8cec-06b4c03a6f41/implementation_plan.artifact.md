# Fix OSMDroid HTTP 403 Error (Persistence)

The 403 error persists, likely due to cached error tiles or the User-Agent not being applied correctly before tile requests.

## Proposed Changes

### [Component: UI - Activities]

#### [MODIFY] [UbicacionActivity.kt](file:///C:/Users/Aprendiz/Documents/actividades_android/ClimaTrackProject/app/src/main/java/com/example/climatrack/activities/UbicacionActivity.kt)
- Update `onCreate` to ensure `userAgentValue` is set *after* loading the configuration to prevent it from being overridden.
- Use a more descriptive User-Agent.
- Add logic to clear the tile cache programmatically once to remove any cached 403 error tiles.
- Ensure `MapView` is initialized after the configuration is fully set.

## Verification Plan

### Manual Verification
- Deploy the app.
- If tiles still show 403, try clearing App Data manually in Android Settings, as some tiles might be cached at the OS level or persistent storage.
