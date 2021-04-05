package org.hiro.noteapp.util

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Html
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.databinding.BindingAdapter
import org.hiro.noteapp.database.model.Note
import org.hiro.noteapp.viewmodel.NoteViewModel
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
@BindingAdapter("formattedTimeText")
fun TextView.setFormattedTimeText(note: Note) {
    note.time?.let {
        text = formatTime(it)
    }
}


@BindingAdapter("titleVisibility")
fun TextView.setTitleVisibility(note: Note) {
    visibility = if (note.title == null || note.title.equals("")) View.GONE else View.VISIBLE
}

@BindingAdapter("onClick")
fun setOnClickListener(button: ImageButton, viewModel: NoteViewModel) {
    button.setOnClickListener {
        if (button.background == null) button.background = ColorDrawable(0)
        val isApplied: Boolean = it.background.alpha != Color.TRANSPARENT
        val color = if (isApplied) Color.TRANSPARENT else Color.parseColor("#FF018786")
        it.setBackgroundColor(color)
        viewModel.textEditSelect(it.id, isApplied)
    }
}

