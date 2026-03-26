package com.example.smartinventory

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.example.smartinventory.util.SessionManager
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set up toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Set up drawer
        drawerLayout = findViewById(R.id.drawerLayout)
        toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // Set up navigation
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Set up navigation view
        val navigationView = findViewById<NavigationView>(R.id.navigationView)

        // Update drawer header with user info
        val sessionManager = SessionManager(this)
        val headerView = navigationView.getHeaderView(0)
        val tvDrawerUsername = headerView.findViewById<TextView>(R.id.tvDrawerUsername)
        val tvDrawerEmail = headerView.findViewById<TextView>(R.id.tvDrawerEmail)
        val tvAvatarLetter = headerView.findViewById<TextView>(R.id.tvAvatarLetter)

        if (sessionManager.isLoggedIn()) {
            tvDrawerUsername.text = sessionManager.getUsername() ?: "User"
            tvDrawerEmail.text = sessionManager.getEmail() ?: ""
            tvAvatarLetter.text = sessionManager.getUsername()?.first()?.uppercase() ?: "U"
            navController.navigate(R.id.dashboardFragment)
        }

        // Handle drawer item clicks
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.dashboardFragment -> {
                    navController.navigate(R.id.dashboardFragment)
                }
                R.id.itemListFragment -> {
                    navController.navigate(R.id.itemListFragment)
                }
                R.id.addEditItemFragment -> {
                    navController.navigate(R.id.addEditItemFragment)
                }
                R.id.action_logout -> {
                    sessionManager.clearSession()
                    drawerLayout.closeDrawer(GravityCompat.START)
                    navController.navigate(R.id.loginFragment)
                }
            }
            menuItem.isChecked = true
            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // Show or hide the drawer toggle based on which screen we are on
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.loginFragment, R.id.registerFragment -> {
                    // Hide toolbar on login and register screens
                    toolbar.visibility = View.GONE
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                }
                else -> {
                    // Show toolbar on all other screens
                    toolbar.visibility = View.VISIBLE
                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)

                    // Update drawer header when navigating back after login
                    if (sessionManager.isLoggedIn()) {
                        tvDrawerUsername.text = sessionManager.getUsername() ?: "User"
                        tvDrawerEmail.text = sessionManager.getEmail() ?: ""
                        tvAvatarLetter.text = sessionManager.getUsername()?.first()?.uppercase() ?: "U"
                    }
                }
            }
        }
    }

    // Close drawer on back press if it is open
    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }
}