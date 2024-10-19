package com.example.foodrunner.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodrunner.R
import com.example.foodrunner.util.ConnectionManager
import org.json.JSONObject
import java.lang.Exception
import java.util.regex.Pattern

class LoginActivity : AppCompatActivity() {

    lateinit var etUsername : EditText
    lateinit var etPassword : EditText
    lateinit var loginBtn : Button
    lateinit var txtForgotPwd : TextView
    lateinit var txtRegister : TextView

    lateinit var userInput:String
    lateinit var password:String

    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)

        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn",false)

        if(isLoggedIn){
            val intent = Intent(this@LoginActivity, DrawerActivity::class.java)
            startActivity(intent)
            finish()
        }

        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        loginBtn = findViewById(R.id.loginBtn)
        txtForgotPwd = findViewById(R.id.txtForgotPwd)
        txtRegister = findViewById(R.id.txtRegister)

        etUsername.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (mobileValidate(etUsername.text.toString())) {
                    loginBtn.isEnabled = true
                } else {
                    loginBtn.isEnabled = false
                    etUsername.error = "Invalid Mobile Number"
                }

            }
        })

        loginBtn.setOnClickListener {
           logIn()
        }

        txtForgotPwd.setOnClickListener {
            val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
        txtRegister.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
    }

    fun logIn(){
        userInput = etUsername.text.toString().trim()
        password = etPassword.text.toString().trim()
        if (userInput.isNotEmpty() && password.isNotEmpty()) {
            val queue = Volley.newRequestQueue(this@LoginActivity)
            val url = "http://13.235.250.119/v2/login/fetch_result"

            val jsonParams = JSONObject()
            jsonParams.put("mobile_number", userInput)
            jsonParams.put("password", password)


            if (ConnectionManager().checkConnectivity(this@LoginActivity)) {
                val jsonRequest =
                    object : JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener {

                        try {
                            val data = it.getJSONObject("data")
                            val success = data.getBoolean("success")
                            if (success) {
                                data.getJSONObject("data")
                                savePreferences()
                                val intent = Intent(this@LoginActivity, DrawerActivity::class.java)
                                startActivity(intent)
                            } else {
                                Toast.makeText(
                                    this@LoginActivity,
                                    "Invalid credentials,You may not registered yet!!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(this@LoginActivity, "Error occurred!!", Toast.LENGTH_SHORT).show()
                        }

                    }, Response.ErrorListener {
                        Toast.makeText(this@LoginActivity, "Volley Error occurred!!", Toast.LENGTH_SHORT).show()
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
                val dialog = AlertDialog.Builder(this@LoginActivity)
                dialog.setTitle("Success")
                dialog.setMessage("Internet Connection Found")
                dialog.setPositiveButton("Open Settings") { text, listner ->
                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingsIntent)
                    finish()
                }
                dialog.setNegativeButton("Exit") { text, listner ->
                    ActivityCompat.finishAffinity(this@LoginActivity)
                }
                dialog.create()
                dialog.show()

            }

        }
    }


    private fun mobileValidate(text: String?): Boolean {
        var p = Pattern.compile("[6-9][0-9]{9}")
        var m = p.matcher(text)
        return m.matches()
    }


    fun savePreferences(){
        sharedPreferences.edit().putBoolean("isLoggedIn",true).apply()
    }

}