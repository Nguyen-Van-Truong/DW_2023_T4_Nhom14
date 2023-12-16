package org.example.model.staging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents weather data for a specific location and time.
 * This class stores information about various weather parameters such as temperature, humidity, wind speed, etc.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WeatherData {

    /**
     * The province where the weather data is recorded.
     */
    public String province;

    /**
     * The district where the weather data is recorded.
     */
    public String district;

    /**
     * The date of the weather data.
     */
    public String date;

    /**
     * The time of day when the weather data is recorded.
     */
    public String time;

    /**
     * The minimum temperature recorded for the given location and time.
     */
    public String temperatureMin;

    /**
     * The maximum temperature recorded for the given location and time.
     */
    public String temperatureMax;

    /**
     * The description of the weather conditions (e.g., sunny, rainy, cloudy).
     */
    public String description;

    /**
     * The humidity level at the recorded location and time.
     */
    public String humidity;

    /**
     * The wind speed at the recorded location and time.
     */
    public String windSpeed;

    /**
     * The UV index at the recorded location and time.
     */
    public String uvIndex;

    /**
     * The visibility at the recorded location and time.
     */
    public String visibility;

    /**
     * The atmospheric pressure at the recorded location and time.
     */
    public String pressure;

    /**
     * The stop point associated with the weather data.
     */
    public String stopPoint;

    /**
     * The URL or source of the weather data.
     */
    public String url;

    /**
     * The IP address associated with the weather data recording.
     */
    public String ip;
}
