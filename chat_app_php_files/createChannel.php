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
    
    if($_SERVER["REQUEST_METHOD"] != "POST"){
        echo json_encode(array( 'Success' => false,'status' => 404, 'message' => 'Page Not Found!'));
    }
    
    elseif( !isset($data['user1']) 
    || !isset($data['user2'])

    || empty(trim($data['user1']))
    || empty(trim($data['user2']))
    ){
        
        echo json_encode(array( 'Success' => false, 'status' => 422, 'message' => 'Please Fill in all Required Fields!'));
    }
    else{
        
        
        $user1 = trim($data['user1']);
        $user2 = trim($data['user2']);
    
        $sql = "INSERT INTO channels (user1, user2) VALUES ('{$user1}', '{$user2}')";
    
        if (mysqli_query($conn, $sql)) {
            // Retrieve the newly inserted channel data
            $newChannelId = mysqli_insert_id($conn);
            $selectSql = "SELECT * FROM channels WHERE id = {$newChannelId}";
            $result = mysqli_query($conn, $selectSql);
    
            if ($result && mysqli_num_rows($result) > 0) {
                $channelData = mysqli_fetch_assoc($result);
                echo json_encode(array('Success' => true, 'status' => 201, 'message' => 'Channel Created.', 'channel' => $channelData));
            } else {
                echo json_encode(array('Success' => false, 'message' => 'Channel data retrieval failed.'));
            }
        }else{
            echo json_encode(array('Success' => false, 'message' => 'Channel Not Created.'));
        }
    }