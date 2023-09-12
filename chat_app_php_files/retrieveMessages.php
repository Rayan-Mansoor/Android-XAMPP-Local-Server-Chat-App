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
} elseif (isset($data['channelId']) && !empty(trim($data['channelId']))) {
    // Only user1 is filled, run a query to search all rows where user1 matches
    $channelId = trim($data['channelId']);
    $sql = "SELECT * FROM messages WHERE channelId = '{$channelId}'";
    $result = mysqli_query($conn, $sql);

    if ($result) {
        $rows = array();
        while ($row = mysqli_fetch_assoc($result)) {
            $rows[] = $row;
        }
        echo json_encode(array('Success' => true, 'status' => 200, 'message' => 'Messages found.', 'messages' => $rows));
    } else {
        echo json_encode(array('Success' => false, 'message' => 'Query failed.'));
    }
} else {
    echo json_encode(array('Success' => false, 'status' => 422, 'message' => 'Please Fill in all Required Fields!'));
}
?>
