package com.example.chatapplication.Activities

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.chatapplication.R
import com.example.chatapplication.Utils.RetrofitClient
import com.example.chatapplication.databinding.ActivityProfileSettingsBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream

class ProfileSettingsActivity : AppCompatActivity() {
    private lateinit var binding : ActivityProfileSettingsBinding

    private lateinit var imageURI : Uri
    private val SELECT_IMAGE_REQUEST = 1
    private var userId : Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityProfileSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = intent.getIntExtra("currentUserID", 0)

        CoroutineScope(Dispatchers.Main).launch {

            val myDP = getUserImage(userId)

            val decodedImageBytes = Base64.decode(myDP, Base64.DEFAULT)

            // Convert the decoded image data to a Bitmap
            val DPbitmap = BitmapFactory.decodeByteArray(decodedImageBytes, 0, decodedImageBytes.size)

            Glide.with(this@ProfileSettingsActivity)
                .load(DPbitmap)
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.ic_loginuser)  // Optional placeholder image
                        .error(R.drawable.ic_loginuser))  // Optional error image
                .diskCacheStrategy(DiskCacheStrategy.ALL)  // Caching strategy
                .into(binding.DP)
        }


        binding.DP.setOnClickListener {
            selectImageFromGallery()
        }

        binding.saveBtn.setOnClickListener {
            if (::imageURI.isInitialized){
                val apiService = RetrofitClient.apiService

                val imageFile = uriToFile(this, imageURI)

                val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), imageFile)
                val imagePart = MultipartBody.Part.createFormData("image", imageFile!!.name, requestFile)


                Log.e("ProfileSettingsActivity",  userId.toString())
                val userIdRequestBody = RequestBody.create(MediaType.parse("text/plain"), userId.toString())


                val call = apiService.uploadImage(imagePart, userIdRequestBody)


                call.enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            Log.e("ProfileSettingsActivity",  response.body().toString())
                            val uploadResponse = response.body()
                            finish()
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Log.e("ProfileSettingsActivity", t.message.toString())
                        // Handle the error
                    }
                })
            }
            else{
                Toast.makeText(this,"No new image set", Toast.LENGTH_SHORT).show()
            }
        }

    }

    private fun uriToFile(context: Context, uri: Uri): File? {
        val contentResolver: ContentResolver = context.contentResolver
        val file = File(context.cacheDir, "temp_file")

        try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            if (inputStream != null) {
                val outputStream = FileOutputStream(file)
                val buffer = ByteArray(4 * 1024) // Adjust the buffer size as needed
                var read: Int
                while (inputStream.read(buffer).also { read = it } != -1) {
                    outputStream.write(buffer, 0, read)
                }
                outputStream.flush()
                outputStream.close()
                inputStream.close()
                return file
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    private fun selectImageFromGallery() {
        pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->

        if (uri != null) {
            imageURI = uri
            val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
            contentResolver.takePersistableUriPermission(uri, flag)
            Log.d("PhotoPicker", "Selected URI: $uri")
            Glide.with(applicationContext)
                .load(uri)
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.baseline_person_24)  // Optional placeholder image
                        .error(R.drawable.baseline_error_24))  // Optional error image
                .diskCacheStrategy(DiskCacheStrategy.ALL)  // Caching strategy
                .into(binding.DP)
        } else {
            Log.d("PhotoPicker", "No media selected")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SELECT_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            imageURI = data.data!!

            grantUriPermission(
                packageName,
                imageURI,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
            )

        }
    }

    private suspend fun getUserImage(UserID: Int): String {
        val apiService = RetrofitClient.apiService

        val body: MutableMap<String, Any> = HashMap()
        body["userId"] = UserID

        return try {
            val response = withContext(Dispatchers.IO) {
                apiService.getImage(body).execute()
            }

            if (response.isSuccessful) {
                val myDpPath = response.body()?.base64Image ?: ""
                Log.e("MainActivity", myDpPath )
                myDpPath
            } else {
                // Handle the case where the response is not successful.
                ""
            }
        } catch (e: Exception) {
            // Handle the network call exception.
            ""
        }
    }

    override fun onBackPressed() {
        if (::imageURI.isInitialized){
            val intent = Intent()
            intent.putExtra("UpdatedDP", imageURI)
            setResult(Activity.RESULT_OK, intent)
        }
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!isFinishing) {
            Glide.with(this).clear(binding.DP)
        }
    }

}