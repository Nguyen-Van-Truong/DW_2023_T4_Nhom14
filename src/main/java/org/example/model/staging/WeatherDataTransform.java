package org.example.model.staging;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;
import java.sql.Time;

/**
 * Represents transformed weather data with standardized data types.
 * This class is used to store weather data with standardized data types, facilitating data transformation and processing.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WeatherDataTransform {

    /**
     * The province where the transformed weather data is recorded.
     */
    public String province;

    /**
     * The district where the transformed weather data is recorded.
     */
    public String district;

    /**
     * The date of the transformed weather data.
     */
    public Date date;

    /**
     * The time of day when the transformed weather data is recorded.
     */
    public Time time;

    /**
     * The minimum temperature recorded for the given location and time.
     */
    public Float temperatureMin;

    /**
     * The maximum temperature recorded for the given location and time.
     */
    public Float temperatureMax;

    /**
     * The description of the weather conditions (e.g., sunny, rainy, cloudy).
     */
    public String description;

    /**
     * The humidity level at the recorded location and time.
     */
    public Float humidity;

    /**
     * The wind speed at the recorded location and time.
     */
    public Float windSpeed;

    /**
     * The UV index at the recorded location and time.
     */
    public Float uvIndex;

    /**
     * The visibility at the recorded location and time.
     */
    public Float visibility;

    /**
     * The atmospheric pressure at the recorded location and time.
     */
    public Integer pressure;

    /**
     * The stop point associated with the transformed weather data.
     */
    public Float stopPoint;

    /**
     * The URL or source of the transformed weather data.
     */
    public String url;

    /**
     * The IP address associated with the transformed weather data recording.
     */
    public String ip;
}
