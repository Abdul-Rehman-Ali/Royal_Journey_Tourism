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
import androidx.lifecycle.lifecycleScope
import com.RoyalJourneyTourism.RJT.data.Booking
import com.RoyalJourneyTourism.RJT.data.LocalDatabase
import com.RoyalJourneyTourism.RJT.repository.FirebaseRepository
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        binding.btnGenerateInvoice.setOnClickListener { collectData() }

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
            val timePicker = MaterialTimePicker.Builder()
                .setHour(12)
                .setTitleText("Select Time")
                .build()

            timePicker.show(parentFragmentManager, "MaterialTimePicker")

            timePicker.addOnPositiveButtonClickListener {
                val selectedHour = timePicker.hour
                val selectedMinute = timePicker.minute
                val formattedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                etTime.setText(formattedTime)
            }
        }


    }
    private fun collectData() {
        val bookingDao = LocalDatabase.getDatabase(requireContext()).bookingDao()
        val firebaseRepository = FirebaseRepository(bookingDao)

        val name = binding.etUsername.text.toString()
        val email = binding.etEmail.text.toString()
        val phone = binding.etPhone.text.toString()
        val packageName = binding.etPackageName.text.toString()
        val additionalAddon = binding.etAddonDescription.text.toString()
        val noOfAdults = binding.etAdults.text.toString().toIntOrNull()
        val pkgPricePerAdult = binding.etPackagePrice.text.toString().toDoubleOrNull()
        val noOfKids = binding.etKids.text.toString().toIntOrNull()
        val pkgPricePerKid = binding.etPackagePriceKids.text.toString().toDoubleOrNull()
        val pickupDate = binding.etDate.text.toString()
        val pickupTime = binding.etTime.text.toString()
        val paymentStatus = when (binding.radioGroupPaymentStatus.checkedRadioButtonId) {
            R.id.radio_paid -> true
            R.id.radio_pay_on_arrival -> false
            else -> null
        }

        val booking = Booking(
            id = 0,
            name = name,
            email = email.ifBlank { null },
            phone = phone.ifBlank { null },
            packageName = packageName.ifBlank { null },
            additionalAddon = additionalAddon.ifBlank { null },
            noOfAdults = noOfAdults,
            pkgPricePerAdult = pkgPricePerAdult,
            noOfKids = noOfKids,
            pkgPricePerKid = pkgPricePerKid,
            pickupDate = pickupDate.ifBlank { null },
            pickupTime = pickupTime.ifBlank { null },
            paymentStatus = paymentStatus
        )

        lifecycleScope.launch(Dispatchers.IO) {
            firebaseRepository.syncNewRecord(booking)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
