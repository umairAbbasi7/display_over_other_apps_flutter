import 'package:display_over_other_apps_flutter/display_over_other_apps_flutter.dart';
import 'package:display_over_other_apps_flutter/display_over_other_apps_flutter_method_channel.dart';
import 'package:display_over_other_apps_flutter/display_over_other_apps_flutter_platform_interface.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockDisplayOverOtherAppsFlutterPlatform
    with MockPlatformInterfaceMixin
    implements DisplayOverOtherAppsFlutterPlatform {
  @override
  Future<bool> closeOverlay() => Future.value(true);

  @override
  Future<bool> hasPermission() => Future.value(true);

  @override
  Future<bool> isOverlayRunning() => Future.value(false);

  @override
  Future<bool> requestPermission() => Future.value(true);

  @override
  Future<bool> showOverlay() => Future.value(true);
}

void main() {
  final DisplayOverOtherAppsFlutterPlatform initialPlatform =
      DisplayOverOtherAppsFlutterPlatform.instance;

  tearDown(() {
    DisplayOverOtherAppsFlutterPlatform.instance = initialPlatform;
  });

  test('$MethodChannelDisplayOverOtherAppsFlutter is the default instance', () {
    expect(
      initialPlatform,
      isInstanceOf<MethodChannelDisplayOverOtherAppsFlutter>(),
    );
  });

  test('hasPermission', () async {
    DisplayOverOtherAppsFlutter displayOverOtherAppsFlutterPlugin =
        DisplayOverOtherAppsFlutter();
    MockDisplayOverOtherAppsFlutterPlatform fakePlatform =
        MockDisplayOverOtherAppsFlutterPlatform();
    DisplayOverOtherAppsFlutterPlatform.instance = fakePlatform;

    expect(await displayOverOtherAppsFlutterPlugin.hasPermission(), true);
  });

  test('requestPermission', () async {
    DisplayOverOtherAppsFlutter displayOverOtherAppsFlutterPlugin =
        DisplayOverOtherAppsFlutter();
    MockDisplayOverOtherAppsFlutterPlatform fakePlatform =
        MockDisplayOverOtherAppsFlutterPlatform();
    DisplayOverOtherAppsFlutterPlatform.instance = fakePlatform;

    expect(await displayOverOtherAppsFlutterPlugin.requestPermission(), true);
  });

  test('showOverlay', () async {
    DisplayOverOtherAppsFlutter displayOverOtherAppsFlutterPlugin =
        DisplayOverOtherAppsFlutter();
    MockDisplayOverOtherAppsFlutterPlatform fakePlatform =
        MockDisplayOverOtherAppsFlutterPlatform();
    DisplayOverOtherAppsFlutterPlatform.instance = fakePlatform;

    expect(await displayOverOtherAppsFlutterPlugin.showOverlay(), true);
  });

  test('closeOverlay', () async {
    DisplayOverOtherAppsFlutter displayOverOtherAppsFlutterPlugin =
        DisplayOverOtherAppsFlutter();
    MockDisplayOverOtherAppsFlutterPlatform fakePlatform =
        MockDisplayOverOtherAppsFlutterPlatform();
    DisplayOverOtherAppsFlutterPlatform.instance = fakePlatform;

    expect(await displayOverOtherAppsFlutterPlugin.closeOverlay(), true);
  });

  test('isOverlayRunning', () async {
    DisplayOverOtherAppsFlutter displayOverOtherAppsFlutterPlugin =
        DisplayOverOtherAppsFlutter();
    MockDisplayOverOtherAppsFlutterPlatform fakePlatform =
        MockDisplayOverOtherAppsFlutterPlatform();
    DisplayOverOtherAppsFlutterPlatform.instance = fakePlatform;

    expect(await displayOverOtherAppsFlutterPlugin.isOverlayRunning(), false);
  });

  test('overlay event mapping', () {
    expect(OverlayEvent.fromRaw('tap').type, OverlayEventType.tap);
    expect(OverlayEvent.fromRaw('dismissed').type, OverlayEventType.dismissed);
    expect(
      OverlayEvent.fromRaw('anything-else').type,
      OverlayEventType.unknown,
    );
  });
}
