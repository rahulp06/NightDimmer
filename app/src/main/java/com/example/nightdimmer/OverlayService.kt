package com.example.nightdimmer

import android.app.Service
import android.content.Intent
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.IBinder
import android.view.View
import android.view.WindowManager

class OverlayService : Service() {

    companion object {
        var currentAlpha = 150
    }

    private lateinit var windowManager: WindowManager
    private lateinit var overlayView: View

    override fun onCreate() {
        super.onCreate()

        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager

        overlayView = View(this)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.TRANSLUCENT
        )

        overlayView.setBackgroundColor(Color.argb(currentAlpha, 0, 0, 0))
        windowManager.addView(overlayView, params)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (intent?.getBooleanExtra("stop", false) == true) {
            stopSelf()
            return START_NOT_STICKY
        }

        val alpha = intent?.getIntExtra("alpha", currentAlpha) ?: currentAlpha
        val isNight = intent?.getBooleanExtra("night", false) ?: false

        currentAlpha = alpha

        if (isNight) {
            overlayView.setBackgroundColor(Color.argb(alpha, 255, 180, 80))
        } else {
            overlayView.setBackgroundColor(Color.argb(alpha, 0, 0, 0))
        }

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        windowManager.removeView(overlayView)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}