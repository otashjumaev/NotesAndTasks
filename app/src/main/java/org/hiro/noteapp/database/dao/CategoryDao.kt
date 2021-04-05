package org.hiro.noteapp.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import org.hiro.noteapp.database.model.Category
import org.hiro.noteapp.database.model.Note

@Dao
interface CategoryDao {
    
    @Insert
    fun insert(categ: Category): Long
    
    @Update
    fun update(categ: Category)
    
    @Delete
    fun delete(categ: Category)
    
    @Query("SELECT * FROM Category WHERE id = :id")
    fun get(id: Long): Category?
    
    @Query("SELECT * FROM Category ")
    fun getCategories(): List<Category>
    
    @Query("DELETE FROM Category")
    fun clear()
    
}