package org.hiro.noteapp.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Category(
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,
    var name: String? = null,
)