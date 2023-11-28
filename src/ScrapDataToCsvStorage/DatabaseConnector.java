package ScrapDataToCsvStorage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConnector {

    /**
     * Establishes a connection to the specified database.
     *
     * @param databaseName the name of the database to connect to
     * @param userName the username for the database connection
     * @param password the password for the database connection
     * @return a Connection object
     * @throws SQLException if a database access error occurs
     */
    public static Connection connect(String databaseName, String userName, String password) throws SQLException {
        String databaseUrl = "jdbc:mysql://localhost:3306/" + databaseName + "?useSSL=false&allowPublicKeyRetrieval=true";
        System.out.println("Connecting to the database: " + databaseName);
        return DriverManager.getConnection(databaseUrl, userName, password);
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
        try (Connection connection = connect(databaseName, "root", "")) {
            printAllDataFromTable(connection, tableName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
