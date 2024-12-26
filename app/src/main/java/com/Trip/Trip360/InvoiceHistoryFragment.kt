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

class InvoiceHistoryFragment : Fragment() {

    private var _binding: FragmentInvoiceHistoryBinding? = null
    private val binding get() = _binding!!

    private val bookingDao by lazy { LocalDatabase.getDatabase(requireContext()).bookingDao() }

    private val adapter by lazy { InvoiceHistoryAdapter(emptyList(), requireContext())}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout using view binding
        _binding = FragmentInvoiceHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // fetch invoices from db

        lifecycleScope.launch(Dispatchers.Main) {
            adapter.updateData(bookingDao.getAll())
        }

        binding.rvInvoices.adapter = adapter
        binding.rvInvoices.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
