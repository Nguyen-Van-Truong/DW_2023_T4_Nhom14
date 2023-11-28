package ScrapDataToCsvStorage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public class ControlDatabaseManager {

	private Connection connection;

	public ControlDatabaseManager(String databaseName, String userName, String password) throws SQLException {
		// Establish a database connection
		this.connection = DatabaseConnector.connect(databaseName, userName, password);
	}

	public ResultSet getAllDataFiles() throws SQLException {
		String query = "SELECT * FROM data_files";
		PreparedStatement preparedStatement = connection.prepareStatement(query);
		return preparedStatement.executeQuery();
	}

	public ResultSet getDataFileById(int id) throws SQLException {
		String query = "SELECT * FROM data_files WHERE id = ?";
		PreparedStatement preparedStatement = connection.prepareStatement(query);
		preparedStatement.setInt(1, id);
		return preparedStatement.executeQuery();
	}

	public void insertDataFile(String name, long rowCount, Integer dfConfigId, String status) throws SQLException {
		String query = "INSERT INTO data_files (name, row_count, df_config_id, status) VALUES (?, ?, ?, ?)";
		PreparedStatement preparedStatement = connection.prepareStatement(query);
		preparedStatement.setString(1, name);
		preparedStatement.setLong(2, rowCount);
		preparedStatement.setObject(3, dfConfigId); // set object allows null value
		preparedStatement.setString(4, status);
		preparedStatement.executeUpdate();
	}

	public void updateDataFile(int id, String newName, Long newRowCount, Integer newDfConfigId, String newStatus)
			throws SQLException {
		String query = "UPDATE data_files SET name = ?, row_count = ?, df_config_id = ?, status = ? WHERE id = ?";
		PreparedStatement preparedStatement = connection.prepareStatement(query);
		preparedStatement.setString(1, newName);
		preparedStatement.setObject(2, newRowCount); // setObject allows null
		preparedStatement.setObject(3, newDfConfigId);
		preparedStatement.setString(4, newStatus);
		preparedStatement.setInt(5, id);
		preparedStatement.executeUpdate();
	}

	public void deleteDataFile(int id) throws SQLException {
		String query = "DELETE FROM data_files WHERE id = ?";
		PreparedStatement preparedStatement = connection.prepareStatement(query);
		preparedStatement.setInt(1, id);
		preparedStatement.executeUpdate();
	}

	// Additional methods for interacting with other tables can be added here

	// Close the connection when done
	public void closeConnection() throws SQLException {
		if (this.connection != null && !this.connection.isClosed()) {
			this.connection.close();
		}
	}
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

	public static void main(String[] args) throws SQLException {
		ControlDatabaseManager controlDatabaseManager = new ControlDatabaseManager("control", "root", "");

		ResultSet rs = controlDatabaseManager.getAllDataFiles();
		printResultSet(rs);
//		ResultSetMetaData rsmd = rs.getMetaData();
//
//		int columnsNumber = rsmd.getColumnCount();
//		while (rs.next()) {
//			for (int i = 1; i <= columnsNumber; i++) {
//				if (i > 1)
//					System.out.print(",  ");
//				String columnValue = rs.getString(i);
//				System.out.print(columnValue + " " + rsmd.getColumnName(i));
//			}
//		}
	}
}
