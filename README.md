# display_over_other_apps_flutter

Android Flutter plugin to show a draggable floating bubble over other apps.

## Platform support

- Android only

## What this package provides

- Check overlay permission
- Open overlay permission settings
- Show and close floating overlay bubble
- Check whether overlay service is running
- Stream overlay events (`tap`, `dismissed`, `unknown`)
- Drag-to-move bubble and drag-to-dismiss target

## Installation

Add dependency:

```yaml
dependencies:
  display_over_other_apps_flutter: ^0.0.1
```

Import:

```dart
import 'package:display_over_other_apps_flutter/display_over_other_apps_flutter.dart';
```

## Android setup (required)

This package uses manual manifest setup. In your app, edit:

`android/app/src/main/AndroidManifest.xml`

Add these under `<manifest>`:

```xml
<uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE_SPECIAL_USE" />
```

Add this under `<application>`:

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

If these entries are missing, `showOverlay()` will return `false`.

## Quick start

```dart
final plugin = DisplayOverOtherAppsFlutter();

Future<void> showBubble() async {
  final granted = await plugin.hasPermission();
  if (!granted) {
    final permissionAfterSettings = await plugin.requestPermission();
    if (!permissionAfterSettings) return;
  }

  await plugin.showOverlay();
}

final sub = DisplayOverOtherAppsFlutter.events.listen((event) {
  switch (event.type) {
    case OverlayEventType.tap:
      // Bubble tapped
      break;
    case OverlayEventType.dismissed:
      // Bubble closed
      break;
    case OverlayEventType.unknown:
      break;
  }
});
```

## API

- `hasPermission()` -> returns current overlay permission state
- `requestPermission()` -> always opens overlay settings and resolves when user returns
- `showOverlay()` -> starts service and shows bubble (`true` on success)
- `closeOverlay()` -> closes bubble/service (`true` on success)
- `isOverlayRunning()` -> returns whether overlay service is running

## Behavior notes

- Bubble icon uses the host app icon (`applicationInfo.icon`).
- Tapping bubble opens host app launch activity.
- Overlay runs as foreground service, so users see an ongoing notification.

## Common integration issues

- `showOverlay()` returns `false`:
  - Missing manifest permissions or service declaration
  - Overlay permission not granted by user
- `requestPermission()` returns `false`:
  - User denied permission in settings
  - No active host activity when request was made

