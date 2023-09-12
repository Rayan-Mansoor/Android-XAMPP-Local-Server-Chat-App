# Android_Local_Server_Chat_App
An Local Server Chat App that uses XAMPP (Authorization + database) as backend

STEP BY STEP GUIDE :

1) Download the repository and open the project in Android Studio.
2) Download and install XAMPP from https://www.apachefriends.org/download.html
3) Open php myadmin from xampp control panel and create a new database exactly by the name "xampp_chat_app_db".
   Keep the other settings default. Go to the import option and choose the xampp_chat_app_db.sql file provided in the repository.
   This will create all the necessary tables in your database.
4) Move the chat_app_php_files folder from the repository to the htdocs Folder in xampp. i.e. C:\xampp\htdocs. (Assuming You installed Xampp in the default location)
5) In the Android project. Open RetrofitClient.kt and only replace the IP address part of the baseURL with the IP address of your PC. you can find your IP address by running ipconfig in cmd
