package org.example;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.example.model.control.DataFile;
import org.example.model.control.DataFileConfig;
import org.example.model.staging.WeatherData;
import org.example.model.staging.WeatherDataTransform;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class Main {

    /**
     * Đọc chuỗi JSON và chuyển thành đối tượng JsonObject
     *
     * @param json chuỗi JSON
     * @return đối tượng JsonObject
     * @throws IOException
     */
    public static JsonObject readJsonString(String json) throws IOException, IOException {
        return JsonParser.parseString(json).getAsJsonObject();
    }

    /**
     * Đọc nội dung của tệp văn bản thành chuỗi
     *
     * @param filePath
     * @return
     * @throws IOException
     */
    public static String readFileToString(String filePath) throws IOException {
        return new String(Files.readAllBytes(Paths.get(filePath)));
    }

    /**
     * Tạo kết nối đến cơ sở dữ liệu
     *
     * @param url
     * @param user
     * @param password
     * @return
     */
    public static Connection getConnection(String url, String user, String password) {
        Connection connection = null;

        try {
            // Đăng ký driver JDBC
            Class.forName("com.mysql.cj.jdbc.Driver");

            // Tạo kết nối đến cơ sở dữ liệu
            connection = DriverManager.getConnection(url, user, "");

            System.out.println("Connected to the database!");
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }

        return connection;
    }

    /**
     * Đóng kết nối đến cơ sở dữ liệu
     *
     * @param connection
     */
    public static void closeConnection(Connection connection) {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                    System.out.println("Connection closed.");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * Truy xuất và trả về danh sách các dữ liệu thời tiết từ bảng staging trong cơ sở dữ liệu.
     *
     * @param connection Kết nối tới cơ sở dữ liệu.
     * @return Danh sách chứa các dữ liệu thời tiết từ bảng staging.
     */
    public static List<WeatherData> getAllStaging(Connection connection) {
        List<WeatherData> stagings = new ArrayList<>();

        try {
            Statement statement = connection.createStatement();
            String query = "SELECT * FROM weather_data";
            ResultSet resultSet = statement.executeQuery(query);

            while (resultSet.next()) {
                WeatherData staging = new WeatherData();

                staging.setDate(resultSet.getString("Date"));
                staging.setTime(resultSet.getString("Time"));
                staging.setProvince(resultSet.getString("Province"));
                staging.setDistrict(resultSet.getString("District"));
                staging.setHumidity(resultSet.getString("Humidity"));
                staging.setUrl(resultSet.getString("URL"));
                staging.setTemperatureMin(resultSet.getString("TemperatureMin"));
                staging.setTemperatureMax(resultSet.getString("TemperatureMax"));
                staging.setWindSpeed(resultSet.getString("WindSpeed"));
                staging.setUvIndex(resultSet.getString("UVIndex"));
                staging.setVisibility(resultSet.getString("Visibility"));
                staging.setPressure(resultSet.getString("Pressure"));
                staging.setStopPoint(resultSet.getString("Pressure"));
                staging.setDescription(resultSet.getString("Description"));
                staging.setIp(resultSet.getString("IP"));


                stagings.add(staging);
            }


            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return stagings;
    }

    /**
     * Lấy danh sách các đối tượng DataFile đang chờ xử lý.
     * Đối tượng DataFile được xem xét nếu đáp ứng các điều kiện sau:
     * - Trạng thái (status) của DataFile là 'SU' (Success).
     * - Ngày tạo (created_at) của DataFile là ngày hiện tại.
     * - Trạng thái is_inserted của DataFile là 0 (chưa được chèn vào cơ sở dữ liệu).
     *
     * @param connection Đối tượng Connection đến cơ sở dữ liệu.
     * @return Danh sách các đối tượng DataFile chờ xử lý.
     */
    public static List<DataFile> getPendingDataFiles(Connection connection) {
        List<DataFile> pendingDataFiles = new ArrayList<>();

        try {
            // Tạo đối tượng Statement để thực hiện truy vấn
            Statement statement = connection.createStatement();


            // Thực hiện truy vấn với điều kiện
            String query = "SELECT \n" + "    data_files.id AS id,\n" + "    data_files.name AS name,\n" + "    data_files.row_count AS row_count,\n" + "    data_files.df_config_id AS df_config_id,\n" + "    data_files.status AS status,\n" + "    data_files.file_timestamp AS file_timestamp,\n" + "    data_files.data_range_from AS data_range_from,\n" + "    data_files.data_range_to AS data_range_to,\n" + "    data_files.note AS note,\n" + "    data_files.created_at AS created_at,\n" + "    data_files.updated_at AS updated_at,\n" + "    data_files.created_by AS created_by,\n" + "    data_files.updated_by AS updated_by,\n" + "    data_files.is_inserted AS is_inserted,\n" + "    data_files.deleted_at AS deleted_at,\n" + "   data_file_configs.id AS df_id,\n" + "    data_file_configs.name AS df_name,\n" + "    data_file_configs.code AS df_code,\n" + "    data_file_configs.description AS df_description,\n" + "    data_file_configs.source_path AS df_source_path,\n" + "    data_file_configs.location AS df_location,\n" + "    data_file_configs.format AS df_format,\n" + "    data_file_configs.separator AS df_separator,\n" + "    data_file_configs.columns AS df_columns,\n" + "    data_file_configs.destination AS df_destination,\n" + "    data_file_configs.created_at AS df_created_at,\n" + "    data_file_configs.updated_at AS df_updated_at,\n" + "    data_file_configs.created_by AS df_created_by,\n" + "    data_file_configs.updated_by AS df_updated_by,\n" + "    data_file_configs.backup_path AS df_backup_path,\n" + "    data_file_configs.sourcetination AS df_sourcetination\n" + "FROM \n" + "    data_files\n" + "LEFT JOIN \n" + "    data_file_configs ON data_files.df_config_id = data_file_configs.id\n" + "WHERE \n" + "" + "    data_files.status = 'SU' \n" + "    AND DATE(data_files.created_at) = DATE(CURRENT_DATE) \n" + "    AND data_files.is_inserted = 0;";
            java.sql.PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();


            // Xử lý kết quả truy vấn
            while (resultSet.next()) {
                DataFile dataFile = DataFile.builder().id(resultSet.getInt("id")).name(resultSet.getString("name")).row_count(resultSet.getInt("row_count")).df_config_id(resultSet.getInt("df_config_id")).status(resultSet.getString("status")).file_timestamp(resultSet.getTimestamp("file_timestamp")).data_range_from(resultSet.getTimestamp("data_range_from")).data_range_to(resultSet.getTimestamp("data_range_to")).note(resultSet.getString("note")).created_at(resultSet.getTimestamp("created_at")).updated_at(resultSet.getTimestamp("updated_at")).created_by(resultSet.getInt("created_by")).updated_by(resultSet.getInt("updated_by")).is_inserted(resultSet.getBoolean("is_inserted")).deleted_at(resultSet.getTimestamp("deleted_at")).dataFileConfig(DataFileConfig.builder().id(resultSet.getInt("df_id")).name(resultSet.getString("df_name")).code(resultSet.getString("df_code")).description(resultSet.getString("df_description")).source_path(resultSet.getString("df_source_path")).location(resultSet.getString("df_location")).format(resultSet.getString("df_format")).separator(resultSet.getString("df_separator")).columns(resultSet.getString("df_columns")).destination(resultSet.getString("df_destination")).created_at(resultSet.getTimestamp("df_created_at")).updated_at(resultSet.getTimestamp("df_updated_at")).created_by(resultSet.getInt("df_created_by")).updated_by(resultSet.getInt("df_updated_by")).backup_path(resultSet.getString("df_backup_path")).sourcetination(resultSet.getString("df_sourcetination")).build()).build();
//
                // Thêm đối tượng DataFile vào danh sách
                pendingDataFiles.add(dataFile);
            }

            // Đóng tài nguyên
            resultSet.close();
            preparedStatement.close();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return pendingDataFiles;
    }

    /**
     * Xóa toàn bộ dữ liệu trong bảng weather_data.
     * Phương thức này sẽ thực hiện câu lệnh TRUNCATE TABLE để xóa hết dữ liệu trong bảng,
     * làm cho trạng thái is_inserted của các đối tượng DataFile trở về 0 (chưa được chèn).
     *
     * @param connection Đối tượng Connection đến cơ sở dữ liệu.
     * @throws SQLException Nếu có lỗi xảy ra trong quá trình thực hiện câu lệnh SQL.
     */
    public static void truncateTable(Connection connection) throws SQLException {
        String query = "TRUNCATE TABLE weather_data";

        // Using a Statement to execute the query directly (no need for PreparedStatement)
        Statement statement = connection.createStatement();
        statement.executeUpdate(query);

        // Optional: Close the statement to free up resources
        statement.close();
    }

    /**
     * Kiểm tra sự tồn tại của một tệp trong thư mục.
     * Phương thức này kiểm tra xem thư mục và tệp cụ thể có tồn tại không, và trả về đường dẫn đầy đủ của tệp nếu nó tồn tại.
     * Nếu thư mục hoặc tệp không tồn tại, hoặc đường dẫn không hợp lệ, phương thức sẽ trả về chuỗi rỗng.
     *
     * @param folderPath Đường dẫn đến thư mục chứa tệp.
     * @param fileName   Tên của tệp cần kiểm tra.
     * @return Đường dẫn đầy đủ của tệp nếu tồn tại, hoặc chuỗi rỗng nếu không tồn tại.
     */
    public static String isFileExists(String folderPath, String fileName) {
        Path folder = Paths.get(folderPath);
        Path filePath = Paths.get(folderPath, fileName);
        // Kiểm tra xem thư mục tồn tại hay không
        if (Files.exists(folder) && Files.isDirectory(folder)) {
            // Kiểm tra xem tệp có tồn tại hay không
            if (Files.exists(filePath) && Files.isRegularFile(filePath)) {
                return filePath.toString();
            }
        }

        return "";
    }


    /**
     * Sao chép một tệp từ một đường dẫn nguồn đến một thư mục đích.
     * Phương thức này thực hiện việc sao chép tệp từ đường dẫn nguồn đến thư mục đích,
     * và trả về đường dẫn đầy đủ của tệp mới tạo nếu sao chép thành công.
     * Nếu có lỗi trong quá trình sao chép, phương thức in ra trace của lỗi và trả về chuỗi rỗng.
     *
     * @param sourceFilePath        Đường dẫn đến tệp nguồn cần sao chép.
     * @param destinationFolderPath Đường dẫn đến thư mục đích cho tệp sao chép.
     * @return Đường dẫn đầy đủ của tệp mới nếu sao chép thành công, hoặc chuỗi rỗng nếu có lỗi.
     */
    public static String copyFileToFolder(String sourceFilePath, String destinationFolderPath) {
        try {
            File sourceFile = new File(sourceFilePath);
            String fileNameNew = destinationFolderPath + sourceFile.getName();


            Path sourcePath = Paths.get(sourceFilePath);
            Path destinationPath = Paths.get(destinationFolderPath, sourcePath.getFileName().toString());

            Files.copy(sourcePath, destinationPath, StandardCopyOption.REPLACE_EXISTING);

            System.out.println("File copied successfully.");
            return fileNameNew;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * Cập nhật thông tin của đối tượng DataFile trong cơ sở dữ liệu.
     * Phương thức này thực hiện truy vấn SQL UPDATE để cập nhật các thuộc tính của đối tượng DataFile
     * trong bảng data_files dựa trên giá trị của trường id.
     *
     * @param connection Đối tượng Connection đến cơ sở dữ liệu.
     * @param dataFile   Đối tượng DataFile chứa thông tin mới cần cập nhật.
     * @return true nếu cập nhật thành công, false nếu có lỗi hoặc không có dòng nào bị ảnh hưởng.
     */
    public static boolean updateDataFile(Connection connection, DataFile dataFile) {
        try {
            // Tạo truy vấn SQL UPDATE
            String query = "UPDATE data_files\n" + "SET\n" + "    name = ?,\n" + "    row_count = ?,\n" + "    df_config_id = ?,\n" + "    status = ?,\n" + "    file_timestamp = ?,\n" + "    data_range_from = ?,\n" + "    data_range_to = ?,\n" + "    note = ?,\n" + "    updated_at = ?,\n" + "    created_by = ?,\n" + "    updated_by = ?,\n" + "    is_inserted = ?,\n" + "    deleted_at = ?\n" + "WHERE\n" + "    id = ?;\n";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                // Đặt giá trị cho các tham số trong truy vấn
                preparedStatement.setString(1, dataFile.name);
                preparedStatement.setInt(2, dataFile.row_count);
                preparedStatement.setInt(3, dataFile.df_config_id);
                preparedStatement.setString(4, dataFile.status);
                preparedStatement.setTimestamp(5, dataFile.file_timestamp);
                preparedStatement.setTimestamp(6, dataFile.data_range_from);
                preparedStatement.setTimestamp(7, dataFile.data_range_to);
                preparedStatement.setString(8, dataFile.note);
                preparedStatement.setTimestamp(9, new Timestamp(System.currentTimeMillis()));
                preparedStatement.setInt(10, dataFile.created_by);
                preparedStatement.setInt(11, dataFile.updated_by);
                preparedStatement.setBoolean(12, dataFile.is_inserted);
                preparedStatement.setTimestamp(13, dataFile.deleted_at);

                // Đặt giá trị cho tham số WHERE (id)
                preparedStatement.setInt(14, dataFile.id);

                // Thực hiện truy vấn
                int rowsAffected = preparedStatement.executeUpdate();

                // Kiểm tra xem có dòng nào bị ảnh hưởng hay không
                if (rowsAffected > 0) {
                    System.out.println("DataFile updated successfully.");
                    return true;
                } else {
                    System.out.println("No rows affected. DataFile not found.");
                    return false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Cập nhật trạng thái is_inserted của đối tượng DataFile
     *
     * @param connection
     * @param storedProcedureName
     * @param parameters
     * @return
     */
    public static CompletableFuture<Integer> callStoredProcedureAsync(Connection connection, String storedProcedureName, List<Object> parameters) {
        return CompletableFuture.supplyAsync(() -> callStoredProcedure(connection, storedProcedureName, parameters));
    }

    /**
     * Gọi một danh sách các stored procedure từ cơ sở dữ liệu bất đồng bộ (asynchronously).
     * Phương thức này thực hiện việc gọi các stored procedure từ danh sách được cung cấp và trả về một CompletableFuture
     * chứa danh sách các giá trị đầu ra của các stored procedure tương ứng.
     *
     * @param connection           Đối tượng Connection đến cơ sở dữ liệu.
     * @param storedProcedureInfos Danh sách các thông tin về stored procedure cần gọi. Mỗi phần tử trong danh sách chứa
     *                             tên của stored procedure (String) và danh sách tham số đầu vào (List<Object>).
     * @return CompletableFuture<List < Integer>> Chứa danh sách các giá trị đầu ra của các stored procedure tương ứng.
     */
    public static CompletableFuture<List<Integer>> callStoredProceduresAsync(Connection connection, List<List<Object>> storedProcedureInfos) {
        List<CompletableFuture<Integer>> futures = storedProcedureInfos.stream().map(info -> callStoredProcedureAsync(connection, (String) info.get(0), (List<Object>) info.get(1))).collect(Collectors.toList());

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).thenApply(ignored -> futures.stream().map(CompletableFuture::join).collect(Collectors.toList()));
    }

    /**
     * Gọi một stored procedure từ cơ sở dữ liệu.
     * Phương thức này thực hiện việc gọi một stored procedure với tên và tham số cung cấp,
     * bao gồm cả tham số đầu vào và tham số đầu ra, và trả về giá trị của tham số đầu ra sau khi gọi procedure.
     *
     * @param connection          Đối tượng Connection đến cơ sở dữ liệu.
     * @param storedProcedureName Tên của stored procedure cần gọi.
     * @param parameters          Danh sách các tham số đầu vào của stored procedure.
     * @return Giá trị của tham số đầu ra sau khi gọi stored procedure, hoặc -1 nếu có lỗi.
     */
    public static int callStoredProcedure(Connection connection, String storedProcedureName, List<Object> parameters) {
        StringBuilder callStatement = new StringBuilder("{CALL " + storedProcedureName + "(");

        for (int i = 0; i < parameters.size(); i++) {
            callStatement.append("?");
            if (i < parameters.size() - 1) {
                callStatement.append(", ");
            }
        }

        callStatement.append(", ?)}");  // Add an output parameter

        try (CallableStatement callableStatement = connection.prepareCall(callStatement.toString())) {
            // Set input parameters
            for (int i = 0; i < parameters.size(); i++) {
                callableStatement.setObject(i + 1, parameters.get(i));
            }

            // Register the output parameter
            callableStatement.registerOutParameter(parameters.size() + 1, Types.INTEGER);

            // Execute the stored procedure
            callableStatement.execute();

            // Retrieve the value of the output parameter
            return callableStatement.getInt(parameters.size() + 1);

        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
    }


    /**
     * Xóa một tệp văn bản từ hệ thống tệp.
     * Phương thức này thực hiện việc xóa một tệp văn bản từ hệ thống tệp dựa trên đường dẫn của tệp.
     *
     * @param filePath Đường dẫn đến tệp văn bản cần xóa.
     * @return true nếu xóa thành công, false nếu tệp không tồn tại hoặc có lỗi trong quá trình xóa.
     */
    public static boolean deleteFile(String filePath) {
        try {
            File file = new File(filePath);

            if (file.exists()) {
                if (file.delete()) {
                    System.out.println("File deleted successfully: " + filePath);
                    return true;
                } else {
                    System.out.println("Failed to delete file: " + filePath);
                    return false;
                }
            } else {
                System.out.println("File not found: " + filePath);
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Thực hiện quá trình staging bằng cách nạp dữ liệu từ tệp văn bản vào bảng weather_data trong cơ sở dữ liệu.
     *
     * @param path       Đường dẫn đến tệp văn bản chứa dữ liệu cần staging.
     * @param connection Đối tượng Connection đến cơ sở dữ liệu.
     */
    public static void excuteStaging(String path, Connection connection) {
        try {
            path = path.replace("\\", "\\\\");
            System.out.println(path);

            String sql = String.format("\tLOAD DATA INFILE '%s' \n" + "\tINTO TABLE weather_data\n" + "\tFIELDS TERMINATED BY ',' \n" + "\tLINES TERMINATED BY '\\n' IGNORE 1 LINES;\n", path);

            System.out.println(sql);
            Statement statement = connection.createStatement();
            statement.execute(sql);

        } catch (SQLException e) {
            System.err.println("SQL Exception: " + e.getMessage());

            e.printStackTrace();
        }
    }

    public static void main2() throws IOException {
        JsonObject controlInfor = readJsonString(readFileToString("control.json"));
        Connection connectionControl = getConnection(controlInfor.get("url").getAsString(), controlInfor.get("user").getAsString(), controlInfor.get("password").getAsString());
        List<DataFile> pendingDataFiles = getPendingDataFiles(connectionControl);
        System.out.println(pendingDataFiles);
        pendingDataFiles.forEach(dataFile -> {
            String fileOrigin = isFileExists(dataFile.getDataFileConfig().getSource_path(), dataFile.getName());
            System.out.println(fileOrigin);
            if (!"".equals(fileOrigin)) {
                String newFilePath = copyFileToFolder(dataFile.getDataFileConfig().getSource_path() + File.separator + dataFile.getName(), dataFile.getDataFileConfig().getBackup_path() + File.separator);
                String f = null;
                try {
                    f = new File(newFilePath).getCanonicalPath();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                String sourcetination = dataFile.getDataFileConfig().getSourcetination();
                String destination = dataFile.getDataFileConfig().getDestination();

                try {
                    JsonObject stagingInfor = readJsonString(sourcetination);
                    JsonObject warehouseInfor = readJsonString(destination);

                    Connection connectionStaging = getConnection(stagingInfor.get("url").getAsString(), stagingInfor.get("user").getAsString(), stagingInfor.get("password").getAsString());
                    Connection connectionWarehouse = getConnection(warehouseInfor.get("url").getAsString(), warehouseInfor.get("user").getAsString(), warehouseInfor.get("password").getAsString());
                    excuteStaging(f, connectionStaging);
                    List<WeatherData> allStaging = getAllStaging(connectionStaging);
                    for (WeatherData staging : allStaging) {
                        System.out.println(staging);
                        WeatherDataTransform weatherDataTransform = WeatherDataTransform.builder().district(staging.getDistrict()).province(staging.getProvince()).date(Date.valueOf(staging.getDate())).time(Time.valueOf(staging.getTime() + ":00")).temperatureMin(Float.parseFloat(staging.getTemperatureMin())).temperatureMax(Float.parseFloat(staging.getTemperatureMax())).description(staging.getDescription()).humidity(Float.parseFloat(staging.getHumidity())).windSpeed(Float.parseFloat(staging.getWindSpeed())).uvIndex(Float.parseFloat(staging.getUvIndex())).visibility(Float.parseFloat(staging.getVisibility())).pressure(Integer.parseInt(staging.getPressure())).stopPoint(Float.parseFloat(staging.getStopPoint())).url(staging.getUrl()).ip(staging.getIp()).build();

                        int result = callStoredProcedure(connectionWarehouse, "load_datetime_dim_type_2", List.of(weatherDataTransform.getDate(), weatherDataTransform.getTime()));
                        int lo = callStoredProcedure(connectionWarehouse, "load_location_dim_type_3", List.of(weatherDataTransform.getProvince(), weatherDataTransform.getDistrict(), weatherDataTransform.getUrl()));

                        int idFact = callStoredProcedure(connectionWarehouse, "load_weather_fact_type_2", List.of(result, lo, weatherDataTransform.getTemperatureMin(), weatherDataTransform.getTemperatureMax(), weatherDataTransform.getHumidity(), weatherDataTransform.getVisibility(), weatherDataTransform.getWindSpeed(), weatherDataTransform.getStopPoint(), weatherDataTransform.getUvIndex(), weatherDataTransform.getUrl(), weatherDataTransform.getDescription(), weatherDataTransform.getPressure()));
                    }
                    truncateTable(connectionStaging);
                    dataFile.setIs_inserted(true);
                    dataFile.setNote("File inserted");
                    updateDataFile(connectionControl, dataFile);
                    boolean deleteFile = deleteFile(fileOrigin);
                    if (deleteFile) {
                        dataFile.setDeleted_at(new Timestamp(System.currentTimeMillis()));
                        updateDataFile(connectionControl, dataFile);
                    } else {
                        dataFile.setNote("File not deleted");
                        updateDataFile(connectionControl, dataFile);
                    }
                    closeConnection(connectionStaging);
                    closeConnection(connectionWarehouse);
                } catch (IOException | SQLException e) {
                    throw new RuntimeException(e);
                }


            } else {
                dataFile.setIs_inserted(true);
                dataFile.setNote("File not found");
                updateDataFile(connectionControl, dataFile);
            }

        });

        closeConnection(connectionControl);
    }

    public static void main(String[] args) throws IOException {
        main2();

    }


}