# Implementation Plan - Comprehensive Equipment Maintenance Suite

Implement the full suite of maintenance functions as shown in the provided designs: **General Maintenance Registration**, **Spare Parts Usage**, and **Photo Evidence**.

## User Review Required

> [!IMPORTANT]
> - The database schema will be updated to include prices for spare parts and technician links.
> - New activities will be created for each section.
> - Navigation will be established from the Equipment list to the Maintenance flow.

## Proposed Changes

### Database Layer

#### [MODIFY] [DatabaseHelper.kt](file:///C:/Users/Aprendiz/AndroidStudioProjects/ClimaTrack/app/src/main/java/com/example/climatrack/database/DatabaseHelper.kt)
- Update `mantenimientos` table: add `hora_inicio`, `tipo_servicio`, `tecnico_id`, `tiempo_empleado`, `estado_equipo`.
- Update `repuestos` table: add `precio` (REAL/INTEGER).
- Update `detalle_repuestos` table: add `precio_unidad` (to record price at time of service).

### Models

#### [MODIFY] [Mantenimiento.kt](file:///C:/Users/Aprendiz/AndroidStudioProjects/ClimaTrack/app/src/main/java/com/example/climatrack/models/Mantenimiento.kt)
- Update model to include new fields.

#### [NEW] [RepuestoDetalle.kt](file:///C:/Users/Aprendiz/AndroidStudioProjects/ClimaTrack/app/src/main/java/com/example/climatrack/models/RepuestoDetalle.kt)
- Data class for items in the "Repuestos utilizados" list.

### UI / Layout

#### [MODIFY] [activity_registro_mantenimiento.xml](file:///C:/Users/Aprendiz/AndroidStudioProjects/ClimaTrack/app/src/main/res/layout/activity_registro_mantenimiento.xml)
- Redesign to match the "Mantenimiento" screen (Card header, segmented type, date/time pickers, counters).

#### [NEW] [activity_repuestos_utilizados.xml](file:///C:/Users/Aprendiz/AndroidStudioProjects/ClimaTrack/app/src/main/res/layout/activity_repuestos_utilizados.xml)
- Header card (OT, Equipment, Client).
- RecyclerView for spare parts list (item layout matching design).
- Bottom summary for "Total repuestos".
- Add button (+) in toolbar.

#### [NEW] [activity_evidencias.xml](file:///C:/Users/Aprendiz/AndroidStudioProjects/ClimaTrack/app/src/main/res/layout/activity_evidencias.xml)
- Header card.
- GridView or RecyclerView for photos.
- "TOMAR NUEVA FOTO" primary button.

### Logic Layer

#### [MODIFY] [RegistroMantenimientoActivity.kt](file:///C:/Users/Aprendiz/AndroidStudioProjects/ClimaTrack/app/src/main/java/com/example/climatrack/activities/RegistroMantenimientoActivity.kt)
- Implement full registration logic.
- Add navigation buttons to "Repuestos" and "Evidencias".

#### [NEW] [RepuestosActivity.kt](file:///C:/Users/Aprendiz/AndroidStudioProjects/ClimaTrack/app/src/main/java/com/example/climatrack/activities/RepuestosActivity.kt)
- Logic to list, add, and delete spare parts associated with the maintenance.

#### [NEW] [EvidenciasActivity.kt](file:///C:/Users/Aprendiz/AndroidStudioProjects/ClimaTrack/app/src/main/java/com/example/climatrack/activities/EvidenciasActivity.kt)
- Logic for camera integration and photo listing.

#### [MODIFY] [EquipoAdapter.kt](file:///C:/Users/Aprendiz/AndroidStudioProjects/ClimaTrack/app/src/main/java/com/example/climatrack/adapters/EquipoAdapter.kt)
- Add item click listener to navigate to the maintenance/detail view.

## Verification Plan

### Manual Verification
1. Open "Equipos" and click on an equipment.
2. Complete the "Mantenimiento" form.
3. Navigate to "Repuestos", add items, and check the total.
4. Navigate to "Evidencias", mock/take a photo, and check the grid.
5. Save everything and verify data in the DB (or via a summary screen).
