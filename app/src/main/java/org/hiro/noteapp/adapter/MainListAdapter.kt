package org.hiro.noteapp.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.ColorSpace
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StrikethroughSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.hiro.noteapp.R
import org.hiro.noteapp.database.model.Item
import org.hiro.noteapp.database.model.Note
import org.hiro.noteapp.database.model.Task
import org.hiro.noteapp.databinding.ListItemNoteBinding
import org.hiro.noteapp.databinding.ListItemTaskBinding
import org.hiro.noteapp.util.formatTime
import org.hiro.noteapp.util.fromHtml

class MainListAdapter(private val onclickListener: (item: Item) -> Unit) :
    ListAdapter<Item, RecyclerView.ViewHolder>(ItemDiffCallback()) {
    
    companion object {
        const val TYPE_NOTE = 0
        const val TYPE_TASK = 1
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        if (viewType == TYPE_NOTE) NoteViewHolder.from(parent) else TaskViewHolder.from(parent)
    
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is NoteViewHolder -> holder.bind(getItem(position) as Note, onclickListener)
            is TaskViewHolder -> holder.bind(getItem(position) as Task, onclickListener)
        }
    }
    
    fun getItemByPos(position: Int): Item {
        return getItem(position)
    }
    
    fun putList(ln: List<Note>?, lt: List<Task>?, isNote: Boolean = true, isTask: Boolean = true) {
        var isChanged = false
        val currList = currentList.toMutableList()
        if (currList.isNotEmpty()) {
            isChanged = true
            currList.clear()
        }
        if (!ln.isNullOrEmpty() && isNote) {
            currList.addAll(ln)
            isChanged = true
        }
        if (!lt.isNullOrEmpty() && isTask) {
            currList.addAll(lt)
            isChanged = true
        }
        if (isChanged) {
            currList.sortByDescending { it.time() }
            submitList(currList)
        }
    }
    
    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Task -> TYPE_TASK
            is Note -> TYPE_NOTE
            else -> throw IllegalArgumentException("Invalid type of data $position")
        }
    }
}

class NoteViewHolder private constructor(private val binding: ListItemNoteBinding) :
    RecyclerView.ViewHolder(binding.root) {
    
    fun bind(item: Note, onItemClickListener: (item: Item) -> Unit) {
        binding.note = item
        binding.noteText.text = fromHtml(item.text ?: "")
        binding.itemLayout.setOnClickListener { onItemClickListener(item) }
        binding.executePendingBindings()
    }
    
    companion object {
        fun from(parent: ViewGroup): NoteViewHolder {
            val binding = DataBindingUtil.inflate<ListItemNoteBinding>(
                LayoutInflater.from(parent.context),
                R.layout.list_item_note,
                parent,
                false
            )
            return NoteViewHolder(binding)
        }
    }
}

class TaskViewHolder private constructor(private val binding: ListItemTaskBinding) :
    RecyclerView.ViewHolder(binding.root) {
    
    @SuppressLint("SetTextI18n")
    fun bind(item: Task, onItemClickListener: (item: Item) -> Unit) {
        with(binding) {
            
            val s = SpannableStringBuilder(item.taskDesc)
            if (item.isDone) {
                s.setSpan(StrikethroughSpan(),
                    0,
                    item.taskDesc?.length ?: 0,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            }
            taskDesc.text = s
            val doneCount = item.subTasks.count { it.isDone }
            subTaskCount.text = "$doneCount/${item.subTasks.size}"
            if (item.reminderTime != null) {
                taskRedmindTime.text = formatTime(item.reminderTime!!)
                taskRedmindTime.visibility = View.VISIBLE
            } else taskRedmindTime.visibility = View.INVISIBLE
            noteTime.text = formatTime(item.time ?: 0)
            progressTask.max = item.subTasks.size
            progressTask.progress = doneCount
            taskItemLayout.background.setTint(if (item.isDone || doneCount == item.subTasks.size) Color.GRAY else item.color)
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
//                progressTask.background.setTint(Color.convert(item.color,
//                    ColorSpace.get(ColorSpace.Named.DISPLAY_P3))
//                    .toInt())
                progressTask.progressDrawable.setTint(Color.convert(item.color,
                    ColorSpace.get(ColorSpace.Named.DCI_P3)).toInt())
            }
            root.setOnClickListener {
                onItemClickListener(item)
            }
        }
    }
    
    companion object {
        fun from(parent: ViewGroup): TaskViewHolder {
            val binding = DataBindingUtil.inflate<ListItemTaskBinding>(
                LayoutInflater.from(parent.context),
                R.layout.list_item_task,
                parent,
                false
            )
            return TaskViewHolder(binding)
        }
    }
}

class ItemDiffCallback : DiffUtil.ItemCallback<Item>() {
    override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
        return if (oldItem is Note && newItem is Note)
            newItem.id == (oldItem as Note).id
        else if (oldItem is Task && newItem is Task)
            newItem.id == (oldItem as Task).id
        else false
    }
    
    
    override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean = when (oldItem) {
        is Task -> {
            oldItem as Task == newItem as Task
        }
        is Note -> {
            oldItem as Note == newItem as Note
        }
        else -> throw IllegalArgumentException("Invalid type")
    }
}


