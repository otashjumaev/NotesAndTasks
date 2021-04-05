package org.hiro.noteapp.viewmodel

import android.graphics.Color
import android.util.Log
import androidx.core.graphics.toColorInt
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import org.hiro.noteapp.database.MyDatabase
import org.hiro.noteapp.database.model.Task
import org.hiro.noteapp.util.colors
import java.util.*

class TaskViewModel(private val db: MyDatabase, id: Long) : ViewModel() {
    private val taskDB = db.taskDao
    val categories = db.categoryDao.getCategories()
    val task: Task = taskDB.getTask(id) ?: Task()
    
    
    private val _setReminder = MutableLiveData(false)
    val setReminder: LiveData<Boolean>
        get() = _setReminder
    private val _taskDone = MutableLiveData(task.isDone)
    val taskDone: LiveData<Boolean>
        get() = _taskDone
    
    fun saveTask(): Boolean {
        Log.d("DBG1", "VM saveTask: $task")
        if (isTaskEmpty()) return false
        
        if (task.color == Color.TRANSPARENT)
            task.color = colors()[Random().nextInt(colors().size)].toColorInt()
        task.time = Date().time
        if (task.subTasks.lastOrNull()?.id == null)
            task.subTasks.removeLastOrNull()
        if (task.id == null) {
            val newItemId = taskDB.insert(task)
            task.id = newItemId
        } else taskDB.update(task)
        Log.d("DBG1", "VM saveTask after: ${task.id}")
        return true
    }
    
    fun setttingReminder() {
        _setReminder.value = true
    }
    
    fun settingReminderCompleted() {
        _setReminder.value = false
    }
    
    
    private fun isTaskEmpty(): Boolean {
        return task.taskDesc.isNullOrEmpty()
    }
    
    fun taskDone(isChecked: Boolean) {
        task.isDone = isChecked
        _taskDone.postValue(isChecked)
    }
    
    
}