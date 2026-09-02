# Plan de Corrección de Errores en Repuestos y Mantenimiento

Se han detectado errores de compilación y de recursos faltantes tras los cambios recientes en las actividades de mantenimiento y repuestos.

## User Review Required

> [!IMPORTANT]
> Se crearán varios archivos de recursos vectoriales (XML) para iconos que faltan en el proyecto. Estos iconos son esenciales para la interfaz de usuario de mantenimiento y la lista de repuestos.

## Proposed Changes

### Resources (Drawables)

#### [NEW] [ic_arrow_back.xml](file:///C:/Users/Aprendiz/Documents/actividades_android/ClimaTrack2/app/src/main/res/drawable/ic_arrow_back.xml)
- Icono de flecha hacia atrás para la Toolbar.

#### [NEW] [ic_calendar.xml](file:///C:/Users/Aprendiz/Documents/actividades_android/ClimaTrack2/app/src/main/res/drawable/ic_calendar.xml)
- Icono de calendario para la selección de fecha.

#### [NEW] [ic_clock.xml](file:///C:/Users/Aprendiz/Documents/actividades_android/ClimaTrack2/app/src/main/res/drawable/ic_clock.xml)
- Icono de reloj para la selección de hora.

#### [NEW] [ic_filter.xml](file:///C:/Users/Aprendiz/Documents/actividades_android/ClimaTrack2/app/src/main/res/drawable/ic_filter.xml)
#### [NEW] [ic_capacitor.xml](file:///C:/Users/Aprendiz/Documents/actividades_android/ClimaTrack2/app/src/main/res/drawable/ic_capacitor.xml)
#### [NEW] [ic_contactor.xml](file:///C:/Users/Aprendiz/Documents/actividades_android/ClimaTrack2/app/src/main/res/drawable/ic_contactor.xml)
#### [NEW] [ic_gas.xml](file:///C:/Users/Aprendiz/Documents/actividades_android/ClimaTrack2/app/src/main/res/drawable/ic_gas.xml)
#### [NEW] [ic_gear.xml](file:///C:/Users/Aprendiz/Documents/actividades_android/ClimaTrack2/app/src/main/res/drawable/ic_gear.xml)
- Iconos específicos para los diferentes tipos de repuestos.

### Layouts

#### [MODIFY] [item_repuesto.xml](file:///C:/Users/Aprendiz/Documents/actividades_android/ClimaTrack2/app/src/main/res/layout/item_repuesto.xml)
- Actualizar el diseño para incluir `imgRepuesto`, `tvPrecio` y `btnOpciones`.
- Estandarizar IDs: `tvCodigo`, `tvDescripcion`.

### Adapters

#### [MODIFY] [RepuestoAdapter.kt](file:///C:/Users/Aprendiz/Documents/actividades_android/ClimaTrack2/app/src/main/java/com/example/climatrack/adapters/RepuestoAdapter.kt)
- Ajustar las referencias de IDs para que coincidan con el nuevo `item_repuesto.xml`.

## Verification Plan

### Automated Tests
- Ejecutar `gradlew assembleDebug` para confirmar que todos los recursos se vinculan correctamente y el proyecto compila.

### Manual Verification
- Verificar visualmente en el emulador que los iconos aparezcan en `MantenimientoActivity` y `RepuestosActivity`.
- Comprobar que la lista de repuestos muestre el precio total y los iconos correspondientes.
