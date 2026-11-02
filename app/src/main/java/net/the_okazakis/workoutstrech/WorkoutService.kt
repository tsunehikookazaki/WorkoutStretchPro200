package net.the_okazakis.workoutstrech

import android.app.*
import android.content.Intent
import android.media.AudioAttributes
import android.media.SoundPool
import android.os.*
import androidx.core.app.NotificationCompat
import android.content.pm.ServiceInfo
import androidx.core.app.ServiceCompat

class WorkoutService : Service() {

    private val binder = LocalBinder()
    private var soundPool: SoundPool? = null

    inner class LocalBinder : Binder() {
        fun getService(): WorkoutService = this@WorkoutService
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onCreate() {
        super.onCreate()
        val aa = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA) // USAGE_ASSISTANCE_SONIFICATION から変更
            .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
            .build()
        soundPool = SoundPool.Builder()
            .setAudioAttributes(aa)
            .setMaxStreams(5)
            .build()
        
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Workout Service Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    fun startForegroundService(workoutName: String) {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("ワークアウト中")
            .setContentText(workoutName)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            .build()

        // 👇 Android 14以降に対応した安全な書き方に修正
        // 👇 OSのバージョンによって開始方法を分ける
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Android 10 (API 29) 以上の最新端末用
            ServiceCompat.startForeground(
                this,
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
            )
        } else {
            // Android 9 (API 28) 以下の古い端末用
            startForeground(NOTIFICATION_ID, notification)
        }
    }

    fun stopForegroundService() {
        stopForeground(STOP_FOREGROUND_REMOVE)
        // stopSelf() // Don't stop self immediately, keep bound
    }

    fun getSoundPool(): SoundPool? = soundPool

    fun playSound(soundId: Int, volume: Float) {
        soundPool?.play(soundId, volume, volume, 1, 0, 1.0f)
    }

    override fun onDestroy() {
        soundPool?.release()
        soundPool = null
        super.onDestroy()
    }

    companion object {
        const val CHANNEL_ID = "WorkoutServiceChannel"
        const val NOTIFICATION_ID = 1
    }
}
