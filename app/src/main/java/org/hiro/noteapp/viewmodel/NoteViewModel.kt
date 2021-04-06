package org.hiro.noteapp.viewmodel

import android.graphics.Color
import android.graphics.Typeface
import android.text.Editable
import android.text.Spannable
import android.text.Spanned
import android.text.style.*
import android.util.Log
import androidx.annotation.IdRes
import androidx.core.graphics.toColorInt
import androidx.core.text.set
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.hiro.noteapp.R
import org.hiro.noteapp.database.MyDatabase
import org.hiro.noteapp.database.model.Note
import org.hiro.noteapp.util.*
import java.util.*

class NoteViewModel(val db: MyDatabase, key: Long) : ViewModel() {
    private val noteDB = db.noteDao
    val categories = db.categoryDao.getCategories()
    val note: Note = noteDB.get(key) ?: Note()
    
    private val _textEdit = MutableLiveData<BaseSpan?>()
    val textEdit: LiveData<BaseSpan?>
        get() = _textEdit
    
    
    var textColor = Color.BLACK
    
    var textBgColor = Color.TRANSPARENT
    
    fun textEditSelect(@IdRes id: Int, isApplied: Boolean) {
        _textEdit.value = BaseSpan(id, isApplied)
    }
    
    fun textEditSelectDone() {
        _textEdit.value = null
    }
    
    fun getTitleColor() = if (note.color == Color.TRANSPARENT) Color.BLACK else note.color
    
    
    private
    
    fun isNoteEmpty(): Boolean {
        return note.title.isNullOrEmpty() && note.text.isNullOrEmpty()
    }
    
    fun noteTextFromHtml(): Spanned? = fromHtml(note.text ?: "")
    
    fun saveNote(): Boolean {
        Log.d("DBGN", "saveNote: $note")
        if (isNoteEmpty()) return false
        
        if (note.color == Color.TRANSPARENT)
            note.color = colors()[Random().nextInt(colors().size)].toColorInt()
        note.time = Date().time
        
        if (note.id == null) {
            val newItemId = noteDB.insert(note)
            note.id = newItemId
            Log.d("DBGN", "newID: $newItemId")
        } else noteDB.update(note)
        return true
    }
    
    fun getSpan(selectedSpan: Int): Any =
        when (selectedSpan) {
            R.id.action_bold -> StyleSpan(Typeface.BOLD)
            R.id.action_italic -> StyleSpan(Typeface.ITALIC)
            R.id.action_underline -> UnderlineSpan()
            R.id.action_strikethrough -> StrikethroughSpan()
            R.id.action_bg_color -> BackgroundColorSpan(textBgColor)
            R.id.action_txt_color -> ForegroundColorSpan(textColor)
            
            else -> {
            }
        }
    
    
    fun setSpanText(text: Editable, span: BaseSpan) {
        Log.d("DBGN", "BEFORE:|${toHtml(text)}|")
        with(span) {
            val spans = text.getSpans(start, end, getSpan(spanID).javaClass)
            var spanStart: Int = if (spans.isNotEmpty()) text.getSpanStart(spans[0]) else -1
            var spanEnd: Int = if (spans.isNotEmpty()) text.getSpanEnd(spans[0]) else -1
            var spanToRemove: Any?
            if (isApplied)
                for (sp in spans) {
                    spanToRemove = sp
                    spanStart = text.getSpanStart(sp)
                    spanEnd = text.getSpanEnd(sp)
                    
                    if (sp is StyleSpan)
                        if (sp.style != (getSpan(spanID) as StyleSpan).style)
                            spanToRemove = null
                    
                    text.removeSpan(spanToRemove)
                    if (spanToRemove != null)
                        when {
                            //12 16 -- 12 16
                            (start > spanStart && end < spanEnd && start != end) -> {
                                text[spanStart, start] = getSpan(spanID)
                                text[end, spanEnd] = getSpan(spanID)
                            }
                            //aaa bbb cc
                            (start <= spanStart && end < spanEnd && start != end) -> {
                                text[end, spanEnd] = getSpan(spanID)
                            }
                            
                            (start > spanStart && end >= spanEnd && start != end) -> {
                                text[spanStart, start] = getSpan(spanID)
                            }
                            (start == end) -> {
                                text.setSpan(
                                    getSpan(spanID),
                                    spanStart,
                                    spanEnd,
                                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                                )
                            }
                            
                        }
                }
            else {
//                if (spanStart <= start && spanEnd >= end && spanStart != -1)
//                    return
                text.setSpan(getSpan(spanID),
                    start,
                    end,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            }
        }
        Log.d("DBGN", "AFTER:|${toHtml(text)}|")
    }
}
