package org.hiro.noteapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import org.hiro.noteapp.database.dao.CategoryDao
import org.hiro.noteapp.database.dao.NoteDao
import org.hiro.noteapp.database.dao.TaskDao
import org.hiro.noteapp.database.model.Category
import org.hiro.noteapp.database.model.Note
import org.hiro.noteapp.database.model.Task

@Database(entities = [Note::class, Task::class, Category::class], version = 1, exportSchema = false)
@TypeConverters(SubTaskTypeConverter::class)
abstract class MyDatabase : RoomDatabase() {
    
    abstract val noteDao: NoteDao
    abstract val taskDao: TaskDao
    abstract val categoryDao: CategoryDao
    
    companion object {
        private var INSTANCE: MyDatabase? = null
        
        fun getInstance(context: Context): MyDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        MyDatabase::class.java,
                        "MyNotesDB"
                    )
                        .allowMainThreadQueries()
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}