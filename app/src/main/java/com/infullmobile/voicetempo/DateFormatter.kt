package com.infullmobile.voicetempo

import java.text.SimpleDateFormat
import java.util.*

open class DateFormatter {

    companion object {
        private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        private val sdfTo = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.ENGLISH)

        fun convertToSendableDate(year: Int, month: Int, day: Int): String {
            val strDate = "$year-$month-$day"
            val date = sdf.parse(strDate)
            return sdfTo.format(date)
        }
    }
}
