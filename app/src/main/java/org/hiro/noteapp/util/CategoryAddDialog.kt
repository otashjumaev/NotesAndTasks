package org.hiro.noteapp.util

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import org.hiro.noteapp.databinding.DialogCategAddBinding

class CategoryAddDialog(context: Context, val save: (name: String) -> Unit) :
    AlertDialog.Builder(context) {
    
    init {
        val binding = DialogCategAddBinding.inflate(LayoutInflater.from(context))
        setView(binding.root)
        
        val dialog = create()
        
        binding.positiveBtn.setOnClickListener {
            save(binding.nameInput.text.toString())
            dialog.dismiss()
        }
        binding.negativeBtn.setOnClickListener {
            dialog.dismiss()
        }
        
        dialog.show()
    }
}