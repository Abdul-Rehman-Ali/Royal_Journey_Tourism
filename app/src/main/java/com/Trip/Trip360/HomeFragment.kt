package com.Trip.Trip360

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.lifecycle.lifecycleScope
import com.Trip.Trip360.data.Booking
import com.Trip.Trip360.data.LocalDatabase
import com.Trip.Trip360.databinding.FragmentHomeBinding
import com.Trip.Trip360.repository.FirebaseRepository
import com.Trip.Trip360.utils.CustomDialog.showMessageDialog
import com.Trip.Trip360.utils.PdfUtils
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

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

        val db = LocalDatabase.getDatabase(requireContext())

        binding.btnGenerateInvoice.setOnClickListener {
            showTemplateDialog { selectedTemplate ->
                val templateLayout = getTemplateLayout(selectedTemplate)
                templateLayout?.let { layout ->
                    collectData(layout)
                }
            }
        }

        etDate = binding.etDate
        etTime = binding.etTime

        // Set up the Date Picker
        etDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { _, selectedYear, selectedMonth, selectedDay ->
                    val date = "$selectedDay/${selectedMonth + 1}/$selectedYear"
                    etDate.setText(date)

                    val timePickerDialog = MaterialTimePicker.Builder()
                        .setTimeFormat(TimeFormat.CLOCK_24H)
                        .setHour(calendar.get(Calendar.HOUR_OF_DAY))
                        .setMinute(calendar.get(Calendar.MINUTE))
                        .setTitleText("Select Time")
                        .build()

                    timePickerDialog.addOnPositiveButtonClickListener {
                        val selectedHour = timePickerDialog.hour
                        val selectedMinute = timePickerDialog.minute
                        val time = String.format("%02d:%02d", selectedHour, selectedMinute)
                        etDate.append(" $time")
                    }

                    timePickerDialog.show(childFragmentManager, "TimePicker")
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
                val amPm = if (selectedHour < 12) "AM" else "PM"
                val hourIn12HourFormat = if (selectedHour > 12) {
                    selectedHour - 12
                } else if (selectedHour == 0) {
                    12
                } else {
                    selectedHour
                }

                val formattedTime = String.format("%02d:%02d %s", hourIn12HourFormat, selectedMinute, amPm)
                etTime.setText(formattedTime)
            }
        }
    }

    private fun getTemplateLayout(selectedTemplate: String): Int? {
        return when (selectedTemplate) {
            "Template 1" -> R.layout.invoice_layout_1
            "Template 2" -> R.layout.invoice_layout_2
            "Template 3" -> R.layout.invoice_layout_3
            else -> null
        }
    }

    private fun collectData(selectedTemplate: Int) {
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
        val pickupLocation = binding.etPickupLocation.text.toString()
        val paymentStatus = when (binding.radioGroupPaymentStatus.checkedRadioButtonId) {
            R.id.radio_paid -> true
            R.id.radio_pay_on_arrival -> false
            else -> false
        }

        val totalPrice = calculateTotalPrice(noOfAdults, pkgPricePerAdult, noOfKids, pkgPricePerKid)

        val currentDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        // Retrieve webName from SharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        val webName = sharedPreferences.getString("webName", "default_collection") ?: "default_collection"

        val booking = Booking(
            id = 0,
            invoiceId = UUID.randomUUID().toString(), // Unique ID for each invoice
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
            pickupLocation = pickupLocation.ifBlank { null },
            paymentStatus = paymentStatus,
            webName = webName,
            currentDate = currentDate,
            totalPrice = totalPrice
        )


        showMessageDialog("Generating Pdf, please wait...", "Action", requireContext())
        lifecycleScope.launch(Dispatchers.IO) {
            bookingDao.upsertRecord(booking)
            firebaseRepository.syncNewRecord(booking, webName) // Using webName as the collection name
            withContext(Dispatchers.Main) {
                PdfUtils.generateInvoicePdf(selectedTemplate, booking, requireContext())
            }
        }
    }

    private fun calculateTotalPrice(
        noOfAdults: Int?, pkgPricePerAdult: Double?,
        noOfKids: Int?, pkgPricePerKid: Double?
    ): Double {
        val adultsTotal = (noOfAdults ?: 0) * (pkgPricePerAdult ?: 0.0)
        val kidsTotal = (noOfKids ?: 0) * (pkgPricePerKid ?: 0.0)
        return adultsTotal + kidsTotal
    }

    fun showTemplateDialog(onTemplateSelected: (String) -> Unit) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_template_list, null)

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Select a Template")
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialogView.findViewById<View>(R.id.item1).setOnClickListener {
            onTemplateSelected("Template 1")
            dialog.dismiss()
        }

        dialogView.findViewById<View>(R.id.item2).setOnClickListener {
            onTemplateSelected("Template 2")
            dialog.dismiss()
        }

        dialogView.findViewById<View>(R.id.item3).setOnClickListener {
            onTemplateSelected("Template 3")
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
