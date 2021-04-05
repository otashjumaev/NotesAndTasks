package org.hiro.noteapp.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import org.hiro.noteapp.database.MyDatabase
import org.hiro.noteapp.viewmodel.HomeViewModel
import java.lang.IllegalArgumentException

class MainViewModelFactory(private val db: MyDatabase, private val catID: Int?) :
    ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(db, catID) as T
        }
        throw IllegalArgumentException("ERROR ARGUMENT TYPE")
    }
}