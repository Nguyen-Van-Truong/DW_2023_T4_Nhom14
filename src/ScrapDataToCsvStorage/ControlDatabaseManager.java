package ScrapDataToCsvStorage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;


public class ControlDatabaseManager {
	private Connection connection;

	public ControlDatabaseManager(String databaseName) throws SQLException {
		// Establish a database connection
		this.connection = DatabaseConnector.connect(databaseName);
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

	public void insertDataFile(String name, long rowCount, Integer dfConfigId, String status, Timestamp fileTimestamp,
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
		try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
			preparedStatement.setString(1, name);
			preparedStatement.setLong(2, rowCount);
			preparedStatement.setObject(3, dfConfigId); // setObject allows null value
			preparedStatement.setString(4, status);
			preparedStatement.setTimestamp(5, fileTimestamp);
			preparedStatement.setTimestamp(6, dataRangeFrom);
			preparedStatement.setTimestamp(7, dataRangeTo);
			preparedStatement.setString(8, note);
			preparedStatement.setTimestamp(9, createdAt);
			preparedStatement.setTimestamp(10, new Timestamp(System.currentTimeMillis())); // assuming updated_at is set
																							// to current time
			preparedStatement.setObject(11, createdBy);
			preparedStatement.setObject(12, updatedBy);
			preparedStatement.setObject(13, isInserted ? 1 : 0); // converting Boolean to bit
			preparedStatement.setTimestamp(14, deletedAt);
			preparedStatement.executeUpdate();
		}
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
		ControlDatabaseManager controlDatabaseManager = new ControlDatabaseManager("control");

		ResultSet rs = controlDatabaseManager.getAllDataFiles();
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
		controlDatabaseManager.insertDataFile(name, 0, null, null, null, null, null,
				null, null, null, null, isInserted, null);

		// Print the updated state of data_files
		rs = controlDatabaseManager.getAllDataFiles();
		printResultSet(rs);

		// Close the connection when done
		controlDatabaseManager.closeConnection();
	}
}
