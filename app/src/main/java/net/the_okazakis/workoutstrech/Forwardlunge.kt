package net.the_okazakis.workoutstrech

import android.os.Bundle

class ForwardLunge : BaseActivity() {


    private val defaultTimes = 10
    private val defaultReps = 12  //スピード　標準10　20で半分のスピード

    private val runnable = object : Runnable {
        override fun run() {
            timeCount++

            num++
            if (extimes <= maxextimes) {
                when (num) {
                    1 -> {
                        tv.text = getString(R.string.exercise_count, extimes, maxextimes)

                        tv2.text = getString(R.string.goleft)
                        playSoundSingle(sndright)
                    }

                    2 -> {
                        tv2.text = getString(R.string.goback)
                        playSoundSingle(sndback)
                    }

                    3 ->{
                        tv2.text = getString(R.string.goright)
                        playSoundSingle(sndleft)
                    }
                    4 ->{
                        tv2.text = getString(R.string.goback)
                        playSoundSingle(sndback)
                        extimes ++
                        num = 0
                    }

                    else ->{}
                }
                // ← これ追加
                handler.postDelayed(this, speedTime)

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


        //説明文
        val myExplanation =
            "膝の筋力アップ(フロントランジ)\n胸を張って、手を腰に当て、片方の足をゆっくり前に," +
                    "大きく踏み出す。後ろの膝がつきそうなくらい腰を下ろし、" +
                    "ゆっくり元に戻る。反対の足も同じように。10回で1セット。1セット標準。" +
                    "1セットの回(秒)数が10で標準スピード　12でやや遅く　20が半分のスピード"


        val standardText = "10回で1セット。1セット標準\n1セットの回(秒)数が10で標準スピード　12でやや遅く　20が半分のスピード"
        val masxlimit = 99
        val maxRep =21
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
            setUIForSpeedStarting(runnable, -2, btnback,btnyoutube, btnChangeTimes)
        }

        btnback.setOnClickListener {
            finish()
        }
        //Youtubeのリンクを開く
        btnyoutube.setOnClickListener {
            // 固有のURLを渡すだけ
            openYoutube("https://youtu.be/yVOKHhqiXlw?t=515")
        }
        btnChangeTimes.setOnClickListener {
            // 引数なしで呼ぶだけ（必要なデータはBaseが持っているため）
            openChangeTimes(standardText, masxlimit,maxRep)
        }
        loadSettingsTick(defaultTimes, defaultReps)
        //スピードを変える
        speedTime = maxReps.toLong()*100
    }
    override fun onDestroy() {
        // soundPool.release() // サービスで共有しているため、Activityでは解放しない
        super.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        loadSettingsTick(defaultTimes, defaultReps)
        normalspeedTime = maxReps.toLong()*100
        tv.text = getString(R.string.exercise_count_fixed, maxextimes)   // ← UIも更新
    }
}