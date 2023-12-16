<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Weather Report</title>
    <style>
        /* CSS styles */
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f4f4;
            padding: 20px;
            margin: 0;
        }
        .container {
            /* max-width: 800px; */
            margin: auto;
            background: white;
            padding: 20px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        }
        table {
            width: 100%;
            border-collapse: collapse;
        }
        th, td {
            padding: 8px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }
        th {
            background-color: #f4f4f4;
        }
        .weather-form {
            margin-bottom: 20px;
        }
        .weather-form input[type="date"],
        .weather-form select {
            padding: 8px;
            margin-right: 10px;
        }
        .weather-form input[type="submit"] {
            padding: 8px 15px;
            background-color: #4CAF50;
            color: white;
            border: none;
            cursor: pointer;
        }
        .weather-form input[type="submit"]:hover {
            background-color: #45a049;
        }
    </style>
</head>
<body>
<div class="container">
        <!-- Include PHP Script -->
        <?php include 'process.php'; ?>

        <!-- Weather Form -->
        <form class="weather-form" method="get">
            Select Location: 
            <select name="location">
                <option value="">--Select Location--</option>
                <?php foreach ($locations as $location): ?>
                    <?php $selected = ($location['location_name'] == $selectedLocation) ? 'selected' : ''; ?>
                    <option value="<?php echo htmlspecialchars($location['location_name']); ?>" <?php echo $selected; ?>>
                        <?php echo htmlspecialchars($location['location_name']); ?>
                    </option>
                <?php endforeach; ?>
            </select>

            Select Date: 
            <input type="date" name="date" value="<?php echo htmlspecialchars($selectedDate); ?>">
            <input type="submit" value="Show Weather">
        </form>

        <!-- Weather Data Display -->
        <?php if (!empty($weatherData)): ?>
            <table>
                <tr>
                    <th>Date</th>
                    <th>Location</th>
                    <th>Temperature</th>
                    <th>Humidity</th>
                    <th>Visibility</th>
                    <th>Air Pressure</th>
                    <th>Wind Speed</th>
                    <th>UV Index</th>
                    <th>Forecast Time</th>
                    <th>Forecast Temperature</th>
                    <th>Weather Icon</th>
                    <th>Description</th>
                </tr>
                <?php foreach ($weatherData as $row): ?>
                    <tr>
                        <td><?php echo $row["date"]; ?></td>
                        <td><?php echo $row["location_name"]; ?></td>
                        <td><?php echo $row["temperature_degC"] . "°C"; ?></td>
                        <td><?php echo $row["humidity_percentage"] . "%"; ?></td>
                        <td><?php echo $row["visibility_km"] . "km"; ?></td>
                        <td><?php echo $row["air_pressure_hPa"] . " hPa"; ?></td>
                        <td><?php echo $row["wind_speed_mph"] . " mph"; ?></td>
                        <td><?php echo $row["UV_index"]; ?></td>
                        <td><?php echo $row["forecast_time"]; ?></td>
                        <td><?php echo $row["forecast_temperature_degC"] . "°C"; ?></td>
                        <td><img src="<?php echo $row["weather_icon"]; ?>" alt="Weather Icon"></td>
                        <td><?php echo $row["weather_description"]; ?></td>
                    </tr>
                <?php endforeach; ?>
            </table>
        <?php else: ?>
            <?php if ($selectedLocation && $selectedDate): ?>
                <p>No results found for the selected location and date.</p>
            <?php endif; ?>
        <?php endif; ?>
    </div>
</body>
</html>
