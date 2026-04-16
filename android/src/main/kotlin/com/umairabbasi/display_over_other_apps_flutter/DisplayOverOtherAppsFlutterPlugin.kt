package com.umairabbasi.display_over_other_apps_flutter

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import io.flutter.plugin.common.PluginRegistry

/** DisplayOverOtherAppsFlutterPlugin */
class DisplayOverOtherAppsFlutterPlugin :
    FlutterPlugin,
    MethodCallHandler,
    ActivityAware,
    EventChannel.StreamHandler,
    PluginRegistry.ActivityResultListener {
    private lateinit var channel: MethodChannel
    private lateinit var eventChannel: EventChannel
    private lateinit var applicationContext: Context
    private var activityBinding: ActivityPluginBinding? = null
    private var activity: Activity? = null
    private var pendingPermissionResult: Result? = null

    override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        applicationContext = flutterPluginBinding.applicationContext
        channel = MethodChannel(
            flutterPluginBinding.binaryMessenger,
            "display_over_other_apps_flutter/methods"
        )
        eventChannel = EventChannel(
            flutterPluginBinding.binaryMessenger,
            "display_over_other_apps_flutter/events"
        )
        channel.setMethodCallHandler(this)
        eventChannel.setStreamHandler(this)
    }

    override fun onMethodCall(
        call: MethodCall,
        result: Result
    ) {
        when (call.method) {
            "hasPermission" -> result.success(hasOverlayPermission())
            "requestPermission" -> requestOverlayPermission(result)
            "showOverlay" -> result.success(startOverlay())
            "closeOverlay" -> result.success(stopOverlay())
            "isOverlayRunning" -> result.success(isOverlayServiceRunning())
            else -> result.notImplemented()
        }
    }

    override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
        eventChannel.setStreamHandler(null)
    }

    override fun onAttachedToActivity(binding: ActivityPluginBinding) {
        activityBinding = binding
        activity = binding.activity
        binding.addActivityResultListener(this)
    }

    override fun onDetachedFromActivityForConfigChanges() {
        activityBinding?.removeActivityResultListener(this)
        activityBinding = null
        pendingPermissionResult?.success(false)
        pendingPermissionResult = null
        activity = null
    }

    override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
        activityBinding = binding
        activity = binding.activity
        binding.addActivityResultListener(this)
    }

    override fun onDetachedFromActivity() {
        activityBinding?.removeActivityResultListener(this)
        activityBinding = null
        pendingPermissionResult?.success(false)
        pendingPermissionResult = null
        activity = null
    }

    override fun onListen(arguments: Any?, events: EventChannel.EventSink?) {
        OverlayEventStream.setSink(events)
    }

    override fun onCancel(arguments: Any?) {
        OverlayEventStream.setSink(null)
    }

    private fun hasOverlayPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(applicationContext)
        } else {
            true
        }
    }

    private fun requestOverlayPermission(result: Result) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            result.success(true)
            return
        }
        if (pendingPermissionResult != null) {
            result.error("PERMISSION_REQUEST_IN_PROGRESS", "Overlay permission request is already in progress.", null)
            return
        }
        val hostActivity = activity
        if (hostActivity == null) {
            result.success(false)
            return
        }
        val intent = Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:${applicationContext.packageName}")
        )
        pendingPermissionResult = result
        try {
            @Suppress("DEPRECATION")
            hostActivity.startActivityForResult(intent, OVERLAY_PERMISSION_REQUEST_CODE)
        } catch (_: Exception) {
            pendingPermissionResult = null
            result.success(false)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?): Boolean {
        if (requestCode != OVERLAY_PERMISSION_REQUEST_CODE) return false
        pendingPermissionResult?.success(hasOverlayPermission())
        pendingPermissionResult = null
        return true
    }

    private fun startOverlay(): Boolean {
        if (!hasOverlayPermission()) return false
        val intent = Intent(applicationContext, DisplayOverAppsService::class.java).apply {
            action = DisplayOverAppsService.ACTION_SHOW_OVERLAY
        }
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                applicationContext.startForegroundService(intent)
            } else {
                applicationContext.startService(intent)
            }
            OverlayHolder.isRunning = true
            true
        } catch (_: Exception) {
            false
        }
    }

    private fun stopOverlay(): Boolean {
        val intent = Intent(applicationContext, DisplayOverAppsService::class.java).apply {
            action = DisplayOverAppsService.ACTION_HIDE_OVERLAY
        }
        return try {
            applicationContext.stopService(intent)
            OverlayHolder.isRunning = false
            true
        } catch (_: Exception) {
            false
        }
    }

    private fun isOverlayServiceRunning(): Boolean {
        val manager = applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        @Suppress("DEPRECATION")
        val runningServices = manager.getRunningServices(Int.MAX_VALUE)
        return runningServices.any {
            it.service.className == DisplayOverAppsService::class.java.name
        }
    }

    companion object {
        private const val OVERLAY_PERMISSION_REQUEST_CODE = 41271
    }
}
