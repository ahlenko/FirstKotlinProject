package com.mycompany.testtask.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

class OrientationChangeReceiver : BroadcastReceiver() {

    private var listener: OrientationChangeListener? = null

    interface OrientationChangeListener {
        fun onOrientationChange(orientation: Int)
    }

    fun setListener(listener: OrientationChangeListener) {
        this.listener = listener
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "android.intent.action.CONFIGURATION_CHANGED") {
            val orientation = context?.resources?.configuration?.orientation

            if (orientation != null && listener != null) {
                listener?.onOrientationChange(orientation)
            }
        }
    }

    companion object {
        fun getIntentFilter(): IntentFilter {
            return IntentFilter("android.intent.action.CONFIGURATION_CHANGED")
        }
    }
}