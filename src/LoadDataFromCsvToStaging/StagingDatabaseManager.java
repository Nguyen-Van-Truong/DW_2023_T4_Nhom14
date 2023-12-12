package LoadDataFromCsvToStaging;

import DBConnectControlDB.ControlDatabaseManager;
import DBConnectControlDB.DatabaseConnector;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

/**
 * The StagingDatabaseManager class is responsible for managing database operations related to the staging environment.
 * This includes connecting to the database, truncating tables, loading data from CSV files, and other CRUD operations.
 */
public class StagingDatabaseManager {

    public static final String nameProcess = "LoadCsvToStaging";
    private Connection connection;

    /**
     * Constructs a new StagingDatabaseManager instance.
     *
     * @param databaseName The name of the database to connect to.
     * @throws SQLException If a database access error occurs.
     */
    public StagingDatabaseManager(String databaseName) throws SQLException {
        this.connection = DatabaseConnector.connect(databaseName);
    }

    /**
     * Truncates the 'weatherdata' table in the database.
     *
     * @throws SQLException If a database access error occurs during the truncate operation.
     */
    public void truncateTable() throws SQLException {
        String truncateQuery = "TRUNCATE TABLE weatherdata";
        try (Statement statement = connection.createStatement()) {
            statement.execute(truncateQuery);
            System.out.println("Table truncated successfully.");
        }
    }

    public static int insertToControlStartProcess() {
        try {
            ControlDatabaseManager dbManager = new ControlDatabaseManager("control");
            Timestamp now = Timestamp.valueOf(LocalDateTime.now());
            Timestamp threeDaysLater = Timestamp.valueOf(LocalDateTime.now().plusDays(3));

            // Insert into data_files with status "SE"
            int fileId = dbManager.insertDataFile(nameProcess, 0, null, "SE", now, now, threeDaysLater, "Loading data from csv file to staging process started", now, 1, 1, false, null);

//            dbManager.closeConnection();
            System.out.println("Scraping process started and insert to data_files success");
            return fileId;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1; // Return an error indicator
        }
    }

    /**
     * Loads data from a specified CSV file into the 'weatherdata' staging table.
     *
     * @throws SQLException If a database access error occurs.
     * @throws IOException  If an I/O error occurs while reading the CSV file.
     */
    public void loadCsvToStaging() throws SQLException, IOException {
        ControlDatabaseManager control = new ControlDatabaseManager("control");

        if (!control.isReadyToRun(nameProcess)) {
            System.out.println("There are no csv files available today or have loadCsvToStaging process is ongoing.");
            return;
        }

        truncateTable();

        int dataFileId = insertToControlStartProcess();

        String csvFilePath = control.getLatestSuccessfulDestination();
        System.out.println("Get csvFilePath success: " + csvFilePath);

        String insertQuery = "INSERT INTO weatherdata (Date, Time, Province, Wards, District, Temperature, Feeling, Status, Humidity, Vision" +
                ", Wind_speed, Stop_point, Uv_index, Airquality, Last_update_time, Breadcrumb, Url, Path, Dtrequest, Request, Method, Protocols" +
                ", Status_code, Host, Server, Ip) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String lastUpdateTime = LocalDateTime.now().format(formatter);

        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String time2 = LocalDateTime.now().format(formatter2);

        int totalLines = countLines(csvFilePath) - 1;
        int processedLines = 0;
        int lastReportedProgress = -1;
        int progressPercentage = 0;
        try (CSVReader reader = new CSVReader(new FileReader(csvFilePath));
             PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            String[] nextLine;
            reader.readNext(); // Skip header line
            while ((nextLine = reader.readNext()) != null) {
                mapCsvLineToPreparedStatement(nextLine, preparedStatement, lastUpdateTime);
                preparedStatement.executeUpdate();
                processedLines++;

                progressPercentage = (int) (((double) processedLines / totalLines) * 100);
                if (progressPercentage != lastReportedProgress) {
                    System.out.println("Progress: " + progressPercentage + "% - " + processedLines + "/" + totalLines);
                    lastReportedProgress = progressPercentage;
                }
            }

            System.out.println("Progress: 100% - " + processedLines + "/" + totalLines);
            String code = "LCTS" + time2 + totalLines;
            insertToControlSuccessProcess(code, csvFilePath, dataFileId, totalLines);

            control.closeConnection();
        } catch (CsvValidationException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Maps a line from a CSV file to a PreparedStatement.
     *
     * @param csvLine           An array of strings representing the columns in a single line of the CSV file.
     * @param preparedStatement The PreparedStatement to which the data should be mapped.
     * @param currentTime       The current time, formatted as a string.
     * @throws SQLException If an error occurs while setting values in the PreparedStatement.
     */
    private void mapCsvLineToPreparedStatement(String[] csvLine, PreparedStatement preparedStatement, String currentTime) throws SQLException {
        // Example mapping based on provided column order
        String date = csvLine[2]; // Date
        String time = csvLine[3]; // Time
        String province = csvLine[0]; // Province
        String wards = null; // Placeholder, as it's not in the CSV
        String district = csvLine[1]; // District
        String temperature = csvLine[4] + "/" + csvLine[5]; // Combining TemperatureMin and TemperatureMax
        String feeling = "Cảm giác như " + csvLine[5] + " độ"; // Placeholder for 'Feeling'
        String status = csvLine[6]; // Status (assuming it maps to 'Description')
        String humidity = csvLine[7]; // Humidity
        String vision = csvLine[10]; // Placeholder for 'Vision'
        String windSpeed = csvLine[8]; // Wind_speed
        String stopPoint = csvLine[12]; // Stop_point
        String uvIndex = csvLine[9]; // Uv_index
        String airQuality = csvLine[13]; // Airquality
        String lastUpdateTime = currentTime;
        String breadcrumb = province + ", Việt Nam"; // Placeholder for 'Breadcrumb'
        if (!district.isEmpty())
            breadcrumb = district + ", " + breadcrumb;

        String url = csvLine[14]; // Url
        String path = extractPathFromUrl(url); // Extract path from URL
        String dtrequest = null; // Placeholder for 'Dtrequest'
        String request = null; // Placeholder for 'Request'
        String method = null; // Placeholder for 'Method'
        String protocols = null; // Placeholder for 'Protocols'
        String statusCode = null; // Placeholder for 'Status_code'
        String host = "localhost"; // Placeholder for 'Host'
        String server = "localhost"; // Placeholder for 'Server'
        String ip = csvLine[15]; // Ip

        // Set each parameter in the prepared statement
        preparedStatement.setString(1, date);
        preparedStatement.setString(2, time);
        preparedStatement.setString(3, province);
        preparedStatement.setString(4, wards);
        preparedStatement.setString(5, district);
        preparedStatement.setString(6, temperature);
        preparedStatement.setString(7, feeling);
        preparedStatement.setString(8, status);
        preparedStatement.setString(9, humidity);
        preparedStatement.setString(10, vision);
        preparedStatement.setString(11, windSpeed);
        preparedStatement.setString(12, stopPoint);
        preparedStatement.setString(13, uvIndex);
        preparedStatement.setString(14, airQuality);
        preparedStatement.setString(15, lastUpdateTime);
        preparedStatement.setString(16, breadcrumb);
        preparedStatement.setString(17, url);
        preparedStatement.setString(18, path);
        preparedStatement.setString(19, dtrequest);
        preparedStatement.setString(20, request);
        preparedStatement.setString(21, method);
        preparedStatement.setString(22, protocols);
        preparedStatement.setString(23, statusCode);
        preparedStatement.setString(24, host);
        preparedStatement.setString(25, server);
        preparedStatement.setString(26, ip);
    }

    /**
     * Extracts the path from a given URL.
     *
     * @param urlString The URL string from which the path is to be extracted.
     * @return The path extracted from the URL.
     */
    private String extractPathFromUrl(String urlString) {
        try {
            int domainEndIndex = urlString.indexOf("/", urlString.indexOf("//") + 2);
            if (domainEndIndex != -1) {
                return urlString.substring(domainEndIndex + 1); // Extract part after domain
            }
            return null; // or some default value, if the URL doesn't have the expected format
        } catch (Exception e) {
            e.printStackTrace();
            return null; // or a default value if needed
        }
    }

    /**
     * Counts the number of lines in a file.
     *
     * @param filePath The path of the file to be read.
     * @return The total number of lines in the file.
     * @throws IOException If an I/O error occurs while reading the file.
     */
    private int countLines(String filePath) throws IOException {
        int lines = 0;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            while (reader.readLine() != null) lines++;
        }
        return lines;
    }

    /**
     * Updates a specific column in the 'weatherdata' table for a given ID.
     *
     * @param id       The ID of the record to be updated.
     * @param column   The name of the column to be updated.
     * @param newValue The new value for the specified column.
     * @throws SQLException If a database access error occurs.
     */
    public void updateWeatherData(int id, String column, String newValue) throws SQLException {
        String query = "UPDATE weatherdata SET " + column + " = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, newValue);
            preparedStatement.setInt(2, id);
            preparedStatement.executeUpdate();
        }
    }

    /**
     * Retrieves a single record from the 'weatherdata' table based on its ID.
     *
     * @param id The ID of the record to be retrieved.
     * @return A ResultSet containing the data of the retrieved record.
     * @throws SQLException If a database access error occurs.
     */
    public ResultSet retrieveWeatherData(int id) throws SQLException {
        String query = "SELECT * FROM weatherdata WHERE id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(query);
        preparedStatement.setInt(1, id);
        return preparedStatement.executeQuery();
    }

    private static boolean isReadyToRun() {
        try {
            ControlDatabaseManager dbManager = new ControlDatabaseManager("control");
            if (!dbManager.isReadyToRun(nameProcess)) {
                System.out.println("Scraping process is not ready to run. Either a process is ongoing or a successful process was completed today.");
                dbManager.closeConnection();
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
        return false;
    }

    private static void insertToControlSuccessProcess(String code, String absolutePath, int dataFileId, int rowCount) {
        try {
            ControlDatabaseManager dbManager = new ControlDatabaseManager("control");

            LocalDateTime scrapingTime = LocalDateTime.now();
            Timestamp now = Timestamp.valueOf(scrapingTime);
            // Insert into data_file_configs
            String columns = "id, Date, Time, Province, Wards, District, Temperature, Feeling" +
                    ", Status, Humidity, Vision, Wind_speed, Stop_point, Uv_index" +
                    ", Airquality, Last_update_time, Breadcrumb, Url, Path, Dtrequest, Request, Method, Protocols, Status_code, Host, Server, Ip";
            int configId = dbManager.insertDataFileConfig("LoadCsvToStagingConfig", code, "Configuration for load csv file to staging config"
                    , absolutePath, "localhost", null, null, columns
                    , "table weatherdata in staging database", now, 1, 1, "/backup_path");

            // Update into data_files
            dbManager.updateDataFile(dataFileId, (long) rowCount, configId, "SU", now, true, "Successfully load csv file to staging database");

            // Insert into data_checkpoints
            dbManager.insertDataCheckpoint("LoadCsvToStagingCheckpoint", "Load Csv File To Staging Database Completed", code, now, "Completed Load Csv File To Staging Database", now, 1, 1);

            dbManager.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deletes a record from the 'weatherdata' table based on its ID.
     *
     * @param id The ID of the record to be deleted.
     * @throws SQLException If a database access error occurs.
     */
    public void deleteWeatherData(int id) throws SQLException {
        String query = "DELETE FROM weatherdata WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        }
    }

    /**
     * Closes the database connection.
     *
     * @throws SQLException If a database access error occurs.
     */
    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public static void main(String[] args) throws SQLException, IOException {
        StagingDatabaseManager stagingDatabaseManager = new StagingDatabaseManager("staging");
        stagingDatabaseManager.loadCsvToStaging();

    }
}
