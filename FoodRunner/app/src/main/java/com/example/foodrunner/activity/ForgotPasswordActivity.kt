package com.example.foodrunner.activity

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.JsonRequest
import com.android.volley.toolbox.Volley
import com.example.foodrunner.R
import com.example.foodrunner.util.ConnectionManager
import org.json.JSONObject
import java.lang.Exception
import java.util.regex.Pattern

class ForgotPasswordActivity : AppCompatActivity() {

    lateinit var etUsername: EditText
    lateinit var etEmail: EditText
    lateinit var btnForgotPassword: Button

    lateinit var mobileInput: String
    lateinit var emailInput: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)


        etUsername = findViewById(R.id.etUsername)
        etEmail = findViewById(R.id.etEmail)
        btnForgotPassword = findViewById(R.id.btnForgotPassword)

        btnForgotPassword.setOnClickListener {
            mobileInput = etUsername.text.toString().trim()
            emailInput = etEmail.text.toString().trim()

            if (mobileInput.isNotEmpty() && emailInput.isNotEmpty()) {
                val queue = Volley.newRequestQueue(this@ForgotPasswordActivity)
                val url = "http://13.235.250.119/v2/forgot_password/fetch_result"

                val jsonParams = JSONObject()
                jsonParams.put("mobile_number", mobileInput)
                jsonParams.put("email", emailInput)

                if (ConnectionManager().checkConnectivity(this@ForgotPasswordActivity)) {
                    val jsonRequest =
                        object : JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener {

                            println("OTP Response is $it")
                            try {
                                val data = it.getJSONObject("data")
                                val success = data.getBoolean("success")
                                if (success) {
                                    val firstTry = data.getBoolean("first_try")
                                    val intent =
                                        Intent(this@ForgotPasswordActivity, PasswordResetActivity::class.java)
                                    intent.putExtra("mobile_number",mobileInput)
                                    startActivity(intent)
                                    if (firstTry) {
                                        Toast.makeText(
                                            this@ForgotPasswordActivity,
                                            "OTP is sent to your registered Email Id",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    } else {
                                        Toast.makeText(
                                            this@ForgotPasswordActivity,
                                            "OTP will be same for 24hours, already sent to your email",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    }
                                } else {
                                    Toast.makeText(this@ForgotPasswordActivity, "Invalid Credentials, Please Register", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            } catch (e: Exception) {
                                Toast.makeText(this@ForgotPasswordActivity, "some Error occurred!!", Toast.LENGTH_SHORT)
                                    .show()
                            }

                        }, Response.ErrorListener {
                            Toast.makeText(this@ForgotPasswordActivity, "Volley Error occurred!!", Toast.LENGTH_SHORT)
                                .show()
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
                    val dialog = AlertDialog.Builder(this@ForgotPasswordActivity)
                    dialog.setTitle("Success")
                    dialog.setMessage("Internet Connection not Found")
                    dialog.setPositiveButton("Open Settings") { text, listner ->
                        val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(settingsIntent)
                        finish()
                    }
                    dialog.setNegativeButton("Exit") { text, listner ->
                        ActivityCompat.finishAffinity(this@ForgotPasswordActivity)
                    }
                    dialog.create()
                    dialog.show()

                }
            }
        }

    }


    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}