# Update "Equipos" Icon in Bottom Navigation Bar

The user wants the "Equipos" icon in the bottom navigation bar to match a specific design (an AC unit icon). Currently, the bottom menu uses `ic_equi`, but there is another drawable `ic_equipos` that seems to match the desired design better and is already used in the Dashboard cards.

## Proposed Changes

### [Component Name] UI Resources

#### [MODIFY] [bottom_menu.xml](file:///C:/Users/Aprendiz/Documents/actividades_android/ClimaTrack2project/app/src/main/res/menu/bottom_menu.xml)
- Change the `android:icon` attribute for the `nav_equipos` item from `@drawable/ic_equi` to `@drawable/ic_equipos`.

## Verification Plan

### Manual Verification
- Deploy the app to a device or emulator.
- Verify that the bottom navigation bar shows the new AC unit icon for the "Equipos" tab.
- Ensure the icon looks consistent with the image provided by the user.