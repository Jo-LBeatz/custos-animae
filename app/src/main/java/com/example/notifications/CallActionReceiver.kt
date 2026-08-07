package com.example.notifications

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.MainActivity
import com.example.data.CallLogRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CallActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val notificationId = intent.getIntExtra("EXTRA_NOTIFICATION_ID", 1001)
        val title = intent.getStringExtra("EXTRA_CALL_TITLE") ?: "Rule of Life Call"
        val message = intent.getStringExtra("EXTRA_CALL_MESSAGE") ?: "Spiritual Rule Reminder"
        
        val serviceIntent = Intent(context, RuleOfLifeCallService::class.java).apply {
            setAction(RuleOfLifeCallService.ACTION_STOP_CALL)
            putExtra("EXTRA_NOTIFICATION_ID", notificationId)
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as? NotificationManager
        manager?.cancel(notificationId)

        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val repository = CallLogRepository.getInstance(context)
                val status = if (action == ACTION_ANSWER_CALL) "ANSWERED" else "DECLINED"
                repository.logCall(title, message, status)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                pendingResult.finish()
            }
        }

        if (action == ACTION_ANSWER_CALL) {
            val mainIntent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
                setAction(ACTION_ANSWER_CALL)
                putExtra("EXTRA_CALL_TITLE", title)
                putExtra("EXTRA_CALL_MESSAGE", message)
            }
            context.startActivity(mainIntent)
        }
    }

    companion object {
        const val ACTION_ANSWER_CALL = "com.example.ACTION_ANSWER_CALL"
        const val ACTION_DECLINE_CALL = "com.example.ACTION_DECLINE_CALL"
    }
}
