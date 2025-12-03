package com.gbr.work

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.WorkManager
import java.util.UUID

class CancelDownloadReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val workIdString = intent.getStringExtra("work_id") ?: return
        val workId = UUID.fromString(workIdString)
        WorkManager.getInstance(context).cancelWorkById(workId)
    }
}

