package org.hiro.noteapp.util

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import org.hiro.noteapp.databinding.DialogDateTimeBinding
import java.util.*


@SuppressLint("InflateParams")
class DateTimePickerDialog(context: Context, val save: (time: Long?) -> Unit) :
    AlertDialog.Builder(context) {
    private var time: Long? = null
    
    init {
        val inflater = LayoutInflater.from(context)
        
        val binding = DialogDateTimeBinding.inflate(inflater)
        
        val datePicker = binding.datePicker
        val timePicker = binding.timePicker
        
        datePicker.minDate = System.currentTimeMillis() - 1000
        timePicker.setIs24HourView(true)
        
        
        setView(binding.root)
        val dialog = create()
        
        binding.positiveBtn.setOnClickListener {
            val calendar: Calendar = GregorianCalendar(
                datePicker.year,
                datePicker.month,
                datePicker.dayOfMonth,
                timePicker.hour,
                timePicker.minute
            )
            time = calendar.timeInMillis
            if (Date().before(Date(time!!))) {
                save(time)
                dialog.dismiss()
            } else {
                Toast.makeText(context, "Reminder can not bet set before now", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        binding.negativeBtn.setOnClickListener {
            save(null)
            dialog.dismiss()
        }
        dialog.show()
    }
    
}

