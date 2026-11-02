package net.the_okazakis.workoutstrech

import android.os.Bundle

class HizaKakae : BaseActivity() {

    private val defaultTimes = 3
    private val defaultReps = 1

    private val runnable = object : Runnable {
        override fun run() {
            timeCount++

            num++
            if (extimes <= maxextimes) {
                when (num) {
                    1 -> {
                        tv.text = getString(R.string.set_count, extimes, maxextimes)
                        tv2.text = getString(R.string.hizakakae)
                        playSoundSingle(sndholdknee)
                    }
                    3 -> { tv2.text = getString(R.string.keep20s)
                        playSoundSingle(sndkeep20s)
                    }

                    in 5 ..24->{
                        tv2.text = getString(R.string.seconds, num - 4)
                        playSoundSingle(sounds[num-5])
                    }
                    25 -> {
                        if (count) {   //false →true 足が2回変わったら
                            extimes++   //回数を増やす
                            count = false    //
                        } else {
                            count = true
                        }
                        if (extimes <= maxextimes) {
                            tv2.text = getString(R.string.change3)
                            playSoundSingle(sndchangleg)
                            num = 0
                        }
                    }
                    else -> {}
                }

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

        val myExplanation =
            "膝抱え（腰のストレッチ）\n仰向けに寝て、片方の膝を両手で抱え込み、ゆっくり胸の方に引き寄せ20秒キープ。腰が伸びているのを感じる。反対の足も同様に。左右1回ずつで1セット。3セット標準。"

        val standardText ="左右1回ずつで1セット。\n3セット（回）標準。　最大99回"
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
            openYoutube("https://youtu.be/xzK58pHkbME?t=66")
        }


        btnChangeTimes.setOnClickListener {
            // 引数なしで呼ぶだけ（必要なデータはBaseが持っているため）
            openChangeTimes(standardText, masxlimit,maxRep)
        }

        loadSettingsTick(defaultTimes, defaultReps)

    }
    override fun onResume() {
        super.onResume()
        loadSettingsTick(defaultTimes, defaultReps)
        tv.text = getString(R.string.set_count_fixed, maxextimes)   // ← UIも更新
    }
}