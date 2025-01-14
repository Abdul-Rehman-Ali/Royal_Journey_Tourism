package com.Trip.Trip360

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
import com.bumptech.glide.Glide
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

class InvoiceActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInvoiceBinding
    private var isEditMode: Boolean = false
    private var invoiceId: Long? = null
    private var existingInvoice: Invoice? = null
    private var selectedTemplate: Int = R.layout.invoice_layout_1
    private lateinit var bookingDao: BookingDao
    private lateinit var firebaseRepository: FirebaseRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInvoiceBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        setSupportActionBar(binding.materialToolbarInvoice)
        val statusBarColor = ContextCompat.getColor(this, R.color.primaryColor)

        // Apply insets to adjust for system bars
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            window.statusBarColor = statusBarColor
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Initialize database and repository
        bookingDao = LocalDatabase.getDatabase(this).bookingDao()
        firebaseRepository = FirebaseRepository(bookingDao)

        // Check if editing an existing invoice
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

        // Setup UI
        setupDateTimePicker()
        setupTimePicker()
//        applyPrimaryColor(SharedPrefUtils.getValue(this, KEY_COLOR, "#FF5733"))

        binding.btnGenerateInvoice.text = if (isEditMode) "Update Invoice" else "Generate Invoice"

        // Button actions
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
            binding.etPickupLocation.setText(invoice.pickupLocation)
            binding.radioGroupPaymentStatus.check(
                if (invoice.paymentStatus) R.id.radio_paid else R.id.radio_pay_on_arrival
            )
        }
    }

//    private fun populateFields(invoice: Invoice) {
//        runOnUiThread {
//            binding.etUsername.setText(invoice.name)
//            binding.etEmail.setText(invoice.email)
//            binding.etPhone.setText(invoice.phone)
//            binding.etPackageName.setText(invoice.packageName)
//            binding.etAddonDescription.setText(invoice.additionalAddon)
//            binding.etAdults.setText(invoice.noOfAdults?.toString())
//            binding.etPackagePrice.setText(invoice.pkgPricePerAdult?.toString())
//            binding.etKids.setText(invoice.noOfKids?.toString())
//            binding.etPackagePriceKids.setText(invoice.pkgPricePerKid?.toString())
//            binding.etDate.setText(invoice.pickupDate)
//            binding.etTime.setText(invoice.pickupTime)
//            binding.etPickupLocation.setText(invoice.pickupLocation)
//            binding.radioGroupPaymentStatus.check(
//                if (invoice.paymentStatus) R.id.radio_paid else R.id.radio_pay_on_arrival
//            )
//
//            // Fetch the logo URL from SharedPreferences
//            val sharedPreferences = getSharedPreferences("ClientDataPref", Context.MODE_PRIVATE)
//            val savedLogoUrl = sharedPreferences.getString("logoURL", "") ?: ""
//
//            // Use either the logo URL from the Invoice object or the one from SharedPreferences
//            val logoUrl = invoice.logoUrl ?: savedLogoUrl
//
//            // Log URLs for debugging
//            Log.d("populateFields", "Saved Logo URL: $savedLogoUrl")
//            Log.d("populateFields", "Final Logo URL: $logoUrl")
//
//            // Use findViewById to bind the ImageView
//            val logoImageView = findViewById<ImageView>(R.id.logoUrl)
//
//            // Populate the logo image
//            if (logoUrl.isNotEmpty()) {
//                Glide.with(this)
//                    .load(logoUrl)
//                    .placeholder(R.drawable.logo) // Set a default logo while loading
//                    .error(R.drawable.logo) // Set a fallback logo if the URL fails
//                    .into(logoImageView)
//            } else {
//                // If the URL is null or empty, set the default logo
//                logoImageView.setImageResource(R.drawable.logo)
//            }
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
//        PdfUtils.generateInvoicePdf(selectedTemplate, invoice, this, object : PdfGenerationCallback {
//            override fun onPdfGenerated(filePath: String?) {
//                invoice.filePath = filePath
//                lifecycleScope.launch(Dispatchers.IO) {
//                    bookingDao.insertInvoice(invoice)
//                    firebaseRepository.syncRecord(invoice, COLLECTION)
//                }
//                showMessageDialog("Invoice successfully created!", "Success", this@InvoiceActivity)
//            }
//
//            override fun onFailure(errorMessage: String?) {
//                showMessageDialog("Failed to generate PDF: $errorMessage", "Error", this@InvoiceActivity)
//            }
//        })
//    }

    private fun createInvoice() {
        val invoice = collectInvoiceData() ?: return
        val collectionName = getCollectionName(this) // Fetch the dynamic collection name

        PdfUtils.generateInvoicePdf(selectedTemplate, invoice, this, object : PdfGenerationCallback {
            override fun onPdfGenerated(filePath: String?) {
                invoice.filePath = filePath
                lifecycleScope.launch(Dispatchers.IO) {
                    bookingDao.insertInvoice(invoice)
                    firebaseRepository.syncRecord(invoice, collectionName) // Use dynamic collection name
                }
                showMessageDialog("Invoice successfully created!", "Success", this@InvoiceActivity)
            }

            override fun onFailure(errorMessage: String?) {
                showMessageDialog("Failed to generate PDF: $errorMessage", "Error", this@InvoiceActivity)
            }
        })
    }



//    private fun updateInvoice() {
//        val updatedInvoice = collectInvoiceData() ?: return
//        existingInvoice?.filePath?.let { path ->
//            File("$path.pdf").takeIf { it.exists() }?.delete()
//        }
//        PdfUtils.generateInvoicePdf(selectedTemplate, updatedInvoice, this, object : PdfGenerationCallback {
//            override fun onPdfGenerated(filePath: String?) {
//                updatedInvoice.filePath = filePath
//                lifecycleScope.launch(Dispatchers.IO) {
//                    updatedInvoice.id = existingInvoice!!.id
//                    updatedInvoice.firestoreDocRef = existingInvoice!!.firestoreDocRef
//                    bookingDao.updateInvoice(updatedInvoice)
//                    firebaseRepository.syncRecord(updatedInvoice, COLLECTION)
//                }
//                showMessageDialog("Invoice successfully updated!", "Success", this@InvoiceActivity)
//            }
//
//            override fun onFailure(errorMessage: String?) {
//                showMessageDialog("Failed to generate PDF: $errorMessage", "Error", this@InvoiceActivity)
//            }
//        })
//    }

    private fun updateInvoice() {
        val updatedInvoice = collectInvoiceData() ?: return
        val collectionName = getCollectionName(this) // Fetch the dynamic collection name

        existingInvoice?.filePath?.let { path ->
            File("$path.pdf").takeIf { it.exists() }?.delete()
        }
        PdfUtils.generateInvoicePdf(selectedTemplate, updatedInvoice, this, object : PdfGenerationCallback {
            override fun onPdfGenerated(filePath: String?) {
                updatedInvoice.filePath = filePath
                lifecycleScope.launch(Dispatchers.IO) {
                    updatedInvoice.id = existingInvoice!!.id
                    updatedInvoice.firestoreDocRef = existingInvoice!!.firestoreDocRef
                    bookingDao.updateInvoice(updatedInvoice)
                    firebaseRepository.syncRecord(updatedInvoice, collectionName) // Use the dynamic collection name
                }
                showMessageDialog("Invoice successfully updated!", "Success", this@InvoiceActivity)
            }

            override fun onFailure(errorMessage: String?) {
                showMessageDialog("Failed to generate PDF: $errorMessage", "Error", this@InvoiceActivity)
            }
        })
    }


//    private fun collectInvoiceData(): Invoice? {
//        val name = binding.etUsername.text.toString()
//        val email = binding.etEmail.text.toString()
//        val phone = binding.etPhone.text.toString()
//        if (name.isBlank() || email.isBlank() || phone.isBlank()) {
//            showMessageDialog("Name, email, and phone cannot be empty", "Error", this)
//            return null
//        }
//        return Invoice(
//            name = name,
//            email = email,
//            phone = phone,
//            packageName = binding.etPackageName.text.toString(),
//            additionalAddon = binding.etAddonDescription.text.toString(),
//            noOfAdults = binding.etAdults.text.toString().toIntOrNull(),
//            pkgPricePerAdult = binding.etPackagePrice.text.toString().toDoubleOrNull(),
//            noOfKids = binding.etKids.text.toString().toIntOrNull(),
//            pkgPricePerKid = binding.etPackagePriceKids.text.toString().toDoubleOrNull(),
//            pickupDate = binding.etDate.text.toString(),
//            pickupTime = binding.etTime.text.toString(),
//            pickupLocation = binding.etPickupLocation.text.toString(),
//            paymentStatus = binding.radioGroupPaymentStatus.checkedRadioButtonId == R.id.radio_paid,
//            webName = COLLECTION,
//            currentDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
//            totalPrice = 0.0 // Replace with calculation logic
//        )
//    }

//    private fun collectInvoiceData(): Invoice? {
//        val name = binding.etUsername.text.toString()
//        val email = binding.etEmail.text.toString()
//        val phone = binding.etPhone.text.toString()
//
//        if (name.isBlank() || email.isBlank() || phone.isBlank()) {
//            showMessageDialog("Name, email, and phone cannot be empty", "Error", this)
//            return null
//        }
//
//        // Dynamically fetch webName from SharedPreferences
//        val sharedPreferences = getSharedPreferences("YourPreferenceName", Context.MODE_PRIVATE)
//        val webName = sharedPreferences.getString("webName", "default_collection") ?: "default_collection"
//
//        return Invoice(
//            name = name,
//            email = email,
//            phone = phone,
//            packageName = binding.etPackageName.text.toString(),
//            additionalAddon = binding.etAddonDescription.text.toString(),
//            noOfAdults = binding.etAdults.text.toString().toIntOrNull(),
//            pkgPricePerAdult = binding.etPackagePrice.text.toString().toDoubleOrNull(),
//            noOfKids = binding.etKids.text.toString().toIntOrNull(),
//            pkgPricePerKid = binding.etPackagePriceKids.text.toString().toDoubleOrNull(),
//            pickupDate = binding.etDate.text.toString(),
//            pickupTime = binding.etTime.text.toString(),
//            pickupLocation = binding.etPickupLocation.text.toString(),
//            paymentStatus = binding.radioGroupPaymentStatus.checkedRadioButtonId == R.id.radio_paid,
//            webName = webName, // Set the dynamically fetched webName here
//            currentDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
//            totalPrice = 0.0 // Replace with calculation logic if needed
//        )
//    }


    private fun collectInvoiceData(): Invoice? {
        val name = binding.etUsername.text.toString()
        val email = binding.etEmail.text.toString()
        val phone = binding.etPhone.text.toString()

        if (name.isBlank() || email.isBlank() || phone.isBlank()) {
            showMessageDialog("Name, email, and phone cannot be empty", "Error", this)
            return null
        }

        // Dynamically fetch webName from SharedPreferences
        val sharedPreferences = getSharedPreferences("ClientDataPref", Context.MODE_PRIVATE)
        val webName = sharedPreferences.getString("webName", "default_collection") ?: "default_collection"
        val logoUrl = sharedPreferences.getString("logoURL", "") ?: ""
        Log.d("InvoiceData", "My Fetched logoURL: $logoUrl")


        val invoice = Invoice(
            name = name,
            email = email,
            phone = phone,
            packageName = binding.etPackageName.text.toString(),
            additionalAddon = binding.etAddonDescription.text.toString(),
            noOfAdults = binding.etAdults.text.toString().toIntOrNull(),
            pkgPricePerAdult = binding.etPackagePrice.text.toString().toDoubleOrNull(),
            noOfKids = binding.etKids.text.toString().toIntOrNull(),
            pkgPricePerKid = binding.etPackagePriceKids.text.toString().toDoubleOrNull(),
            pickupDate = binding.etDate.text.toString(),
            pickupTime = binding.etTime.text.toString(),
            pickupLocation = binding.etPickupLocation.text.toString(),
            paymentStatus = binding.radioGroupPaymentStatus.checkedRadioButtonId == R.id.radio_paid,
            webName = webName, // Set the dynamically fetched webName here
            currentDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date()),
            totalPrice = 0.0, // Placeholder, we'll calculate below
            logoUrl = logoUrl
        )

        // Calculate the total price and set it in the Invoice object
        invoice.totalPrice = PdfUtils.calculateTotalPrice(invoice)

        return invoice
    }



    private fun hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun applyPrimaryColor(colorHex: String) {
        val primaryColor = try {
            Color.parseColor(colorHex)
        } catch (e: IllegalArgumentException) {
            ContextCompat.getColor(this, R.color.primaryColor)
        }
        binding.materialToolbarInvoice.setBackgroundColor(primaryColor)
    }

    private fun getTemplateLayout(template: String): Int? {
        return when (template) {
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

    companion object {
        fun getCollectionName(context: Context): String {
            val sharedPreferences = context.getSharedPreferences("ClientDataPref", Context.MODE_PRIVATE)
            return sharedPreferences.getString("webName", "default_collection") ?: "default_collection"
        }
    }



//    companion object {
//        private const val COLLECTION = "default_collection"
//    }
}
