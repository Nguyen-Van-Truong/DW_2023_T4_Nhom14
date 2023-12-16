package org.example.model.control;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * Represents a data file with associated metadata.
 * This class is used to store information about data files, including their configuration, status, and timestamps.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DataFile {

    /**
     * The unique identifier for the data file.
     */
    public Integer id;

    /**
     * The name of the data file.
     */
    public String name;

    /**
     * The number of rows in the data file.
     */
    public Integer row_count;

    /**
     * The configuration identifier for the data file.
     */
    public Integer df_config_id;

    /**
     * The status of the data file.
     */
    public String status;

    /**
     * The timestamp when the data file was created.
     */
    public Timestamp file_timestamp;

    /**
     * The starting timestamp of the data range covered by the file.
     */
    public Timestamp data_range_from;

    /**
     * The ending timestamp of the data range covered by the file.
     */
    public Timestamp data_range_to;

    /**
     * Additional notes or comments about the data file.
     */
    public String note;

    /**
     * The timestamp when the data file was created.
     */
    public Timestamp created_at;

    /**
     * The timestamp when the data file was last updated.
     */
    public Timestamp updated_at;

    /**
     * The user identifier who created the data file.
     */
    public Integer created_by;

    /**
     * The user identifier who last updated the data file.
     */
    public Integer updated_by;

    /**
     * Indicates whether the data file has been inserted.
     */
    public Boolean is_inserted;

    /**
     * The timestamp when the data file was deleted.
     */
    public Timestamp deleted_at;

    /**
     * The configuration of the data file.
     */
    public DataFileConfig dataFileConfig;

}
