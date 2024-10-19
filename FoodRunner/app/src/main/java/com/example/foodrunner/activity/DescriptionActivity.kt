package com.example.foodrunner.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import android.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodrunner.R
import com.example.foodrunner.adapter.DescriptionRecyclerAdapter
import com.example.foodrunner.model.Food
import com.example.foodrunner.model.Restaurant
import com.example.foodrunner.util.ConnectionManager
import org.json.JSONObject

class DescriptionActivity : AppCompatActivity() {
    lateinit var descriptionRecyclerView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: DescriptionRecyclerAdapter


    val foodInfoList = arrayListOf<Food>()

    var restaurantId: String? = "100"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_description)

        descriptionRecyclerView = findViewById(R.id.descriptionRecyclerView)
        layoutManager = LinearLayoutManager(this)

        setSupportActionBar(findViewById(R.id.toolbar))


        if (intent != null) {
            val restaurantName = intent.getStringExtra("restaurant_name")
            supportActionBar?.title = restaurantName
            restaurantId = intent.getStringExtra("restaurant_id")


            val queue = Volley.newRequestQueue(this@DescriptionActivity)
            val url = "http://13.235.250.119/v2/restaurants/fetch_result/$restaurantId"

            if (ConnectionManager().checkConnectivity(this@DescriptionActivity)) {
                val jsonObjectRequest = object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {

                    try {
                        println("The response is $it")
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")

                        if (success) {
                            val data1 = data.getJSONArray("data")
                            for (i in 0 until data1.length()) {
                                val foodJsonObject = data1.getJSONObject(i)
                                val foodObject = Food(
                                    foodJsonObject.getString("id").toInt(),
                                    foodJsonObject.getString("name"),
                                    foodJsonObject.getString("cost_for_one").toInt(),
                                    foodJsonObject.getString("restaurant_id").toInt()
                                )
                                foodInfoList.add(foodObject)
                                recyclerAdapter = DescriptionRecyclerAdapter(this,foodInfoList)
                                descriptionRecyclerView.adapter = recyclerAdapter
                                descriptionRecyclerView.layoutManager = layoutManager
                                descriptionRecyclerView.setHasFixedSize(true)
                            }
                        }else {
                            Toast.makeText(this, "some error Occurred!!", Toast.LENGTH_SHORT).show()
                        }

                    } catch (e: Exception) {
                        Toast.makeText(this, "some Unexpected error Occurred!!", Toast.LENGTH_SHORT).show()
                    }

                }, Response.ErrorListener {
                    println("Error is $it")
                    Toast.makeText(this, "Volley error Occurred!!", Toast.LENGTH_SHORT).show()
                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "0a7b9112e11135"
                        return headers
                    }
                }
                queue.add(jsonObjectRequest)
            }else{
                val dialog = AlertDialog.Builder(this)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection not Found")
                dialog.setPositiveButton("Open Settings"){text,listner ->
                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingsIntent)
                    finish()
                }
                dialog.setPositiveButton("Exit"){text,listner ->
                    ActivityCompat.finishAffinity(this)
                }
                dialog.create()
                dialog.show()
            }
        } else {
            finish()
            Toast.makeText(this@DescriptionActivity, " unexpected error occurred", Toast.LENGTH_SHORT).show()
        }
        if (restaurantId == "100") {
            finish()
            Toast.makeText(this@DescriptionActivity, "Unexpected error occurred", Toast.LENGTH_SHORT).show()
        }


    }
}