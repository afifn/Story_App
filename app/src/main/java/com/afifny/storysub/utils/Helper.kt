package com.afifny.storysub.utils

import android.widget.TextView
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

fun TextView.withLocalDateFormat(timestamp: String) {
    val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
    val date = simpleDateFormat.parse(timestamp) as Date
    val formate = DateFormat.getDateInstance(DateFormat.FULL).format(date)
    this.text = formate
}