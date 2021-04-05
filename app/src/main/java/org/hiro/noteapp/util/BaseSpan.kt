package org.hiro.noteapp.util

data class BaseSpan(
    var spanID: Int,
    var isApplied: Boolean,
    var start: Int = -1,
    var end: Int = -1
)