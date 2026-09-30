# Implementation Plan - Project Review, Bug Fixes & Feature Completion

Review and enhance the ClimaTrack project to ensure all features are fully functional, database persistence is correctly implemented for maintenance records, and navigation and UI interactions are optimized.

## User Review Required

> [!IMPORTANT]
> - **Mantenimiento Persistence**: `MantenimientoActivity` currently only shows a toast message when saving. We will update it to persist the diagnosis, work done, observations, recommendations, and time spent into the `mantenimientos` SQLite table and update the order status to "EN PROCESO".
> - **Navigation & Flow**: Ensure seamless navigation across all activities (Dashboard, Ordenes, Equipos, Historial, Mantenimiento, Repuestos, Evidencias, Ubicación, Aprobación).

## Proposed Changes

### Database & Activities Enhancement

#### [MODIFY] [MantenimientoActivity.kt](file:///C:/Users/Aprendiz/Documents/actividades_android/Clima/app/src/main/java/com/example/climatrack/activities/MantenimientoActivity.kt)
- Receive `ORDEN_ID` from Intent.
- Populate spinners and capture input fields (Diagnosis, Work Done, Observations, Recommendations, Time Spent, Type, Equipment State, Technician).
- Persist data into the `mantenimientos` table and update the order status to `"EN PROCESO"` in the `ordenes` table.

#### [MODIFY] [DatabaseHelper.kt](file:///C:/Users/Aprendiz/Documents/actividades_android/Clima/app/src/main/java/com/example/climatrack/database/DatabaseHelper.kt)
- Add a helper method `insertarOActualizarMantenimiento(...)` or ensure maintenance insertion handles data correctly.

## Verification Plan

### Automated Tests
- Run unit tests: `./gradlew app:testDebugUnitTest`
- Build app: `./gradlew app:assembleDebug`

### Manual Verification
- Deploy and test the complete workflow:
  1. Login with `tecnico01` / `123456`.
  2. Navigate to Dashboard -> Órdenes -> Select an Order -> Start Maintenance (`MantenimientoActivity`).
  3. Fill in maintenance details and save (verify database insertion and status change).
  4. Add parts (`RepuestosActivity`), take photos (`EvidenciasActivity`), capture GPS (`UbicacionActivity`), and sign/approve (`AprobacionActivity`).
