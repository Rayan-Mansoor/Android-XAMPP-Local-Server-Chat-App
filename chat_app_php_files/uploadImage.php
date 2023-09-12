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

$uploadDir = 'C:/xampp/htdocs/chat_app_php_files/Images/'; // Specify the absolute path to your directory

if ($_SERVER["REQUEST_METHOD"] != "POST") {
    echo json_encode(array('Success' => false, 'status' => 404, 'message' => 'Page Not Found!'));
} elseif (isset($_FILES['image']) && $_FILES['image']['error'] === UPLOAD_ERR_OK) {
    $uploadFile = $uploadDir . uniqid() . '_' . basename($_FILES['image']['name']);

    if (move_uploaded_file($_FILES['image']['tmp_name'], $uploadFile)) {
        // Image uploaded successfully
        if (isset($_POST['userId'])) {
            $id = trim($_POST['userId']);

            $sql = "UPDATE users SET image = ? WHERE id = ?";
            $stmt = $conn->prepare($sql);
            $stmt->bind_param("ss", $uploadFile, $id);

            if ($stmt->execute()) {
                echo json_encode(array('Success' => true, 'status' => 200, 'message' => 'Image saved for user.'));
            } else {
                echo json_encode(array('Success' => false, 'message' => 'Image not saved.'));
            }
        } else {
            echo json_encode(array('Success' => false, 'message' => 'User ID not provided.'));
        }
    } else {
        echo json_encode(array('Success' => false, 'message' => 'Error uploading image'));
    }
} else {
    echo json_encode(array('Success' => false, 'status' => 422, 'message' => 'Invalid Input Data.'));
}

$conn->close();
?>
