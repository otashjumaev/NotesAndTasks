package org.hiro.noteapp.adapter

import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StrikethroughSpan
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.hiro.noteapp.R
import org.hiro.noteapp.database.model.SubTask
import org.hiro.noteapp.databinding.ListItemInputTaskBinding
import org.hiro.noteapp.databinding.ListItemSubtaskBinding

class SubTaskAdapter(
    private val addSubTask: (subTask: String) -> Unit,
    private val delete: (pos: Int) -> Unit,
) :
    ListAdapter<SubTask, RecyclerView.ViewHolder>(SubTaskDiffCallBack()) {
    companion object {
        private const val ITEM_SUB_TASK: Int = 1
        private const val ITEM_INPUT: Int = 0
    }
    
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        if (viewType == ITEM_SUB_TASK)
            SubTaskViewHolder.from(parent)
        else
            SubTaskInputViewHolder.from(parent)
    
    
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (getItemViewType(position)) {
            ITEM_SUB_TASK ->
                (holder as SubTaskViewHolder).bind(getItem(position) as SubTask, position, delete)
            ITEM_INPUT -> (holder as SubTaskInputViewHolder).bind(addSubTask)
        }
    }
    
    override fun getItemViewType(position: Int): Int =
        if (position == itemCount - 1) ITEM_INPUT else ITEM_SUB_TASK
    
}

class SubTaskViewHolder private constructor(private val binding: ListItemSubtaskBinding) :
    RecyclerView.ViewHolder(binding.root) {
    
    fun bind(task: SubTask, pos: Int, delete: (pos: Int) -> Unit) {
        binding.taskCheck.text = task.desc
        binding.taskCheck.isChecked = task.isDone
        binding.taskCheck.setOnCheckedChangeListener { _, isChecked ->
            task.isDone = isChecked
            val s = SpannableStringBuilder(task.desc)
            if (task.isDone) {
                s.setSpan(StrikethroughSpan(),
                    0,
                    task.desc?.length ?: 0,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            }
            binding.taskCheck.text = s
        }
        binding.deleteBtn.setOnClickListener {
            delete(pos)
        }
        binding.executePendingBindings()
    }
    
    companion object {
        fun from(parent: ViewGroup): SubTaskViewHolder {
            val binding = DataBindingUtil.inflate<ListItemSubtaskBinding>(
                LayoutInflater.from(parent.context),
                R.layout.list_item_subtask,
                parent,
                false
            )
            return SubTaskViewHolder(binding)
        }
    }
}

class SubTaskInputViewHolder private constructor(private val binding: ListItemInputTaskBinding) :
    RecyclerView.ViewHolder(binding.root) {
    
    fun bind(addSubTask: (subTask: String) -> Unit) {
        binding.inputSubTask.imeOptions = EditorInfo.IME_ACTION_NEXT
        binding.inputSubTask.setImeActionLabel("Add", KeyEvent.KEYCODE_ENTER)
        binding.inputSubTask.text?.clear()
        
        binding.inputSubTask.setOnEditorActionListener { v, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                addSubTask(v.text.toString())
                return@setOnEditorActionListener true
            }
            false
        }
    }
    
    companion object {
        fun from(parent: ViewGroup): SubTaskInputViewHolder {
            val binding = DataBindingUtil.inflate<ListItemInputTaskBinding>(
                LayoutInflater.from(parent.context),
                R.layout.list_item_input_task,
                parent,
                false
            )
            return SubTaskInputViewHolder(binding)
        }
    }
}


class SubTaskDiffCallBack : DiffUtil.ItemCallback<SubTask>() {
    override fun areItemsTheSame(oldItem: SubTask, newItem: SubTask): Boolean =
        oldItem.id == newItem.id
    
    override fun areContentsTheSame(oldItem: SubTask, newItem: SubTask) = oldItem == newItem
    
}