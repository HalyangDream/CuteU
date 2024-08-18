package com.amigo.tool

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object TimeUtil {


    // 将时间戳转换为日期字符串
    fun timestampToDateString(timestamp: Long, format: String): String {
        val date = Date(timestamp)
        val dateFormat = SimpleDateFormat(format, Locale.getDefault())
        return dateFormat.format(date)
    }

    // 将日期字符串转换为时间戳
    fun dateStringToTimestamp(dateString: String, format: String): Long {
        val dateFormat = SimpleDateFormat(format, Locale.getDefault())
        val date = dateFormat.parse(dateString)
        return date?.time ?: 0L
    }

    // 格式化日期字符串为不同的日期显示格式
    fun formatDateString(
        inputDateString: String,
        inputFormat: String,
        outputFormat: String
    ): String {
        val timestamp = dateStringToTimestamp(inputDateString, inputFormat)
        return timestampToDateString(timestamp, outputFormat)
    }


    fun formatSeconds(seconds: Int): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%02d:%02d", minutes, remainingSeconds)
    }

    fun formatTimestampCarryDate(timestamp: Long): String {
        val now = Calendar.getInstance()
        val targetTime = Calendar.getInstance().apply { timeInMillis = timestamp }
        val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())

        when {
            targetTime.isSameDay(now) -> {
                val hour = targetTime.get(Calendar.HOUR_OF_DAY)
                return when (hour) {
                    in 0..11 -> "AM ${dateFormat.format(targetTime.time)}"
                    else -> "PM ${dateFormat.format(targetTime.time)}"
                }
            }
            targetTime.isYesterday(now) -> return "Yesterday ${dateFormat.format(targetTime.time)}"
            targetTime.isDayBeforeYesterday(now) -> return "Day before yesterday ${dateFormat.format(targetTime.time)}"
            targetTime.isSameWeek(now) -> {
                val dayFormat = SimpleDateFormat("EEEE", Locale.getDefault())
                return "${dayFormat.format(targetTime.time)} ${dateFormat.format(targetTime.time)}"
            }
            targetTime.isSameYear(now) -> {
                val monthDayFormat = SimpleDateFormat("MM/dd", Locale.getDefault())
                return "${monthDayFormat.format(targetTime.time)} ${dateFormat.format(targetTime.time)}"
            }
            else -> {
                val yearMonthDayFormat = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
                return "${yearMonthDayFormat.format(targetTime.time)} ${dateFormat.format(targetTime.time)}"
            }
        }
    }

   private fun Calendar.isSameDay(other: Calendar): Boolean =
        this.get(Calendar.YEAR) == other.get(Calendar.YEAR) &&
                this.get(Calendar.DAY_OF_YEAR) == other.get(Calendar.DAY_OF_YEAR)

    private fun Calendar.isYesterday(other: Calendar): Boolean =
        this.get(Calendar.YEAR) == other.get(Calendar.YEAR) &&
                this.get(Calendar.DAY_OF_YEAR) == other.get(Calendar.DAY_OF_YEAR) - 1

    private fun Calendar.isDayBeforeYesterday(other: Calendar): Boolean =
        this.get(Calendar.YEAR) == other.get(Calendar.YEAR) &&
                this.get(Calendar.DAY_OF_YEAR) == other.get(Calendar.DAY_OF_YEAR) - 2

    private fun Calendar.isSameWeek(other: Calendar): Boolean =
        this.get(Calendar.YEAR) == other.get(Calendar.YEAR) &&
                this.get(Calendar.WEEK_OF_YEAR) == other.get(Calendar.WEEK_OF_YEAR)

    private fun Calendar.isSameYear(other: Calendar): Boolean =
        this.get(Calendar.YEAR) == other.get(Calendar.YEAR)
}