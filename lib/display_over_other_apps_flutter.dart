import 'package:flutter/services.dart';

import 'display_over_other_apps_flutter_platform_interface.dart';

enum OverlayEventType { tap, dismissed, unknown }

class OverlayEvent {
  const OverlayEvent(this.type);
  final OverlayEventType type;

  factory OverlayEvent.fromRaw(Object? raw) {
    final value = (raw ?? '').toString();
    switch (value) {
      case 'tap':
        return const OverlayEvent(OverlayEventType.tap);
      case 'dismissed':
        return const OverlayEvent(OverlayEventType.dismissed);
      default:
        return const OverlayEvent(OverlayEventType.unknown);
    }
  }
}

class DisplayOverOtherAppsFlutter {
  static const EventChannel _eventChannel = EventChannel(
    'display_over_other_apps_flutter/events',
  );
  static Stream<OverlayEvent>? _events;

  static Stream<OverlayEvent> get events {
    return _events ??= _eventChannel
        .receiveBroadcastStream()
        .map(OverlayEvent.fromRaw)
        .asBroadcastStream();
  }

  Future<bool> hasPermission() {
    return DisplayOverOtherAppsFlutterPlatform.instance.hasPermission();
  }

  Future<bool> requestPermission() {
    return DisplayOverOtherAppsFlutterPlatform.instance.requestPermission();
  }

  Future<bool> showOverlay() {
    return DisplayOverOtherAppsFlutterPlatform.instance.showOverlay();
  }

  Future<bool> closeOverlay() {
    return DisplayOverOtherAppsFlutterPlatform.instance.closeOverlay();
  }

  Future<bool> isOverlayRunning() {
    return DisplayOverOtherAppsFlutterPlatform.instance.isOverlayRunning();
  }
}
