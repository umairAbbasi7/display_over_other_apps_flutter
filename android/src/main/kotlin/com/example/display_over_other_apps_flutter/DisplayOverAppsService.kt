package com.umairabbasi.display_over_other_apps_flutter

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.IBinder
import android.util.TypedValue
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.core.app.NotificationCompat

class DisplayOverAppsService : Service() {
    private var windowManager: WindowManager? = null
    private var overlayView: View? = null
    private var closeTargetView: View? = null
    private var isCloseTargetAttached = false
    private var isViewAttached = false

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        OverlayHolder.isRunning = true
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification())
        showOverlay()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_SHOW_OVERLAY -> showOverlay()
            ACTION_HIDE_OVERLAY -> stopSelf()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        OverlayHolder.isRunning = false
        removeOverlay()
        OverlayEventStream.send("dismissed")
        super.onDestroy()
    }

    private fun showOverlay() {
        if (isViewAttached) return

        val container = FrameLayout(this)
        val bubble = FrameLayout(this)
        val icon = ImageView(this)
        icon.setImageResource(applicationInfo.icon)
        icon.scaleType = ImageView.ScaleType.CENTER_CROP

        val bg = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(0xCC000000.toInt())
        }
        bubble.background = bg

        val sizePx = dpToPx(68f)
        val bubbleSizePx = dpToPx(56f)
        val iconSizePx = dpToPx(46f)
        val paddingPx = dpToPx(2f)

        val iconParams = FrameLayout.LayoutParams(iconSizePx, iconSizePx).apply {
            gravity = Gravity.CENTER
        }
        bubble.setPadding(paddingPx, paddingPx, paddingPx, paddingPx)
        bubble.addView(icon, iconParams)

        val bubbleParams = FrameLayout.LayoutParams(bubbleSizePx, bubbleSizePx).apply {
            gravity = Gravity.CENTER
        }
        container.addView(bubble, bubbleParams)

        val layoutParams = WindowManager.LayoutParams(
            sizePx,
            sizePx,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE
            },
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT
        )

        layoutParams.gravity = Gravity.TOP or Gravity.END
        layoutParams.x = dpToPx(16f)
        layoutParams.y = dpToPx(180f)

        setupTouchHandling(container, layoutParams)
        overlayView = container
        windowManager?.addView(container, layoutParams)
        isViewAttached = true
    }

    private fun setupTouchHandling(view: View, params: WindowManager.LayoutParams) {
        var initialX = 0
        var initialY = 0
        var initialTouchX = 0f
        var initialTouchY = 0f
        var moved = false
        var closeByDrag = false

        view.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    moved = false
                    closeByDrag = false
                    initialX = params.x
                    initialY = params.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    showCloseTarget()
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = (event.rawX - initialTouchX).toInt()
                    val dy = (event.rawY - initialTouchY).toInt()
                    if (!moved && (kotlin.math.abs(dx) > 8 || kotlin.math.abs(dy) > 8)) {
                        moved = true
                    }
                    params.x = initialX - dx
                    params.y = initialY + dy
                    windowManager?.updateViewLayout(view, params)
                    closeByDrag = isOverCloseTarget(event.rawX, event.rawY)
                    updateCloseTargetHighlight(closeByDrag)
                    true
                }
                MotionEvent.ACTION_UP -> {
                    hideCloseTarget()
                    if (closeByDrag) {
                        stopSelf()
                        return@setOnTouchListener true
                    }
                    if (!moved) {
                        openMainApp()
                        OverlayEventStream.send("tap")
                    }
                    true
                }
                MotionEvent.ACTION_CANCEL -> {
                    hideCloseTarget()
                    true
                }
                else -> false
            }
        }
    }

    private fun showCloseTarget() {
        if (isCloseTargetAttached) return
        val target = FrameLayout(this).apply {
            alpha = 0.95f
            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(Color.parseColor("#CC1A1A1A"))
            }
        }
        val icon = ImageView(this).apply {
            setImageResource(android.R.drawable.ic_menu_close_clear_cancel)
            setColorFilter(Color.WHITE)
        }
        val iconParams = FrameLayout.LayoutParams(dpToPx(26f), dpToPx(26f)).apply {
            gravity = Gravity.CENTER
        }
        target.addView(icon, iconParams)
        val targetParams = WindowManager.LayoutParams(
            dpToPx(72f),
            dpToPx(72f),
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            } else {
                @Suppress("DEPRECATION")
                WindowManager.LayoutParams.TYPE_PHONE
            },
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            y = dpToPx(36f)
        }
        closeTargetView = target
        windowManager?.addView(target, targetParams)
        isCloseTargetAttached = true
    }

    private fun hideCloseTarget() {
        if (!isCloseTargetAttached) return
        closeTargetView?.let {
            try {
                windowManager?.removeView(it)
            } catch (_: Exception) {
            }
        }
        closeTargetView = null
        isCloseTargetAttached = false
    }

    private fun updateCloseTargetHighlight(active: Boolean) {
        val target = closeTargetView as? FrameLayout ?: return
        val color = if (active) "#E53935" else "#CC1A1A1A"
        (target.background as? GradientDrawable)?.setColor(Color.parseColor(color))
    }

    private fun isOverCloseTarget(rawX: Float, rawY: Float): Boolean {
        val target = closeTargetView ?: return false
        if (!isCloseTargetAttached) return false
        val location = IntArray(2)
        target.getLocationOnScreen(location)
        val rect = Rect(
            location[0],
            location[1],
            location[0] + target.width,
            location[1] + target.height
        )
        return rect.contains(rawX.toInt(), rawY.toInt())
    }

    private fun openMainApp() {
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName) ?: return
        launchIntent.addFlags(
            Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_SINGLE_TOP or
                Intent.FLAG_ACTIVITY_CLEAR_TOP
        )
        startActivity(launchIntent)
    }

    private fun removeOverlay() {
        hideCloseTarget()
        if (!isViewAttached) return
        overlayView?.let {
            try {
                windowManager?.removeView(it)
            } catch (_: Exception) {
            }
        }
        overlayView = null
        isViewAttached = false
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = getSystemService(NotificationManager::class.java)
        if (manager.getNotificationChannel(CHANNEL_ID) != null) return
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Display Over Other Apps",
            NotificationManager.IMPORTANCE_LOW
        )
        manager.createNotificationChannel(channel)
    }

    private fun buildNotification(): Notification {
        val openIntent = packageManager.getLaunchIntentForPackage(packageName)
        val pendingIntent = PendingIntent.getActivity(
            this,
            1001,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or pendingIntentFlagMutable()
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Overlay active")
            .setContentText("Tap floating icon to return quickly")
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun pendingIntentFlagMutable(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.FLAG_MUTABLE
        } else {
            0
        }
    }

    private fun dpToPx(dp: Float): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            resources.displayMetrics
        ).toInt()
    }

    companion object {
        const val ACTION_SHOW_OVERLAY = "display.over.apps.SHOW"
        const val ACTION_HIDE_OVERLAY = "display.over.apps.HIDE"
        private const val CHANNEL_ID = "display_over_other_apps_channel"
        private const val NOTIFICATION_ID = 9162
    }
}
