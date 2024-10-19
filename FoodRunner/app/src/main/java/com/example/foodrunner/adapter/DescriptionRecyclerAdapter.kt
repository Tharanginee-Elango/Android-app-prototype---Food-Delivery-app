package com.example.foodrunner.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.foodrunner.R
import com.example.foodrunner.model.Food

class DescriptionRecyclerAdapter(val context: Context, val foodList: ArrayList<Food>):RecyclerView.Adapter<DescriptionRecyclerAdapter.DescriptionViewHolder>() {

    class DescriptionViewHolder(view:View):RecyclerView.ViewHolder(view){
        val serialNo : TextView = view.findViewById(R.id.serialNo)
        val nameOfFood: TextView = view.findViewById(R.id.nameOfFood)
        val foodPrice : TextView = view.findViewById(R.id.foodPrice)
        val addButton: Button = view.findViewById(R.id.addButton)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DescriptionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_description_food_items,parent,false)
        return DescriptionViewHolder(view)
    }

    override fun onBindViewHolder(holder: DescriptionViewHolder, position: Int) {
        val food = foodList[position]
        holder.nameOfFood.text = food.foodName
        holder.foodPrice.text = food.foodPrice.toString()
        //holder.serialNo.text = foodList.count().toString() //need to check if any error occurs
        holder.addButton.setOnClickListener {
            if(holder.addButton.text == "Add"){
                holder.addButton.text = "Remove"
                val removeColor = ContextCompat.getColor(context,R.color.removeBtnColor)
                holder.addButton.setBackgroundColor(removeColor)
                Toast.makeText(context,
                    "Food added to Order List", Toast.LENGTH_SHORT).show()
            }else{
                holder.addButton.text = "Add"
                val addColor = ContextCompat.getColor(context,R.color.colorAccent)
                holder.addButton.setBackgroundColor(addColor)
                Toast.makeText(context,
                    "Food removed from Order List", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int {
        return foodList.size
    }
}