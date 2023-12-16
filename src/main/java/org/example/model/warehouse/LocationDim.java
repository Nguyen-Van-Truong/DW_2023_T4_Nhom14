package org.example.model.warehouse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * Represents a dimensional model for storing location information.
 * This class is used in a data warehouse environment to store dimensions related to geographical locations.
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LocationDim {

    /**
     * The unique identifier for the location dimension.
     */
    public int id;

    /**
     * The URL associated with the location.
     */
    public String url;

    /**
     * The name of the district where the location is situated.
     */
    public String districtName;

    /**
     * The name of the province where the location is situated.
     */
    public String provinceName;
}
