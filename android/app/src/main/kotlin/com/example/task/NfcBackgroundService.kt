package com.example.task

import android.app.*
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.IBinder
import android.os.Build
import android.provider.Settings
import androidx.core.app.NotificationCompat

class NfcBackgroundService : Service() {
    private val mioIdTag = "04:43:55:0C:35:02:89"

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val tag = intent?.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
        tag?.let {
            val idRilevato = it.id.joinToString(":") { b -> "%02X".format(b) }
            if (idRilevato == mioIdTag) {
                executeSystemChanges()
            }
        }
        return START_STICKY
    }

    private fun executeSystemChanges() {
        try {
            Settings.Secure.putInt(contentResolver, Settings.Secure.LOCATION_MODE, 3)
            Settings.Global.putInt(contentResolver, "low_power", 0)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreate() {
        super.onCreate()
        val channelId = "nfc_service"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "NFC Tasker", NotificationManager.IMPORTANCE_LOW)
            getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
        }
        val notification = NotificationCompat.Builder(this, channelId)
            .setContentTitle("NFC Tasker Attivo")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
        startForeground(1, notification)
    }

    override fun onBind(intent: Intent?): IBinder? = null
}