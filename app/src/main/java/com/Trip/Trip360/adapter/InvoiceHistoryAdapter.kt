package com.Trip.Trip360.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.Trip.Trip360.InvoiceDetailsActivity
import com.Trip.Trip360.data.Invoice
import com.Trip.Trip360.databinding.RvItemInvoiceBinding
import com.Trip.Trip360.utils.PdfUtilsKt

class InvoiceHistoryAdapter(
    private var invoices: List<Invoice>,
    private val context: Context
) : RecyclerView.Adapter<InvoiceHistoryAdapter.InvoiceViewHolder>() {

    // ViewHolder class with View Binding
    inner class InvoiceViewHolder(private val binding: RvItemInvoiceBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(invoice: Invoice) {

            binding.tvCustomerName.text = invoice.name
            binding.tvTripPackageName.text = invoice.packageName
            binding.tvDateCreated.text = invoice.pickupDate

            // for going to invoice detail screen
            binding.btnInvoiceDetails.setOnClickListener {
                val intent = Intent(context, InvoiceDetailsActivity::class.java).apply {
                    putExtra("invoiceId", invoice.id)
                }
                context.startActivity(intent)
            }

            // for viewing pdf
            binding.btnViewInvoice.setOnClickListener {
                invoice.filePath?.let { it1 -> PdfUtilsKt.viewPdf(it1, context) }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InvoiceViewHolder {
        val binding = RvItemInvoiceBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return InvoiceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: InvoiceViewHolder, position: Int) {
        val invoice = invoices[position]
        holder.bind(invoice)
    }

    override fun getItemCount(): Int = invoices.size

    fun updateData(data: List<Invoice>) {
        invoices = data
        notifyDataSetChanged()
    }
}
