package com.Trip.Trip360

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.Trip.Trip360.data.Invoice
import com.Trip.Trip360.data.LocalDatabase
import com.Trip.Trip360.databinding.ActivityInvoiceDetailsBinding
import com.Trip.Trip360.utils.IntentActionUtils.INTENT_ACTION_EDIT
import com.Trip.Trip360.utils.PdfUtils
import com.Trip.Trip360.utils.PdfUtilsKt
import kotlinx.coroutines.launch

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

        if (invoiceId.toInt() != -1) {
            lifecycleScope.launch {
                invoice = bookingDao.getInvoiceById(invoiceId)

                invoice?.let {
                    setData()
                    enableActions()
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

    private fun enableActions() {

        // view pdf
        binding.btnViewPdf.setOnClickListener {
            invoice?.filePath?.let { it1 -> PdfUtilsKt.showInvoicePdf(it1, this) }
        }

        binding.btnEditInvoice.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                action = INTENT_ACTION_EDIT
            }
            startActivity(intent)
        }

        binding.btnDeleteInvoice.setOnClickListener {
           lifecycleScope.launch {
               invoice?.let { it1 -> bookingDao.deleteInvoice(it1) }
           }
        }
    }
}