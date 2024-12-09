package com.RoyalJourneyTourism.RJT

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.lifecycleScope
import com.RoyalJourneyTourism.RJT.data.LocalDatabase
import com.RoyalJourneyTourism.RJT.databinding.ActivityMainBinding
import com.RoyalJourneyTourism.RJT.repository.FirebaseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var firebaseRepo: FirebaseRepository

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        window.statusBarColor = ContextCompat.getColor(this, R.color.primaryColor)

        // firebase sync
        val bookingDao = LocalDatabase.getDatabase(this).bookingDao()
        lifecycleScope.launch(Dispatchers.IO) {
            firebaseRepo = FirebaseRepository(bookingDao)
            firebaseRepo.syncMissedRecords()
        }

        // Initialize View Binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up Toolbar
        setSupportActionBar(binding.toolbar)

        // Configure Drawer Toggle
        toggle = ActionBarDrawerToggle(this, binding.drawerLayout, binding.toolbar,
            R.string.nav_open, R.string.nav_close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Handle Navigation Item Clicks
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.about_us -> {
                    val i = Intent(this, splashScreen::class.java)
                    startActivity(i)
                }

                R.id.invoiceHistory -> {
                    openFragment(InvoiceHistoryFragment())
                }
            }
            binding.drawerLayout.closeDrawers()
            true
        }
        openFragment(HomeFragment())
    }

    private fun openFragment(fragment: Fragment){
        val fragmentTransaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.content_frame, fragment)
        fragmentTransaction.commit()
    }
}
