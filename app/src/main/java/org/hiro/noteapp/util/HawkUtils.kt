package org.hiro.noteapp.util


import com.google.gson.Gson
import com.orhanobut.hawk.Hawk

object HawkUtils {
    
    var firstOpened: Boolean
        get() = Hawk.get("FIRST_OPEN", false)
        set(value) {
            Hawk.put("FIRST_OPEN", value)
        }
    
    var userLoggedIn: Boolean
        get() = Hawk.get("LOGGED_IN", false)
        set(value) {
            Hawk.put("LOGGED_IN", value)
        }
    
    var isTaskSelected: Boolean
        get() = Hawk.get("TASK_SELECTED", true)
        set(value) {
            Hawk.put("TASK_SELECTED", value)
        }
    var isNoteSelected: Boolean
        get() = Hawk.get("NOTE_SELECTED", true)
        set(value) {
            Hawk.put("NOTE_SELECTED", value)
        }
}