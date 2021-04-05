package org.hiro.noteapp.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import org.hiro.noteapp.database.model.SubTask
import java.util.*

class SubTaskTypeConverter {

    @TypeConverter
    fun listToJson(value: List<SubTask>?): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun jsonToList(value: String): List<SubTask> {
        val objects = Gson().fromJson(value, Array<SubTask>::class.java) as Array<SubTask>
        return objects.toMutableList()
    }
}