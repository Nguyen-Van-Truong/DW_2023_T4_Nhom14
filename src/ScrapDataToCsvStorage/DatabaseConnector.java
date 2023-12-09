package ScrapDataToCsvStorage;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class DatabaseConnector {

    private static Properties properties = new Properties();

    static {
        try (FileInputStream input = new FileInputStream("src/config.properties")) {
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Unable to load config.properties", ex);
        }
    }

    public static Connection connect(String databaseName) throws SQLException {
        String databaseUrl = properties.getProperty("database.url") + databaseName;
        return DriverManager.getConnection(databaseUrl, properties.getProperty("database.username"), properties.getProperty("database.password"));
    }

    /**
     * Fetches and prints all data from the specified table.
     *
     * @param connection the database connection
     * @param tableName the name of the table to fetch data from
     * @throws SQLException if a database access error occurs
     */
    public static void printAllDataFromTable(Connection connection, String tableName) throws SQLException {
        String query = "SELECT * FROM " + tableName;
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                // You'll need to change this depending on the structure of your table
                int id = rs.getInt("id");
                String filename = rs.getString("name");
                String status = rs.getString("status");
                // Print or process the data as needed
                System.out.println("ID: " + id + ", Filename: " + filename + ", Status: " + status);
            }
        }
    }

    public static void main(String[] args) {
        String databaseName = "control"; // can be changed to any database name
        String tableName = "data_files"; // can be changed to any table name
        try (Connection connection = connect(databaseName)) {
            printAllDataFromTable(connection, tableName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
