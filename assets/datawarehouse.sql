/*
 Navicat Premium Data Transfer

 Source Server         : localhost
 Source Server Type    : MySQL
 Source Server Version : 100424
 Source Host           : localhost:3306
 Source Schema         : datawarehouse

 Target Server Type    : MySQL
 Target Server Version : 100424
 File Encoding         : 65001

 Date: 07/10/2023 08:08:02
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for data_checkpoints
-- ----------------------------
DROP TABLE IF EXISTS `data_checkpoints`;
CREATE TABLE `data_checkpoints`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `group_name` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `name` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `code` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `data_upto_date` date NULL DEFAULT NULL,
  `note` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  `created_by` int NULL DEFAULT NULL,
  `updated_by` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of data_checkpoints
-- ----------------------------

-- ----------------------------
-- Table structure for data_file_configs
-- ----------------------------
DROP TABLE IF EXISTS `data_file_configs`;
CREATE TABLE `data_file_configs`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `code` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `source_path` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `location` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `format` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `separator` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `columns` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `destination` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  `created_by` int NULL DEFAULT NULL,
  `updated_by` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `code`(`code` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of data_file_configs
-- ----------------------------
INSERT INTO `data_file_configs` VALUES (1, 'thoitiet.vn', '123a', 'trang web thời tiết 63 tỉnh thành việt nam', 'https://thoitiet.vn/', 'F:\\warehouse\\data_file\\thoitiet.vn', 'csv', 'tinh	nhiet do	đá	ádasd	ádasd', 'tinh	nhiet do	đá	ádasd	ádasd\r\n', '{\r\n\"username\":\"root\",\r\n\"password\":\"\",\r\n\"port\":8080,\r\n\"ip\":\"localhost\",\r\n\"type\":\"mysql\",\r\n\"name\":\"datawarehouse\"\r\n}', NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for data_files
-- ----------------------------
DROP TABLE IF EXISTS `data_files`;
CREATE TABLE `data_files`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `name` varchar(1000) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `row_count` bigint NULL DEFAULT NULL,
  `df_config_id` int NULL DEFAULT NULL,
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `file_timestamp` timestamp NULL DEFAULT NULL,
  `data_range_from` timestamp NULL DEFAULT NULL,
  `date_range_to` timestamp NULL DEFAULT NULL,
  `note` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  `created_by` int NULL DEFAULT NULL,
  `updated_by` int NULL DEFAULT NULL,
  `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `df`(`df_config_id` ASC) USING BTREE,
  CONSTRAINT `data_files_ibfk_1` FOREIGN KEY (`df_config_id`) REFERENCES `data_file_configs` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of data_files
-- ----------------------------
INSERT INTO `data_files` VALUES (1, '1696493528925_E_500.csv', 500, 1, 'CE', '2023-10-04 16:00:19', '2023-10-05 15:53:30', '2023-10-06 15:53:35', 'đã hoàn thành', NULL, NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for logs
-- ----------------------------
DROP TABLE IF EXISTS `logs`;
CREATE TABLE `logs`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `status` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `created_at` timestamp NULL DEFAULT NULL,
  `updated_at` timestamp NULL DEFAULT NULL,
  `count` int NULL DEFAULT NULL,
  `message` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `excelId` int NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of logs
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
