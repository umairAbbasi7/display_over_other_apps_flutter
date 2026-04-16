import 'package:display_over_other_apps_flutter/display_over_other_apps_flutter_method_channel.dart';
import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  MethodChannelDisplayOverOtherAppsFlutter platform =
      MethodChannelDisplayOverOtherAppsFlutter();
  const MethodChannel channel = MethodChannel(
    'display_over_other_apps_flutter/methods',
  );
  final invokedMethods = <String>[];

  setUp(() {
    invokedMethods.clear();
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
        .setMockMethodCallHandler(channel, (MethodCall methodCall) async {
          invokedMethods.add(methodCall.method);
          return true;
        });
  });

  tearDown(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
        .setMockMethodCallHandler(channel, null);
  });

  test('hasPermission', () async {
    expect(await platform.hasPermission(), true);
    expect(invokedMethods, ['hasPermission']);
  });

  test('requestPermission', () async {
    expect(await platform.requestPermission(), true);
    expect(invokedMethods, ['requestPermission']);
  });

  test('showOverlay', () async {
    expect(await platform.showOverlay(), true);
    expect(invokedMethods, ['showOverlay']);
  });

  test('closeOverlay', () async {
    expect(await platform.closeOverlay(), true);
    expect(invokedMethods, ['closeOverlay']);
  });

  test('isOverlayRunning', () async {
    expect(await platform.isOverlayRunning(), true);
    expect(invokedMethods, ['isOverlayRunning']);
  });
}
