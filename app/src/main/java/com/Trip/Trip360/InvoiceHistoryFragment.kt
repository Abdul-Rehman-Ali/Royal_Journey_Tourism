package com.Trip.Trip360

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.Trip.Trip360.adapter.InvoiceHistoryAdapter
import com.Trip.Trip360.data.LocalDatabase
import com.Trip.Trip360.databinding.FragmentInvoiceHistoryBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
//
//class InvoiceHistoryFragment : Fragment() {
//
//    private var _binding: FragmentInvoiceHistoryBinding? = null
//    private val binding get() = _binding!!
//
//    private val bookingDao by lazy { LocalDatabase.getDatabase(requireContext()).bookingDao() }
//
//    private val adapter by lazy { InvoiceHistoryAdapter(emptyList(), requireContext())}
//
//    override fun onCreateView(
//        inflater: LayoutInflater, container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ): View {
//        // Inflate the layout using view binding
//        _binding = FragmentInvoiceHistoryBinding.inflate(inflater, container, false)
//        return binding.root
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//
//        // fetch invoices from db
//
//        bookingDao.getAll().observe(viewLifecycleOwner) { data ->
//            adapter.updateData(data)
//            binding.tvNoInvoiceRecordFound.visibility = if (data.isEmpty()) {
//                View.VISIBLE
//            } else {
//                View.GONE
//            }
//        }
//
//        binding.rvInvoices.adapter = adapter
//        binding.rvInvoices.layoutManager = LinearLayoutManager(requireContext())
//
//        binding.searchBarInvoiceHistory.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String?): Boolean {
//                query?.let { searchInvoices(it) }
//                return true
//            }
//
//            override fun onQueryTextChange(newText: String?): Boolean {
//                newText?.let { searchInvoices(it) }
//                return true
//            }
//        })
//    }
//
//    private fun searchInvoices(query: String) {
//        val searchQuery = "%${query.lowercase()}%"
//        lifecycleScope.launch(Dispatchers.IO) {
//            val filteredInvoices = bookingDao.searchByNameOrPackage(searchQuery)
//            launch(Dispatchers.Main) {
//                adapter.updateData(filteredInvoices)
//                binding.tvNoInvoiceRecordFound.visibility =
//                    if (filteredInvoices.isEmpty()) View.VISIBLE else View.GONE
//            }
//        }
//    }
//
//    override fun onDestroyView() {
//        super.onDestroyView()
//        _binding = null
//    }
//}


import android.app.DatePickerDialog
import android.util.Log
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class InvoiceHistoryFragment : Fragment() {

    private var _binding: FragmentInvoiceHistoryBinding? = null
    private val binding get() = _binding!!

    private val bookingDao by lazy { LocalDatabase.getDatabase(requireContext()).bookingDao() }
    private val adapter by lazy { InvoiceHistoryAdapter(emptyList(), requireContext()) }

    private var startDate: Long = 0
    private var endDate: Long = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInvoiceHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Load all invoices initially
        bookingDao.getAll().observe(viewLifecycleOwner) { data ->
            adapter.updateData(data)
            binding.tvNoInvoiceRecordFound.visibility = if (data.isEmpty()) View.VISIBLE else View.GONE
        }

        binding.rvInvoices.adapter = adapter
        binding.rvInvoices.layoutManager = LinearLayoutManager(requireContext())

        binding.searchBarInvoiceHistory.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let { searchInvoices(it) }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let { searchInvoices(it) }
                return true
            }
        })

        // Handle calendar icon click
        binding.ivcalender.setOnClickListener {
            showDateRangePicker()
        }
    }

    private fun showDateRangePicker() {
        val calendar = Calendar.getInstance()

        // Select Start Date
        DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
            val startCalendar = Calendar.getInstance()
            startCalendar.set(year, month, dayOfMonth, 0, 0, 0)
            startDate = startCalendar.timeInMillis

            // Select End Date
            DatePickerDialog(requireContext(), { _, endYear, endMonth, endDay ->
                val endCalendar = Calendar.getInstance()
                endCalendar.set(endYear, endMonth, endDay, 23, 59, 59)
                endDate = endCalendar.timeInMillis

                filterInvoicesByDate()

            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun filterInvoicesByDate() {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        val startDateString = sdf.format(Date(startDate)) // Convert Long to String
        val endDateString = sdf.format(Date(endDate)) // Convert Long to String

        Log.d("InvoiceFilter", "Filtering from $startDateString to $endDateString") // Debugging Log

        lifecycleScope.launch(Dispatchers.IO) {
            val filteredInvoices = bookingDao.getInvoicesByDateRange(startDateString, endDateString)
            launch(Dispatchers.Main) {
                adapter.updateData(filteredInvoices)
                binding.tvNoInvoiceRecordFound.visibility = if (filteredInvoices.isEmpty()) View.VISIBLE else View.GONE
            }
        }
    }

    private fun searchInvoices(query: String) {
        val searchQuery = "%${query.lowercase()}%"
        lifecycleScope.launch(Dispatchers.IO) {
            val filteredInvoices = bookingDao.searchByNameOrPackage(searchQuery)
            launch(Dispatchers.Main) {
                adapter.updateData(filteredInvoices)
                binding.tvNoInvoiceRecordFound.visibility =
                    if (filteredInvoices.isEmpty()) View.VISIBLE else View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
