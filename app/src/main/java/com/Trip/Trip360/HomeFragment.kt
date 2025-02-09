//package com.Trip.Trip360
//
//import android.content.Intent
//import android.graphics.drawable.Drawable
//import android.os.Bundle
//import android.util.Log
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import androidx.fragment.app.Fragment
//import com.Trip.Trip360.databinding.FragmentHomeBinding
//import com.Trip.Trip360.utils.SharedPrefUtils
//import com.Trip.Trip360.utils.SharedPrefUtils.KEY_LOGO_LOCAL_FILE_PATH
//import com.Trip.Trip360.utils.SharedPrefUtils.KEY_LOGO_URL
//import com.Trip.Trip360.utils.SharedPrefUtils.KEY_WEB_NAME
//import com.Trip.Trip360.utils.SharedPrefUtils.getValue
//import com.google.android.material.dialog.MaterialAlertDialogBuilder
//
//class HomeFragment : Fragment() {
//
//    private var _binding: FragmentHomeBinding? = null
//    private val binding get() = _binding!!
//
//    private lateinit var webName: String
//    private lateinit var imageUrl: String
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        _binding = FragmentHomeBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        // Initialize webName and imageUrl here where the context is available
//        webName = SharedPrefUtils.getValue(requireContext(), KEY_WEB_NAME, "Default WebName")
//        imageUrl = SharedPrefUtils.getValue(requireContext(), KEY_LOGO_URL, "")
//
//        Log.d("HomeFragment", "WebName: $webName")
//        Log.d("HomeFragment", "Logo URL: $imageUrl")
//
//        // Set the header text to webName
//        binding.tvHeaderText.text = "Welcome to $webName dashboard"
//
//        // Load the image from the URL into the ImageView
//        val logoLocalPath = getValue(requireContext(), KEY_LOGO_LOCAL_FILE_PATH, "")
//        binding.imgHeader.setImageDrawable(Drawable.createFromPath(logoLocalPath))
////
////        if (imageUrl.isNotEmpty()) {
////
////
//////            Glide.with(this)
//////                .load(imageUrl)
//////                .apply(RequestOptions.placeholderOf(R.drawable.logo).error(R.drawable.logo))
//////                .listener(object : com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable> {
//////                    override fun onLoadFailed(
//////                        e: GlideException?,
//////                        model: Any?,
//////                        target: com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable>?,
//////                        isFirstResource: Boolean
//////                    ): Boolean {
//////                        Log.e("HomeFragment", "Image Load Failed: ${e?.message}")
//////                        return false
//////                    }
//////
//////                    override fun onResourceReady(
//////                        resource: android.graphics.drawable.Drawable?,
//////                        model: Any?,
//////                        target: com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable>?,
//////                        dataSource: com.bumptech.glide.load.DataSource?,
//////                        isFirstResource: Boolean
//////                    ): Boolean {
//////                        Log.d("HomeFragment", "Image Load Successful")
//////                        return false
//////                    }
//////                })
//////                .into(binding.imgHeader)
////        } else {
////            // Set a default placeholder if no URL is found
////            binding.imgHeader.setImageResource(R.drawable.logo)
////            Log.d("HomeFragment", "Logo URL is empty, using default placeholder")
////        }
//
//        // Handle button click
//        binding.btnHomeGenerateInvoice.setOnClickListener {
//            startActivity(Intent(requireContext(), InvoiceActivity::class.java))
//        }
//
//        binding.btnHomeSeeInvoiceHistory.setOnClickListener {
//            requireActivity().supportFragmentManager.beginTransaction()
//                .replace(R.id.content_frame, InvoiceHistoryFragment()) // Replace with your container ID
//                .addToBackStack(null) // Allows back navigation
//                .commit()
//        }
//
//        binding.btnHomeSeeTemplates.setOnClickListener {
//            showTemplateDialog { template ->
//                Log.d("HomeFragment", "Selected template: $template")
//            }
//        }
//    }
//
//
//    private fun showTemplateDialog(onTemplateSelected: (String) -> Unit) {
//        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_template_list, null)
//        val dialog = MaterialAlertDialogBuilder(requireContext())
//            .setTitle("Select a Template")
//            .setView(dialogView)
//            .setCancelable(true)
//            .create()
//
//        dialogView.findViewById<View>(R.id.item1).setOnClickListener {
//            onTemplateSelected("Template 1")
//            dialog.dismiss()
//        }
//        dialogView.findViewById<View>(R.id.item2).setOnClickListener {
//            onTemplateSelected("Template 2")
//            dialog.dismiss()
//        }
//        dialogView.findViewById<View>(R.id.item3).setOnClickListener {
//            onTemplateSelected("Template 3")
//            dialog.dismiss()
//        }
//        dialog.show()
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}

package com.Trip.Trip360

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.Trip.Trip360.databinding.FragmentHomeBinding
import com.Trip.Trip360.utils.SharedPrefUtils
import com.Trip.Trip360.utils.SharedPrefUtils.KEY_LOGO_LOCAL_FILE_PATH
import com.Trip.Trip360.utils.SharedPrefUtils.KEY_LOGO_URL
import com.Trip.Trip360.utils.SharedPrefUtils.KEY_WEB_NAME
import com.Trip.Trip360.utils.SharedPrefUtils.getValue
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var webName: String
    private lateinit var imageUrl: String
    private val firestore = FirebaseFirestore.getInstance()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize webName and imageUrl
        webName = SharedPrefUtils.getValue(requireContext(), KEY_WEB_NAME, "Default WebName")
        imageUrl = SharedPrefUtils.getValue(requireContext(), KEY_LOGO_URL, "")

        Log.d("HomeFragment", "WebName: $webName")
        Log.d("HomeFragment", "Logo URL: $imageUrl")

        // Set the header text to webName
//        binding.tvHeaderText.text = "Welcome to $webName dashboard"

        // Load the image from the local path
        val logoLocalPath = getValue(requireContext(), KEY_LOGO_LOCAL_FILE_PATH, "")
//        binding.imgHeader.setImageDrawable(Drawable.createFromPath(logoLocalPath))

        // Initial fetch without filters
        fetchInvoiceData(null, null)

        // Show Date Range Picker when clicking on calendarIcon
        binding.calendarIcon.setOnClickListener {
            showDateRangePicker()
        }

        // Handle button clicks
        binding.btnHomeGenerateInvoice.setOnClickListener {
            startActivity(Intent(requireContext(), InvoiceActivity::class.java))
        }

        binding.btnHomeSeeInvoiceHistory.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.content_frame, InvoiceHistoryFragment())
                .addToBackStack(null)
                .commit()
        }

        binding.btnHomeSeeTemplates.setOnClickListener {
            showTemplateDialog { template ->
                Log.d("HomeFragment", "Selected template: $template")
            }
        }
    }

    /**
     * Show Date Range Picker when clicking the calendar icon.
     */
    private fun showDateRangePicker() {
        val calendar = Calendar.getInstance()
        val startDate = Calendar.getInstance()
        val endDate = Calendar.getInstance()

        val startDatePicker = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                startDate.set(year, month, dayOfMonth)

                val endDatePicker = DatePickerDialog(
                    requireContext(),
                    { _, endYear, endMonth, endDay ->
                        endDate.set(endYear, endMonth, endDay)

                        // Convert dates to string format
                        val startDateStr = dateFormat.format(startDate.time)
                        val endDateStr = dateFormat.format(endDate.time)

                        Log.d("HomeFragment", "Selected Date Range: $startDateStr to $endDateStr")

                        // Fetch and filter invoices based on the selected date range
                        fetchInvoiceData(startDateStr, endDateStr)
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )

                endDatePicker.datePicker.minDate = startDate.timeInMillis // Ensures end date is after start date
                endDatePicker.datePicker.maxDate = calendar.timeInMillis // Restricts to today's date
                endDatePicker.show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        startDatePicker.datePicker.maxDate = calendar.timeInMillis // Restrict to today
        startDatePicker.show()
    }

    /**
     * Fetch invoices from Firestore based on the optional date range filter.
     */
    private fun fetchInvoiceData(startDate: String?, endDate: String?) {
        val collectionRef = firestore.collection(webName) // Reference collection
        var query: Query = collectionRef

        if (startDate != null && endDate != null) {
            try {
                Log.d("HomeFragment", "Filtering invoices from $startDate to $endDate")

                // Directly filter using Firestore string date
                query = query
                    .whereGreaterThanOrEqualTo("timeStamp", startDate)
                    .whereLessThanOrEqualTo("timeStamp", endDate)

            } catch (e: Exception) {
                Log.e("HomeFragment", "Error parsing date", e)
                return
            }
        }

        query.get()
            .addOnSuccessListener { documents ->
                if (documents.isEmpty) {
                    Log.d("HomeFragment", "No invoices found in selected date range.")
                }

                var totalInvoiceCount = 0
                var totalRevenue = 0.0

                for (document in documents) {
                    val invoiceData = document.data
                    totalInvoiceCount++

                    val price = (invoiceData["totalPrice"] as? Number)?.toDouble() ?: 0.0
                    totalRevenue += price

                    Log.d("HomeFragment", "Invoice found: ${document.id}, Price: $price")
                }

                Log.d("HomeFragment", "Final Invoice Count: $totalInvoiceCount, Revenue: $totalRevenue")

                // Update UI with fetched results
                binding.totalInvoiceCount.text = "$totalInvoiceCount"
                binding.totalRevenueCount.text = "AED: ${String.format("%.2f", totalRevenue)}"
            }
            .addOnFailureListener { exception ->
                Log.e("HomeFragment", "Error fetching invoices", exception)
            }
    }





    /**
     * Show a template selection dialog.
     */
    private fun showTemplateDialog(onTemplateSelected: (String) -> Unit) {
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
