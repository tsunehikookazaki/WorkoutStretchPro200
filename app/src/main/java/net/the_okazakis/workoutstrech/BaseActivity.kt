package net.the_okazakis.workoutstrech

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.media.SoundPool
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {

    protected lateinit var tv: TextView
    protected lateinit var tv2: TextView
    protected lateinit var textmenu: TextView
    protected lateinit var tvexpla: TextView
    protected lateinit var btnback: Button
    protected lateinit var btnstart: Button
    protected lateinit var btnstop: Button
    protected lateinit var btnrestart: Button
    protected lateinit var btnChangeTimes: Button
    protected lateinit var btnspeed: Button
    protected lateinit var btnyoutube: ImageButton

    // --- サービス関連 ---
    protected var workoutService: WorkoutService? = null
    protected var isBound = false

    // サービスから取得したSoundPoolを保持する
    protected var soundPool: SoundPool? = null

    private val connection = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as WorkoutService.LocalBinder
            workoutService = binder.getService()
            soundPool = workoutService?.getSoundPool()
            isBound = true

            // 接続されたら音源をロードする
            loadAllStandardSounds()

            // サブクラスで追加の処理が必要な場合（オーバーライド用）
            onServiceConnected()
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            isBound = false
            workoutService = null
            soundPool = null
        }
    }

    /**
     * サービス接続時に追加の処理を行いたい場合にサブクラスでオーバーライドする
     */
    open fun onServiceConnected() {}

    // --- 共通の変数 ---
    protected val handler = Handler(Looper.getMainLooper())
    protected var workoutId = 0
    protected var timeCount = 0
    protected var workmenu: String = ""
    protected var countVolume: Float = 1.0f
    protected var maxextimes = 2
    protected var maxReps = 5
    protected var extimes = 0
    protected var num = 0
    protected var nn = 0
    protected var speedTime = 1000L
    protected var normalspeedTime = 1000L
    protected var speedspeedTime = 700L
    protected var isSpeed = false
    protected var count = false
    protected var isSaved = false
    protected var isUp = true
    protected var choki = true
    protected var isStart = true

    // --- 共通のサウンドID ---
    protected var sndstr = 0
    protected var send = 0
    protected var sndup = 0
    protected var snddown = 0
    protected var sndchangleg = 0
    protected var sndstand = 0
    protected var sndsit = 0
    protected var sndgo = 0
    protected var sndchoki = 0
    protected var sndurachoki = 0
    protected var sndpa = 0
    protected var sndstre = 0
    protected var sndbent = 0
    protected var sndnon = 0
    protected var sndtaoshite = 0
    protected var sndkeep30s = 0
    protected var sndkeep = 0
    protected var sndloosen = 0
    protected var sndtsubu = 0
    protected var sndmodo = 0
    protected var sndkeep3s = 0
    protected var sndkaeteagete = 0
    protected var sndbreak = 0
    protected var sndright = 0
    protected var sndleft = 0
    protected var sndback = 0
    protected var sndkeep1s = 0
    protected var sndtsubushite = 0
    protected var snd10re = 0
    protected var sndkeep20s = 0
    protected var sndpi = 0
    protected var sndtoesup = 0
    protected var sndstandtoes = 0
    protected var snddropdown = 0
    protected var sndslowup = 0
    protected var sndslowdown = 0
    protected var sndkeep5 = 0
    protected var sndsizunde = 0
    protected var sndtaete = 0
    protected var sndhikitsuke = 0
    protected var sndslowopen = 0
    protected var sndslowclose = 0
    protected var sndkeepmama = 0
    protected var sndopen = 0
    protected var sndholdknee = 0
    protected var sndstretch = 0
    protected var sndbend = 0

    protected val sounds: MutableList<Int> = mutableListOf()
    protected val soundskeep: MutableList<Int> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // サービスにバインド
        val intent = Intent(this, WorkoutService::class.java)
        bindService(intent, connection, BIND_AUTO_CREATE)
    }

    protected fun loadAllStandardSounds() {
        val sp = soundPool ?: return

        sndstr = sp.load(this, R.raw.start, 1)
        send = sp.load(this, R.raw.goodjob, 1)
        sndup = sp.load(this, R.raw.up, 1)
        sndchangleg = sp.load(this, R.raw.changeleg, 1)
        sndstand = sp.load(this, R.raw.slowstand, 1)
        sndsit = sp.load(this, R.raw.sitslow, 1)
        sndgo = sp.load(this, R.raw.gu, 1)
        sndchoki = sp.load(this, R.raw.choki, 1)
        sndurachoki = sp.load(this, R.raw.urachoki, 1)
        sndpa = sp.load(this, R.raw.pa, 1)
        sndstre = sp.load(this, R.raw.stretch, 1)
        sndbent = sp.load(this, R.raw.bend, 1)
        sndnon = sp.load(this, R.raw.nosound, 1)
        sndtaoshite = sp.load(this, R.raw.taoshite, 1)
        sndkeep30s = sp.load(this, R.raw.keep30s, 1)
        sndkeep = sp.load(this, R.raw.v10, 1)
        sndloosen = sp.load(this, R.raw.loosen, 1)
        sndtsubu = sp.load(this, R.raw.tsubushite, 1)
        sndmodo = sp.load(this, R.raw.modo, 1)
        sndkeep3s = sp.load(this, R.raw.keep3s, 1)
        sndkaeteagete = sp.load(this, R.raw.kaeteagete, 1)
        sndbreak = sp.load(this, R.raw.takeabreak, 1)
        sndright = sp.load(this, R.raw.leftlegforward, 1)
        sndleft = sp.load(this, R.raw.rightlegforward, 1)
        sndback = sp.load(this, R.raw.goback, 1)
        sndopen = sp.load(this, R.raw.open, 1)
        sndslowclose = sp.load(this, R.raw.slowclose, 1)
        sndkeep1s = sp.load(this, R.raw.keep1sec, 1)
        sndtsubushite = sp.load(this, R.raw.tsubushite, 1)
        snd10re = sp.load(this, R.raw.relax10sec, 1)
        snddown = sp.load(this, R.raw.down, 1)
        sndkeep20s = sp.load(this, R.raw.keep20s, 1)
        sndpi = sp.load(this, R.raw.pi, 1)
        sndtoesup = sp.load(this, R.raw.toesup, 1)
        sndstandtoes = sp.load(this, R.raw.standtoes, 1)
        snddropdown = sp.load(this, R.raw.dropdown, 1)
        sndslowup = sp.load(this, R.raw.slowup, 1)
        sndslowdown = sp.load(this, R.raw.slowdown, 1)
        sndkeep5 = sp.load(this, R.raw.keep5sec, 1)
        sndsizunde = sp.load(this, R.raw.sizunde, 1)
        sndtaete = sp.load(this, R.raw.taete, 1)
        sndhikitsuke = sp.load(this, R.raw.hikitsuke, 1)
        sndslowopen = sp.load(this, R.raw.slowopen, 1)
        sndkeepmama = sp.load(this, R.raw.keepmama, 1)
        sndholdknee = sp.load(this, R.raw.holdknee, 1)
        sndstretch = sp.load(this, R.raw.stretch, 1)
        sndbend = sp.load(this, R.raw.bend, 1)

        sounds.clear()

        // 1から30までのリソースIDを直接リストにする
        val rawResources = listOf(
            R.raw.v1,  R.raw.v2,  R.raw.v3,  R.raw.v4,  R.raw.v5,
            R.raw.v6,  R.raw.v7,  R.raw.v8,  R.raw.v9,  R.raw.v10,
            R.raw.v11, R.raw.v12, R.raw.v13, R.raw.v14, R.raw.v15,
            R.raw.v16, R.raw.v17, R.raw.v18, R.raw.v19, R.raw.v20,
            R.raw.v21, R.raw.v22, R.raw.v23, R.raw.v24, R.raw.v25,
            R.raw.v26, R.raw.v27, R.raw.v28, R.raw.v29, R.raw.v30
        )

        for (resId in rawResources) {
            // リソースIDが直接取れているため、0チェックは不要（コンパイル時点で存在が保証される）
            sounds.add(sp.load(this, resId, 1))
        }
        soundskeep.clear()

// 1sから30sまでのリソースIDを直接リスト化する
        val keepResources = listOf(
            R.raw.keep1s,  R.raw.keep2s,  R.raw.keep3s,  R.raw.keep4s,  R.raw.keep5s,
            R.raw.keep6s,  R.raw.keep7s,  R.raw.keep8s,  R.raw.keep9s,  R.raw.keep10s,
            )

        for (resId in keepResources) {
            soundskeep.add(sp.load(this, resId, 1))
        }
    }

    /**
    protected fun loadCommonSounds(countResIds: List<Int>) {
        val sp = soundPool ?: return
        sndstr = sp.load(this, R.raw.start, 1)
        send = sp.load(this, R.raw.goodjob, 1)
        sndup = sp.load(this, R.raw.up, 1)
        sndchangleg = sp.load(this, R.raw.changeleg, 1)

        sounds.clear()
        countResIds.forEach { resId ->
            sounds.add(sp.load(this, resId, 1))
        }
    }
**/
    protected fun initializeStandardSettings(explanation: String, dTimes: Int = 2, dReps: Int = 15) {
        countVolume = intent.getFloatExtra("TEXT_KEY", 1.0f)
        workoutId = intent.getIntExtra("TEXT_KEY2", 0)
        val menuArray = resources.getStringArray(R.array.lv_menu)
        workmenu = menuArray.getOrElse(workoutId) { "" }

        setupActivityUI(workmenu, explanation)
        loadSettingsTick(dTimes, dReps)
        updateHeaderUI()
    }

    protected fun openYoutube(youtubeUrl: String) {
        val intentY = Intent(this, Youtube::class.java)
        intentY.putExtra("yID", youtubeUrl)
        startActivity(intentY)
    }

    protected fun openChangeTimes(stdText: String, maxLimit: Int, maxRepsLimit: Int) {
        val intentC = Intent(this, MainActivity2::class.java)
        intentC.putExtra("TEXT_KEY4", workmenu)
        intentC.putExtra("TEXT_KEY5", workoutId)
        intentC.putExtra("STD_TEXT", stdText)
        intentC.putExtra("MAX_LIMIT", maxLimit)
        intentC.putExtra("MAX_REPS_LIMIT", maxRepsLimit)
        // 👇 追加：現在のコード上の初期値を送る
        intentC.putExtra("DEFAULT_TIMES", maxextimes)
        intentC.putExtra("DEFAULT_REPS", maxReps)
        startActivity(intentC)
    }

    private fun setupActivityUI(title: String, explanation: String) {
        tv = findViewById(R.id.tv)
        tv2 = findViewById(R.id.tv2)
        textmenu = findViewById(R.id.textmenu)
        tvexpla = findViewById(R.id.tvexpla)
        btnback = findViewById(R.id.btnback)
        btnstart = findViewById(R.id.btStart)
        btnstop = findViewById(R.id.btStop)
        btnrestart = findViewById(R.id.btnrestart)
        btnyoutube = findViewById(R.id.youtube)
        btnChangeTimes = findViewById(R.id.button2)
        btnspeed = findViewById(R.id.btspeed)

        textmenu.text = title
        tvexpla.text = explanation
        btnstop.isEnabled = false
        btnrestart.isEnabled = false

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        supportActionBar?.hide()
    }

    protected fun updateHeaderUI() {
        if (::tv.isInitialized) {
            val isSetUnit = workmenu.contains("伸ばし") || workmenu.contains("キープ") || workmenu.contains("腓腹筋") || workmenu.contains("セット") || workmenu.contains("潰し")
            val resId = if (isSetUnit) R.string.set_count_fixed else R.string.exercise_count_fixed
            tv.text = getString(resId, maxextimes)
        }
    }




    protected fun playSoundSingle(soundId: Int) {
        if (soundId != 0) {
           workoutService?.playSound(soundId, countVolume)
        }
    }

    open fun loadSettingsTick(dTimes: Int = 2, dReps: Int = 15) {
        val pref = getSharedPreferences("WorkoutSettings", MODE_PRIVATE)
        // SharedPreferencesに値がない場合のみ、コード上の初期値(dTimes)を使う
        maxextimes = pref.getInt("times_$workoutId", dTimes)
        maxReps = pref.getInt("reps_$workoutId", dReps)
    }
/**
    protected fun getPreferenceSettings(workoutId: Int, defaultTimes: Int = 2, defaultReps: Int = 15): Pair<Int, Int> {
        val pref = getSharedPreferences("WorkoutSettings", MODE_PRIVATE)
        val times = pref.getInt("times_$workoutId", defaultTimes)
        val reps = pref.getInt("reps_$workoutId", defaultReps)
        return Pair(times, reps)
    }
    **/

    protected fun setUIForStarting(runnable: Runnable, startNum: Int, vararg otherButtons: View) {
        btnstart.isEnabled = false
        btnstop.isEnabled = true
        btnrestart.isEnabled = false
        btnspeed.isEnabled = false
        otherButtons.forEach { it.isEnabled = false }

        isSaved = false
        isStart = true
        isSpeed = false
        extimes = 1
        nn = 0
        timeCount = 0
        num = startNum
        speedTime = normalspeedTime

        // フォアグラウンドサービス開始
        workoutService?.startForegroundService(workmenu)

        playSoundSingle(sndstr)
        tv2.text = getString(R.string.let_us_start)
        handler.removeCallbacks(runnable)
        handler.post(runnable)
    }

    protected fun restartTraining(runnable: Runnable, vararg otherButtons: View) {
        btnstart.isEnabled = false
        btnstop.isEnabled = true
        btnrestart.isEnabled = false
        btnspeed.isEnabled = false
        otherButtons.forEach { it.isEnabled = false }

        workoutService?.startForegroundService(workmenu)
        handler.removeCallbacks(runnable)
        handler.post(runnable)
    }

    protected fun setUIForStopping(vararg otherButtons: View) {
        btnstart.isEnabled = true
        btnstop.isEnabled = false
        btnrestart.isEnabled = true
        btnspeed.isEnabled = true
        otherButtons.forEach { it.isEnabled = true }

        handler.removeCallbacksAndMessages(null)
        workoutService?.stopForegroundService()
    }

    protected fun setUIForSpeedStarting(runnable: Runnable, startNum: Int, vararg otherButtons: View) {
        btnstart.isEnabled = false
        btnstop.isEnabled = true
        btnrestart.isEnabled = false
        btnspeed.isEnabled = false
        otherButtons.forEach { it.isEnabled = false }

        isSaved = false
        isStart = true
        isSpeed = true
        extimes = 1
        nn = 0
        timeCount = 0
        num = startNum

        workoutService?.startForegroundService(workmenu)

        playSoundSingle(sndstr)
        tv2.text = getString(R.string.let_us_start)
        speedTime = speedspeedTime
        handler.removeCallbacks(runnable)
        handler.post(runnable)
    }

    protected fun handleTrainingComplete(tvMessage: TextView, vararg otherButtons: View, onSaveComplete: () -> Unit) {
        btnstart.isEnabled = true
        btnstop.isEnabled = false
        btnrestart.isEnabled = false
        otherButtons.forEach { it.isEnabled = true }

        workoutService?.stopForegroundService()

        if (!isSaved) {
            RecordManager.saveRecord(this, "%02d%s".format(workoutId, workmenu))
            SharedRecordManager.updateStats(this, "strech", workmenu)
            isSaved = true
            tvMessage.text = getString(R.string.good_job)
            onSaveComplete()
        }
    }

    override fun onDestroy() {
        if (isBound) {
            unbindService(connection)
            isBound = false
        }
        super.onDestroy()
    }
    // --- onResume を追加（設定変更を即座に反映させるため） ---
    override fun onResume() {
        super.onResume()
        // 画面が戻ってきたときに、最新の回数設定を読み込む
        loadSettingsTick()
        // 画面上部の「1/2 回」などの表示も更新する
        updateHeaderUI()
    }
}
