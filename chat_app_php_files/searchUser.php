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
} elseif (!isset($data['email']) && !isset($data['userId'])) {
    echo json_encode(array('Success' => false, 'status' => 422, 'message' => 'Please provide either email or userId!'));
} else {
    $email = isset($data['email']) ? trim($data['email']) : null;
    $userId = isset($data['userId']) ? trim($data['userId']) : null;

    if ($email !== null) {
        // Search by email
        $sql = "SELECT * FROM users WHERE email = ?";
        $stmt = $conn->prepare($sql);
        if ($stmt) {
            $stmt->bind_param('s', $email);
        } else {
            echo json_encode(array('Success' => false, 'message' => 'Failed to prepare statement.'));
            exit;
        }
    } elseif ($userId !== null) {
        // Search by userId
        $sql = "SELECT * FROM users WHERE id = ?";
        $stmt = $conn->prepare($sql);
        if ($stmt) {
            $stmt->bind_param('i', $userId);
        } else {
            echo json_encode(array('Success' => false, 'message' => 'Failed to prepare statement.'));
            exit;
        }
    } else {
        echo json_encode(array('Success' => false, 'status' => 422, 'message' => 'Please provide either email or userId!'));
        exit;
    }

    // Execute the statement
    if ($stmt->execute()) {
        // Fetch the result
        $result = $stmt->get_result();

        if ($result->num_rows === 1) {
            // User found
            $user = $result->fetch_assoc();
            echo json_encode(array('Success' => true, 'status' => 200, 'message' => 'User Found.', 'user' => $user));
        } else {
            // User not found
            echo json_encode(array('Success' => false, 'status' => 401, 'message' => 'User Not Found.'));
        }
    } else {
        // Error executing the statement
        echo json_encode(array('Success' => false, 'status' => 500, 'message' => 'Internal Server Error.'));
    }

    // Close the statement
    $stmt->close();
}
?>
