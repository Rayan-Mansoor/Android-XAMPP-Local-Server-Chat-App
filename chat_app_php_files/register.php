<?php

header('Content-Type: application/json');
header('Access-Control-Allow-Origin: *');
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Allow-Headers: Access-Control-Allow-Headers, Content-Type, Access-Control-Allow-Methods, Authorization, X-Requested-With");

$servername = "localhost";
$username = "root";
$password = ""; // Leave it empty if you haven't set a password
$database = "xampp_chat_app_db";

// Create a connection to the database
$conn = new mysqli($servername, $username, $password, $database);

// Check the connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

$data = json_decode(file_get_contents("php://input"), true);

if ($_SERVER["REQUEST_METHOD"] != "POST") {
    echo json_encode(array('Success' => false, 'status' => 404, 'message' => 'Page Not Found!'));
} elseif (isset($data['imageData']) && isset($data['id'])) {
    // Check if 'imageData' and 'email' are provided; if so, save the image for the user

    $id = trim($data['id']);
    $imageData = trim($data['imageData']);

    // Perform image saving logic here (insert $imageData into the user's record in the database)

    // Example code to insert the image into the user's record (modify as needed):
    $sql = "UPDATE users SET image = ? WHERE id = ?";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("ss", $imageData, $id);

    if ($stmt->execute()) {
        echo json_encode(array('Success' => true, 'status' => 200, 'message' => 'Image saved for user.'));
    } else {
        echo json_encode(array('Success' => false, 'message' => 'Image not saved.'));
    }
} elseif (isset($data['email']) && isset($data['password']) && !empty(trim($data['email'])) && !empty(trim($data['password']))) {
    // Check if 'email' and 'password' are provided; if so, register the user

    $email = trim($data['email']);
    $password = trim($data['password']);

    // Perform user registration logic here (insert $email and $password into the 'users' table)

    // Example code to insert the user into the 'users' table (modify as needed):
    $sql = "INSERT INTO users (email, password) VALUES (?, ?)";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("ss", $email, $password);

    if ($stmt->execute()) {
        echo json_encode(array('Success' => true, 'status' => 201, 'message' => 'You have successfully registered.'));
    } else {
        echo json_encode(array('Success' => false, 'message' => 'User Record Not Inserted.'));
    }
} else {
    echo json_encode(array('Success' => false, 'status' => 422, 'message' => 'Invalid Input Data.'));
}

// Close the database connection
$conn->close();
?>
