package com.example.task

import android.app.PendingIntent
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.MethodChannel

class MainActivity: FlutterActivity() {
    private val CHANNEL = "com.example.task/system_control"
    private var nfcAdapter: NfcAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Esegue i comandi se l'app viene aperta da un Intent NFC
        if (intent != null && isNfcIntent(intent)) {
            executeSystemChanges()
        }

        val serviceIntent = Intent(this, NfcBackgroundService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)
        nfcAdapter = NfcAdapter.getDefaultAdapter(this)
        MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).setMethodCallHandler { call, result ->
            if (call.method == "enableSystemFeatures") {
                val success = executeSystemChanges()
                if (success) result.success(null) else result.error("ERR", "Fallito", null)
            } else {
                result.notImplemented()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (isNfcIntent(intent)) {
            executeSystemChanges()
        }
    }

    private fun isNfcIntent(intent: Intent): Boolean {
        val action = intent.action
        return NfcAdapter.ACTION_TAG_DISCOVERED == action || 
               NfcAdapter.ACTION_TECH_DISCOVERED == action || 
               NfcAdapter.ACTION_NDEF_DISCOVERED == action
    }

    private fun executeSystemChanges(): Boolean {
        return try {
            Settings.Secure.putInt(contentResolver, Settings.Secure.LOCATION_MODE, 3)
            Settings.Global.putInt(contentResolver, "low_power", 0)
            true
        } catch (e: Exception) {
            false
        }
    }
}