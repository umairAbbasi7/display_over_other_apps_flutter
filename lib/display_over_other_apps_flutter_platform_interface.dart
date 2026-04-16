import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'display_over_other_apps_flutter_method_channel.dart';

abstract class DisplayOverOtherAppsFlutterPlatform extends PlatformInterface {
  /// Constructs a DisplayOverOtherAppsFlutterPlatform.
  DisplayOverOtherAppsFlutterPlatform() : super(token: _token);

  static final Object _token = Object();

  static DisplayOverOtherAppsFlutterPlatform _instance =
      MethodChannelDisplayOverOtherAppsFlutter();

  /// The default instance of [DisplayOverOtherAppsFlutterPlatform] to use.
  ///
  /// Defaults to [MethodChannelDisplayOverOtherAppsFlutter].
  static DisplayOverOtherAppsFlutterPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [DisplayOverOtherAppsFlutterPlatform] when
  /// they register themselves.
  static set instance(DisplayOverOtherAppsFlutterPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<bool> hasPermission() {
    throw UnimplementedError('hasPermission() has not been implemented.');
  }

  Future<bool> requestPermission() {
    throw UnimplementedError('requestPermission() has not been implemented.');
  }

  Future<bool> showOverlay() {
    throw UnimplementedError('showOverlay() has not been implemented.');
  }

  Future<bool> closeOverlay() {
    throw UnimplementedError('closeOverlay() has not been implemented.');
  }

  Future<bool> isOverlayRunning() {
    throw UnimplementedError('isOverlayRunning() has not been implemented.');
  }
}
