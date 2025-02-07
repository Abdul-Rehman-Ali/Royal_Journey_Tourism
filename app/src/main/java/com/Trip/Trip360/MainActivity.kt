package com.Trip.Trip360

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.Trip.Trip360.data.LocalDatabase
import com.Trip.Trip360.databinding.ActivityMainBinding
import com.Trip.Trip360.repository.FirebaseRepository
import com.Trip.Trip360.utils.NetworkUtils
import com.Trip.Trip360.utils.SharedPrefUtils
import com.Trip.Trip360.utils.SharedPrefUtils.KEY_WEB_NAME
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var firebaseRepo: FirebaseRepository

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize SharedPreferences
//        val sharedPreferences = getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)

        // Fetch webName and primaryColor from SharedPreferences
        val webName = SharedPrefUtils.getValue(this, KEY_WEB_NAME,"Default WebName")
//        val colorHex = SharedPrefUtils.getValue(this, KEY_COLOR,"#FF5733")

//        Log.d("testingBaseApplication", "Webname: $webName, color: $colorHex")

        // Firebase sync


//        NativeApi.generateInvoicePdf(layoutInflater)

        if (NetworkUtils.isInternetAvailable(this)) {
            val bookingDao = LocalDatabase.getDatabase(this).bookingDao()

            lifecycleScope.launch(Dispatchers.IO) {
                firebaseRepo = FirebaseRepository(bookingDao)
                firebaseRepo.syncMissedRecords(webName)
            }
        }

        // Set up the layout and toolbar
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up toolbar
//        setSupportActionBar(binding.toolbar)
//        supportActionBar?.title = webName // Set the toolbar title to webName

        binding.toolbarTitle.text = webName

        // Apply the primary color across the app
//        applyPrimaryColor(colorHex)

        // Navigation drawer setup
        toggle = ActionBarDrawerToggle(
            this, binding.drawerLayout, binding.toolbar,
            R.string.nav_open, R.string.nav_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()


        // Handle navigation item selection
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menuHome -> {
                    openFragment(HomeFragment())
                }

                R.id.menuAboutUs -> {
                    openFragment(AboutUsFragment())
                }

                R.id.menuInvoiceHistory -> {
                    openFragment(InvoiceHistoryFragment())
                }
                R.id.menuLogout -> {
                    // Firebase sign-out
                    FirebaseAuth.getInstance().signOut()

                    // Clear SharedPreferences
                    val sharedPreferences = getSharedPreferences("ClientDataPref", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.clear() // Clears all stored data in SharedPreferences
                    editor.apply()

                    // Redirect to the login screen
                    val intent = Intent(this, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)

                    // Optionally show a message to the user (e.g., Toast)
                    Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()

                    true
                }

            }
            binding.drawerLayout.closeDrawers()
            true
        }

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.menuHome -> {
                    openFragment(HomeFragment())
                }
                R.id.menuAboutUs -> {
                    openFragment(AboutUsFragment())
                }
                R.id.menuProfile -> {
                    openFragment(ProfileFragment())
                }
            }
            true
        }

        // Open HomeFragment by default
        openFragment(HomeFragment())
    }


    private fun applyPrimaryColor(colorHex: String?) {
        // Parse the color or use a fallback default
        val primaryColor = try {
            Color.parseColor(colorHex)
        } catch (e: IllegalArgumentException) {
            ContextCompat.getColor(
                this,
                R.color.primaryColor
            )
        }

        // Apply the color to the toolbar, status bar, and navigation drawer
        binding.toolbar.setBackgroundColor(primaryColor)
        window.statusBarColor = primaryColor // Status bar color

        val headerView = binding.navigationView.getHeaderView(0)
        headerView.setBackgroundColor(primaryColor)
    }

    private fun openFragment(fragment: Fragment) {
            supportFragmentManager.beginTransaction().apply {
            replace(R.id.content_frame, fragment)
            addToBackStack(null)
            commit()
        }
    }
}