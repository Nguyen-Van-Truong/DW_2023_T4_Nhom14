package org.example.model.control;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * Represents the configuration settings for a data file.
 * This class contains information about the configuration of a data file, including its name, code, format,
 * source path, destination, and other related attributes.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DataFileConfig {

    /**
     * The unique identifier for the data file configuration.
     */
    public Integer id;

    /**
     * The name of the data file configuration.
     */
    public String name;

    /**
     * The code associated with the data file configuration.
     */
    public String code;

    /**
     * A description of the data file configuration.
     */
    public String description;

    /**
     * The source path of the data file configuration.
     */
    public String source_path;

    /**
     * The location of the data file configuration.
     */
    public String location;

    /**
     * The format of the data file configuration.
     */
    public String format;

    /**
     * The separator used in the data file configuration.
     */
    public String separator;

    /**
     * The columns included in the data file configuration.
     */
    public String columns;

    /**
     * The destination of the data file configuration.
     */
    public String destination;

    /**
     * The timestamp when the data file configuration was created.
     */
    public Timestamp created_at;

    /**
     * The timestamp when the data file configuration was last updated.
     */
    public Timestamp updated_at;

    /**
     * The user identifier who created the data file configuration.
     */
    public Integer created_by;

    /**
     * The user identifier who last updated the data file configuration.
     */
    public Integer updated_by;

    /**
     * The backup path associated with the data file configuration.
     */
    public String backup_path;

    /**
     * The source and destination information related to the data file configuration.
     */
    public String sourcetination;

}
