package com.Trip.Trip360

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.Trip.Trip360.data.Invoice
import com.Trip.Trip360.data.LocalDatabase
import com.Trip.Trip360.databinding.ActivityInvoiceDetailsBinding
import com.Trip.Trip360.utils.CustomDialog.showConfirmationDialog
import com.Trip.Trip360.utils.IntentActionUtils.INTENT_ACTION_EDIT
import com.Trip.Trip360.utils.PdfUtils
import com.Trip.Trip360.utils.PdfUtilsKt
import kotlinx.coroutines.launch
import java.io.File

class InvoiceDetailsActivity : AppCompatActivity() {
    private val binding by lazy { ActivityInvoiceDetailsBinding.inflate(layoutInflater) }
    private val bookingDao by lazy { LocalDatabase.getDatabase(this@InvoiceDetailsActivity).bookingDao() }
    private var invoice: Invoice? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val invoiceId = intent.getLongExtra("invoiceId", -1)
        Log.d("fjskjfs", "Invoice id: $invoiceId")

        if (invoiceId.toInt() != -1) {
            lifecycleScope.launch {
                invoice = bookingDao.getInvoiceById(invoiceId)
                runOnUiThread{
                    invoice?.let {
                        setData()
                        setActions()
                    }
                }
            }
        }
        //fetch invoiceRecord by invoiceId
        // set Invoice data
        // add edit and delete options
    }

    private fun setData() {
        // set invoice data on detail screen here
    }


    // actions;
    // pdf view
    // edit
    // delete
    // share

    private fun setActions() {

        binding.btnViewPdf.setOnClickListener {
            invoice?.filePath?.let { it1 -> PdfUtilsKt.viewPdf(it1, this) }
        }

        binding.btnEditInvoice.setOnClickListener {
            val intent = Intent(this, InvoiceActivity::class.java).apply {
                action = INTENT_ACTION_EDIT
                invoice?.let { it1 -> putExtra("invoiceId", it1.id) }
            }
            startActivity(intent)
        }

        binding.btnDeleteInvoice.setOnClickListener {
            showConfirmationDialog(message = "Invoice record will be permanently deleted from device", title = "Delete Invoice", this, onProceed = {
                lifecycleScope.launch {
                    invoice?.filePath?.let { path ->
                        File(path+".pdf").takeIf { it.exists() }?.delete()
                    }
                    invoice?.let { it1 -> bookingDao.deleteInvoice(it1) }
                }
                finish()
            })
        }

        binding.btnShareInvoice.setOnClickListener{
            invoice?.filePath?.let { it1 -> PdfUtilsKt.shareInvoice(it1, this) } ?: Toast.makeText(this, "Invoice null", Toast.LENGTH_SHORT).show()
        }
    }
}