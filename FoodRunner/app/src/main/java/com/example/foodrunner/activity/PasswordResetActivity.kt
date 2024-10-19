package com.example.foodrunner.activity

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.foodrunner.R
import com.example.foodrunner.util.ConnectionManager
import org.json.JSONObject
import java.util.regex.Pattern

class PasswordResetActivity : AppCompatActivity() {

    lateinit var etOTP: EditText
    lateinit var etPassword: EditText
    lateinit var etConfirmPassword: EditText
    lateinit var btnResetPassword: Button

    lateinit var OTPInput: String
    lateinit var passwordInput: String
    lateinit var confirmPasswordInput: String
    lateinit var mobileNumber: String

    private val OTP_PATTERN = Pattern.compile("^" + ".{3,}" + "$")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_password_reset)

        if (intent != null) {
            mobileNumber = intent.getStringExtra("mobile_number").toString()
        }

        etOTP = findViewById(R.id.etOTP)
        etPassword = findViewById(R.id.etPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        btnResetPassword = findViewById(R.id.btnResetPassword)
        btnResetPassword.setOnClickListener {
            if (!validateOTP() or !validatePassword() or !validateConfirmPassword()) {
                return@setOnClickListener
            } else {
                val queue = Volley.newRequestQueue(this@PasswordResetActivity)
                val url = "http://13.235.250.119/v2/reset_password/fetch_result"

                val jsonParams = JSONObject()
                jsonParams.put("mobile_number", mobileNumber)
                jsonParams.put("password", passwordInput)
                jsonParams.put("otp", OTPInput)

                if (ConnectionManager().checkConnectivity(this@PasswordResetActivity)) {
                    val jsonRequest =
                        object : JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener {
                            println("password change response is $it")
                            try {
                                val data = it.getJSONObject("data")
                                val success = data.getBoolean("success")
                                if (success) {
                                    Toast.makeText(this@PasswordResetActivity, "Password has successfully changed", Toast.LENGTH_SHORT)
                                        .show()
                                    val intent = Intent(this@PasswordResetActivity,LoginActivity::class.java)
                                    startActivity(intent)
                                }else {
                                    Toast.makeText(this@PasswordResetActivity, "password unable to change,  Error occurred!!", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }catch (e:Exception){
                                Toast.makeText(this@PasswordResetActivity, "some Error occurred!!", Toast.LENGTH_SHORT)
                                    .show()
                            }

                        }, Response.ErrorListener {
                            Toast.makeText(this@PasswordResetActivity, "Volley Error occurred!!", Toast.LENGTH_SHORT)
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
                }else {
                    val dialog = AlertDialog.Builder(this@PasswordResetActivity)
                    dialog.setTitle("Success")
                    dialog.setMessage("Internet Connection not Found")
                    dialog.setPositiveButton("Open Settings") { text, listner ->
                        val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                        startActivity(settingsIntent)
                        finish()
                    }
                    dialog.setNegativeButton("Exit") { text, listner ->
                        ActivityCompat.finishAffinity(this@PasswordResetActivity)
                    }
                    dialog.create()
                    dialog.show()

                }
            }
        }
    }

    private fun validateOTP(): Boolean {
        OTPInput = etOTP.text.toString().trim()
        return if (OTPInput.isEmpty()) {
            etOTP.error = "Field can't be empty"
            false
        } else if (!OTP_PATTERN.matcher(OTPInput).matches()) {
            etOTP.error = "Username too long"
            false
        } else {
            etOTP.error = null
            true
        }
    }

    private fun validatePassword(): Boolean {
        passwordInput = etPassword.text.toString().trim()
        return if (passwordInput.isEmpty()) {
            etPassword.error = "Field can't be empty"
            false
        } else if (!OTP_PATTERN.matcher(passwordInput).matches()) {
            etPassword.error = "Username too long"
            false
        } else {
            etPassword.error = null
            true
        }
    }

    private fun validateConfirmPassword(): Boolean {
        confirmPasswordInput = etConfirmPassword.text.toString().trim()
        return if (confirmPasswordInput.isEmpty()) {
            etConfirmPassword.error = "Field can't be empty"
            false
        } else {
            etConfirmPassword.error = null
            true
        }
    }
}