package org.hiro.noteapp.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hannesdorfmann.adapterdelegates4.dsl.adapterDelegateViewBinding
import org.hiro.noteapp.database.MyDatabase
import org.hiro.noteapp.database.model.Item
import org.hiro.noteapp.database.model.Note
import org.hiro.noteapp.database.model.Task
import org.hiro.noteapp.databinding.ListItemNoteBinding
import org.hiro.noteapp.databinding.ListItemTaskBinding
import org.hiro.noteapp.util.formatTime
import org.hiro.noteapp.util.fromHtml


class HomeViewModel(db: MyDatabase, catID: Int?) : ViewModel() {
    
    val noteDB = db.noteDao
    val taskDB = db.taskDao
    val categoriesDB = db.categoryDao
    
    val notes = if (catID == null) noteDB.getNotes() else noteDB.getNotes(catID.toLong())
    val tasks = if (catID == null) taskDB.getTasks() else taskDB.getTasks(catID.toLong())
    
    var currentCategory = catID ?: 0
    
    private var _onAddClickEvent = MutableLiveData(0)
    val onAddClickEvent: LiveData<Int>
        get() = _onAddClickEvent
    
    fun addNote() {
        _onAddClickEvent.value = 1
    }
    
    fun addCompleted() {
        _onAddClickEvent.value = 0
    }
    
    fun addTask() {
        _onAddClickEvent.value = 2
    }
    
    
    @SuppressLint("SetTextI18n")
    fun adapterTaskDelegate(itemClickedListener: (item: Task) -> Unit) =
        adapterDelegateViewBinding<Task, Item, ListItemTaskBinding>(
            { layoutInflater, root -> ListItemTaskBinding.inflate(layoutInflater, root, false) }
        ) {
            binding.root.setOnClickListener {
                itemClickedListener(item)
            }
            bind {
                binding.apply {
                    taskDesc.text = item.taskDesc
                    subTaskCount.text = "${item.subTasks.count { it.isDone }}/${item.subTasks.size}"
                    taskRedmindTime.text = formatTime(item.reminderTime ?: 0)
                    noteTime.text = formatTime(item.time ?: 0)
                    progressTask.isIndeterminate = true
                    progressTask.max = item.subTasks.size
                    progressTask.progress = item.subTasks.count { it.isDone }
                    progressTask.secondaryProgress = item.subTasks.size - 1
                }
            }
        }
    
    fun adapterNoteDelegate(itemClickedListener: (item: Note) -> Unit) =
        adapterDelegateViewBinding<Note, Item, ListItemNoteBinding>(
            { layoutInflater, root -> ListItemNoteBinding.inflate(layoutInflater, root, false) }
        ) {
            binding.root.setOnClickListener {
                itemClickedListener(item)
            }
            bind {
                binding.apply {
                    binding.note = item
                    binding.noteText.text = fromHtml(item.text ?: "")
                }
            }
        }
}
