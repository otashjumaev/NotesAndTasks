package org.hiro.noteapp.database.model

import android.graphics.Color
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity
data class Note(
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,
    var catId: Long? = null,
    var title: String? = null,
    var text: String? = null,
    var time: Long? = null,
    var color: Int = Color.TRANSPARENT,
) : Item {
    
    @Ignore
    override fun time() = time
}