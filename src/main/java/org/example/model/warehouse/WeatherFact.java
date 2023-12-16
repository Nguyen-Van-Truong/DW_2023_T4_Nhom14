package org.example.model.warehouse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * Represents a fact table for storing weather-related data in a data warehouse.
 * This class is used to store measured weather data, associating it with datetime and location dimensions.
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WeatherFact {

    /**
     * The unique identifier for the weather fact.
     */
    public int id;

    /**
     * The foreign key referencing the datetime dimension.
     */
    public int datetimeId;

    /**
     * The foreign key referencing the location dimension.
     */
    public int locationId;

    /**
     * The minimum temperature recorded for the weather event.
     */
    public float temperatureMin;

    /**
     * The humidity level during the weather event.
     */
    public float humidity;

    /**
     * The visibility during the weather event.
     */
    public float visibility;

    /**
     * The wind speed during the weather event.
     */
    public float windSpeed;

    /**
     * The stop point associated with the weather event.
     */
    public String stopPoint;

    /**
     * The UV index during the weather event.
     */
    public int uvIndex;

    /**
     * The URL or source of the weather data.
     */
    public String url;

    /**
     * The maximum temperature recorded for the weather event.
     */
    public float temperatureMax;

    /**
     * The description of the weather conditions during the event (e.g., sunny, rainy, cloudy).
     */
    public String description;
}
