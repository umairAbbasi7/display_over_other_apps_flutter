# display_over_other_apps_flutter

Android Flutter plugin to display a draggable floating overlay over other apps.

## Features (v1)

- Permission check/request
- Show/close overlay
- Running status check
- Tap callback via event stream
- Drag/move overlay
- Drag-to-dismiss using close target

## Platform support

- Android only

## Usage

```dart
final plugin = DisplayOverOtherAppsFlutter();

final hasPermission = await plugin.hasPermission();
if (!hasPermission) {
  await plugin.requestPermission();
}

await plugin.showOverlay();

DisplayOverOtherAppsFlutter.events.listen((event) {
  // event.type => tap / dismissed / unknown
});
```

`requestPermission()` opens Android's overlay settings page and resolves after
the user returns to the app with the latest permission state.

## Android setup (host app)

This package uses manual Android manifest setup. Add the following to your app's
`android/app/src/main/AndroidManifest.xml`.

Add these permissions under `<manifest>`:

```xml
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_SPECIAL_USE" />
```

Add this service under `<application>`:

```xml
<service
    android:name="com.umairabbasi.display_over_other_apps_flutter.DisplayOverAppsService"
    android:exported="false"
    android:foregroundServiceType="specialUse">
    <property
        android:name="android.app.PROPERTY_SPECIAL_USE_FGS_SUBTYPE"
        android:value="Floating overlay quick return icon" />
</service>
```

If these manifest entries are missing, `showOverlay()` will fail.

## Notes

- Overlay support is Android-only.
- The overlay is managed through a foreground service, so users will see an
  ongoing notification while the overlay is active.

