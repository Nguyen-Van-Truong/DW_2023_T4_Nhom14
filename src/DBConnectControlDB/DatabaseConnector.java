package DBConnectControlDB;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class DatabaseConnector {

    private static Properties properties = new Properties();

    /**
     * Static initializer to load database configuration properties.
     * This block reads the database configuration settings from the 'config.properties' file located in the classpath.
     * It initializes the properties object with these settings.
     * If the 'config.properties' file is not found or cannot be read, a runtime exception is thrown.
     */
    static {
        try (InputStream input = DatabaseConnector.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("config.properties could not be found in the classpath");
            }
            properties.load(input);
        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Unable to load config.properties", ex);
        }
    }

    /**
     * Establishes a connection to the database using the provided database name.
     * This method constructs the database URL by appending the database name to the base URL specified in 'config.properties'.
     * It then uses this URL along with the username and password from 'config.properties' to establish a connection.
     *
     * @param databaseName The name of the database to connect to.
     * @return A Connection object to the specified database.
     * @throws SQLException If a database access error occurs or the connection attempt fails.
     */
    public static Connection connect(String databaseName) throws SQLException {
        String databaseUrl = properties.getProperty("database.url") + databaseName;
        return DriverManager.getConnection(databaseUrl, properties.getProperty("database.username"), properties.getProperty("database.password"));
    }

    /**
     * Prints all data from the specified table in the database.
     * This method executes a 'SELECT *' query on the specified table and iterates over the ResultSet to print each row.
     *
     * @param connection The database connection to use for the query.
     * @param tableName  The name of the table from which to fetch data.
     * @throws SQLException If a database access error occurs or the query fails.
     */
    public static void printAllDataFromTable(Connection connection, String tableName) throws SQLException {
        String query = "SELECT * FROM " + tableName;
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            ResultSetMetaData metaData = rs.getMetaData();
            int columnCount = metaData.getColumnCount();

            // In tên các cột
            for (int i = 1; i <= columnCount; i++) {
                System.out.print(metaData.getColumnName(i) + "\t");
            }
            System.out.println();

            // In dữ liệu từ các dòng
            while (rs.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    System.out.print(rs.getString(i) + "\t");
                }
                System.out.println();
            }
        }
    }


    public static void main(String[] args) {
        String databaseName = "control";
        String tableName = "data_files";
        try (Connection connection = connect(databaseName)) {
            printAllDataFromTable(connection, tableName);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
