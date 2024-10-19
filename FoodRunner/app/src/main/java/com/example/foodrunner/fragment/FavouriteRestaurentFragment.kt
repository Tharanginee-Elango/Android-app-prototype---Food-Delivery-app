package com.example.foodrunner.fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.foodrunner.R
import com.example.foodrunner.adapter.RestaurantRecyclerAdapter
import com.example.foodrunner.database.RestaurantDatabase
import com.example.foodrunner.database.RestaurantEntity
import com.example.foodrunner.model.Restaurant

class FavouriteRestaurentFragment : Fragment() {
    lateinit var restaurantRecyclerView: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var restaurantRecyclerAdapter: RestaurantRecyclerAdapter

    lateinit var rlFavorites: RelativeLayout
    lateinit var rlNoFavorites: RelativeLayout
    lateinit var imgEmptyCart: ImageView

    lateinit var progressLayout: RelativeLayout
    lateinit var progressBar: ProgressBar

    var restaurant = arrayListOf<Restaurant>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_favourite_restaurent, container, false)
        restaurantRecyclerView = view.findViewById(R.id.restaurantRecyclerView)
        rlFavorites = view.findViewById(R.id.rlFavorites)
        rlNoFavorites = view.findViewById(R.id.rlNoFavorites)
        imgEmptyCart = view.findViewById(R.id.imgEmptyCart)

        layoutManager = LinearLayoutManager(activity)

        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)
        progressLayout.visibility = View.VISIBLE

        val dbRestaurantList = RetrieveFavourite(activity as Context).execute().get()
        if (dbRestaurantList.isEmpty()) {
            progressLayout.visibility = View.GONE
            rlFavorites.visibility = View.GONE
            rlNoFavorites.visibility = View.VISIBLE
        } else {
            progressLayout.visibility = View.GONE
            rlFavorites.visibility = View.VISIBLE
            rlNoFavorites.visibility = View.GONE
            for (i in dbRestaurantList) {
                restaurant.add(
                    Restaurant(
                        i.restaurant_Id,
                        i.restaurantName,
                        i.restaurantRating,
                        i.restaurantPrice.toInt(),
                        i.restaurantImage
                        )
                )
            }
        }
        restaurantRecyclerAdapter = RestaurantRecyclerAdapter(activity as Context,restaurant)
        restaurantRecyclerView.adapter = restaurantRecyclerAdapter
        restaurantRecyclerView.layoutManager = layoutManager
        restaurantRecyclerView.itemAnimator = DefaultItemAnimator()
        restaurantRecyclerView.setHasFixedSize(true)

        return view
    }

    class RetrieveFavourite(val context: Context) : AsyncTask<Void, Void, List<RestaurantEntity>>() {
        override fun doInBackground(vararg params: Void?): List<RestaurantEntity> {
            val db = Room.databaseBuilder(context, RestaurantDatabase::class.java, "restaurant_db").build()

            return db.restaurantDao().getAllRestaurants()
        }

    }


}