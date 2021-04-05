package org.hiro.noteapp.database.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import org.hiro.noteapp.database.model.Note
import org.hiro.noteapp.database.model.Task

@Dao
interface TaskDao {
    
    @Insert
    fun insert(task: Task): Long
    
    @Update
    fun update(task: Task)
    
    @Delete
    fun delete(task: Task)
    
    @Query("SELECT * FROM Task WHERE id = :id")
    fun getTask(id: Long): Task?
    
    @Query("SELECT * FROM Task WHERE catId = :catID ORDER BY time DESC")
    fun getTasks(catID: Long): LiveData<List<Task>>
    
    @Query("SELECT * FROM Task ORDER BY time DESC")
    fun getTasks(): LiveData<List<Task>>
    
    @Query("DELETE FROM Task")
    fun clear()
}