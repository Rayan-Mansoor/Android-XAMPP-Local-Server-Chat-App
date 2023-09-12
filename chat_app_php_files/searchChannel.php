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
} elseif (isset($data['user1']) && !empty(trim($data['user1'])) && !isset($data['user2'])) {
    // Only user1 is filled, run a query to search all rows where user1 matches
    $user1 = trim($data['user1']);
    $sql = "SELECT * FROM channels WHERE user1 = '{$user1}'";
    $result = mysqli_query($conn, $sql);

    if ($result) {
        $rows = array();
        while ($row = mysqli_fetch_assoc($result)) {
            $rows[] = $row;
        }
        echo json_encode(array('Success' => true, 'status' => 200, 'message' => 'Channels found.', 'channels' => $rows));
    } else {
        echo json_encode(array('Success' => false, 'message' => 'Query failed.'));
    }
} elseif (isset($data['user1']) && !empty(trim($data['user1'])) && isset($data['user2']) && !empty(trim($data['user2']))) {
    // Both user1 and user2 are filled, run a query to search for rows where both user1 and user2 match
    $user1 = trim($data['user1']);
    $user2 = trim($data['user2']);
    $sql = "SELECT * FROM channels WHERE user1 = '{$user1}' AND user2 = '{$user2}'";
    $result = mysqli_query($conn, $sql);

    if ($result) {
        $rows = array();
        while ($row = mysqli_fetch_assoc($result)) {
            $rows[] = $row;
        }
        echo json_encode(array('Success' => true, 'status' => 200, 'message' => 'Channels found.', 'channels' => $rows));
    } else {
        echo json_encode(array('Success' => false, 'message' => 'Query failed.'));
    }
} else {
    echo json_encode(array('Success' => false, 'status' => 422, 'message' => 'Please Fill in all Required Fields!'));
}
?>
