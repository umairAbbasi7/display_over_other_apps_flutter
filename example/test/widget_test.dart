// This is a basic Flutter widget test.
//
// To perform an interaction with a widget in your test, use the WidgetTester
// utility in the flutter_test package. For example, you can send tap and scroll
// gestures. You can also use WidgetTester to find child widgets in the widget
// tree, read text, and verify that the values of widget properties are correct.

import 'package:display_over_other_apps_flutter_example/main.dart';
import 'package:flutter_test/flutter_test.dart';

void main() {
  testWidgets('shows plugin example controls', (WidgetTester tester) async {
    await tester.pumpWidget(const MyApp());

    expect(find.text('Plugin example app'), findsOneWidget);
    expect(find.text('Check Permission'), findsOneWidget);
    expect(find.text('Request Permission'), findsOneWidget);
    expect(find.text('Show Overlay'), findsOneWidget);
    expect(find.text('Close Overlay'), findsOneWidget);
    expect(find.text('Is Running'), findsOneWidget);
    expect(find.text('Overlay events:'), findsOneWidget);
  });
}
