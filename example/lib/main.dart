import 'package:display_over_other_apps_flutter/display_over_other_apps_flutter.dart';
import 'package:flutter/material.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({super.key});

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  final _events = <String>[];
  final _displayOverOtherAppsFlutterPlugin = DisplayOverOtherAppsFlutter();
  final GlobalKey<ScaffoldMessengerState> _scaffoldMessengerKey =
      GlobalKey<ScaffoldMessengerState>();

  @override
  void initState() {
    super.initState();
    DisplayOverOtherAppsFlutter.events.listen((event) {
      if (!mounted) return;
      setState(() {
        _events.insert(0, event.type.name);
        if (_events.length > 6) _events.removeLast();
      });
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      scaffoldMessengerKey: _scaffoldMessengerKey,
      home: Scaffold(
        appBar: AppBar(title: const Text('Plugin example app')),
        body: Padding(
          padding: const EdgeInsets.all(16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              ElevatedButton(
                onPressed: () async {
                  final granted = await _displayOverOtherAppsFlutterPlugin
                      .hasPermission();
                  _show(context, 'Permission: $granted');
                },
                child: const Text('Check Permission'),
              ),
              ElevatedButton(
                onPressed: () async {
                  final granted = await _displayOverOtherAppsFlutterPlugin
                      .requestPermission();
                  _show(context, 'Request result: $granted');
                },
                child: const Text('Request Permission'),
              ),
              ElevatedButton(
                onPressed: () async {
                  final ok = await _displayOverOtherAppsFlutterPlugin
                      .showOverlay();
                  _show(context, 'Show overlay: $ok');
                },
                child: const Text('Show Overlay'),
              ),
              ElevatedButton(
                onPressed: () async {
                  final ok = await _displayOverOtherAppsFlutterPlugin
                      .closeOverlay();
                  _show(context, 'Close overlay: $ok');
                },
                child: const Text('Close Overlay'),
              ),
              ElevatedButton(
                onPressed: () async {
                  final running = await _displayOverOtherAppsFlutterPlugin
                      .isOverlayRunning();
                  _show(context, 'Running: $running');
                },
                child: const Text('Is Running'),
              ),
              const SizedBox(height: 16),
            ],
          ),
        ),
      ),
    );
  }

  void _show(BuildContext context, String msg) {
    _scaffoldMessengerKey.currentState?.showSnackBar(
      SnackBar(content: Text(msg), duration: const Duration(seconds: 1)),
    );
  }
}
