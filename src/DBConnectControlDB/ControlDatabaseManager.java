package DBConnectControlDB;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Manages interactions with a control database, providing methods to query and update various tables.
 */
public class ControlDatabaseManager {
    public static final String DATA_FILES = "data_files";
    public static final String DATA_FILE_CONFIGS = "data_file_configs";
    public static final String DATA_CHECKPOINTS = "data_checkpoints";

    private static Connection connection;

    /**
     * Constructs a ControlDatabaseManager instance and establishes a connection to the specified database.
     * This constructor initializes the connection field using the provided database name.
     *
     * @param databaseName The name of the database to connect to.
     * @throws SQLException If a database access error occurs or the connection attempt fails.
     */
    public ControlDatabaseManager(String databaseName) throws SQLException {
        this.connection = DatabaseConnector.connect(databaseName);
    }

    /**
     * Retrieves a DataFile, its associated DataFileConfig, and corresponding DataCheckpoint based on the DataFile's ID.
     * @param dataFileId The ID of the DataFile.
     * @return An array of objects where the first element is a DataFile, the second is a DataFileConfig, and the third is a DataCheckpoint.
     * @throws SQLException If a database access error occurs.
     */
    public Object[] getDataFileConfigAndCheckpoint(int dataFileId) throws SQLException {
        DataFile dataFile = getDataFileById(dataFileId);
        if (dataFile == null) {
            return null;
        }

        DataFileConfig dataFileConfig = getDataFileConfigById(dataFile.getDfConfigId());
        if (dataFileConfig == null) {
            return null;
        }

        DataCheckpoint dataCheckpoint = getDataCheckpointByCode(dataFileConfig.getCode());
        return new Object[] { dataFile, dataFileConfig, dataCheckpoint };
    }

    private DataFile getDataFileById(int id) throws SQLException {
        String query = "SELECT * FROM data_files WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                // Initialize DataFile object using the ResultSet
                return new DataFile(rs.getInt("id"), rs.getString("name"), rs.getLong("row_count"), rs.getInt("df_config_id"), rs.getString("status"),
                        rs.getTimestamp("file_timestamp"), rs.getTimestamp("data_range_from"), rs.getTimestamp("data_range_to"),
                        rs.getString("note"), rs.getTimestamp("created_at"), rs.getTimestamp("updated_at"), rs.getInt("created_by"),
                        rs.getInt("updated_by"), rs.getBoolean("is_inserted"), rs.getTimestamp("deleted_at"));
            }
        }
        return null;
    }

    private DataFileConfig getDataFileConfigById(int id) throws SQLException {
        String query = "SELECT * FROM data_file_configs WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                // Initialize DataFileConfig object using the ResultSet
                return new DataFileConfig(rs.getInt("id"), rs.getString("name"), rs.getString("code"), rs.getString("description"),
                        rs.getString("source_path"), rs.getString("location"), rs.getString("format"), rs.getString("separator"),
                        rs.getString("columns"), rs.getString("destination"), rs.getTimestamp("created_at"), rs.getTimestamp("updated_at"),
                        rs.getInt("created_by"), rs.getInt("updated_by"), rs.getString("backup_path"));
            }
        }
        return null;
    }

    private DataCheckpoint getDataCheckpointByCode(String code) throws SQLException {
        String query = "SELECT * FROM data_checkpoints WHERE code = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, code);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                // Initialize DataCheckpoint object using the ResultSet
                return new DataCheckpoint(rs.getInt("id"), rs.getString("group_name"), rs.getString("name"), rs.getString("code"),
                        rs.getTimestamp("data_upto_date"), rs.getString("note"), rs.getTimestamp("created_at"), rs.getTimestamp("updated_at"),
                        rs.getInt("created_by"), rs.getInt("updated_by"));
            }
        }
        return null;
    }

    /**
     * Retrieves all records from a specified table.
     *
     * @param tableName The name of the table from which records are to be fetched.
     * @return A ResultSet containing all records from the specified table.
     * @throws SQLException If a database access error occurs.
     */
    public ResultSet getAllRecords(String tableName) throws SQLException {
        String query = "SELECT * FROM " + tableName;
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        return preparedStatement.executeQuery();
    }

    /**
     * Retrieves a specific record from a table by its ID.
     *
     * @param tableName The name of the table.
     * @param id        The ID of the record to retrieve.
     * @return A ResultSet containing the specified record.
     * @throws SQLException If a database access error occurs.
     */
    public ResultSet getRecordById(String tableName, int id) throws SQLException {
        String query = "SELECT * FROM " + tableName + " WHERE id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, id);
        return preparedStatement.executeQuery();
    }

    /**
     * Inserts a new checkpoint record into the data_checkpoints table.
     * This method is used to record significant events or milestones in data processing.
     *
     * @param groupName    The group name associated with the checkpoint.
     * @param name         The name of the checkpoint.
     * @param code         A unique code to identify the checkpoint.
     * @param dataUptoDate The timestamp indicating the data coverage of the checkpoint.
     * @param note         Additional notes or details about the checkpoint.
     * @param createdAt    The timestamp when the checkpoint is created.
     * @param createdBy    The ID of the user who created the checkpoint.
     * @param updatedBy    The ID of the user who last updated the checkpoint.
     * @throws SQLException If a database access error occurs or the insert operation fails.
     */
    public void insertDataCheckpoint(String groupName, String name, String code, Timestamp dataUptoDate, String note,
                                     Timestamp createdAt, Integer createdBy, Integer updatedBy) throws SQLException {
        String query = "INSERT INTO data_checkpoints (group_name, name, code, data_upto_date, note, created_at, " +
                "updated_at, created_by, updated_by) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, groupName);
            preparedStatement.setString(2, name);
            preparedStatement.setString(3, code);
            preparedStatement.setTimestamp(4, dataUptoDate);
            preparedStatement.setString(5, note);
            preparedStatement.setTimestamp(6, createdAt);
            preparedStatement.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
            preparedStatement.setObject(8, createdBy);
            preparedStatement.setObject(9, updatedBy);
            preparedStatement.executeUpdate();
        }
    }

    /**
     * Inserts a new data file record into the data_files table.
     *
     * @param name          The name of the file.
     * @param rowCount      The number of rows in the file.
     * @param dfConfigId    The configuration ID associated with the file.
     * @param status        The status of the file.
     * @param fileTimestamp The timestamp associated with the file.
     * @param dataRangeFrom The start range of the data.
     * @param dataRangeTo   The end range of the data.
     * @param note          Additional notes about the file.
     * @param createdAt     The timestamp when the record was created.
     * @param createdBy     The ID of the user who created the record.
     * @param updatedBy     The ID of the user who last updated the record.
     * @param isInserted    Indicates whether the file has been inserted.
     * @param deletedAt     The timestamp when the file was deleted, if applicable.
     * @return The auto-generated ID of the new record.
     * @throws SQLException If a database access error occurs.
     */
    public int insertDataFile(String name, long rowCount, Integer dfConfigId, String status, Timestamp fileTimestamp,
                              Timestamp dataRangeFrom, Timestamp dataRangeTo, String note, Timestamp createdAt, Integer createdBy,
                              Integer updatedBy, Boolean isInserted, Timestamp deletedAt) throws SQLException {

        // Check if df_config_id exists in the data_file_configs table
        if (dfConfigId != null) {
            String configQuery = "SELECT COUNT(*) FROM data_file_configs WHERE id = ?";
            try (PreparedStatement configStatement = connection.prepareStatement(configQuery)) {
                configStatement.setInt(1, dfConfigId);
                ResultSet configResult = configStatement.executeQuery();
                if (configResult.next() && configResult.getInt(1) == 0) {
                    throw new SQLException(
                            "df_config_id " + dfConfigId + " does not exist in data_file_configs table.");
                }
            }
        }

        // Continue with the insertion as the df_config_id is valid or null
        String query = "INSERT INTO data_files (name, row_count, df_config_id, status, file_timestamp, "
                + "data_range_from, data_range_to, note, created_at, updated_at, created_by, "
                + "updated_by, is_inserted, deleted_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, name);
            preparedStatement.setLong(2, rowCount);
            preparedStatement.setObject(3, dfConfigId); // setObject allows null value
            preparedStatement.setString(4, status);
            preparedStatement.setTimestamp(5, fileTimestamp);
            preparedStatement.setTimestamp(6, dataRangeFrom);
            preparedStatement.setTimestamp(7, dataRangeTo);
            preparedStatement.setString(8, note);
            preparedStatement.setTimestamp(9, createdAt);
            preparedStatement.setTimestamp(10, new Timestamp(System.currentTimeMillis())); // assuming updated_at is set to current time
            preparedStatement.setObject(11, createdBy);
            preparedStatement.setObject(12, updatedBy);
            preparedStatement.setObject(13, isInserted ? 1 : 0); // converting Boolean to bit
            preparedStatement.setTimestamp(14, deletedAt);
            preparedStatement.executeUpdate();

            // Retrieve the auto-generated key
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Assuming the ID is the first field
                } else {
                    throw new SQLException("Creating file failed, no ID obtained.");
                }
            }
        }
    }

    /**
     * Inserts a new configuration record into the data_file_configs table.
     *
     * @param name        The name of the configuration.
     * @param code        A unique code for the configuration.
     * @param description A description of the configuration.
     * @param sourcePath  The source path for the configuration.
     * @param location    The location associated with the configuration.
     * @param format      The format of the data.
     * @param separator   The separator used in the data.
     * @param columns     The columns in the data.
     * @param destination The destination path for the data.
     * @param createdAt   The timestamp when the configuration was created.
     * @param createdBy   The ID of the user who created the configuration.
     * @param updatedBy   The ID of the user who last updated the configuration.
     * @param backupPath  The backup path for the data.
     * @return The auto-generated ID of the new configuration record.
     * @throws SQLException If a database access error occurs.
     */
    public int insertDataFileConfig(String name, String code, String description, String sourcePath, String location, String format,
                                    String separator, String columns, String destination, Timestamp createdAt,
                                    Integer createdBy, Integer updatedBy, String backupPath) throws SQLException {
        String query = "INSERT INTO data_file_configs (name, code, description, source_path, location, format, `separator`, " +
                "columns, destination, created_at, updated_at, created_by, updated_by, backup_path) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, name);
            preparedStatement.setString(2, code);
            preparedStatement.setString(3, description);
            preparedStatement.setString(4, sourcePath);
            preparedStatement.setString(5, location);
            preparedStatement.setString(6, format);
            preparedStatement.setString(7, separator);
            preparedStatement.setString(8, columns);
            preparedStatement.setString(9, destination);
            preparedStatement.setTimestamp(10, createdAt);
            preparedStatement.setTimestamp(11, new Timestamp(System.currentTimeMillis())); // Assuming updated_at is set to current time
            preparedStatement.setObject(12, createdBy);
            preparedStatement.setObject(13, updatedBy);
            preparedStatement.setString(14, backupPath);
            preparedStatement.executeUpdate();

            // Retrieve the auto-generated key
            try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // Assuming the ID is the first field
                } else {
                    throw new SQLException("Creating config failed, no ID obtained.");
                }
            }
        }
    }

    /**
     * Updates a data checkpoint record in the data_checkpoints table.
     *
     * @param id              The ID of the checkpoint to update.
     * @param newGroupName    The new group name for the checkpoint.
     * @param newName         The new name for the checkpoint.
     * @param newCode         The new code for the checkpoint.
     * @param newDataUptoDate The new date up to which the data is valid.
     * @param newNote         The new note for the checkpoint.
     * @param newUpdatedBy    The ID of the user who updated the checkpoint.
     * @throws SQLException If a database access error occurs.
     */
    public void updateDataCheckpoint(int id, String newGroupName, String newName, String newCode, Timestamp newDataUptoDate,
                                     String newNote, Integer newUpdatedBy) throws SQLException {
        String query = "UPDATE data_checkpoints SET group_name = ?, name = ?, code = ?, data_upto_date = ?, " +
                "note = ?, updated_at = ?, updated_by = ? WHERE id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, newGroupName);
        preparedStatement.setString(2, newName);
        preparedStatement.setString(3, newCode);
        preparedStatement.setTimestamp(4, newDataUptoDate);
        preparedStatement.setString(5, newNote);
        preparedStatement.setTimestamp(6, new Timestamp(System.currentTimeMillis())); // Set updated_at to current time
        preparedStatement.setObject(7, newUpdatedBy);
        preparedStatement.setInt(8, id);
        preparedStatement.executeUpdate();
    }

    /**
     * Updates a data file configuration record in the data_file_configs table.
     *
     * @param id             The ID of the configuration to update.
     * @param newName        The new name for the configuration.
     * @param newDescription The new description for the configuration.
     * @param newSourcePath  The new source path for the configuration.
     * @param newLocation    The new location for the configuration.
     * @param newFormat      The new format for the data.
     * @param newSeparator   The new separator used in the data.
     * @param newColumns     The new columns in the data.
     * @param newDestination The new destination path for the data.
     * @param newUpdatedBy   The ID of the user who updated the configuration.
     * @param newBackupPath  The new backup path for the data.
     * @throws SQLException If a database access error occurs.
     */
    public void updateDataFileConfig(int id, String newName, String newDescription, String newSourcePath,
                                     String newLocation, String newFormat, String newSeparator, String newColumns,
                                     String newDestination, Integer newUpdatedBy, String newBackupPath) throws SQLException {
        String query = "UPDATE data_file_configs SET name = ?, description = ?, source_path = ?, location = ?, format = ?, " +
                "`separator` = ?, columns = ?, destination = ?, updated_at = ?, updated_by = ?, backup_path = ? " +
                "WHERE id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setString(1, newName);
        preparedStatement.setString(2, newDescription);
        preparedStatement.setString(3, newSourcePath);
        preparedStatement.setString(4, newLocation);
        preparedStatement.setString(5, newFormat);
        preparedStatement.setString(6, newSeparator); // Enclose separator in backticks
        preparedStatement.setString(7, newColumns);
        preparedStatement.setString(8, newDestination);
        preparedStatement.setTimestamp(9, new Timestamp(System.currentTimeMillis())); // Assuming updated_at is set to current time
        preparedStatement.setObject(10, newUpdatedBy);
        preparedStatement.setString(11, newBackupPath);
        preparedStatement.setInt(12, id);
        preparedStatement.executeUpdate();
    }

    /**
     * Deletes a data checkpoint from the data_checkpoints table.
     *
     * @param id The ID of the checkpoint to delete.
     * @throws SQLException If a database access error occurs.
     */
    public void deleteDataCheckpoint(int id) throws SQLException {
        String query = "DELETE FROM data_checkpoints WHERE id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
    }

    /**
     * Deletes a data file configuration from the data_file_configs table.
     *
     * @param id The ID of the configuration to delete.
     * @throws SQLException If a database access error occurs.
     */
    public void deleteDataFileConfig(int id) throws SQLException {
        String query = "DELETE FROM data_file_configs WHERE id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
    }

    /**
     * Updates an existing data file record in the data_files table.
     * This method is typically used to modify details of a data file after its initial creation.
     *
     * @param id            The ID of the data file record to update.
     * @param newRowCount   The updated row count of the data file.
     * @param newDfConfigId The updated data file configuration ID associated with the file.
     * @param newStatus     The new status of the file (e.g., processed, failed).
     * @param updatedAt     The timestamp of the update.
     * @param isInserted    Indicates whether the file has been successfully inserted into another system or database.
     * @param note          Additional notes or remarks about the file or the update.
     * @throws SQLException If a database access error occurs or the update operation fails.
     */
    public void updateDataFile(int id, Long newRowCount, Integer newDfConfigId,
                               String newStatus, Timestamp updatedAt, Boolean isInserted, String note)
            throws SQLException {
        String query = "UPDATE data_files SET row_count = ?, df_config_id = ?, status = ?, " +
                "updated_at = ?, is_inserted = ?, note = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setObject(1, newRowCount); // setObject allows null
            preparedStatement.setObject(2, newDfConfigId);
            preparedStatement.setString(3, newStatus);
            preparedStatement.setTimestamp(4, updatedAt);
            preparedStatement.setObject(5, isInserted ? 1 : 0); // Convert Boolean to Integer (1 for true, 0 for false)
            preparedStatement.setString(6, note);
            preparedStatement.setInt(7, id);
            preparedStatement.executeUpdate();
        }
    }


    /**
     * Deletes a data file from the data_files table.
     *
     * @param id The ID of the file to delete.
     * @throws SQLException If a database access error occurs.
     */
    public void deleteDataFile(int id) throws SQLException {
        String query = "DELETE FROM data_files WHERE id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, id);
        preparedStatement.executeUpdate();
    }

    /**
     * Closes the database connection.
     *
     * @throws SQLException If a database access error occurs.
     */
    public void closeConnection() throws SQLException {
        if (this.connection != null && !this.connection.isClosed()) {
            this.connection.close();
        }
    }

    /**
     * Prints the contents of a ResultSet in a tabular format.
     * This method iterates over the ResultSet and prints each row and column, providing a visual representation of the data.
     *
     * @param rs The ResultSet to be printed.
     * @throws SQLException If an error occurs while accessing the ResultSet data.
     */
    public static void printResultSet(ResultSet rs) throws SQLException {
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();

        // Print column names
        for (int i = 1; i <= columnCount; i++) {
            System.out.print(rsmd.getColumnName(i) + "\t");
        }
        System.out.println();

        // Print rows
        while (rs.next()) {
            for (int i = 1; i <= columnCount; i++) {
                System.out.print(rs.getString(i) + "\t");
            }
            System.out.println();
        }
    }

    /**
     * Determines if the system is ready to run a new scraping process based on the status of existing processes.
     *
     * @return true if the system is ready to run a new process, false otherwise.
     * @throws SQLException If a database access error occurs.
     */
    public boolean isReadyToRun(String nameProcess) throws SQLException {
        boolean hasSuccessfulProcessToday = hasSuccessfulProcessToday(nameProcess);
        boolean isProcessOngoing = isProcessOngoing(nameProcess);

        return !hasSuccessfulProcessToday && !isProcessOngoing;
    }

    /**
     * Checks if any scraping process has successfully completed today.
     *
     * @return true if a successful process exists, false otherwise.
     * @throws SQLException If a database access error occurs.
     */
    public boolean hasSuccessfulProcessToday(String nameProcess) throws SQLException {
        String query = "SELECT COUNT(*) FROM data_files WHERE name = ? AND status = 'SU' AND DATE(created_at) = CURDATE()";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, nameProcess);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        }
        return false;
    }


    /**
     * Checks if any scraping process is currently ongoing and started within the last 30 minutes.
     *
     * @return true if an ongoing process exists, false otherwise.
     * @throws SQLException If a database access error occurs.
     */
    public boolean isProcessOngoing(String nameProcess) throws SQLException {
        String query = "SELECT COUNT(*) FROM data_files WHERE name = ? AND status = 'SE' AND created_at >= ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, nameProcess);
            Timestamp thirtyMinutesAgo = Timestamp.valueOf(LocalDateTime.now().minus(30, ChronoUnit.MINUTES));
            preparedStatement.setTimestamp(2, thirtyMinutesAgo); // Set the created_at parameter in the query
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        }
        return false;
    }


    /**
     * Checks if any scraping process has failed today.
     *
     * @return true if a failed process exists, false otherwise.
     * @throws SQLException If a database access error occurs.
     */
    public boolean hasFailedProcessToday(String nameProcess) throws SQLException {
        String query = "SELECT COUNT(*) FROM data_files WHERE name = ? AND status = 'EF' AND DATE(created_at) = CURDATE()";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, nameProcess);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt(1) > 0;
            }
        }
        return false;
    }


    public String getLatestSuccessfulDestination() throws SQLException {
        int dfConfigId = getLatestSuccessfulDfConfigId();
        if (dfConfigId != -1) {
            return getDestinationFromDfConfigId(dfConfigId);
        }
        return null;
    }

    private int getLatestSuccessfulDfConfigId() throws SQLException {
        String query = "SELECT df_config_id FROM data_files WHERE status = 'SU' AND DATE(created_at) = CURDATE() ORDER BY created_at DESC LIMIT 1";
        try (Statement stmt = connection.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
            if (rs.next()) {
                return rs.getInt("df_config_id");
            }
        }
        return -1; // Indicates no record found
    }

    private String getDestinationFromDfConfigId(int dfConfigId) throws SQLException {
        String query = "SELECT destination FROM data_file_configs WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, dfConfigId);
            try (ResultSet rs = preparedStatement.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("destination");
                }
            }
        }
        return null; // Indicates no destination found
    }

    public static void main(String[] args) throws SQLException {
//        testDataFilesMethod();
//        testConfigMethods();
//		testCheckpointMethods();
        ControlDatabaseManager manager = new ControlDatabaseManager("control");
        Object[] dataFileObjects = manager.getDataFileConfigAndCheckpoint(1); // Assuming you are fetching the record with ID 1

        if (dataFileObjects != null) {
            for (Object obj : dataFileObjects) {
                if (obj != null) {
                    System.out.println(obj.toString());
                } else {
                    System.out.println("Null object found in the array.");
                }
            }
        } else {
            System.out.println("No data found for the specified DataFile ID.");
        }

        // Closing the connection
        manager.closeConnection();
    }

    public static void testCheckpointMethods() throws SQLException {
        ControlDatabaseManager manager = new ControlDatabaseManager("control");

        // Test insertDataCheckpoint
        // Insert a sample data checkpoint
        manager.insertDataCheckpoint("Group1", "Checkpoint1", "Code1", new Timestamp(System.currentTimeMillis()),
                "Note for Checkpoint1", new Timestamp(System.currentTimeMillis()), 1, 1);

        // Test getAllRecords for data_checkpoints
        // Fetch and print all data checkpoints
        ResultSet rs = manager.getAllRecords(DATA_CHECKPOINTS);
        printResultSet(rs);

        // Test getRecordById for a specific checkpoint
        // Assuming there's a record with ID 1
        rs = manager.getRecordById(DATA_CHECKPOINTS, 1);
        printResultSet(rs);

        // Test updateDataCheckpoint
        // Update the checkpoint with ID 1 (assuming it exists)
        manager.updateDataCheckpoint(1, "NewGroup", "NewCheckpoint", "NewCode",
                new Timestamp(System.currentTimeMillis()), "Updated note", 2);

        // Test deleteDataCheckpoint
        // Delete a checkpoint (assuming a record with ID 2 exists)
        manager.deleteDataCheckpoint(2);

        // Fetch and print all data checkpoints after deletion
        rs = manager.getAllRecords(DATA_CHECKPOINTS);
        printResultSet(rs);

        // Close connection
        manager.closeConnection();
    }

    public static void testConfigMethods() throws SQLException {
        ControlDatabaseManager manager = new ControlDatabaseManager("control");

        // Test getDataFileConfigById
//		rs = manager.getRecordById(DATA_FILE_CONFIGS,1); // Assuming there's a record with ID 1
//		printResultSet(rs);

        // Test insertDataFileConfig
        // Provide appropriate arguments here based on the method signature
        int configId = manager.insertDataFileConfig("TestConfig", "a", "Description", "/path/source", "location", "csv", ",", "col1,col2", "/path/dest",
                new Timestamp(System.currentTimeMillis()), 1, 1, "/path/backup");
        System.out.println("configId:" + configId);
        // Test updateDataFileConfig
        // Provide appropriate arguments here based on the method signature
//		manager.updateDataFileConfig(1, "UpdatedName", "Updated Description", "/new/path/source", "new location", "json", "|",
//				"col1,col2,col3", "/new/path/dest", 2, "/new/path/backup");

        // Test deleteDataFileConfig
//		manager.deleteDataFileConfig(2); // Assuming there's a record with ID 2

        // Test getAllDataFileConfigs
        ResultSet rs = manager.getAllRecords(DATA_FILE_CONFIGS);
        printResultSet(rs);

        // Close connection
        manager.closeConnection();
    }

    private static void testDataFilesMethod() throws SQLException {
        ControlDatabaseManager controlDatabaseManager = new ControlDatabaseManager("control");

        ResultSet rs = controlDatabaseManager.getAllRecords(DATA_FILES);
//		printResultSet(rs);

        // Generate random data for demonstration
        String name = "RandomDataFile_" + new Random().nextInt(1000);
        long rowCount = new Random().nextLong();
        Integer dfConfigId = new Random().nextInt(10); // Assuming there are some config IDs between 1 and 10
        String status = "NEW";
        Timestamp fileTimestamp = new Timestamp(System.currentTimeMillis());
        Timestamp dataRangeFrom = new Timestamp(
                System.currentTimeMillis() - ThreadLocalRandom.current().nextInt(1000000));
        Timestamp dataRangeTo = new Timestamp(
                System.currentTimeMillis() + ThreadLocalRandom.current().nextInt(1000000));
        String note = "This is a random note";
        Timestamp createdAt = new Timestamp(System.currentTimeMillis());
        Integer createdBy = new Random().nextInt(10); // Assuming there are user IDs between 1 and 10
        Integer updatedBy = createdBy; // For simplicity, using the same user ID
        Boolean isInserted = new Random().nextBoolean();
        Timestamp deletedAt = null; // Assuming the record is not deleted at the time of insertion

        // Insert the random data into data_files
        int id = controlDatabaseManager.insertDataFile("11/12", 0, null, "SE", fileTimestamp, fileTimestamp, null,
                null, fileTimestamp, null, null, isInserted, null);
        System.out.println("id:" + id);

//        controlDatabaseManager.updateDataFile(2, "fileName222", (long) rowCount, 1, "SU",fileTimestamp,false,"Load data weather success");

        // Print the updated state of data_files
        rs = controlDatabaseManager.getAllRecords(DATA_FILES);
        printResultSet(rs);

        // Close the connection when done
        controlDatabaseManager.closeConnection();
    }
}
