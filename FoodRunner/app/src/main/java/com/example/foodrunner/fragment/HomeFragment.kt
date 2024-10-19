package com.example.foodrunner.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodrunner.R
import com.example.foodrunner.adapter.RestaurantRecyclerAdapter
import com.example.foodrunner.database.RestaurantDatabase
import com.example.foodrunner.database.RestaurantEntity
import com.example.foodrunner.model.Restaurant
import com.example.foodrunner.util.ConnectionManager
import org.json.JSONException

class HomeFragment : Fragment() {

    lateinit var restaurantRecyclerView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var restaurantRecyclerAdapter : RestaurantRecyclerAdapter

    lateinit var progressLayout : RelativeLayout
    lateinit var progressBar  :ProgressBar

    val restaurantInfoList = arrayListOf<Restaurant>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        restaurantRecyclerView = view.findViewById(R.id.restaurantRecyclerView)
        layoutManager = LinearLayoutManager(activity)

        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)
        progressLayout.visibility = View.VISIBLE

        val queue = Volley.newRequestQueue(activity as Context)

        val url = "http://13.235.250.119/v2/restaurants/fetch_result/"
            if(ConnectionManager().checkConnectivity(activity as Context)) {

            val jsonObjectRequest = object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {

                try {
                    println("Response is $it")
                    progressLayout.visibility = View.GONE
                    val data = it.getJSONObject("data")

                    val success = data.getBoolean("success")

                    if (success) {
                        val data1 = data.getJSONArray("data")
                        for (i in 0 until data1.length()) {
                            val restaurantJsonObject = data1.getJSONObject(i)
                            val restaurantObject = Restaurant(
                                restaurantJsonObject.getString("id").toInt(),
                                restaurantJsonObject.getString("name"),
                                restaurantJsonObject.getString("rating"),
                                restaurantJsonObject.getString("cost_for_one").toInt(),
                                restaurantJsonObject.getString("image_url")
                            )
                            restaurantInfoList.add(restaurantObject)
                            restaurantRecyclerAdapter =
                                RestaurantRecyclerAdapter(activity as Context, restaurantInfoList)
                            restaurantRecyclerView.adapter = restaurantRecyclerAdapter
                            restaurantRecyclerView.layoutManager = layoutManager
                        }
                    } else {
                        Toast.makeText(activity as Context, "some error Occurred!!", Toast.LENGTH_SHORT).show()
                    }
                }catch (e: JSONException){
                    Toast.makeText(activity as Context, "some Unexpected error Occurred!!", Toast.LENGTH_SHORT).show()

                }
            }, Response.ErrorListener {
                println("Error is $it")
                Toast.makeText(activity as Context, "Volley error Occurred!!", Toast.LENGTH_SHORT).show()

            }) {
                override fun getHeaders(): MutableMap<String, String> {
                    val headers = HashMap<String, String>()
                    headers["Content_type"] = "application/json"
                    headers["token"] = "0a7b9112e11135"
                    return headers
                }
            }
            queue.add(jsonObjectRequest)
        }else{
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection not Found")
            dialog.setPositiveButton("Open Settings"){text,listner ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()
            }
            dialog.setPositiveButton("Exit"){text,listner ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }

        return view
    }




}