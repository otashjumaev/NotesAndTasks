package org.hiro.noteapp.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.content.res.Resources
import android.os.Bundle
import android.text.Spannable
import android.text.style.StrikethroughSpan
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.dhaval2404.colorpicker.MaterialColorPickerDialog
import com.google.android.material.chip.Chip
import kotlinx.coroutines.launch
import org.hiro.noteapp.R
import org.hiro.noteapp.adapter.SubTaskAdapter
import org.hiro.noteapp.database.MyDatabase
import org.hiro.noteapp.database.dao.TaskDao
import org.hiro.noteapp.database.model.SubTask
import org.hiro.noteapp.databinding.FragmentTaskBinding
import org.hiro.noteapp.util.*
import org.hiro.noteapp.viewmodel.TaskViewModel
import org.hiro.noteapp.viewmodel.factory.TaskViewModelFactory
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class TaskFragment : Fragment() {
    private lateinit var adapter: SubTaskAdapter
    private lateinit var viewModel: TaskViewModel
    lateinit var binding: FragmentTaskBinding
    private lateinit var alarmManager: AlarmManager
    
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentTaskBinding.inflate(inflater, container, false)
        val id = arguments?.getLong("KEY") ?: -1
        viewModel = ViewModelProvider(this,
            TaskViewModelFactory(MyDatabase.getInstance(requireContext()), id))
            .get(TaskViewModel::class.java)
        viewModel.task.subTasks.add(SubTask())
        
        initClickListeners()
        initLiveEvents()
        initAdapter()
        initCategChips()
        if (id != -1L) {
            binding.editTaskDesc.setText(viewModel.task.taskDesc)
            setReminderChip()
            binding.doneCheck.isChecked = viewModel.task.isDone
        }
        binding.subTaskList.adapter = adapter
        binding.subTaskList.layoutManager = LinearLayoutManager(context)
        alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        
        return binding.root
    }
    
    private fun initCategChips() {
        val currentCatId = viewModel.task.catId
        viewModel.categories.forEach {
            val chip = Chip(activity)
            chip.id = it.id!!.toInt()
            chip.text = (it.name)
            chip.isCheckable = true
            chip.isCheckedIconVisible = false
            chip.setChipBackgroundColorResource(R.color.chip_bg)
            chip.setRippleColorResource(R.color.chip_ripple_bg)
            chip.setTextColor(resources.getColorStateList(R.color.chip_text_bg, null))
            if (it.id == currentCatId)
                chip.isChecked = true
            binding.categoryChips.addView(chip)
        }
        binding.categoryChips.setOnCheckedChangeListener { group, checkedId ->
            viewModel.task.catId = checkedId.toLong()
            Log.d("DBGCAT", "initCategChips: $checkedId")
        }
    }
    
    private fun initClickListeners() {
        binding.editTaskDesc.doOnTextChanged { text, _, _, _ ->
            viewModel.task.taskDesc = text.toString()
        }
        binding.doneCheck.setOnCheckedChangeListener { _, isChecked ->
            viewModel.taskDone(isChecked)
        }
        binding.btnSetReminder.setOnClickListener { viewModel.setttingReminder() }
        binding.reminderChip.setOnCloseIconClickListener {
            it.visibility = View.GONE
            viewModel.task.reminderTime = null
            binding.btnSetReminder.visibility = View.VISIBLE
        }
        binding.reminderChip.setOnClickListener {
            viewModel.task.reminderTime = null
            viewModel.setttingReminder()
        }
        binding.saveBtn.setOnClickListener {
            saveTask()
        }
        binding.colorPickerBtn.setOnClickListener {
            lifecycleScope.launch {
                viewModel.task.color = showColorPicker()
            }
        }
        binding.buttonBackArrow.setOnClickListener {
            findNavController().navigateUp()
        }
    }
    
    private fun initAdapter() {
        adapter = SubTaskAdapter({ subTask ->
            Log.d("DBG1", "onCreateView: $subTask")
            val index = viewModel.task.subTasks.size - 1
            viewModel.task.subTasks.add(index, SubTask(id = index.toLong(), desc = subTask))
            hideKeyboard(requireActivity())
            Log.d("DBG1", "onCreateView: ${viewModel.task.subTasks.size}")
            adapter.submitList(viewModel.task.subTasks)
        }, {
            Log.d("DBG11", "bfr:${viewModel.task.subTasks.size} ${viewModel.task.subTasks[it]}")
            viewModel.task.subTasks.removeAt(it)
            adapter.submitList(viewModel.task.subTasks)
            adapter.notifyItemRemoved(it)
            Log.d("DBG11", "aft: ${viewModel.task.subTasks.size}")
        })
        adapter.submitList(viewModel.task.subTasks)
    }
    
    private fun initLiveEvents() {
        viewModel.setReminder.observe(viewLifecycleOwner) {
            if (it) {
                DateTimePickerDialog(requireContext()) { t ->
                    viewModel.task.reminderTime = t
                    setReminderChip()
                    t?.let { time -> setNotification(time) }
                }
                viewModel.settingReminderCompleted()
            }
        }
        
        viewModel.taskDone.observe(viewLifecycleOwner) {
            binding.editTaskDesc.text.apply {
                Log.d("DBG", "taskdone: $this $it")
                if (it) setSpan(StrikethroughSpan(), 0, length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
                else {
                    getSpans(0, length, StrikethroughSpan::class.java).forEach {
                        removeSpan(it)
                    }
                }
            }
        }
        
    }
    
    private fun setReminderChip() {
        if (viewModel.task.reminderTime != null) {
            binding.reminderChip.text = formatTime(viewModel.task.reminderTime!!)
            binding.reminderChip.visibility = View.VISIBLE
            binding.btnSetReminder.visibility = View.INVISIBLE
        } else {
            binding.reminderChip.visibility = View.INVISIBLE
            binding.btnSetReminder.visibility = View.VISIBLE
        }
    }
    
    private fun setNotification(time: Long) {
        val intent = Intent(requireContext(), MyAlertReceiver::class.java)
        intent.putExtra("TASK_DESC", viewModel.task.taskDesc)
        val pendingIntent =
            PendingIntent.getBroadcast(requireContext(), 1, intent, 0)
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent)
    }
    
    private fun saveTask() {
        val snackBarText = if (
            viewModel.saveTask()
        ) "Saved" else "Empty task!!!"
        Toast.makeText(requireContext(), snackBarText, Toast.LENGTH_SHORT).show()
    }
    
    private suspend fun showColorPicker() = suspendCoroutine<Int> {
        MaterialColorPickerDialog
            .Builder(requireContext())
            .setColors(colors())
            .setColorListener { color, _ ->
                it.resume(color)
                Log.d("DBG", "showColorPicker: $color")
            }
            .show()
    }
}