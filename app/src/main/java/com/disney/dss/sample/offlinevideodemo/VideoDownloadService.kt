package com.disney.dss.sample.offlinevideodemo

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.google.android.exoplayer2.offline.DownloadAction
import com.google.android.exoplayer2.offline.DownloadManager
import com.google.android.exoplayer2.offline.DownloadService
import com.google.android.exoplayer2.scheduler.PlatformScheduler
import com.google.android.exoplayer2.scheduler.Requirements

class VideoDownloadService : DownloadService(0) {

    override fun getDownloadManager() = offlineVideoApplication.downloadManager

    override fun getForegroundNotification(taskStates: Array<out DownloadManager.TaskState>?): Notification {
        Log.d("VideoDownloadService", "Creating notification")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(NotificationChannel(CHANNEL_ID, "Downloads", NotificationManager.IMPORTANCE_HIGH))
        }
        return NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Downloading..")
                .setContentText(taskStates?.joinToString("-") { it.action.uri.toString() }
                        ?: "NO_DOWNLOAD")
                .build()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d("VideoDownloadService", "Start action: ${intent.action}")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun getScheduler() = PlatformScheduler(this, R.id.action0)

    override fun getRequirements() = Requirements(Requirements.NETWORK_TYPE_UNMETERED, false, false)

    companion object {
        const val CHANNEL_ID = "downloads"

        fun startWithAction(context: Context, action: DownloadAction) =
                DownloadService.startWithAction(context, VideoDownloadService::class.java, action, true)
    }
}
