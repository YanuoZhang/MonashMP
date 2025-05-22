package com.example.monashMP.utils

import android.app.DatePickerDialog
import android.content.Context
import android.view.ContextThemeWrapper
import java.util.Calendar

fun showDatePickerDialog(
    context: Context,
    onDateSelected: (String) -> Unit
) {
    val calendar = Calendar.getInstance()
    val year = calendar.get(Calendar.YEAR)
    val month = calendar.get(Calendar.MONTH)
    val day = calendar.get(Calendar.DAY_OF_MONTH)
    val themedContext = ContextThemeWrapper(context, android.R.style.Theme_Material)
    val datePickerDialog = DatePickerDialog(
        themedContext,
        { _, selectedYear, selectedMonth, selectedDay ->
            val formattedDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            onDateSelected(formattedDate)
        },
        year,
        month,
        day
    )

    datePickerDialog.datePicker.maxDate = System.currentTimeMillis()

    datePickerDialog.show()
}
