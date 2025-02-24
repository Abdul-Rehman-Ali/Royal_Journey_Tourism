package com.Trip.Trip360

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import com.Trip.Trip360.data.BookingDao
import com.Trip.Trip360.data.Invoice
import com.Trip.Trip360.data.LocalDatabase
import com.Trip.Trip360.databinding.ActivityInvoiceBinding
import com.Trip.Trip360.databinding.ActivityPrivateInvoiceBinding
import com.Trip.Trip360.repository.FirebaseRepository
import com.Trip.Trip360.testPdf.NativeApi
import com.Trip.Trip360.utils.CustomDialog.MessageDialog
import com.Trip.Trip360.utils.CustomDialog.showMessageDialog
import com.Trip.Trip360.utils.IntentActionUtils.INTENT_ACTION_EDIT
import com.Trip.Trip360.utils.PdfGenCallback
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

class PrivateInvoiceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPrivateInvoiceBinding
    private var isEditMode: Boolean = false
    private var invoiceId: Long? = null
    private var existingInvoice: Invoice? = null
    private var selectedTemplate: Int = R.layout.invoice_layout_1
    private lateinit var bookingDao: BookingDao
    private lateinit var firebaseRepository: FirebaseRepository
//    private lateinit var xmlToPDFLifecycleObserver: PdfGenerator.XmlToPDFLifecycleObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivateInvoiceBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        setSupportActionBar(binding.materialToolbarInvoice)
        val statusBarColor = ContextCompat.getColor(this, R.color.primaryColor)

        // Initialize XmlToPDFLifecycleObserver
//        xmlToPDFLifecycleObserver = PdfGenerator.XmlToPDFLifecycleObserver(this)
//        lifecycle.addObserver(xmlToPDFLifecycleObserver)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            window.statusBarColor = statusBarColor
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        bookingDao = LocalDatabase.getDatabase(this).bookingDao()
        firebaseRepository = FirebaseRepository(bookingDao)

        intent?.let {
            isEditMode = it.action == INTENT_ACTION_EDIT
            invoiceId = it.getLongExtra("invoiceId", -1)
        }

        if (isEditMode && invoiceId != null && invoiceId != -1L) {
            binding.materialToolbarInvoice.title = "Update Invoice"
            lifecycleScope.launch(Dispatchers.IO) {
                existingInvoice = bookingDao.getInvoiceById(invoiceId!!)
                existingInvoice?.let { populateFields(it) }
            }
        } else {
            binding.materialToolbarInvoice.title = "Create Invoice"
        }

        setupDateTimePicker()
        setupTimePicker()

        binding.btnGenerateInvoice.text = if (isEditMode) "Update Invoice" else "Generate Invoice"

        binding.btnGenerateInvoice.setOnClickListener {
            showTemplateDialog { template ->
                selectedTemplate = getTemplateLayout(template) ?: R.layout.invoice_layout_1
                if (isEditMode) {
                    updateInvoice()
                } else {
                    createInvoice()
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
            binding.etKids.setText(invoice.noOfPeople?.toString())
            binding.etPickupLocation.setText(invoice.pickupLocation)
            binding.etDropoffLocation.setText(invoice.dropLocation)
            binding.radioGroupPaymentStatus.check(
                if (invoice.paymentStatus) R.id.radio_paid else R.id.radio_pay_on_arrival
            )
        }
    }

    private fun setupDateTimePicker() {
        binding.etDate.setOnClickListener {
            hideKeyboard(binding.etDate)
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select Date")
                .build()

            datePicker.show(supportFragmentManager, "MaterialDatePicker")
            datePicker.addOnPositiveButtonClickListener { selection ->
                val calendar = Calendar.getInstance().apply { timeInMillis = selection }
                val formattedDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)
                binding.etDate.setText(formattedDate)
            }
        }
    }

    private fun setupTimePicker() {
        binding.etTime.setOnClickListener {
            hideKeyboard(binding.etTime)
            val timePicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_12H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Select Time")
                .build()

            timePicker.show(supportFragmentManager, "MaterialTimePicker")
            timePicker.addOnPositiveButtonClickListener {
                val hour = timePicker.hour
                val minute = timePicker.minute
                val amPm = if (hour < 12) "AM" else "PM"
                val formattedTime = String.format(
                    "%02d:%02d %s",
                    if (hour == 0) 12 else if (hour > 12) hour - 12 else hour,
                    minute,
                    amPm
                )
                binding.etTime.setText(formattedTime)
            }
        }
    }

//    private fun createInvoice() {
//        val invoice = collectInvoiceData() ?: return
//        val collectionName = getCollectionName(this)
//
//        PdfUtils.generateInvoicePdf(selectedTemplate, invoice, this, object : PdfGenerationCallback {
//            override fun onPdfGenerated(filePath: String?) {
//                invoice.filePath = filePath
//                lifecycleScope.launch(Dispatchers.IO) {
//                    bookingDao.insertInvoice(invoice)
//                    firebaseRepository.syncRecord(invoice, collectionName)
//                }
//                showMessageDialog("Invoice successfully created.", "Success", this@InvoiceActivity)
//            }
//
//            override fun onFailure(errorMessage: String?) {
//                showMessageDialog("Failed to generate PDF: $errorMessage", "Error", this@InvoiceActivity)
//            }
//        })
//    }

    private fun createInvoice() {
        val invoice = collectInvoiceData() ?: return
        val collectionName = getCollectionName(this)

        if (selectedTemplate == 0) {
            showMessageDialog("Invalid template selected.", "Error", this@PrivateInvoiceActivity)
            return
        }

        if (this.isFinishing || this.isDestroyed) {
            Log.e("InvoiceActivity", "Cannot generate PDF, activity is finishing or destroyed.")
            return
        }

        NativeApi.generatePrivateInvoicePdf(selectedTemplate = selectedTemplate, termsTemplate = R.layout.terms_and_conditions, booking =  invoice, context = this, callback =  object : PdfGenCallback {
            override fun onPdfGenerated(filePath: String?) {
                if (filePath.isNullOrEmpty()) {
                    showMessageDialog("File path is empty. Please try again.", "Error", this@PrivateInvoiceActivity)
                    return
                }

                invoice.filePath = filePath
                if (lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        bookingDao.insertInvoice(invoice)
                        firebaseRepository.syncRecord(invoice, collectionName)
                    }
                } else {
                    Log.e("InvoiceActivity", "Cannot perform operations, activity is not in a valid state.")
                }
                Log.d("InvoiceActivity", "PDF File Path: $filePath")


//                showMessageDialog("Invoice successfully created.", "Success", this@InvoiceActivity)
                MessageDialog("Invoice successfully created.", "Success", this@PrivateInvoiceActivity, filePath)

            }

            override fun onFailure(errorMessage: String?) {
                Log.e("PdfUtils", "Failed to generate PDF: $errorMessage")
                showMessageDialog("Failed to generate PDF: $errorMessage", "Error", this@PrivateInvoiceActivity)
            }
        })
    }



    private fun updateInvoice() {
        val updatedInvoice = collectInvoiceData() ?: return
        val collectionName = getCollectionName(this)

        existingInvoice?.filePath?.let { path ->
            File("$path.pdf").takeIf { it.exists() }?.delete()
        }
        NativeApi.generatePrivateInvoicePdf(selectedTemplate = selectedTemplate,termsTemplate = R.layout.terms_and_conditions,  booking = updatedInvoice, context =  this, callback = object : PdfGenCallback {
            override fun onPdfGenerated(filePath: String?) {
                updatedInvoice.filePath = filePath
                lifecycleScope.launch(Dispatchers.IO) {
                    updatedInvoice.id = existingInvoice!!.id
                    updatedInvoice.firestoreDocRef = existingInvoice!!.firestoreDocRef
                    bookingDao.updateInvoice(updatedInvoice)
                    firebaseRepository.syncRecord(updatedInvoice, collectionName)
                }
                showMessageDialog("Invoice successfully updated!", "Success", this@PrivateInvoiceActivity)
            }

            override fun onFailure(errorMessage: String?) {
                showMessageDialog("Failed to generate PDF: $errorMessage", "Error", this@PrivateInvoiceActivity)
            }
        })
    }

    private fun collectInvoiceData(): Invoice? {
        val name = binding.etUsername.text.toString()
        val phone = binding.etPhone.text.toString()
        val email = binding.etEmail.text.toString()
        val title = binding.etPackageName.text.toString()
        val pickupTime = binding.etTime.text.toString()
        val bookingDate = binding.etDate.text.toString()

        if (name.isBlank() || title.isBlank() || pickupTime.isBlank() || bookingDate.isBlank() ||  phone.isBlank()) {
            showMessageDialog("Please add the required detail", "Error", this)
            return null
        }

        // Generate unique booking code
        val bookingCode = generateBookingCode()

        val sharedPreferences = getSharedPreferences("ClientDataPref", Context.MODE_PRIVATE)
        val webName = sharedPreferences.getString("webName", "default_collection") ?: "default_collection"
        val color = sharedPreferences.getString("KEY_COLOR", "#FFFFFF") ?: "#FFFFFF"

        val invoice = Invoice(
            name = name,
            phone = phone,
            email = email,
            packageName = binding.etPackageName.text.toString(),
            additionalAddon = binding.etAddonDescription.text.toString(),
            noOfAdults = binding.etAdults.text.toString().toIntOrNull(),
            pkgPricePerAdult = binding.etPackagePrice.text.toString().toDoubleOrNull(),
            noOfKids = binding.etKids.text.toString().toIntOrNull(),
            pkgPricePerKid = binding.etPackagePriceKids.text.toString().toDoubleOrNull(),
            pickupDate = binding.etDate.text.toString(),
            pickupTime = binding.etTime.text.toString(),
            noOfPeople = binding.etKids.text.toString().toIntOrNull(),
            pickupLocation = binding.etPickupLocation.text.toString(),
            dropLocation = binding.etDropoffLocation.text.toString(),
            paymentStatus = binding.radioGroupPaymentStatus.checkedRadioButtonId == R.id.radio_paid,
            webName = webName,
            currentDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
            totalPrice = 0.0,
            color = color,
            bookingCode = bookingCode
        )

        invoice.totalPrice = NativeApi.calculateTotalPrice(invoice)

        return invoice
    }

    private fun generateBookingCode(): String {
        val sharedPreferences = getSharedPreferences("InvoicePrefs", Context.MODE_PRIVATE)
        val lastBookingNumber = sharedPreferences.getInt("LAST_BOOKING_NUMBER", 0)

        val newBookingNumber = lastBookingNumber + 1 // Increment from the last stored number
        val bookingCode = String.format("%04d", newBookingNumber) // Format as 0001, 0002, 0003, etc.

        // Save the new booking number
        sharedPreferences.edit().apply {
            putInt("LAST_BOOKING_NUMBER", newBookingNumber)
            apply()
        }

        return bookingCode
    }

    private fun hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun getTemplateLayout(template: String): Int? {
        return when (template) {
            "Template 1" -> R.layout.private_invoice_template
//            "Template 2" -> R.layout.invoice_layout_5
//            "Template 3" -> R.layout.invoice_layout_6
            else -> null
        }
    }

    private fun showTemplateDialog(onTemplateSelected: (String) -> Unit) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.general_template_layout_list, null)
        val dialog = MaterialAlertDialogBuilder(this)
            .setTitle("Select a Template")
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialogView.findViewById<View>(R.id.item1).setOnClickListener {
            onTemplateSelected("Template 1")
            dialog.dismiss()
        }
//        dialogView.findViewById<View>(R.id.item2).setOnClickListener {
//            onTemplateSelected("Template 2")
//            dialog.dismiss()
//        }
//        dialogView.findViewById<View>(R.id.item3).setOnClickListener {
//            onTemplateSelected("Template 3")
//            dialog.dismiss()
//        }
        dialog.show()
    }

    companion object {
        fun getCollectionName(context: Context): String {
            val sharedPreferences = context.getSharedPreferences("ClientDataPref", Context.MODE_PRIVATE)
            return sharedPreferences.getString("webName", "default_collection") ?: "default_collection"
        }
    }
}

