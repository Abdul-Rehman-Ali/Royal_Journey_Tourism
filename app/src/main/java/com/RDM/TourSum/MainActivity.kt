//package com.RDM.TourSum
//
//import android.graphics.Color
//import android.os.Build
//import android.os.Bundle
//import androidx.annotation.RequiresApi
//import androidx.appcompat.app.ActionBarDrawerToggle
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.content.ContextCompat
//import androidx.fragment.app.Fragment
//import androidx.fragment.app.FragmentTransaction
//import androidx.lifecycle.lifecycleScope
//import com.RDM.TourSum.data.LocalDatabase
//import com.RDM.TourSum.databinding.ActivityMainBinding
//import com.RDM.TourSum.repository.FirebaseRepository
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//
//class MainActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivityMainBinding
//    private lateinit var toggle: ActionBarDrawerToggle
//    private lateinit var firebaseRepo: FirebaseRepository
//
//    @RequiresApi(Build.VERSION_CODES.S)
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        // Firebase sync
//        val bookingDao = LocalDatabase.getDatabase(this).bookingDao()
//        lifecycleScope.launch(Dispatchers.IO) {
//            firebaseRepo = FirebaseRepository(bookingDao)
//            firebaseRepo.syncMissedRecords()
//            bookingDao.deleteAllSyncedRecords()
//        }
//
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        // Fetch webName and primaryColor from SharedPreferences
//        val sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE)
//        val webName = sharedPreferences.getString("webName", "Default WebName") // Fallback to "Default WebName"
//        val colorHex = sharedPreferences.getString("primaryColor", "#FF5733") // Default color
//
//        // Set up toolbar
//        setSupportActionBar(binding.toolbar)
//        supportActionBar?.title = webName // Set the toolbar title to webName
//
//        // Apply the primary color across the app
//        applyPrimaryColor(colorHex)
//
//        // Navigation drawer setup
//        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar,
//            R.string.nav_open, R.string.nav_close)
//        binding.drawerLayout.addDrawerListener(toggle)
//        toggle.syncState()
//
//        // Handle navigation item selection
//        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
//            when (menuItem.itemId) {
//                R.id.about_us -> {
//                    openFragment(AboutUsFragment())
//                }
//
//                R.id.invoiceHistory -> {
//                    openFragment(InvoiceHistoryFragment())
//                }
//            }
//            binding.drawerLayout.closeDrawers()
//            true
//        }
//
//        // Open HomeFragment by default
//        openFragment(HomeFragment())
//    }
//
//    private fun applyPrimaryColor(colorHex: String?) {
//        // Parse the color or use a fallback default
//        val primaryColor = try {
//            Color.parseColor(colorHex ?: "#FF5733")
//        } catch (e: IllegalArgumentException) {
//            ContextCompat.getColor(this, R.color.primaryColor) // Fallback to a default color resource
//        }
//
//        // Apply the color to the toolbar, status bar, and navigation drawer
//        binding.toolbar.setBackgroundColor(primaryColor)
//        window.statusBarColor = primaryColor // Status bar color
//
//        // Update the navigation view header or other UI elements as needed
//        val headerView = binding.navigationView.getHeaderView(0)
//        headerView.setBackgroundColor(primaryColor)
//    }
//
//    private fun openFragment(fragment: Fragment) {
//        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
//        fragmentTransaction.replace(R.id.content_frame, fragment)
//        fragmentTransaction.commit()
//    }
//}

package com.RDM.TourSum

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import com.RDM.TourSum.data.LocalDatabase
import com.RDM.TourSum.databinding.ActivityMainBinding
import com.RDM.TourSum.repository.FirebaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var firebaseRepo: FirebaseRepository

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Firebase sync
        val bookingDao = LocalDatabase.getDatabase(this).bookingDao()
        lifecycleScope.launch(Dispatchers.IO) {
            firebaseRepo = FirebaseRepository(bookingDao)
            firebaseRepo.syncMissedRecords()
            bookingDao.deleteAllSyncedRecords()
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Fetch webName from SharedPreferences
        val sharedPreferences = getSharedPreferences("UserPreferences", MODE_PRIVATE)
        val webName = sharedPreferences.getString("webName", "Default WebName") // Fallback to "Default WebName"
        // Set up toolbar
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = webName // Set the toolbar title to webName

        // Navigation drawer setup
        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar,
            R.string.nav_open, R.string.nav_close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Handle navigation item selection
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.about_us -> {
                    openFragment(AboutUsFragment())
                }

                R.id.invoiceHistory -> {
                    openFragment(InvoiceHistoryFragment())
                }
            }
            binding.drawerLayout.closeDrawers()
            true
        }

        // Open HomeFragment by default
        openFragment(HomeFragment())
    }

    private fun openFragment(fragment: Fragment) {
        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.content_frame, fragment)
        fragmentTransaction.commit()
    }
}

