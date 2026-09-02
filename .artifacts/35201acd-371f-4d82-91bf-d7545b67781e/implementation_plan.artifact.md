# Implementation Plan - Perfecting the Login Screen

The goal is to update the Login screen (`MainActivity`) to exactly match the provided design, including the technician illustration, background styling, and all interactive elements.

## Proposed Changes

### 1. Visual Resources
#### [NEW] [ic_login_illustration.xml](file:///C:/Users/Aprendiz/AndroidStudioProjects/ClimaTrack/app/src/main/res/drawable/ic_login_illustration.xml)
- Create a vector illustration representing a technician with an AC unit and a toolbox, matching the style in the image.

#### [NEW] [bg_login_header.xml](file:///C:/Users/Aprendiz/AndroidStudioProjects/ClimaTrack/app/src/main/res/drawable/bg_login_header.xml)
- A light blue/grey gradient or shape with a soft curve at the bottom to serve as the header background.

### 2. Layout Update
#### [MODIFY] [activity_main.xml](file:///C:/Users/Aprendiz/AndroidStudioProjects/ClimaTrack/app/src/main/res/layout/activity_main.xml)
- **Header Section**: Add the `bg_login_header` and the new illustration.
- **Form Section**:
    - "Bienvenido" (Title) and "Inicia sesión para continuar" (Subtitle).
    - `TextInputLayout` for "Usuario" with `person` icon.
    - `TextInputLayout` for "Contraseña" with `lock` icon and password toggle.
    - `CheckBox` for "Recordar usuario".
    - Solid blue `MaterialButton` for "INGRESAR".
    - Blue text for "¿Olvidaste tu contraseña?".
- **Footer**: Add "ClimaTrack © 2026" at the bottom center.

### 3. Logic Refinement
#### [MODIFY] [MainActivity.kt](file:///C:/Users/Aprendiz/AndroidStudioProjects/ClimaTrack/app/src/main/java/com/example/climatrack/MainActivity.kt)
- Ensure all view bindings match the updated IDs.
- (Optional) Add dummy logic for the password recovery link.

## Verification Plan

### Manual Verification
- Deploy the app and navigate to the Login screen.
- Verify that the illustration and background curve match the reference image.
- Check that the input fields have the correct icons and hints.
- Verify the "INGRESAR" button is functional and styled correctly.
- Ensure the layout is responsive and looks good on different screen sizes.
