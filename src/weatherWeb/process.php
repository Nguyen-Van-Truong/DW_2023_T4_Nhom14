<?php
// Database configuration
$servername = "localhost";
$username = "root"; // default XAMPP username
$password = "";     // default XAMPP password
$dbname = "mart";   // your database name

// Create connection
$conn = new mysqli($servername, $username, $password, $dbname);

// Check connection
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

$selectedLocation = $_GET['location'] ?? '';
$selectedDate = $_GET['date'] ?? '';
$weatherData = [];
$locations = [];

// Fetch unique locations from database
$locationsResult = $conn->query("SELECT DISTINCT location_name FROM weather_web_view ORDER BY location_name");
if ($locationsResult->num_rows > 0) {
    while ($location = $locationsResult->fetch_assoc()) {
        $locations[] = $location;
    }
}

// SQL query to fetch data based on selection
if ($selectedLocation && $selectedDate) {
    $sql = "SELECT * FROM weather_web_view WHERE location_name = '$selectedLocation' AND date = '$selectedDate'";
    $result = $conn->query($sql);

    if ($result->num_rows > 0) {
        while($row = $result->fetch_assoc()) {
            $weatherData[] = $row;
        }
    }
}

// Close connection
$conn->close();
?>
