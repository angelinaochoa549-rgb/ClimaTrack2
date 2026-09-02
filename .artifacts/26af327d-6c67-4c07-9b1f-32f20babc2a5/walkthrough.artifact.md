# Resumen de Correcciones en `EquiposAdapter.kt` y `EquiposActivity.kt`

Se han corregido todos los errores de compilación y se han aplicado mejores prácticas de desarrollo Android.

## Cambios Realizados

### Configuración y Dependencias
- Se integró la librería **Glide** para la gestión eficiente de imágenes.
- Se actualizaron `libs.versions.toml` y `build.gradle.kts` para incluir las dependencias necesarias.

### Recursos (UI/UX)
- **Colores**: Se agregó `status_operativo` en `colors.xml` para asegurar que todos los estados tengan un color asignado.
- **Strings**: Se crearon recursos de texto con marcadores de posición (`label_tipo`, `label_marca`, etc.) en `strings.xml` para permitir la localización y evitar cadenas fijas.
- **Iconos**: Se corrigió el mapeo de tipos de equipo a los recursos de imagen reales existentes en el proyecto (`img_split`, `img_chiller`, etc.).

### Lógica del Adaptador (`EquipoAdapter.kt`)
- Se cambió el nombre de la clase a `EquipoAdapter` para coincidir con el nombre del archivo.
- Se implementó la función `filtrar(texto: String)` para permitir la búsqueda de equipos por código, cliente o tipo.
- Se optimizó la carga de imágenes usando Glide con placeholders dinámicos según el tipo de equipo.

### Actividad de Equipos (`EquiposActivity.kt`)
- Se corrigieron los errores de tipo en el constructor de `Equipo` (ID como `Int` en lugar de `String`).
- Se actualizó la inicialización del adaptador para incluir el listener de clics (lambda).
- Se eliminaron referencias incorrectas a recursos de imagen pasados como parámetros de cadena.

## Verificación
- El proyecto compila correctamente (`gradlew assembleDebug` exitoso).
- Se verificó que las referencias a recursos y clases sean consistentes entre la actividad y el adaptador.
