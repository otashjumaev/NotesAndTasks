package org.hiro.noteapp.util

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.text.Html
import android.text.Spanned
import android.view.inputmethod.InputMethodManager
import java.text.SimpleDateFormat
import java.util.*

fun hideKeyboard(activity: Activity) {
    val inputMethodManager =
        activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    activity.currentFocus?.let {
        inputMethodManager.hideSoftInputFromWindow(
            it.windowToken, InputMethodManager.HIDE_NOT_ALWAYS
        )
    }
}


fun toHtml(text: Spanned?): String =
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
        Html.toHtml(text, 0)
    else Html.toHtml(text)


fun fromHtml(text: String): Spanned? =
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
        Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY)
    else Html.fromHtml(text)

@SuppressLint("SimpleDateFormat")
fun formatTime(time: Long): String = SimpleDateFormat("MMM dd',' HH:mm").format(Date(time))

fun colors(): List<String> {
    return listOf(
        "#CABBE9",
        "#99DDCC",
        "#9AD3BC",
        "#89BEB3",
        "#FFB6B9",
        "#94B4A4",
        "#FFAAA5",
        "#7AC7C4",
        "#F5B461",
        "#61C0BF",
        "#FF9999",
        "#898B8A",
        "#15B7B9",
        "#FA7F72",
        "#389393",
        "#537791",
        "#EC524B",
        "#B9290E",
        "#27496D",
        "#283149"
    )
}