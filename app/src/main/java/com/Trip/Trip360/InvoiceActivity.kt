package com.Trip.Trip360

import android.app.DatePickerDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.Trip.Trip360.data.BookingDao
import com.Trip.Trip360.data.Invoice
import com.Trip.Trip360.data.LocalDatabase
import com.Trip.Trip360.databinding.ActivityInvoiceBinding
import com.Trip.Trip360.repository.FirebaseRepository
import com.Trip.Trip360.utils.CustomDialog.showMessageDialog
import com.Trip.Trip360.utils.IntentActionUtils.INTENT_ACTION_EDIT
import com.Trip.Trip360.utils.PdfGenerationCallback
import com.Trip.Trip360.utils.PdfUtils
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.UUID

class InvoiceActivity : AppCompatActivity() {
    private lateinit var binding: ActivityInvoiceBinding

    private var isEditMode: Boolean = false
    private var invoiceId: Long? = null
    private var existingInvoice: Invoice? = null
    private var selectedTemplate: Int = R.layout.invoice_layout_1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInvoiceBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        // Check intent for edit action
        intent?.let {
            isEditMode = it.action == INTENT_ACTION_EDIT
            invoiceId = it.getLongExtra("invoiceId", -1)
        }

        val db = LocalDatabase.getDatabase(this)
        val bookingDao = db.bookingDao()
        val firebaseRepository = FirebaseRepository(bookingDao)

        if (isEditMode && invoiceId != null) {

            binding.materialToolbarInvoice.title = "Update Invoice"

            // Load invoice data for editing
            lifecycleScope.launch(Dispatchers.IO) {
                existingInvoice = bookingDao.getInvoiceById(invoiceId!!)
                existingInvoice?.let { populateFields(it) }
            }
        }

        // Set up UI
        setupDateTimePicker()
        setupTimePicker()

        binding.btnGenerateInvoice.text = if (isEditMode) "Update Invoice" else "Generate Invoice"

        binding.btnGenerateInvoice.setOnClickListener {
            showTemplateDialog { template ->
                selectedTemplate = getTemplateLayout(template) ?: R.layout.invoice_layout_1
                if (isEditMode) {
                    updateInvoice(bookingDao, firebaseRepository)
                } else {
                    createInvoice(bookingDao, firebaseRepository)
                }
            }
        }
    }

    private fun populateFields(invoice: Invoice) {
        runOnUiThread {
            binding.etUsername.setText(invoice.name)
            binding.etEmail.setText(invoice.email)
            binding.etPhone.setText(invoice.phone)
            binding.etPackageName.setText(invoice.packageName)
            binding.etAddonDescription.setText(invoice.additionalAddon)
            binding.etAdults.setText(invoice.noOfAdults?.toString())
            binding.etPackagePrice.setText(invoice.pkgPricePerAdult?.toString())
            binding.etKids.setText(invoice.noOfKids?.toString())
            binding.etPackagePriceKids.setText(invoice.pkgPricePerKid?.toString())
            binding.etDate.setText(invoice.pickupDate)
            binding.etTime.setText(invoice.pickupTime)
            binding.etPickupLocation.setText(invoice.pickupLocation)
            binding.radioGroupPaymentStatus.check(
                if (invoice.paymentStatus) R.id.radio_paid else R.id.radio_pay_on_arrival
            )
        }
    }

//    private fun setupDatePicker() {
//        binding.etDate.setOnClickListener {
//            val calendar = Calendar.getInstance()
//            val year = calendar.get(Calendar.YEAR)
//            val month = calendar.get(Calendar.MONTH)
//            val day = calendar.get(Calendar.DAY_OF_MONTH)
//
//            val datePickerDialog = DatePickerDialog(
//                this,
//                { _, selectedYear, selectedMonth, selectedDay ->
//                    val date = "$selectedDay/${selectedMonth + 1}/$selectedYear"
//                    binding.etDate.setText(date)
//                },
//                year, month, day
//            )
//            datePickerDialog.show()
//        }
//    }

    private fun setupDateTimePicker() {
        binding.etDate.setOnClickListener {
            hideKeyboard(binding.etDate)

            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date")
                .build()

            datePicker.show(supportFragmentManager, "MaterialDatePicker")

            datePicker.addOnPositiveButtonClickListener { selection ->

                val calendar = Calendar.getInstance().apply {
                    timeInMillis = selection
                }
                val selectedDay = calendar.get(Calendar.DAY_OF_MONTH)
                val selectedMonth = calendar.get(Calendar.MONTH) + 1
                val selectedYear = calendar.get(Calendar.YEAR)
                val formattedDate = "$selectedDay/$selectedMonth/$selectedYear"

                binding.etDate.setText(formattedDate)

                val timePicker = MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_12H)
                    .setHour(12)
                    .setMinute(0)
                    .setTitleText("Select Time")
                    .build()

                timePicker.show(supportFragmentManager, "MaterialTimePicker")

                timePicker.addOnPositiveButtonClickListener {

                    val selectedHour = timePicker.hour
                    val selectedMinute = timePicker.minute
                    val amPm = if (selectedHour < 12) "AM" else "PM"
                    val hourIn12HourFormat = if (selectedHour == 0) {
                        12
                    } else if (selectedHour > 12) {
                        selectedHour - 12
                    } else {
                        selectedHour
                    }

                    val formattedTime = String.format("%02d:%02d %s", hourIn12HourFormat, selectedMinute, amPm)

                    binding.etDate.append(" $formattedTime")
                }
            }
        }
    }


    private fun setupTimePicker() {
        binding.etTime.setOnClickListener {
            hideKeyboard(binding.etTime)
            val timePicker = MaterialTimePicker.Builder()
                .setHour(12)
                .setTitleText("Select Time")
                .build()

            timePicker.show(supportFragmentManager, "MaterialTimePicker")

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
                binding.etTime.setText(formattedTime)
            }
        }
    }

    private fun createInvoice(bookingDao: BookingDao, firebaseRepository: FirebaseRepository) {
        val invoice = collectInvoiceData()
        if (invoice == null) {
            showMessageDialog("Invalid data. Please check your inputs.", "Error", this)
            return
        }

        PdfUtils.generateInvoicePdf(selectedTemplate, invoice, this, object : PdfGenerationCallback {
            override fun onPdfGenerated(filePath: String?) {
                invoice.filePath = filePath

                lifecycleScope.launch(Dispatchers.IO) {
                    bookingDao.insertInvoice(invoice)
                    firebaseRepository.syncRecord(invoice, invoice.webName)
                }

                showMessageDialog("Invoice successfully created!", "Success", this@InvoiceActivity)
            }

            override fun onFailure(errorMessage: String?) {
                showMessageDialog("Failed to generate PDF: $errorMessage", "Error", this@InvoiceActivity)
            }
        })
    }

    private fun updateInvoice(bookingDao: BookingDao, firebaseRepository: FirebaseRepository) {
        val updatedInvoice = collectInvoiceData()
        if (updatedInvoice == null) {
            showMessageDialog("Invalid data. Please check your inputs.", "Error", this)
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            // Delete old PDF file if it exists
            existingInvoice?.filePath?.let { path ->
                File(path+".pdf").takeIf { it.exists() }?.delete()
            }

            // Generate the new PDF for the updated invoice
            PdfUtils.generateInvoicePdf(selectedTemplate, updatedInvoice, this@InvoiceActivity, object : PdfGenerationCallback {
                override fun onPdfGenerated(filePath: String?) {
                    updatedInvoice.filePath = filePath

                    lifecycleScope.launch(Dispatchers.IO) {

                        updatedInvoice.id = existingInvoice!!.id
                        bookingDao.updateInvoice(updatedInvoice)
                        firebaseRepository.syncRecord(updatedInvoice, updatedInvoice.webName)
                    }

                    runOnUiThread {
                        showMessageDialog("Invoice successfully updated!", "Success", this@InvoiceActivity)
                    }
                }

                override fun onFailure(errorMessage: String?) {
                    showMessageDialog("Failed to generate PDF: $errorMessage", "Error", this@InvoiceActivity)
                }
            })
        }
    }


    private fun collectInvoiceData(): Invoice {

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
        val sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
        val webName = sharedPreferences.getString("webName", "default_collection") ?: "default_collection"


        val invoice = Invoice(
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

        return invoice
    }

    private fun getTemplateLayout(selectedTemplate: String): Int? {
        return when (selectedTemplate) {
            "Template 1" -> R.layout.invoice_layout_1
            "Template 2" -> R.layout.invoice_layout_2
            "Template 3" -> R.layout.invoice_layout_3
            else -> null
        }
    }

    private fun showTemplateDialog(onTemplateSelected: (String) -> Unit) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_template_list, null)

        val dialog = MaterialAlertDialogBuilder(this)
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

    private fun calculateTotalPrice(
        noOfAdults: Int?, pkgPricePerAdult: Double?,
        noOfKids: Int?, pkgPricePerKid: Double?
    ): Double {
        val adultsTotal = (noOfAdults ?: 0) * (pkgPricePerAdult ?: 0.0)
        val kidsTotal = (noOfKids ?: 0) * (pkgPricePerKid ?: 0.0)
        return adultsTotal + kidsTotal
    }



    fun hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

}
