package com.example.foodrunner.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.*
import com.example.foodrunner.R
import com.example.foodrunner.util.ConnectionManager
import org.json.JSONObject
import java.lang.Exception
import java.util.regex.Pattern

class RegisterActivity : AppCompatActivity() {
    lateinit var etPersonName : EditText
    lateinit var etEmail : EditText
    lateinit var etMobileNumber : EditText
    lateinit var etDeliveryAddress : EditText
    lateinit var etRegisterPassword : EditText
    lateinit var etConfirmPassword : EditText
    lateinit var btnRegister : Button

    lateinit var usernameInput:String
    lateinit var emailInput: String
    lateinit var mobileInput: String
    lateinit var passwordInput: String
    lateinit var confirmPassword: String
    lateinit var addressInput: String

    lateinit var toolbar: Toolbar

    lateinit var sharedPreferences: SharedPreferences


    private val PERSONNAME_PATTERN = Pattern.compile(
        "^" +  //"(?=.*[0-9])" +         //at least 1 digit
                //"(?=.*[a-z])" +         //at least 1 lower case letter
                //"(?=.*[A-Z])" +         //at least 1 upper case letter
                "(?=.*[a-zA-Z])" +  //any letter
                //"(?=.*[@#$%^&+=])" +  //at least 1 special character
                "(?=\\S+$)" +  //no white spaces
                ".{3,}" +  //at least 4 characters
                "$"

    )
    val MOBILENO_PATTERN = Pattern.compile("[6-9][0-9]{9}")
    val PASSWORD_PATTERN = Pattern.compile("^" + "(?=.*[a-zA-Z])" +"(?=\\S+$)" + ".{4,}" + "$")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Register Yourself"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        etPersonName = findViewById(R.id.etPersonName)
        etEmail = findViewById(R.id.etEmail)
        etMobileNumber = findViewById(R.id.etMobileNumber)
        etDeliveryAddress = findViewById(R.id.etDeliveryAddress)
        etRegisterPassword = findViewById(R.id.etRegisterPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnRegister = findViewById(R.id.btnRegister)

        sharedPreferences = getSharedPreferences(getString(R.string.preference_register),Context.MODE_PRIVATE)

        btnRegister.setOnClickListener {
            if (!validateEmail() or !validateUsername() or !validateMobileNo() or !validatePassword() or !validateConfirmPassword() or !validateDeliveryAddress()) {
                return@setOnClickListener
            } else {
                val queue = Volley.newRequestQueue(this@RegisterActivity)
                val url = "http://13.235.250.119/v2/register/fetch_result"

                val jsonParams = JSONObject()
                jsonParams.put("name", usernameInput)
                jsonParams.put("mobile_number", mobileInput)
                jsonParams.put("password", passwordInput)
                jsonParams.put("address", addressInput)
                jsonParams.put("email", emailInput)


                if (ConnectionManager().checkConnectivity(this@RegisterActivity)) {
                    val jsonRequest =
                        object : JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener {

                            try {
                                val data = it.getJSONObject("data")
                                val success = data.getBoolean("success")
                                if (success) {
                                    data.getJSONObject("data")
                                    savePreferences(usernameInput,mobileInput,emailInput,addressInput)
                                    Toast.makeText(this@RegisterActivity, "Successfully Registered", Toast.LENGTH_SHORT)
                                        .show()
                                } else {
                                    Toast.makeText(this@RegisterActivity, "Error occurred!!", Toast.LENGTH_SHORT).show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(this@RegisterActivity, "some Error occurred!!", Toast.LENGTH_SHORT).show()
                            }

                        }, Response.ErrorListener {
                            Toast.makeText(this@RegisterActivity, "Volley Error occurred!!", Toast.LENGTH_SHORT).show()
                        }) {
                            override fun getHeaders(): MutableMap<String, String> {
                                val headers = HashMap<String, String>()
                                headers["Content-type"] = "application/json"
                                headers["token"] = "0a7b9112e11135"
                                return headers
                            }

                        }
                    queue.add(jsonRequest)
                } else {
                    val dialog = AlertDialog.Builder(this@RegisterActivity)
                    dialog.setTitle("Success")
                    dialog.setMessage("Internet Connection Not Found")
                    dialog.setPositiveButton("Open Settings") { text, listner ->
                        val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(settingsIntent)
                        finish()
                    }
                    dialog.setNegativeButton("Exit") { text, listner ->
                        ActivityCompat.finishAffinity(this@RegisterActivity)
                    }
                    dialog.create()
                    dialog.show()


                }
            }
        }
    }


    private fun validateEmail(): Boolean {
        emailInput = etEmail.text.toString().trim()
        return if (emailInput.isEmpty()) {
            etEmail.error = "Field can't be empty"
            false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            etEmail.error = "Please enter a valid email address"
            false
        } else {
            etEmail.error = null
            true
        }
    }

    private fun validateUsername(): Boolean {
       usernameInput = etPersonName.text.toString().trim()
        return if (usernameInput.isEmpty()) {
            etPersonName.error = "Field can't be empty"
            false
        } else if (!PERSONNAME_PATTERN.matcher(usernameInput).matches()) {
            etPersonName.error = "Username too long"
            false
        } else {
            etPersonName.error = null
            true
        }
    }

    private fun validateMobileNo(): Boolean {
        mobileInput = etMobileNumber.text.toString().trim()
        return if (mobileInput.isEmpty()) {
            etMobileNumber.error = "Field can't be empty"
            false
        } else if (!MOBILENO_PATTERN.matcher(mobileInput).matches()) {
            etMobileNumber.error = "Invalid Mobile No"
            false
        } else {
            etMobileNumber.error = null
            true
        }
    }
    private fun validatePassword(): Boolean {
       passwordInput = etRegisterPassword.text.toString().trim()
        return if (passwordInput.isEmpty()) {
            etRegisterPassword.error = "Field can't be empty"
            false
        } else if (!PASSWORD_PATTERN.matcher(passwordInput).matches()) {
            etRegisterPassword.error = "Password too weak"
            false
        } else {
            etRegisterPassword.error = null
            true
        }
    }

    private fun validateConfirmPassword(): Boolean {
        confirmPassword = etConfirmPassword.text.toString().trim()
        return if (confirmPassword.isEmpty()) {
            etConfirmPassword.error = "Field can't be empty"
            false
        } else if ((passwordInput != confirmPassword)) {
            etConfirmPassword.error = "password not matched"
            false
        } else {
            etConfirmPassword.error = null
            true
        }

    }

    private fun validateDeliveryAddress(): Boolean {
        addressInput = etDeliveryAddress.text.toString().trim()
        return if (addressInput.isEmpty()) {
            etDeliveryAddress.error = "Field can't be empty"
            false
        } else {
            etDeliveryAddress.error = null
            true
        }
    }
    fun savePreferences(name:String,mobileNo:String,email:String,deliveryAddress:String){
        sharedPreferences.edit().putString("Name",name).apply()
        sharedPreferences.edit().putString("MobileNo",mobileNo).apply()
        sharedPreferences.edit().putString("Email",email).apply()
        sharedPreferences.edit().putString("DeliveryAddress",deliveryAddress).apply()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}