package com.example.foodrunner.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.example.foodrunner.R
import com.example.foodrunner.fragment.*
import com.google.android.material.navigation.NavigationView

class DrawerActivity : AppCompatActivity() {

    lateinit var drawerLayout : DrawerLayout
    lateinit var coordinatorLayout : CoordinatorLayout
    lateinit var toolbar : Toolbar
    lateinit var frame : FrameLayout
    lateinit var navigationView : NavigationView

    var previousMenuItem :  MenuItem? = null

    lateinit var drawerPersonName: TextView
    lateinit var drawerMobileNo: TextView

    lateinit var sharedPreferences: SharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawer)

        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)


        drawerLayout = findViewById(R.id.drawerLayout)
        coordinatorLayout = findViewById(R.id.coordinatorLayout)
        toolbar = findViewById(R.id.toolbar)
        frame = findViewById(R.id.frame)
        navigationView = findViewById(R.id.navigationView)
        setUpToolbar()

        openHomePage()


        val actionBarDrawerToggle = ActionBarDrawerToggle(
            this@DrawerActivity,
            drawerLayout,
            R.string.open_drawer,
            R.string.close_drawer)

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()


        navigationView.setNavigationItemSelectedListener {


            if(previousMenuItem != null){
                previousMenuItem?.isCheckable = false
            }

            it.isCheckable = true
            it.isChecked = true
            previousMenuItem = it

            when(it.itemId){
                R.id.home ->{
                    openHomePage()
                    drawerLayout.closeDrawers()
                }
                R.id.myProfile ->{
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame,MyProfileFragment())
                        .commit()
                    drawerLayout.closeDrawers()
                    supportActionBar?.title = "My Profile"
                }
                R.id.favouriteRestaurants ->{
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame,FavouriteRestaurentFragment())
                        .commit()
                    drawerLayout.closeDrawers()
                    supportActionBar?.title = "Favourite Restaurants"
                }
                R.id.orderHistory ->{
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame, OrderHistoryFragment())
                        .commit()
                    drawerLayout.closeDrawers()
                    supportActionBar?.title = "Order History"
                }

                R.id.faqs ->{
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame,FAQsFragment())
                        .commit()
                    drawerLayout.closeDrawers()
                    supportActionBar?.title = "FAQs"
                }
                R.id.logOut ->{
                    val builder = AlertDialog.Builder(this@DrawerActivity)
                    builder.setTitle("Confirmation")
                        .setMessage("Are you sure you want to Exit?")
                        .setPositiveButton("Yes"){text,listener ->
                            val intent = Intent(this@DrawerActivity, LoginActivity ::class.java)
                            startActivity(intent)
                            sharedPreferences.edit().clear().apply()
                            finish()
                        }
                        .setNegativeButton("No"){text,listener ->
                            openHomePage()
                        }
                        .create()
                        .show()
                }
            }
            return@setNavigationItemSelectedListener true
        }

    }

    fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Toolbar Title"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item.itemId
        if(id == android.R.id.home){
            drawerLayout.openDrawer(GravityCompat.START)

        }
        return super.onOptionsItemSelected(item)
    }

    fun openHomePage(){
        val fragment = HomeFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame,fragment)
        transaction.commit()
        supportActionBar?.title = "All Restaurants"
        navigationView.setCheckedItem(R.id.home)
    }

    override fun onBackPressed() {
        val frag = supportFragmentManager.findFragmentById(R.id.frame)
        when(frag){
            !is HomeFragment -> openHomePage()
            else -> super.onBackPressed()
        }


    }

}