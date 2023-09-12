<?php
// Database configuration
$servername = "localhost"; // Change to your database server hostname if necessary
$username = "root"; // Change to your database username
$password = ""; // Change to your database password
$database = "xampp_chat_app_db"; // Change to your database name

// Create a connection to the database
$conn = new mysqli($servername, $username, $password, $database);

// Check connection
if ($conn->connect_error) {
    echo json_encode(array('Success' => false, 'message' => 'Couldnt connect to XAMPP sever'));
    die("Connection failed: " . $conn->connect_error);
}
else{
    echo json_encode(array('Success' => true, 'message' => 'Connected to XAMPP sever'));
}

// Set character set to UTF-8 (optional, but recommended)
$conn->set_charset("utf8");
$conn->close();

// You can use the $conn variable in your PHP scripts to interact with the database
// For example, you can perform queries using $conn->query().

// Close the database connection when done (usually at the end of your PHP script)
// $conn->close();
?>
