package org.hiro.noteapp.ui

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.colorpicker.MaterialColorPickerDialog
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import org.hiro.noteapp.R
import org.hiro.noteapp.database.MyDatabase
import org.hiro.noteapp.databinding.FragmentNoteBinding
import org.hiro.noteapp.util.colors
import org.hiro.noteapp.util.hideKeyboard
import org.hiro.noteapp.util.toHtml
import org.hiro.noteapp.viewmodel.NoteViewModel
import org.hiro.noteapp.viewmodel.factory.NoteViewModelFactory
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class NoteFragment : Fragment() {
    private lateinit var binding: FragmentNoteBinding
    private lateinit var viewModel: NoteViewModel
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentNoteBinding.inflate(inflater, container, false)
        val arguments = NoteFragmentArgs.fromBundle(arguments)
        viewModel = ViewModelProvider(this,
            NoteViewModelFactory(MyDatabase.getInstance(requireContext()), arguments.key))
            .get(NoteViewModel::class.java)
        initListeners()
        initObservers()
        initCategChips()
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        return binding.root
    }
    
    private fun initCategChips() {
        val currentCatId = viewModel.note.catId
        viewModel.categories.forEach {
            val chip = Chip(activity)
            chip.id = it.id!!.toInt()
            chip.text = (it.name)
            chip.isCheckable = true
            Log.d("DBGCAT", "init: ${it.id}")
            chip.isCheckedIconVisible = false
            chip.setChipBackgroundColorResource(R.color.chip_bg)
            chip.setRippleColorResource(R.color.chip_ripple_bg)
            chip.setTextColor(resources.getColorStateList(R.color.chip_text_bg, null))
            if (it.id == currentCatId)
                chip.isChecked = true
            binding.categoryChips.addView(chip)
        }
    }
    
    
    private fun initListeners() {
        binding.categoryChips.setOnCheckedChangeListener { group, checkedId ->
            viewModel.note.catId = checkedId.toLong()
        }
        binding.editNote.apply {
            doOnTextChanged { _, _, _, _ ->
                viewModel.note.text = toHtml(text)
            }
        }
        binding.editTitle.doOnTextChanged { t, _, _, _ -> viewModel.note.title = t.toString() }
        binding.saveBtn.setOnClickListener {
            saveNote()
        }
        binding.colorPickerBtn.setOnClickListener {
            lifecycleScope.launch {
                viewModel.note.color = showColorPicker()
            }
        }
        binding.buttonBackArrow.setOnClickListener {
            findNavController().navigateUp()
        }
    }
    
    
    private fun initObservers() {
        viewModel.textEdit.observe(viewLifecycleOwner, { s ->
            s?.let {
                lifecycleScope.launch {
                    with(binding.editNote) {
                        it.start = selectionStart
                        it.end = selectionEnd
                        if (!s.isApplied)
                            if (it.spanID == R.id.action_bg_color)
                                viewModel.textBgColor = showColorPicker()
                            else if (it.spanID == R.id.action_txt_color)
                                viewModel.textColor = showColorPicker()
                        viewModel.setSpanText(text, it)
                        viewModel.textEditSelectDone()
                    }
                }
            }
        })
    }
    
    
    private fun saveNote() {
        viewModel.note.text = toHtml(binding.editNote.text)
        val snackBarText = if (
            viewModel.saveNote()
        ) "Saved" else "Please add some note"
        Snackbar.make(requireView(), snackBarText, Snackbar.LENGTH_SHORT).show()
    }
    
    private suspend fun showColorPicker() = suspendCoroutine<Int> {
        MaterialColorPickerDialog
            .Builder(requireContext())
            .setColors(colors())
            .setColorListener { color, _ ->
                it.resume(color)
            }
            .show()
    }
    
    override fun onStop() {
        hideKeyboard(activity as MainActivity)
        super.onStop()
    }
    
}
