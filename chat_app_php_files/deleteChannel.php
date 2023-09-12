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
} elseif (!empty($data['channelID'])) {
    // If channelID is provided, delete by channelID
    $chID = trim($data['channelID']);
    
    $sql = "DELETE FROM `channels` WHERE id = {$chID}";

    if (mysqli_query($conn, $sql)) {
        echo json_encode(array('Success' => true, 'status' => 201, 'message' => 'Channel Deleted.'));
    } else {
        echo json_encode(array('Success' => false, 'message' => 'Channel Not Deleted.'));
    }
} elseif (!empty($data['user1Id']) && !empty($data['user2Id'])) {
    // If senderId and receiverId are provided, delete by senderId and receiverId
    $senderId = trim($data['user1Id']);
    $receiverId = trim($data['user2Id']);

    $sql = "DELETE FROM `channels` WHERE user1 = {$senderId} AND user2 = {$receiverId}";

    if (mysqli_query($conn, $sql)) {
        echo json_encode(array('Success' => true, 'status' => 201, 'message' => 'Channels Deleted by senderId and receiverId.'));
    } else {
        echo json_encode(array('Success' => false, 'message' => 'Channels Not Deleted.'));
    }
} else {
    echo json_encode(array('Success' => false, 'status' => 422, 'message' => 'Please Fill in all Required Fields!'));
}

?>
