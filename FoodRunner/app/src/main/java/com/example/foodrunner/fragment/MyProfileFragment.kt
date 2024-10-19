package com.example.foodrunner.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.foodrunner.R

class MyProfileFragment : Fragment() {

    lateinit var txtUserName : TextView
    lateinit var txtMobileNo: TextView
    lateinit var txtEmail: TextView
    lateinit var txtDeliveryAddress: TextView

    lateinit var sharedPreferences: SharedPreferences



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_my_profile, container, false)

        txtUserName = view.findViewById(R.id.txtUserName)
        txtMobileNo = view.findViewById(R.id.txtMobileNo)
        txtEmail = view.findViewById(R.id.txtEmail)
        txtDeliveryAddress = view.findViewById(R.id.txtDeliveryAddress)

        sharedPreferences =(activity as Context).getSharedPreferences(getString(R.string.preference_register),Context.MODE_PRIVATE)

            txtUserName.text = sharedPreferences.getString("Name", null)
            txtMobileNo.text = sharedPreferences.getString("MobileNo", null)
            txtEmail.text = sharedPreferences.getString("Email", null)
            txtDeliveryAddress.text = sharedPreferences.getString("DeliveryAddress", null)


        return view
    }


}