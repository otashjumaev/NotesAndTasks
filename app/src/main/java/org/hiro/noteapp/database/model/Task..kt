package org.hiro.noteapp.database.model

import android.graphics.Color
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
data class Task(
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,
    var catId: Long? = null,
    @ColumnInfo(name = "task_desc")
    var taskDesc: String? = null,
    @ColumnInfo(name = "is_done")
    var isDone: Boolean = false,
    @ColumnInfo(name = "sub_tasks")
    var subTasks: MutableList<SubTask> = mutableListOf(),
    var time: Long? = null,
    @ColumnInfo(name = "reminder_time")
    var reminderTime: Long? = null,
    var color: Int = Color.TRANSPARENT,
) : Item {
    @Ignore
    override fun time() = time
}

data class SubTask(
    var id: Long? = null,
    var isDoing: Boolean = false,
    var isDone: Boolean = false,
    var desc: String? = null,
)


