package com.RoyalJourneyTourism.RJT

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import com.RoyalJourneyTourism.RJT.databinding.FragmentHomeBinding
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TimePicker
import java.util.Calendar

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var etDate: EditText
    private lateinit var etTime: EditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment using view binding
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize the EditTexts
        etDate = binding.etDate  // Assuming the EditText in your layout has the ID 'etDate'
        etTime = binding.etTime  // Assuming you have an EditText for time in your layout

        // Set up the Date Picker
        etDate.setOnClickListener {
            // Get the current date
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            // Launch the DatePickerDialog
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, selectedYear, selectedMonth, selectedDay ->
                    // Set the selected date in the EditText
                    val date = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    etDate.setText(date)
                },
                year, month, day
            )

            datePickerDialog.show()
        }

        // Set up the Time Picker
        etTime.setOnClickListener {
            // Get the current time
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            // Launch the TimePickerDialog
            val timePickerDialog = TimePickerDialog(
                requireContext(),
                { _: TimePicker, selectedHour: Int, selectedMinute: Int ->
                    // Format the selected time and set it in the EditText
                    val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                    etTime.setText(formattedTime)
                },
                hour, minute, true // true for 24-hour format
            )

            timePickerDialog.show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Nullify the binding reference to avoid memory leaks
    }
}
