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

if ($_SERVER["REQUEST_METHOD"] != "POST") {
    echo json_encode(array('Success' => false, 'status' => 404, 'message' => 'Page Not Found!'));
} else {
    $data = json_decode(file_get_contents("php://input"), true);

    if (isset($data['channelId'], $data['message_text'], $data['receiver_id'], $data['sender_id'], $data['timestamp']) &&
        !empty(trim($data['channelId'])) && !empty(trim($data['message_text'])) &&
        !empty(trim($data['receiver_id'])) && !empty(trim($data['sender_id'])) &&
        !empty(trim($data['timestamp']))) {

        $channelId = trim($data['channelId']);
        $messageText = trim($data['message_text']);
        $receiverId = trim($data['receiver_id']);
        $senderId = trim($data['sender_id']);
        $timestamp = trim($data['timestamp']);

        // Construct the SQL INSERT statement
        $sql = "INSERT INTO messages (channelId, message_text, receiver_id, sender_id, timestamp) VALUES ('$channelId', '$messageText', $receiverId, $senderId, '$timestamp')";

        if ($conn->query($sql) === TRUE) {
            echo json_encode(array('Success' => true, 'status' => 200, 'message' => 'Message inserted successfully.'));
        } else {
            echo json_encode(array('Success' => false, 'message' => 'Error inserting message: ' . $conn->error));
        }
    } else {
        echo json_encode(array('Success' => false, 'status' => 422, 'message' => 'Please provide all required fields.'));
    }
}

$conn->close();
?>
