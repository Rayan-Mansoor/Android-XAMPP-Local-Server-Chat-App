package com.example.chatapplication.Activities

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.example.chatapplication.ApiResponses.LoginResponse.LoginResponse
import com.example.chatapplication.Utils.RetrofitClient
import com.example.chatapplication.databinding.ActivityLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var binding : ActivityLoginBinding
    private var isInfoValid : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.myEmail.hint = "Email Address"
        binding.myPassword.hint = "Password"

        binding.myEmail.setOnFocusChangeListener { view, hasFocus ->
            if (hasFocus){
                binding.textInputLayout5.error = null
            }
            else if (!hasFocus){
                if (!Patterns.EMAIL_ADDRESS.matcher(binding.myEmail.text.toString()).matches()){
                    isInfoValid = false
                    binding.textInputLayout5.error = "Invalid Email"
                }
                else{
                    isInfoValid = true
                }
            }
        }

        binding.myEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.myEmail.hint = null
                if (binding.myEmail.text.isNullOrEmpty()){
                    binding.myEmail.hint = "Email Address"
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        binding.myPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.myPassword.hint = null
                if (binding.myPassword.text.isNullOrEmpty()){
                    binding.myPassword.hint = "Password"
                }
            }

            override fun afterTextChanged(p0: Editable?) {}

        })


        binding.loginAccBtn.setOnClickListener {
            binding.myEmail.clearFocus()
            loginAcc()
        }

        binding.createAccActivityBtn.setOnClickListener {
            switchToCreateAcc()
        }

    }

    private fun loginAcc() {
        if (isInfoValid){
            setProgressBar(true)
            val apiService = RetrofitClient.apiService


            val body: MutableMap<String, Any> = HashMap()
            body["email"] = binding.myEmail.text.toString()
            body["password"] = binding.myPassword.text.toString()


            val call = apiService.login(body)

            call.enqueue(object : Callback<LoginResponse> {
                override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                    if (response.body()!!.Success) {

                        Log.e("LoginActivity", "onResponse: "+response.body() )
                        setProgressBar(false)


                        val sharedPref = getSharedPreferences("chatapp_shared_prefs", Context.MODE_PRIVATE)
                        val editor = sharedPref.edit()
                        editor.putBoolean("logged_in", true)
                        editor.putInt("User_ID", response.body()!!.user.id)
                        editor.putString("User_Email", response.body()!!.user.email)
                        editor.apply()

                        Toast.makeText(this@LoginActivity,"Login Successful", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()

                    } else {

                        Log.e("LoginActivity", "onResponse: "+response.code() )
                        Toast.makeText(this@LoginActivity,"Invalid Credentials", Toast.LENGTH_SHORT).show()
                        setProgressBar(false)
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    Log.e("LoginActivity", "response failed")
                    setProgressBar(false)
                }
            })
        }
        else{
            Toast.makeText(this,"Invalid Email Entered", Toast.LENGTH_SHORT).show()
        }
    }

    private fun switchToCreateAcc() {
        startActivity(Intent(this, SignUpActivity::class.java))
        finish()
    }

    private fun setProgressBar(progress : Boolean){
        if (progress){
            binding.progressBar.visibility = View.VISIBLE
        }
        else
            binding.progressBar.visibility = View.INVISIBLE
    }

}