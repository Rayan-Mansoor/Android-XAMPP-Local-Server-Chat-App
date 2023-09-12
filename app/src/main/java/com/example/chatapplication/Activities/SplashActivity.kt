package com.example.chatapplication.Activities

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.chatapplication.ApiResponses.ConnectiionApiResponse.ConnectionStatus
import com.example.chatapplication.Utils.RetrofitClient
import com.example.chatapplication.databinding.ActivitySplashBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SplashActivity : AppCompatActivity() {
    private lateinit var progressDialog : ProgressDialog
    private lateinit var binding : ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)

        setContentView(binding.root)


        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Checking Connection TO XAMPP Server 192.168.18.10")
        progressDialog.show()



        checkServerConnectivity()

        binding.closeApp.setOnClickListener{
            finish()
        }

        binding.conn.setOnClickListener{
            val newIP = binding.ipET.text.toString()
            progressDialog = ProgressDialog(this)
            progressDialog.setTitle("Checking Connection TO XAMPP Server ${newIP}" )
            progressDialog.show()
            RetrofitClient.BASE_URL = "http://${newIP}/chat_app_php_files/"
            checkServerConnectivity()
        }
    }

    private fun checkServerConnectivity(){
        Log.e("SplashActivity", "Check Server")
        val apiService = RetrofitClient.apiService
        val call = apiService.connect()

        call.enqueue(object : Callback<ConnectionStatus> {
            override fun onResponse(call: Call<ConnectionStatus>, response: Response<ConnectionStatus>) {
                if (response.body()!!.Success) {

                    Toast.makeText(applicationContext,response.body()!!.message, Toast.LENGTH_SHORT).show()

                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    finish()
                    progressDialog.dismiss()


                } else {

                    Toast.makeText(applicationContext,response.body()!!.message, Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss()

                    binding.ipET.visibility = View.VISIBLE
                    binding.conn.visibility = View.VISIBLE
                    binding.closeApp.visibility = View.VISIBLE


                }
            }

            override fun onFailure(call: Call<ConnectionStatus>, t: Throwable) {
                Toast.makeText(applicationContext,"Couldn't Establish Connection With XAMPP Server", Toast.LENGTH_SHORT).show()

                progressDialog.dismiss()

                binding.ipET.visibility = View.VISIBLE
                binding.conn.visibility = View.VISIBLE
                binding.closeApp.visibility = View.VISIBLE

                binding.closeApp.setOnClickListener{
                    finish()
                }

                Log.e("LoginActivity", "response failed")
            }
        })
    }
}