package org.hiro.noteapp.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.hiro.noteapp.database.MyDatabase
import org.hiro.noteapp.viewmodel.TaskViewModel
import java.lang.IllegalArgumentException

class TaskViewModelFactory(val db: MyDatabase, val id: Long) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskViewModel::class.java))
            return TaskViewModel(db, id) as T
        throw IllegalArgumentException("ERROR ARGUMENT TYPE")
    }
}