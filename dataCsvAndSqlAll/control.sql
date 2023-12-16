/*
 Navicat MySQL Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 100428 (10.4.28-MariaDB)
 Source Host           : localhost:3306
 Source Schema         : control

 Target Server Type    : MySQL
 Target Server Version : 100428 (10.4.28-MariaDB)
 File Encoding         : 65001

 Date: 16/12/2023 08:38:40
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for data_checkpoints
-- ----------------------------
DROP TABLE IF EXISTS `data_checkpoints`;
CREATE TABLE `data_checkpoints`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `group_name` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `name` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `code` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `data_upto_date` timestamp NULL DEFAULT NULL,
  `note` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp,
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp ON UPDATE CURRENT_TIMESTAMP,
  `created_by` int NULL DEFAULT NULL,
  `updated_by` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of data_checkpoints
-- ----------------------------
INSERT INTO `data_checkpoints` VALUES (1, 'ScrapingCheckpoint', 'Data Collection Completed', '2023121115193024', '2023-12-11 15:19:03', 'Completed scraping of weather data', '2023-12-11 15:19:03', '2023-12-11 15:19:04', 1, 1);
INSERT INTO `data_checkpoints` VALUES (2, 'ScrapingCheckpoint', 'Data Collection Completed', '2023121207343360', '2023-12-12 07:34:35', 'Completed scraping of weather data', '2023-12-12 07:34:35', '2023-12-12 07:34:36', 1, 1);
INSERT INTO `data_checkpoints` VALUES (3, 'LoadCsvToStagingCheckpoint', 'Load Csv File To Staging Database Completed', 'LCTS202312121600343360', '2023-12-12 16:02:33', 'Completed Load Csv File To Staging Database', '2023-12-12 16:02:33', '2023-12-12 16:02:33', 1, 1);
INSERT INTO `data_checkpoints` VALUES (4, 'ScrapingCheckpoint', 'Data Collection Completed', '2023121307443408', '2023-12-13 07:44:12', 'Completed scraping of weather data', '2023-12-13 07:44:12', '2023-12-13 07:44:13', 1, 1);
INSERT INTO `data_checkpoints` VALUES (5, 'LoadCsvToStagingCheckpoint', 'Load Csv File To Staging Database Completed', 'LCTS202312130745163408', '2023-12-13 07:47:17', 'Completed Load Csv File To Staging Database', '2023-12-13 07:47:17', '2023-12-13 07:47:17', 1, 1);
INSERT INTO `data_checkpoints` VALUES (6, 'ScrapingCheckpoint', 'Data Collection Completed', '2023121422143264', '2023-12-14 22:14:49', 'Completed scraping of weather data', '2023-12-14 22:14:49', '2023-12-14 22:14:50', 1, 1);
INSERT INTO `data_checkpoints` VALUES (7, 'LoadCsvToStagingCheckpoint', 'Load Csv File To Staging Database Completed', 'LCTS202312142222353264', '2023-12-14 22:24:25', 'Completed Load Csv File To Staging Database', '2023-12-14 22:24:25', '2023-12-14 22:24:25', 1, 1);
INSERT INTO `data_checkpoints` VALUES (8, 'ScrapingCheckpoint', 'Data Collection Completed', '2023121507223504', '2023-12-15 07:22:26', 'Completed scraping of weather data', '2023-12-15 07:22:26', '2023-12-15 07:22:27', 1, 1);
INSERT INTO `data_checkpoints` VALUES (9, 'LoadCsvToStagingCheckpoint', 'Load Csv File To Staging Database Completed', 'LCTS202312150742443504', '2023-12-15 07:44:50', 'Completed Load Csv File To Staging Database', '2023-12-15 07:44:50', '2023-12-15 07:44:50', 1, 1);
INSERT INTO `data_checkpoints` VALUES (10, 'ScrapingCheckpoint', 'Data Collection Completed', '2023121607261968', '2023-12-16 07:26:52', 'Completed scraping of weather data', '2023-12-16 07:26:52', '2023-12-16 07:26:54', 1, 1);
INSERT INTO `data_checkpoints` VALUES (11, 'LoadCsvToStagingCheckpoint', 'Load Csv File To Staging Database Completed', 'LCTS202312160736371968', '2023-12-16 07:37:48', 'Completed Load Csv File To Staging Database', '2023-12-16 07:37:48', '2023-12-16 07:37:48', 1, 1);
INSERT INTO `data_checkpoints` VALUES (12, 'ScrapingCheckpoint', 'Data Collection Completed', '2023121607503504', '2023-12-16 07:50:13', 'Completed scraping of weather data', '2023-12-16 07:50:13', '2023-12-16 07:50:14', 1, 1);
INSERT INTO `data_checkpoints` VALUES (13, 'LoadCsvToStagingCheckpoint', 'Load Csv File To Staging Database Completed', 'LCTS202312160750323504', '2023-12-16 07:52:36', 'Completed Load Csv File To Staging Database', '2023-12-16 07:52:36', '2023-12-16 07:52:36', 1, 1);

-- ----------------------------
-- Table structure for data_file_configs
-- ----------------------------
DROP TABLE IF EXISTS `data_file_configs`;
CREATE TABLE `data_file_configs`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `code` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `source_path` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `location` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `format` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `separator` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `columns` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `destination` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp,
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp ON UPDATE CURRENT_TIMESTAMP,
  `created_by` int NULL DEFAULT NULL,
  `updated_by` int NULL DEFAULT NULL,
  `backup_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 14 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of data_file_configs
-- ----------------------------
INSERT INTO `data_file_configs` VALUES (1, 'WeatherDataScrapingConfig', '2023121115193024', 'Configuration for scraping weather data', 'https://thoitiet.vn', 'localhost', 'CSV', ',', 'Province,District,Date,Time,TemperatureMin,TemperatureMax,Description,Humidity,WindSpeed,UVIndex,Visibility,Pressure,StopPoint,AirQuality,URL,IP', 'D:\\dataWeatherCsv\\2023-12-11_15-19_3024.csv', '2023-12-11 15:19:03', '2023-12-12 10:24:18', 1, 1, '/backup_path');
INSERT INTO `data_file_configs` VALUES (2, 'WeatherDataScrapingConfig', '2023121207343360', 'Configuration for scraping weather data', 'https://thoitiet.vn', 'localhost', 'CSV', ',', 'Province,District,Date,Time,TemperatureMin,TemperatureMax,Description,Humidity,WindSpeed,UVIndex,Visibility,Pressure,StopPoint,AirQuality,URL,IP', 'D:\\dataWeatherCsv\\2023-12-12_07-34_3360.csv', '2023-12-12 07:34:35', '2023-12-12 10:24:18', 1, 1, '/backup_path');
INSERT INTO `data_file_configs` VALUES (3, 'LoadCsvToStagingConfig', 'LCTS202312121600343360', 'Configuration for load csv file to staging config', 'D:\\dataWeatherCsv\\2023-12-12_07-34_3360.csv', 'localhost', NULL, NULL, 'id, Date, Time, Province, Wards, District, Temperature, Feeling, Status, Humidity, Vision, Wind_speed, Stop_point, Uv_index, Airquality, Last_update_time, Breadcrumb, Url, Path, Dtrequest, Request, Method, Protocols, Status_code, Host, Server, Ip', 'table weatherdata in staging database', '2023-12-12 16:02:33', '2023-12-12 16:02:33', 1, 1, '/backup_path');
INSERT INTO `data_file_configs` VALUES (4, 'WeatherDataScrapingConfig', '2023121307443408', 'Configuration for scraping weather data', 'https://thoitiet.vn', 'localhost', 'CSV', ',', 'Province,District,Date,Time,TemperatureMin,TemperatureMax,Description,Humidity,WindSpeed,UVIndex,Visibility,Pressure,StopPoint,AirQuality,URL,IP', 'D:\\dataWeatherCsv\\2023-12-13_07-44_3408.csv', '2023-12-13 07:44:12', '2023-12-13 07:44:13', 1, 1, '/backup_path');
INSERT INTO `data_file_configs` VALUES (5, 'LoadCsvToStagingConfig', 'LCTS202312130745163408', 'Configuration for load csv file to staging config', 'D:\\dataWeatherCsv\\2023-12-13_07-44_3408.csv', 'localhost', NULL, NULL, 'id, Date, Time, Province, Wards, District, Temperature, Feeling, Status, Humidity, Vision, Wind_speed, Stop_point, Uv_index, Airquality, Last_update_time, Breadcrumb, Url, Path, Dtrequest, Request, Method, Protocols, Status_code, Host, Server, Ip', 'table weatherdata in staging database', '2023-12-13 07:47:17', '2023-12-13 07:47:17', 1, 1, '/backup_path');
INSERT INTO `data_file_configs` VALUES (6, 'WeatherDataScrapingConfig', '2023121422143264', 'Configuration for scraping weather data', 'https://thoitiet.vn', 'localhost', 'CSV', ',', 'Province,District,Date,Time,TemperatureMin,TemperatureMax,Description,Humidity,WindSpeed,UVIndex,Visibility,Pressure,StopPoint,AirQuality,URL,IP', 'D:\\dataWeatherCsv\\2023-12-14_22-14_3264.csv', '2023-12-14 22:14:49', '2023-12-14 22:14:50', 1, 1, '/backup_path');
INSERT INTO `data_file_configs` VALUES (7, 'LoadCsvToStagingConfig', 'LCTS202312142222353264', 'Configuration for load csv file to staging config', 'D:\\dataWeatherCsv\\2023-12-14_22-14_3264.csv', 'localhost', NULL, NULL, 'id, Date, Time, Province, Wards, District, Temperature, Feeling, Status, Humidity, Vision, Wind_speed, Stop_point, Uv_index, Airquality, Last_update_time, Breadcrumb, Url, Path, Dtrequest, Request, Method, Protocols, Status_code, Host, Server, Ip', 'table weatherdata in staging database', '2023-12-14 22:24:25', '2023-12-14 22:24:25', 1, 1, '/backup_path');
INSERT INTO `data_file_configs` VALUES (8, 'WeatherDataScrapingConfig', '2023121507223504', 'Configuration for scraping weather data', 'https://thoitiet.vn', 'localhost', 'CSV', ',', 'Province,District,Date,Time,TemperatureMin,TemperatureMax,Description,Humidity,WindSpeed,UVIndex,Visibility,Pressure,StopPoint,AirQuality,URL,IP', 'D:\\dataWeatherCsv\\2023-12-15_07-22_3504.csv', '2023-12-15 07:22:26', '2023-12-15 07:22:27', 1, 1, '/backup_path');
INSERT INTO `data_file_configs` VALUES (9, 'LoadCsvToStagingConfig', 'LCTS202312150742443504', 'Configuration for load csv file to staging config', 'D:\\dataWeatherCsv\\2023-12-15_07-22_3504.csv', 'localhost', NULL, NULL, 'id, Date, Time, Province, Wards, District, Temperature, Feeling, Status, Humidity, Vision, Wind_speed, Stop_point, Uv_index, Airquality, Last_update_time, Breadcrumb, Url, Path, Dtrequest, Request, Method, Protocols, Status_code, Host, Server, Ip', 'table weatherdata in staging database', '2023-12-15 07:44:50', '2023-12-15 07:44:50', 1, 1, '/backup_path');
INSERT INTO `data_file_configs` VALUES (10, 'WeatherDataScrapingConfig', '2023121607261968', 'Configuration for scraping weather data', 'https://thoitiet.vn', 'localhost', 'CSV', ',', 'Province,District,Date,Time,TemperatureMin,TemperatureMax,Description,Humidity,WindSpeed,UVIndex,Visibility,Pressure,StopPoint,AirQuality,URL,IP', 'D:\\dataWeatherCsv\\2023-12-16_07-26_1968.csv', '2023-12-16 07:26:52', '2023-12-16 07:26:54', 1, 1, '/backup_path');
INSERT INTO `data_file_configs` VALUES (11, 'LoadCsvToStagingConfig', 'LCTS202312160736371968', 'Configuration for load csv file to staging config', 'D:\\dataWeatherCsv\\2023-12-16_07-26_1968.csv', 'localhost', NULL, NULL, 'id, Date, Time, Province, Wards, District, Temperature, Feeling, Status, Humidity, Vision, Wind_speed, Stop_point, Uv_index, Airquality, Last_update_time, Breadcrumb, Url, Path, Dtrequest, Request, Method, Protocols, Status_code, Host, Server, Ip', 'table weatherdata in staging database', '2023-12-16 07:37:48', '2023-12-16 07:37:48', 1, 1, '/backup_path');
INSERT INTO `data_file_configs` VALUES (12, 'WeatherDataScrapingConfig', '2023121607503504', 'Configuration for scraping weather data', 'https://thoitiet.vn', 'localhost', 'CSV', ',', 'Province,District,Date,Time,TemperatureMin,TemperatureMax,Description,Humidity,WindSpeed,UVIndex,Visibility,Pressure,StopPoint,AirQuality,URL,IP', 'D:\\dataWeatherCsv\\2023-12-16_07-50_3504.csv', '2023-12-16 07:50:13', '2023-12-16 07:50:14', 1, 1, '/backup_path');
INSERT INTO `data_file_configs` VALUES (13, 'LoadCsvToStagingConfig', 'LCTS202312160750323504', 'Configuration for load csv file to staging config', 'D:\\dataWeatherCsv\\2023-12-16_07-50_3504.csv', 'localhost', NULL, NULL, 'id, Date, Time, Province, Wards, District, Temperature, Feeling, Status, Humidity, Vision, Wind_speed, Stop_point, Uv_index, Airquality, Last_update_time, Breadcrumb, Url, Path, Dtrequest, Request, Method, Protocols, Status_code, Host, Server, Ip', 'table weatherdata in staging database', '2023-12-16 07:52:36', '2023-12-16 07:52:36', 1, 1, '/backup_path');

-- ----------------------------
-- Table structure for data_files
-- ----------------------------
DROP TABLE IF EXISTS `data_files`;
CREATE TABLE `data_files`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `row_count` bigint NULL DEFAULT NULL,
  `df_config_id` int NULL DEFAULT NULL,
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL,
  `file_timestamp` timestamp NULL DEFAULT NULL,
  `data_range_from` timestamp NULL DEFAULT NULL,
  `data_range_to` timestamp NULL DEFAULT NULL,
  `note` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL,
  `created_at` timestamp NOT NULL DEFAULT current_timestamp,
  `updated_at` timestamp NOT NULL DEFAULT current_timestamp ON UPDATE CURRENT_TIMESTAMP,
  `created_by` int NULL DEFAULT NULL,
  `updated_by` int NULL DEFAULT NULL,
  `is_inserted` bit(1) NULL DEFAULT NULL,
  `deleted_at` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `df_config_id`(`df_config_id` ASC) USING BTREE,
  CONSTRAINT `data_files_ibfk_1` FOREIGN KEY (`df_config_id`) REFERENCES `data_file_configs` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of data_files
-- ----------------------------
INSERT INTO `data_files` VALUES (1, 'ScrapDataToCsv', 3024, 1, 'SU', '2023-12-11 15:09:13', '2023-12-11 15:09:13', '2023-12-14 15:09:13', 'Successfully loaded 3-day weather data into CSV from thoitiet.vn', '2023-12-11 15:09:13', '2023-12-11 15:19:40', 1, 1, b'1', NULL);
INSERT INTO `data_files` VALUES (2, 'ScrapDataToCsv', 3360, 2, 'SU', '2023-12-12 07:24:58', '2023-12-12 07:24:58', '2023-12-15 07:24:58', 'Successfully loaded 3-day weather data into CSV from thoitiet.vn', '2023-12-12 07:24:58', '2023-12-12 19:54:38', 1, 1, b'1', NULL);
INSERT INTO `data_files` VALUES (3, 'LoadCsvToStaging', 3360, 3, 'SU', '2023-12-12 16:00:34', '2023-12-12 16:00:34', '2023-12-15 16:00:34', 'Successfully load csv file to staging database', '2023-12-12 16:00:34', '2023-12-12 19:50:30', 1, 1, b'1', NULL);
INSERT INTO `data_files` VALUES (4, 'ScrapDataToCsv', 3408, 4, 'SU', '2023-12-13 07:35:25', '2023-12-13 07:35:25', '2023-12-16 07:35:25', 'Successfully loaded 3-day weather data into CSV from thoitiet.vn', '2023-12-13 07:35:25', '2023-12-13 07:44:12', 1, 1, b'1', NULL);
INSERT INTO `data_files` VALUES (5, 'LoadCsvToStaging', 3408, 5, 'SU', '2023-12-13 07:45:16', '2023-12-13 07:45:16', '2023-12-16 07:45:16', 'Successfully load csv file to staging database', '2023-12-13 07:45:16', '2023-12-13 07:47:17', 1, 1, b'1', NULL);
INSERT INTO `data_files` VALUES (6, 'ScrapDataToCsv', 3264, 6, 'SU', '2023-12-14 22:04:04', '2023-12-14 22:04:04', '2023-12-17 22:04:04', 'Successfully loaded 3-day weather data into CSV from thoitiet.vn', '2023-12-14 22:04:04', '2023-12-14 22:14:49', 1, 1, b'1', NULL);
INSERT INTO `data_files` VALUES (7, 'LoadCsvToStaging', 3264, 7, 'SU', '2023-12-14 22:22:35', '2023-12-14 22:22:35', '2023-12-17 22:22:35', 'Successfully load csv file to staging database', '2023-12-14 22:22:35', '2023-12-14 22:24:59', 1, 1, b'1', NULL);
INSERT INTO `data_files` VALUES (8, 'ScrapDataToCsv', 3504, 8, 'SU', '2023-12-15 07:11:56', '2023-12-15 07:11:56', '2023-12-18 07:11:56', 'Successfully loaded 3-day weather data into CSV from thoitiet.vn', '2023-12-15 07:11:56', '2023-12-15 07:49:19', 1, 1, b'1', NULL);
INSERT INTO `data_files` VALUES (9, 'LoadCsvToStaging', 3504, 9, 'SU', '2023-12-15 07:42:44', '2023-12-15 07:42:44', '2023-12-18 07:42:44', 'Successfully load csv file to staging database', '2023-12-15 07:42:44', '2023-12-15 07:44:50', 1, 1, b'1', NULL);
INSERT INTO `data_files` VALUES (10, 'ScrapDataToCsv', 3504, 12, 'SU', '2023-12-16 07:39:50', '2023-12-16 07:39:50', '2023-12-19 07:39:50', 'Successfully loaded 3-day weather data into CSV from thoitiet.vn', '2023-12-16 07:39:50', '2023-12-16 07:50:13', 1, 1, b'1', NULL);
INSERT INTO `data_files` VALUES (11, 'LoadCsvToStaging', 3504, 13, 'SU', '2023-12-16 07:50:32', '2023-12-16 07:50:32', '2023-12-19 07:50:32', 'Successfully load csv file to staging database', '2023-12-16 07:50:32', '2023-12-16 07:52:36', 1, 1, b'1', NULL);

SET FOREIGN_KEY_CHECKS = 1;
