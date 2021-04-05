package org.hiro.noteapp.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.hiro.noteapp.database.MyDatabase
import org.hiro.noteapp.viewmodel.NoteViewModel
import java.lang.IllegalArgumentException

class NoteViewModelFactory(private val db: MyDatabase, private val key: Long) :
    ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            return NoteViewModel(db, key) as T
        }
        throw IllegalArgumentException("ERROR ARGUMENT TYPE")
    }
}