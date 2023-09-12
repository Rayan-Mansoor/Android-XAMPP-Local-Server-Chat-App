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
} elseif (!empty($data['msgId'])) {
    // If channelID is provided, delete by channelID
    $msgID = trim($data['msgId']);
    
    $sql = "DELETE FROM `messages` WHERE id = {$msgID}";

    if (mysqli_query($conn, $sql)) {
        echo json_encode(array('Success' => true, 'status' => 201, 'message' => 'Mesage Deleted.'));
    } else {
        echo json_encode(array('Success' => false, 'message' => 'Message Not Deleted.'));
    }
} else {
    echo json_encode(array('Success' => false, 'status' => 422, 'message' => 'Please Fill in all Required Fields!'));
}

?>
