package com.example.chatapplication.Activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chatapplication.ApiResponses.ConnectiionApiResponse.ConnectionStatus
import com.example.chatapplication.Utils.RetrofitClient
import com.example.chatapplication.databinding.ActivitySignUpBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SignUpActivity : AppCompatActivity() {
    private lateinit var binding : ActivitySignUpBinding
    private var isInfoValid : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.regEmail.hint = "Email Address"
        binding.regPassword.hint = "Password"
        binding.regRetypePassword.hint = "Confirm Password"

        binding.regEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.regEmail.hint = null
                if (binding.regEmail.text.isNullOrEmpty()){
                    binding.regEmail.hint = "Email Address"
                }
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        binding.regEmail.setOnFocusChangeListener { view, hasFocus ->
            Log.d("CreateAccount","Focused change listener of email called")
            if (hasFocus){
                binding.textInputLayout3.error = null
            }
            else if(!hasFocus){
                if (!Patterns.EMAIL_ADDRESS.matcher(binding.regEmail.text.toString()).matches()){
                    isInfoValid = false
                    binding.textInputLayout3.error = "Invalid Email"
                }
                else{
                    isInfoValid = true
                }
            }
        }


        binding.regPassword.setOnFocusChangeListener { view, hasFocus ->
            Log.d("CreateAccount","Focused change listener of password called")
            if (hasFocus){
                binding.textInputLayout.error = null
            }
            else if (!hasFocus){
                if (binding.regPassword.text.toString().length<6){
                    binding.textInputLayout.error = "Password Length should be at least 6"
                    isInfoValid = false
                }
                else{
                    isInfoValid = true
                }
            }
        }

        binding.regPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.regPassword.hint = null
                if (binding.regPassword.text.isNullOrEmpty()){
                    binding.regPassword.hint = "Password"
                }
            }

            override fun afterTextChanged(p0: Editable?) {}

        })

        binding.regRetypePassword.setOnFocusChangeListener { view, hasFocus ->
            Log.d("CreateAccount","Focused change listener of confirm password called")
            if (hasFocus){
                binding.textInputLayout2.error = null
            }
            else if (!hasFocus){
                if (binding.regRetypePassword.text.toString().length<6){
                    isInfoValid = false
                    binding.textInputLayout2.error = "Passwords don't match"
                }
                else{
                    isInfoValid = true
                }
            }
        }

        binding.regRetypePassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.regRetypePassword.hint = null
                if (binding.regRetypePassword.text.isNullOrEmpty()){
                    binding.regRetypePassword.hint = "Confirm Password"
                }
            }

            override fun afterTextChanged(p0: Editable?) {}

        })

        binding.regAccBtn.setOnClickListener {
            binding.regEmail.clearFocus()
            binding.regPassword.clearFocus()
            binding.regRetypePassword.clearFocus()
            Log.e("TAG", "Create Acc clicked")
            createAcc()
        }

        binding.loginAccActivityBtn.setOnClickListener {
            switchToLoginAcc()
        }
    }

    private fun createAcc(){
        if (isInfoValid){
            setProgressBar(true)
            val apiService = RetrofitClient.apiService


            val body: MutableMap<String, Any> = HashMap()
            body["email"] = binding.regEmail.text.toString()
            body["password"] = binding.regPassword.text.toString()


            val call = apiService.register(body)

            call.enqueue(object : Callback<ConnectionStatus> {
                override fun onResponse(call: Call<ConnectionStatus>, response: Response<ConnectionStatus>) {
                    if (response.isSuccessful) {

                        Log.e("SignUpActivity", "onResponse: "+response.body() )
                        Toast.makeText(this@SignUpActivity,"Account Created Successfully",Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
                        setProgressBar(false)

                    } else {

                        Log.e("SignUpActivity", "onResponse: "+response.code() )
                        setProgressBar(false)

                    }
                }

                override fun onFailure(call: Call<ConnectionStatus>, t: Throwable) {
                    Log.e("SignUpActivity", "response failed")
                    Log.e("SignUpActivity",""+t.message)
                    setProgressBar(false)
                }
            })
        }
    }

    private fun switchToLoginAcc() {
        startActivity(Intent(this, LoginActivity::class.java))
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