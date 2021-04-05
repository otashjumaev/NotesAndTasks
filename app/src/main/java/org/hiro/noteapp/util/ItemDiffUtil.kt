package org.hiro.noteapp.util

import android.annotation.SuppressLint
import android.util.Log
import androidx.recyclerview.widget.DiffUtil
import org.hiro.noteapp.database.model.Item
import org.hiro.noteapp.database.model.Note
import org.hiro.noteapp.database.model.Task

class ItemDiffUtil : DiffUtil.ItemCallback<Item>() {

//    override fun areItemsTheSame(oldItem: Item, newItem: Item) = oldItem. == (newItem as Task).id
    
    
    @SuppressLint("DiffUtilEquals")
    override fun areContentsTheSame(oldItem: Item, newItem: Item) = when (oldItem) {
        is Task -> oldItem == newItem
        is Note -> oldItem == newItem
        else -> {
            Log.d("ERROR", "areContentsTheSame: ERROR")
            false
        }
    }
    
    override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
        return true
    }
    
    
}