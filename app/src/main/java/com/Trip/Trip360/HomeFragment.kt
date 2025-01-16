package com.Trip.Trip360

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

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var webName: String
    private lateinit var imageUrl: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize webName and imageUrl here where the context is available
        webName = SharedPrefUtils.getValue(requireContext(), KEY_WEB_NAME, "Default WebName")
        imageUrl = SharedPrefUtils.getValue(requireContext(), KEY_LOGO_URL, "")

        Log.d("HomeFragment", "WebName: $webName")
        Log.d("HomeFragment", "Logo URL: $imageUrl")

        // Set the header text to webName
        binding.tvHeaderText.text = "Welcome to $webName dashboard"

        // Load the image from the URL into the ImageView
        val logoLocalPath = getValue(requireContext(), KEY_LOGO_LOCAL_FILE_PATH, "")
        binding.imgHeader.setImageDrawable(Drawable.createFromPath(logoLocalPath))
//
//        if (imageUrl.isNotEmpty()) {
//
//
////            Glide.with(this)
////                .load(imageUrl)
////                .apply(RequestOptions.placeholderOf(R.drawable.logo).error(R.drawable.logo))
////                .listener(object : com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable> {
////                    override fun onLoadFailed(
////                        e: GlideException?,
////                        model: Any?,
////                        target: com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable>?,
////                        isFirstResource: Boolean
////                    ): Boolean {
////                        Log.e("HomeFragment", "Image Load Failed: ${e?.message}")
////                        return false
////                    }
////
////                    override fun onResourceReady(
////                        resource: android.graphics.drawable.Drawable?,
////                        model: Any?,
////                        target: com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable>?,
////                        dataSource: com.bumptech.glide.load.DataSource?,
////                        isFirstResource: Boolean
////                    ): Boolean {
////                        Log.d("HomeFragment", "Image Load Successful")
////                        return false
////                    }
////                })
////                .into(binding.imgHeader)
//        } else {
//            // Set a default placeholder if no URL is found
//            binding.imgHeader.setImageResource(R.drawable.logo)
//            Log.d("HomeFragment", "Logo URL is empty, using default placeholder")
//        }

        // Handle button click
        binding.btnHomeGenerateInvoice.setOnClickListener {
            startActivity(Intent(requireContext(), InvoiceActivity::class.java))
        }

        binding.btnHomeSeeInvoiceHistory.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.content_frame, InvoiceHistoryFragment()) // Replace with your container ID
                .addToBackStack(null) // Allows back navigation
                .commit()
        }

        binding.btnHomeSeeTemplates.setOnClickListener {
            showTemplateDialog { template ->
                Log.d("HomeFragment", "Selected template: $template")
            }
        }
    }


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