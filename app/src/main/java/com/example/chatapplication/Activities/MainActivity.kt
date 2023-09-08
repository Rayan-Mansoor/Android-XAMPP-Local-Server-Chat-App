package com.example.chatapplication.Activities

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.InputType
import android.util.Base64
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.chatapplication.ApiResponses.LoginResponse.LoginResponse
import com.example.chatapplication.ApiResponses.ChannelApiResponse.Channel
import com.example.chatapplication.ApiResponses.ChannelApiResponse.ChannelResponse
import com.example.chatapplication.ApiResponses.ChannelApiResponse.CreateChannelResponse
import com.example.chatapplication.R
import com.example.chatapplication.Adapters.UsersListAdapter
import com.example.chatapplication.Utils.RetrofitClient
import com.example.chatapplication.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var currentUser : Int = 0
    private var otherUser : String = ""
    private lateinit var usersListAdapter : UsersListAdapter
    private lateinit var addedUsers : ArrayList<Channel>
    private lateinit var DPbitmap : Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(R.style.Theme_MainAct)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPref = getSharedPreferences("chatapp_shared_prefs", Context.MODE_PRIVATE)
        val loggedIn = sharedPref.getBoolean("logged_in", false)

        if (!loggedIn) {
            Log.d("MainActivity","Not Logged in")
            // Redirect to login activity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        else{
            setSupportActionBar(binding.mainToolbar)
            currentUser = sharedPref.getInt("User_ID",0)!!
            Toast.makeText(this,"Logged In as: "+sharedPref.getString("User_Email",""),Toast.LENGTH_SHORT).show()
            searchChannels()

            binding.currentUser.setText(sharedPref.getString("User_Email",""))


            CoroutineScope(Dispatchers.Main).launch {


                val deferredMyDP = async {
                    getUserImage(currentUser)
                }

                val myDP = deferredMyDP.await()

                Log.e("MainActivity", "myDP : $myDP")
                if (!myDP.isNullOrEmpty()){
                    Log.e("MainActivity", "inside if block")

                    val decodedImageBytes = Base64.decode(myDP, Base64.DEFAULT)

                    // Convert the decoded image data to a Bitmap
                    DPbitmap = BitmapFactory.decodeByteArray(decodedImageBytes, 0, decodedImageBytes.size)

                    Glide.with(this@MainActivity)
                        .load(DPbitmap)
                        .apply(
                            RequestOptions()
                                .placeholder(R.drawable.ic_loginuser)  // Optional placeholder image
                                .error(R.drawable.ic_loginuser))  // Optional error image
                        .diskCacheStrategy(DiskCacheStrategy.ALL)  // Caching strategy
                        .into(binding.toolBarDP)
                }

            }

        }

        addedUsers  = ArrayList()

        usersListAdapter = UsersListAdapter(addedUsers, currentUser, this)
        binding.usersRcv.adapter = usersListAdapter
        binding.usersRcv.layoutManager = LinearLayoutManager(this)

        binding.swipeRefreshLayout.setOnRefreshListener {
            searchChannels()
            binding.swipeRefreshLayout.isRefreshing = false
        }


    }

    override fun onResume() {
        super.onResume()
        CoroutineScope(Dispatchers.Main).launch {

            val deferredMyDP = async {
                getUserImage(currentUser)
            }

            val myDP = deferredMyDP.await()

            Log.e("MainActivity", "myDP : $myDP")
            if (!myDP.isNullOrEmpty()){
                Log.e("MainActivity", "inside if block")

                val decodedImageBytes = Base64.decode(myDP, Base64.DEFAULT)

                // Convert the decoded image data to a Bitmap
                DPbitmap = BitmapFactory.decodeByteArray(decodedImageBytes, 0, decodedImageBytes.size)

                Glide.with(this@MainActivity)
                    .load(DPbitmap)
                    .apply(
                        RequestOptions()
                            .placeholder(R.drawable.ic_loginuser)  // Optional placeholder image
                            .error(R.drawable.ic_loginuser))  // Optional error image
                    .diskCacheStrategy(DiskCacheStrategy.ALL)  // Caching strategy
                    .into(binding.toolBarDP)
            }

        }

        usersListAdapter.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.my_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.logout){
            val sharedPref = getSharedPreferences("chatapp_shared_prefs", Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putBoolean("logged_in", false)
            editor.apply()

            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return true
        }

        if(item.itemId == R.id.uploadImage){

            Log.e("MainActivity", "upload image clicked")

            startActivity(Intent(this, ProfileSettingsActivity::class.java).putExtra("currentUserID", currentUser))
        }

        if (item.itemId == R.id.addPerson){

            var emailToSearch = ""
            val emailET = EditText(this)
            emailET.inputType = InputType.TYPE_CLASS_TEXT
            emailET.hint = "Enter the email of that person"
            val builder = AlertDialog.Builder(this).setView(emailET)
            builder.setPositiveButton("ADD") { _, _ ->
                emailToSearch = emailET.text.toString()
                Log.d("Main",emailToSearch)


                val apiService = RetrofitClient.apiService


                val body: MutableMap<String, Any> = HashMap()
                body["email"] = emailToSearch


                val call = apiService.searchUser(body)

                call.enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                        if (response.body()!!.Success) {

                            Log.e("MainActivity", "onResponse: "+response.body() )
                            Toast.makeText(this@MainActivity,"User Found", Toast.LENGTH_SHORT).show()

                            createChannel(response.body()!!.user.id)
                            otherUser = response.body()!!.user.email

                            Log.e("MainActivity", otherUser )


                        } else {

                            Log.e("MainActivity", "onResponse: "+response.code() )
                            Toast.makeText(this@MainActivity,"User Not Found", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Log.e("MainActivity", "response failed")
                    }
                })

            }.show()

        }
        return false
    }

    private fun createChannel(foundUser : Int){
        val apiService = RetrofitClient.apiService


        val body: MutableMap<String, Any> = HashMap()
        body["user1"] = currentUser
        body["user2"] = foundUser


        val call = apiService.createChannel(body)

        call.enqueue(object : Callback<CreateChannelResponse> {
            override fun onResponse(call: Call<CreateChannelResponse>, response: Response<CreateChannelResponse>) {
                if (response.body()!!.Success) {

                    addedUsers.add(response.body()!!.channel)
                    usersListAdapter.notifyDataSetChanged()
                    Log.e("MainActivity", "onResponse: "+response.body() )
                    Toast.makeText(this@MainActivity,"User Found", Toast.LENGTH_SHORT).show()

                } else {

                    Log.e("MainActivity", "onResponse: "+response.code() )
                    Toast.makeText(this@MainActivity,"User Not Found", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CreateChannelResponse>, t: Throwable) {
                Log.e("MainActivity", "response failed")
            }
        })
    }

    private fun searchChannels(){
        val apiService = RetrofitClient.apiService

        Log.d("MainActivity", currentUser.toString())

        val body: MutableMap<String, Any> = HashMap()
        body["currentUser"] = currentUser


        val call = apiService.searchChannels(body)

        call.enqueue(object : Callback<ChannelResponse> {
            override fun onResponse(call: Call<ChannelResponse>, response: Response<ChannelResponse>) {
                if (!response.body()!!.channels.isNullOrEmpty()) {


                    Log.d("MainActivity", response.body()!!.channels.toString())

                    val newChannels = response.body()!!.channels

                    for (channel in newChannels) {
                        // Check if the message is not already in chatList
                        if (!addedUsers.contains(channel)) {
                            // Add the message to chatList
                            addedUsers.add(channel)
                        }
                    }

                    val iterator = addedUsers.iterator()
                    while (iterator.hasNext()) {
                        val channel = iterator.next()
                        if (!newChannels.contains(channel)) {
                            iterator.remove()
                        }
                    }


//                    addedUsers.addAll(response.body()!!.channels)
                    usersListAdapter.notifyDataSetChanged()

                    Log.e("MainActivity", "onResponse: "+response.body() )
                    Toast.makeText(this@MainActivity,"Channels Found", Toast.LENGTH_SHORT).show()

                } else {

                    Log.e("MainActivity", "onResponse: "+response.code() )
                    Toast.makeText(this@MainActivity,"Channels Not Found", Toast.LENGTH_SHORT).show()
                    addedUsers.clear()
                }
            }

            override fun onFailure(call: Call<ChannelResponse>, t: Throwable) {
                Log.e("MainActivity", "response failed")
            }
        })
    }

    private suspend fun getUserImage(UserID: Int): String? {
        val apiService = RetrofitClient.apiService

        val body: MutableMap<String, Any> = HashMap()
        body["userId"] = UserID

        return try {
            val response = withContext(Dispatchers.IO) {
                apiService.getImage(body).execute()
            }

            if (response.body()!!.Success) {
                val myDpPath = response.body()?.base64Image ?: ""
                Log.e("MainActivity", myDpPath )
                myDpPath
            } else {
                Log.e("MainActivity", "Empty" )
                null
                // Handle the case where the response is not successful.
                ""
            }
        } catch (e: Exception) {
            // Handle the network call exception.
            ""
        }
    }

}