package com.example.foodrunner.adapter

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.foodrunner.R
import com.example.foodrunner.activity.DescriptionActivity
import com.example.foodrunner.database.RestaurantDatabase
import com.example.foodrunner.database.RestaurantEntity
import com.example.foodrunner.model.Restaurant
import com.squareup.picasso.Picasso

class RestaurantRecyclerAdapter(val context: Context,val restaurantList: ArrayList<Restaurant>):RecyclerView.Adapter<RestaurantRecyclerAdapter.RestaurantViewHolder>(){

    class RestaurantViewHolder(view: View):RecyclerView.ViewHolder(view){
        val favouriteRedOutline:ImageView = view.findViewById(R.id.favouriteRedOutline)
        val restaurantImage: ImageView = view.findViewById(R.id.restaurantImage)
        val restaurantName: TextView = view.findViewById(R.id.restaurantName)
        val restaurantPrice: TextView = view.findViewById(R.id.restaurantPrice)
        val restaurantRating: TextView = view.findViewById(R.id.restaurantRating)
        val l1: LinearLayout = view.findViewById(R.id.l1)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_restaurant_items,parent,false)
        return RestaurantViewHolder(view)
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        val restaurant = restaurantList[position]
        holder.restaurantName.text = restaurant.restaurantName
        val restPrice ="${restaurant.restaurantPrice.toString()}/person"
        holder.restaurantPrice.text = restPrice
        holder.restaurantRating.text = restaurant.restaurantRating
        Picasso.get().
        load(restaurant.restaurantImage).
        error(R.drawable.ic_image).
        into(holder.restaurantImage)
        holder.l1.setOnClickListener {
            val intent = Intent(context,DescriptionActivity::class.java)
            intent.putExtra("restaurant_id",restaurant.restaurantId.toString())
            intent.putExtra("restaurant_name",restaurant.restaurantName)
            context.startActivity(intent)
            Toast.makeText(context,"Clicked on ${restaurant.restaurantId}",Toast.LENGTH_SHORT).show()
        }


        val listOfFav = GetAllFavRestaurants(context).execute().get()
        if(listOfFav.isNotEmpty() && listOfFav.contains(restaurant.restaurantId.toString())){
            holder.favouriteRedOutline.setImageResource(R.drawable.ic_favouritefullred)
        }else{
            holder.favouriteRedOutline.setImageResource(R.drawable.ic_favouriteredoutline)

        }

        holder.favouriteRedOutline.setOnClickListener {
            val restaurantEntity = RestaurantEntity(
                restaurant.restaurantId,
                restaurant.restaurantName,
                restaurant.restaurantRating,
                restaurant.restaurantPrice.toString(),
                restaurant.restaurantImage
            )
            if(!DbAsyncTask(context,restaurantEntity,1).execute().get()){
                val async = DbAsyncTask(context,restaurantEntity,2).execute()
                val result = async.get()
                if(result){
                    Toast.makeText(context,"Added to favourite ${restaurant.restaurantName}",Toast.LENGTH_SHORT).show()
                    holder.favouriteRedOutline.setImageResource(R.drawable.ic_favouritefullred)
                }else{
                    Toast.makeText(context,"error occurred!!",Toast.LENGTH_SHORT).show()
                }
            }else{
                val async = DbAsyncTask(context,restaurantEntity,3).execute()
                val result = async.get()
                if(result){
                    Toast.makeText(context,"Removed from favourite ${restaurant.restaurantName}",Toast.LENGTH_SHORT).show()
                    holder.favouriteRedOutline.setImageResource(R.drawable.ic_favouriteredoutline)
                }else{
                    Toast.makeText(context,"error occurred!!",Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return restaurantList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


    class DbAsyncTask(val context: Context, val restaurantEntity: RestaurantEntity, val mode: Int):
        AsyncTask<Void, Void, Boolean>(){
        val db = Room.databaseBuilder(context, RestaurantDatabase::class.java,"restaurant_db").build()

        override fun doInBackground(vararg params: Void?): Boolean {

            when(mode){
                1 ->{
                    //to check DB is the restaurant is in favourites or not
                    val restaurant: RestaurantEntity? = db.restaurantDao().getRestaurantsById(restaurantEntity.restaurant_Id.toString())
                    db.close()
                    return restaurant != null
                }
                2 ->{
                    //to insert details in Db
                    db.restaurantDao().insertRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
                3 ->{
                    //to delete favourite restaurant
                    db.restaurantDao().deleteRestaurant(restaurantEntity)
                    db.close()
                    return true
                }
            }
            return false
        }

    }


    class GetAllFavRestaurants(val context: Context) : AsyncTask<Void,Void,List<String>>() {
        val db = Room.databaseBuilder(context, RestaurantDatabase::class.java,"restaurant_db").build()

        override fun doInBackground(vararg params: Void?): List<String> {
            val list = db.restaurantDao().getAllRestaurants()
            val listOfIds = arrayListOf<String>()
            for(i in list){
                listOfIds.add(i.restaurant_Id.toString())
            }
            return listOfIds
        }
    }
}