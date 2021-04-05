package org.hiro.noteapp.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import org.hiro.noteapp.database.model.Note

@Dao
interface NoteDao {
    
    @Insert
    fun insert(note: Note): Long
    
    @Update
    fun update(note: Note)
    
    @Delete
    fun delete(note: Note)
    
    @Query("SELECT * FROM Note WHERE id = :id")
    fun get(id: Long): Note?
    
    @Query("SELECT * FROM Note WHERE catId=:catID ORDER BY time DESC")
    fun getNotes(catID: Long): LiveData<List<Note>>
    
    @Query("SELECT * FROM Note ORDER BY time DESC")
    fun getNotes(): LiveData<List<Note>>
    
    @Query("DELETE FROM Note")
    fun clear()
    
}