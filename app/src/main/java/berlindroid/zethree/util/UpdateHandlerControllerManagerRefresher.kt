package berlindroid.zethree.util

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_MUTABLE
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.getSystemService
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import berlindroid.zethree.R
import berlindroid.zethree.UpdateActivity
import berlindroid.zethree.util.LowQualityUpdateDownloader.Status.*

private const val CHANNEL_ID = "app_updates"
private const val CHANNEL_NAME = "Berlindroid App Update"
private const val CHANNEL_DESCRIPTION = "The place for Berlindroid app updates."
private const val NOTIFICATION_ID = 0x01

@Suppress("UsePropertyAccessSyntax")
@SuppressLint("ObsoleteSdkInt", "UnspecifiedImmutableFlag")
class UpdateHandlerControllerManagerRefresher(
    appContext: Context,
    workerParams: WorkerParameters,
) : Worker(appContext, workerParams) {

    init {
        val notificationManager = applicationContext.getSystemService<NotificationManager>()!!
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)
                .apply { description = CHANNEL_DESCRIPTION }

            notificationManager.getNotificationChannel(CHANNEL_ID)
                ?: notificationManager.createNotificationChannel(channel)
        }
    }

    override fun doWork(): Result {
        val resolvedApk = LowQualityUpdateDownloader.resolveLatestApk()
        Log.d(
            UpdateHandlerControllerManagerRefresher::class.simpleName,
            "Finished app update check: ${resolvedApk.status}"
        )

        val intent = Intent(applicationContext, UpdateActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            FLAG_UPDATE_CURRENT or FLAG_MUTABLE
        else
            FLAG_UPDATE_CURRENT
        val pendingIntent = PendingIntent.getActivity(
            applicationContext, 0, intent, flags
        )!!
        val builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setContentTitle("Berlindroid App")
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)


        var shouldNotify = true

        val workResult = when (resolvedApk.status) {
            FAILED -> {
                builder
                    .setSmallIcon(R.drawable.ic_missed_call)
                    .setContentText("Couldn't update the app")
                    .setContentInfo(resolvedApk.errorMessage)
                    .setPriority(NotificationCompat.PRIORITY_LOW)

                Result.retry()
            }
            APK_AVAILABLE -> {
                builder
                    .setSmallIcon(R.drawable.ic_download)
                    .setContentText("There's an app update")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)

                Result.success()
            }
            ALREADY_LATEST -> {
                builder
                    .setSmallIcon(R.drawable.ic_code)
                    .setContentText("You're using the latest version")
                    .setContentInfo("GitHub Release ${resolvedApk.appVersion}")
                    .setPriority(NotificationCompat.PRIORITY_LOW)

                shouldNotify = false
                Result.success()
            }
        }

        val notification = builder.build()
        if(shouldNotify) {
            NotificationManagerCompat.from(applicationContext).notify(NOTIFICATION_ID, notification)
        }

        return workResult
    }

}