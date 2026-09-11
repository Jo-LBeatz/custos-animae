package com.example.notifications

import android.media.RingtoneManager

class IncomingCallActivity : CallOverlayActivity() {

    private var ringtoneManager: RingtoneManager? = null

    override fun startRinging() {
        ringtoneManager = RingtoneManager(this).apply {
            setType(RingtoneManager.TYPE_RINGTONE)
        }
        CallRingerManager.startRinging(this)
    }

    override fun stopRinging() {
        super.stopRinging()
        ringtoneManager = null
    }
}

