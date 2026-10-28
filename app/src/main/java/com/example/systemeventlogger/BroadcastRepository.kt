package com.example.systemeventlogger

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.core.content.ContextCompat
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class BroadcastRepository(private val context: Context) {

    val systemEventsFlow: Flow<BroadcastEvent> = callbackFlow {
        // BroadcastReceiver
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val action = intent?.action ?: return

                // поточний час
                val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
                val timeString = timeFormat.format(Date())

                val details = when (action) {
                    Intent.ACTION_AIRPLANE_MODE_CHANGED -> {
                        val state = intent.getBooleanExtra("state", false)
                        "режим 'у літаку' ${if (state) "увімкнено" else "вимкнено"}"
                    }

                    Intent.ACTION_POWER_CONNECTED -> "пристрій підключено до джерела живлення"
                    Intent.ACTION_POWER_DISCONNECTED -> "пристрій відключено від живлення"
                    Intent.ACTION_BATTERY_LOW -> "критично низький рівень заряду батареї"
                    else -> "додаткових деталей немає"
                }

                val event = BroadcastEvent(
                    type = action,
                    timestamp = timeString,
                    details = details
                )

                trySend(event)
            }
        }

        // вимога Android 14+
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED)
            addAction(Intent.ACTION_POWER_CONNECTED)
            addAction(Intent.ACTION_POWER_DISCONNECTED)
            addAction(Intent.ACTION_BATTERY_LOW)
        }

        ContextCompat.registerReceiver(
            context,
            receiver,
            filter,
            ContextCompat.RECEIVER_NOT_EXPORTED
        )

        awaitClose {
            context.unregisterReceiver(receiver)
        }
    }
}