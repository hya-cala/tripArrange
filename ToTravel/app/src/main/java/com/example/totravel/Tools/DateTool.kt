package com.example.totravel.Tools

import java.text.SimpleDateFormat
import java.util.*

class DateTool {
    companion object {
        fun dateToString(date: Date): String {
            val formater = SimpleDateFormat("dd MMM, yyyy", Locale.US)
            return formater.format(date)
        }

        fun stringToDate(str: String): Date? {
            val formater = SimpleDateFormat("dd MMM, yyyy", Locale.US)
            return formater.parse(str)
        }
    }
}