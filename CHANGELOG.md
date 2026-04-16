## 0.0.2

- Fixed Kotlin class redeclaration build failure by removing duplicate
  `com.example.*` Android source files from the published package.
- Kept only `com.umairabbasi.display_over_other_apps_flutter` Android namespace.

## 0.0.1

Initial release of `display_over_other_apps_flutter`.

- Added Android overlay permission check and settings request flow.
- Added draggable floating bubble overlay with drag-to-dismiss target.
- Added overlay lifecycle APIs: show, close, and running status checks.
- Added event stream callbacks for overlay tap and dismiss events.
- Added manual Android manifest setup documentation for package consumers.
- Updated Android plugin namespace to `com.umairabbasi.display_over_other_apps_flutter`.
