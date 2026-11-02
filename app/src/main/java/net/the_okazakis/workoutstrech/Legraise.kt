package net.the_okazakis.workoutstrech

import android.os.Bundle
class LegRaise : BaseActivity() {

    private val defaultTimes = 3
    private val defaultReps = 10

    private val runnable = object : Runnable {
        override fun run() {
            timeCount++
            num++
            if (extimes <= maxextimes) {

                tv.text = getString(R.string.set_count, extimes, maxextimes)

                when (num) {
                    in -10..-4 ->{
                        if(!isStart){
                            tv2.text = getString(R.string.number_only, num * (-1))
                        }
                    }
                    in -3..-1 ->{

                        if(!isStart){
                            tv2.text = getString(R.string.number_only, num * (-1))
                            playSoundSingle(sounds[num*(-1)-1])
                        }
                    }
                    0 ->{
                        if(!isStart){
                            tv2.text = getString(R.string.number_only, num * (-1))
                            playSoundSingle(sndpi)
                        }
                    }

                    in 1..20 -> {

                        if(isUp) {
                            nn++
                            tv2.text = getString(R.string.action_with_count, getString(R.string.up), nn)
                            playSoundSingle(sndup)
                            isUp = false
                        }else{
                            tv2.text = getString(R.string.action_with_count, getString(R.string.down), nn)
                            playSoundSingle(snddown)
                            isUp = true

                        }
                    }
                    21 -> {

                        if (nn >=10) {
                            num = -(11) // 10s relax
                            nn=0
                            extimes++
                            if(extimes <= maxextimes) {
                                tv2.text = getString(R.string.relax10)
                                playSoundSingle(snd10re)
                                isStart = false
                            }
                        }

                    }
                    else -> {}
                }
                // ← これ追加
                handler.postDelayed(this, 1000)
            } else {
                handleTrainingComplete(tv2, btnback, btnChangeTimes, btnyoutube) {
                    isSaved = true
                    playSoundSingle(send)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_sub)

        val myExplanation =
            "仰向けに寝て、手は身体の横に置く\n足はなるべく伸ばす（きつい場合は曲げてもよい.）\n足をそろえてゆっくり上げて(出来れば90度位に)ゆっくりおろす\n" +
                    "下したとき足は床に付けない。\n\n10回で1セット、3セットが標準"

        val standardText ="10回で1セット。\n3セット（回）標準。"
        val masxlimit = 99
        val maxRep =30

        // すべての共通初期化を実行
        initializeStandardSettings(myExplanation)

        // 音声をロード
        loadAllStandardSounds()

        // 各種クリックリスナー
        btnstart.setOnClickListener {
            setUIForStarting(runnable,-2,btnback, btnChangeTimes, btnyoutube)
        }


        btnstop.setOnClickListener {
            setUIForStopping(btnback, btnChangeTimes, btnyoutube)
            handler.removeCallbacks(runnable)
        }

        btnrestart.setOnClickListener {
            restartTraining(runnable,btnback,btnChangeTimes,btnyoutube)
        }

        btnspeed.setOnClickListener {
            setUIForSpeedStarting(runnable, -3, btnback,btnyoutube, btnChangeTimes)
        }

        btnback.setOnClickListener {
            finish()
        }

        btnyoutube.setOnClickListener {
            openYoutube("https://youtu.be/JmG5MLaDS38")
        }
        btnChangeTimes.setOnClickListener {
            // 引数なしで呼ぶだけ（必要なデータはBaseが持っているため）
            openChangeTimes(standardText, masxlimit,maxRep)
        }

        loadSettingsTick(defaultTimes, defaultReps)

    }
    override fun onDestroy() {
        // soundPool.release() // サービスで共有しているため、Activityでは解放しない
        super.onDestroy()
    }

    // 👇ここに書く（onCreateの下）
    override fun onResume() {
        super.onResume()
        loadSettingsTick(defaultTimes, defaultReps)
        tv.text = getString(R.string.exercise_count_fixed, maxextimes)   // ← UIも更新
    }
}