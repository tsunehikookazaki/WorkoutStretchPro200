package net.the_okazakis.workoutstrech

import android.content.Context
import java.text.SimpleDateFormat
import java.util.*
import androidx.core.content.edit

object RecordManager {

    private const val PREF_NAME = "record_prefs"
    private const val KEY_PREFIX = "record_"

    // 公開して外部で参照可能
    val dateFormat = SimpleDateFormat("yyyy.MM.dd.HH.mm.ss", Locale.JAPAN)

    data class Record(
        val activityNumber: Int,
        val activityName: String,
        val dates: List<String>
    )

    // 記録保存
    fun saveRecord(context: Context, name: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val key = KEY_PREFIX + name
        val oldData = prefs.getString(key, "") ?: ""
        val now = dateFormat.format(Date())
        val newData = if (oldData.isEmpty()) now else "$oldData\n$now"

        prefs.edit {
            putString(key, newData)
        }
    }

    // 全記録取得
    fun getRecords(context: Context): Map<String, String> {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.all
            .filterKeys { it.startsWith(KEY_PREFIX) }
            .mapKeys { it.key.removePrefix(KEY_PREFIX) }
            .mapValues { it.value.toString() }
    }

    // 全削除
    fun clearRecords(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit {
            clear()
        }
    }

    // 種目番号順に Record リスト取得
    fun getRecordsSortedByActivity(context: Context): List<Record> {
        return getRecords(context).entries
            .filter { (name, _) ->
                // 01〜24のような種目番号で始まるもの以外（strechなど）を除外
                name.length >= 2 && name.take(2).all { it.isDigit() }
            }
            .map { (name, text) ->
                val number = name.take(2).toInt()
                val activityName = name.drop(2)
                val dates = text.lines().filter { it.isNotBlank() }
                Record(
                    activityNumber = number,
                    activityName = activityName,
                    dates = dates
                )
            }.sortedBy { it.activityNumber }
    }

    // 📅 【新規追加】8日以上前の記録を削除する
    fun cleanUpOldRecords(context: Context) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        // 基準日時の設定: 7日前（丸1週間分残す）
        val sevenDaysAgoBoundary = Calendar.getInstance().apply {
            add(Calendar.DAY_OF_YEAR, -7)
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        // 現在のすべての記録を取得
        val allRecords = getRecords(context)

        // KTXの edit ブロックで一括処理（自動でapplyされます）
        prefs.edit {
            allRecords.forEach { (activityKey, datesString) ->
                val fullKey = KEY_PREFIX + activityKey

                // 日付文字列を解析し、7日以内のものだけをフィルタリング
                val recentDates = datesString.lines()
                    .filter { it.isNotBlank() }
                    .filter { dateString ->
                        // let を外し、直接 ?.after() を呼び出す
                        runCatching { dateFormat.parse(dateString) }.getOrNull()?.after(sevenDaysAgoBoundary) ?: false
                    }

                // 記録数に変化があった場合のみ更新処理を行う
                if (recentDates.size < datesString.lines().size) {
                    if (recentDates.isEmpty()) {
                        // 7日以内の記録が一つも残らなければ、キー自体を削除
                        remove(fullKey)
                    } else {
                        // 7日以内の記録が残っていれば、そのリストで更新
                        putString(fullKey, recentDates.joinToString("\n"))
                    }
                }
            }

            // 誤って作成された可能性のあるキーを削除（存在しない場合は何も起きません）
            remove("record_strech")
        }
    }
}