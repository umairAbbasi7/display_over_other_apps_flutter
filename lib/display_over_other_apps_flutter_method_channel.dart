import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'display_over_other_apps_flutter_platform_interface.dart';

/// An implementation of [DisplayOverOtherAppsFlutterPlatform] that uses method channels.
class MethodChannelDisplayOverOtherAppsFlutter
    extends DisplayOverOtherAppsFlutterPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel(
    'display_over_other_apps_flutter/methods',
  );

  @override
  Future<bool> hasPermission() async {
    final value = await methodChannel.invokeMethod<bool>('hasPermission');
    return value ?? false;
  }

  @override
  Future<bool> requestPermission() async {
    final value = await methodChannel.invokeMethod<bool>('requestPermission');
    return value ?? false;
  }

  @override
  Future<bool> showOverlay() async {
    final value = await methodChannel.invokeMethod<bool>('showOverlay');
    return value ?? false;
  }

  @override
  Future<bool> closeOverlay() async {
    final value = await methodChannel.invokeMethod<bool>('closeOverlay');
    return value ?? false;
  }

  @override
  Future<bool> isOverlayRunning() async {
    final value = await methodChannel.invokeMethod<bool>('isOverlayRunning');
    return value ?? false;
  }
}
