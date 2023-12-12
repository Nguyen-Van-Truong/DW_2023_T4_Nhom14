package ScrapDataToCsvStorage;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import DBConnectControlDB.ControlDatabaseManager;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

/**
 * Class for scraping weather data hourly and storing it in a CSV file.
 * This class uses Selenium WebDriver for web scraping and handles different
 * aspects of the process including fetching data from URLs, parsing weather
 * information, and saving it into a CSV format.
 */
public class WeatherScrapingHourlyToStorage {

    /**
     * The default region name used when no specific region is specified.
     */
    public static final String All = "";
    /**
     * The name of the Đông Nam Bộ region.
     */
    public static final String DongNamBo = "Đông Nam Bộ";

    /**
     * The total number of URLs for the Đông Nam Bộ region.
     */
    public static final int TOTAL_URL_DONG_NAM_BO = 156;

    /**
     * The total number of URLs to be processed for all regions.
     */
    public static int TOTAL_URL = 1542;

    /**
     * Sets the System output to UTF-8 encoding.
     * This method is used to ensure that the console output can handle UTF-8 characters,
     * which is especially important for non-ASCII text.
     *
     * @return true if the encoding is set successfully, false otherwise
     */
    private static boolean setUTF8Output() {

        try {
            System.setOut(new PrintStream(System.out, true, "UTF-8"));
            return true;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves a list of all provinces from the website.
     *
     * @param regionName The name of the region for which to retrieve provinces. If null or empty, all provinces are retrieved.
     * @return List of ProvinceInfo objects containing province names and URLs
     */
    public static List<ProvinceInfo> getAllProvinces(String regionName) {
        List<ProvinceInfo> provinces = new ArrayList<>();
        WebDriver driver = new ChromeDriver(createChromeOption());

        try {
            getWithRetry(driver, "https://thoitiet.vn", 3);

            List<WebElement> provinceElements;

            if (regionName == null || regionName.isEmpty()) {
                // If regionName is null or empty, get all provinces
                provinceElements = driver.findElements(By.cssSelector(".dropdown-menu .mega-submenu a"));
            } else {
                // Find the div that contains the given region name
                WebElement regionDiv = driver.findElement(By.xpath("//h6[text()='" + regionName + "']/ancestor::div[contains(@class, 'col-megamenu')]"));
                // Find all province elements within this div
                provinceElements = regionDiv.findElements(By.cssSelector("ul.mega-submenu a"));
            }

            for (WebElement provinceElement : provinceElements) {
                String provinceName = provinceElement.getAttribute("title");
                String provinceUrl = provinceElement.getAttribute("href");
                provinces.add(new ProvinceInfo(provinceName, provinceUrl));
            }
        } finally {
            driver.quit();
        }

        return provinces;
    }


    /**
     * Attempts to load a page with a given URL, retrying up to a specified number of times.
     * This method is used to handle intermittent network or server issues by retrying the page load.
     *
     * @param driver     The WebDriver instance used to load the page.
     * @param url        The URL to be loaded.
     * @param maxRetries The maximum number of retry attempts.
     * @return true if the page is successfully loaded, false otherwise.
     */
    private static boolean getWithRetry(WebDriver driver, String url, int maxRetries) {
        for (int attempt = 0; attempt < maxRetries; attempt++) {
            try {
                driver.get(url);
                return true;
            } catch (Exception e) {
                System.err.println("Error accessing URL: " + url + ". Retry attempt " + (attempt + 1));
                try {
                    Thread.sleep(10000); // Wait for 10 seconds before retrying
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        return false;
    }

    /**
     * Retrieves a list of districts for a given province URL.
     *
     * @param provinceUrl URL of the province page
     * @return List of DistrictInfo objects containing district names and URLs
     */
    public static List<DistrictInfo> getDistrictsOfProvince(String provinceUrl) {
        List<DistrictInfo> districts = new ArrayList<>();
        WebDriver driver = new ChromeDriver(createChromeOption());

        try {
            getWithRetry(driver, provinceUrl, 3);

            List<WebElement> districtElements = driver.findElements(By.cssSelector(".khu-vuc-lan-can a"));

            for (WebElement districtElement : districtElements) {
                String districtName = districtElement.getText();
                String districtUrl = districtElement.getAttribute("href");
                districts.add(new DistrictInfo(districtName, districtUrl));
            }
        } finally {
            driver.quit();
        }

        return districts;
    }


    /**
     * Safely gets text from a WebElement, returns a default value if not found.
     *
     * @param detail       WebElement to search within
     * @param selector     By selector to find the desired element
     * @param defaultValue Default value to return if element is not found
     * @return String value of the element or default value
     */
    private static String safelyGetText(WebElement detail, By selector, String defaultValue) {
        try {
            return detail.findElement(selector).getText();
        } catch (NoSuchElementException e) {
            return defaultValue;
        }
    }

    /**
     * Safely gets text from the WebDriver, returns a default value if not found.
     *
     * @param driver       WebDriver instance to use for scraping
     * @param selector     By selector to find the desired element
     * @param defaultValue Default value to return if element is not found
     * @return String value of the element or default value
     */
    private static String safelyGetText(WebDriver driver, By selector, String defaultValue) {
        try {
            return driver.findElement(selector).getText();
        } catch (NoSuchElementException e) {
            return defaultValue;
        }
    }

    /**
     * Retrieves air quality data from a given URL.
     *
     * @param url        URL to scrape for air quality data
     * @param maxRetries Maximum number of retries for scraping
     * @return Air quality as a string, or a default unknown value if not found
     * @throws NoSuchElementException if the air quality information element is not found in the page
     */
    private static String getAirQuality(String url, int maxRetries) {
        WebDriver driver = new ChromeDriver(createChromeOption());
        try {
            getWithRetry(driver, url, maxRetries);
            try {
                WebElement airQualityElement = driver.findElement(By.cssSelector(".air-rules .air-active"));
                return airQualityElement.getText().trim();
            } catch (NoSuchElementException e) {
                System.err.println("Air quality information not found.");
                return "Không rõ";
            }
        } finally {
            driver.quit();
        }
    }


    /**
     * Creates and configures ChromeOptions for the WebDriver.
     * This method sets various properties and arguments for the ChromeDriver,
     * such as headless mode and disabling images for better performance.
     *
     * @return Configured ChromeOptions object.
     */
    private static ChromeOptions createChromeOption() {
        ChromeOptions chromeOptions = new ChromeOptions();
        chromeOptions.addArguments("--headless");
        chromeOptions.addArguments("--no-sandbox");
        chromeOptions.addArguments("--disable-dev-shm-usage");
//		chromeOptions.addArguments("--disable-gpu"); // Disables GPU hardware acceleration
        chromeOptions.addArguments("--disable-extensions"); // Disabling extensions
        chromeOptions.addArguments("--disable-popup-blocking"); // Disabling popups
        chromeOptions.addArguments("disable-infobars"); // Disabling infobars
        chromeOptions.addArguments("--disable-images"); // Disable images
        chromeOptions.addArguments("--blink-settings=imagesEnabled=false"); // More aggressive image disabling
        // Add any additional arguments that are relevant to your use case

        // Optional: Set timeouts for script and page load, if needed
        // driver.manage().timeouts().pageLoadTimeout(40, TimeUnit.SECONDS);
        // driver.manage().timeouts().setScriptTimeout(60, TimeUnit.SECONDS);

        return chromeOptions;
    }

    /**
     * Scrapes hourly weather data for the next 3 days from a given URL.
     * The method parses the web page to extract relevant weather data like temperature,
     * humidity, wind speed, etc., and returns a list of HourlyWeatherInfo objects.
     *
     * @param driver     The WebDriver instance used for scraping.
     * @param url        The URL of the weather data page.
     * @param province   The name of the province for which the data is being scraped.
     * @param district   The name of the district for which the data is being scraped.
     * @param airQuality The air quality information.
     * @return A list of HourlyWeatherInfo objects containing the scraped weather data.
     */
    public static List<HourlyWeatherInfo> scrapeHourlyWeatherData3Days(WebDriver driver, String url, String province, String district, String airQuality) {
        List<HourlyWeatherInfo> hourlyData = new ArrayList<>();
        getWithRetry(driver, url, 3);
        List<WebElement> weatherDetails = driver.findElements(By.cssSelector("details.weather-day"));

        String dewPointSelector = ".weather-detail .d-flex:has(.avatar-img svg[name='dewpoint']) .ml-auto > h3";
        WebElement dewPointElement = driver.findElement(By.cssSelector(dewPointSelector));
        String dewPoint = dewPointElement.getText().trim();

        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M");

        for (WebElement detail : weatherDetails) {
            detail.click();
            HourlyWeatherInfo info = new HourlyWeatherInfo();

            // Determine if it should be a new date
            String timeString = safelyGetText(detail, By.cssSelector(".summary-day span"), "Không rõ thời gian").trim();
            if (timeString.contains("/")) {
                currentDate = currentDate.plusDays(1);
            }

            String time = timeString.contains("/") ? "00:00" : timeString;
            info.setTime(time);
            info.setDate(currentDate.toString());

            info.setTemperatureMin(safelyGetText(detail, By.cssSelector(".summary-temperature-min"), "Không rõ nhiệt độ thấp nhất"));
            info.setTemperatureMax(safelyGetText(detail, By.cssSelector(".summary-temperature-max-value"), "Không rõ nhiệt độ cao nhất"));
            info.setDescription(safelyGetText(detail, By.cssSelector(".summary-description-detail"), "Không rõ mô tả"));
            info.setHumidity(safelyGetText(detail, By.cssSelector(".summary-humidity > span:last-child"), "Không có dữ liệu Độ ẩm"));
            info.setWindSpeed(safelyGetText(detail, By.cssSelector(".summary-speed > span:last-child"), "Không có dữ liệu Tốc độ gió"));
            info.setUvIndex(safelyGetText(detail, By.cssSelector(".weather-content-item .op-8.fw-bold"), "Không có dữ liệu UV"));
            info.setVisibility(safelyGetText(detail, By.xpath(".//h6[contains(text(), 'Tầm nhìn')]/following-sibling::div/span"), "Không có dữ liệu Tầm nhìn"));
            info.setPressure(safelyGetText(detail, By.xpath(".//h6[contains(text(), 'Áp suất')]/following-sibling::div/h3"), "Không có dữ liệu Áp suất"));
            info.setStopPoint(dewPoint);
            info.setUrl(url);
            info.setProvince(province);
            info.setDistrict(district);
            info.setAirQuality(airQuality);

            hourlyData.add(info);
        }
        return hourlyData;
    }

    /**
     * Saves the collected weather data to a CSV file.
     * The method checks if the specified directory exists, creates it if necessary,
     * and then writes the weather data in CSV format to the specified file path.
     *
     * @param weatherData The list of HourlyWeatherInfo objects to be saved.
     * @param filePath    The path of the CSV file where the data will be saved.
     */
    public static void saveToCSV(List<HourlyWeatherInfo> weatherData, String filePath) {
        try {
            // Check if the directory exists, create it if it doesn't
            File file = new File(filePath);
            File directory = new File(file.getParent());
            if (!directory.exists()) {
                directory.mkdirs(); // This will create parent directories if they don't exist
            }

            try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file),
                    StandardCharsets.UTF_8)) {
                writer.write('\ufeff');
                writer.append(
                        "Province,District,Date,Time,TemperatureMin,TemperatureMax,Description,Humidity,WindSpeed,UVIndex,Visibility,Pressure,StopPoint,AirQuality,URL,IP\n");

                String ipAddress = getIPAddress();
                for (HourlyWeatherInfo info : weatherData) {
                    writer.append(info.getProvince()).append(",").append(info.getDistrict()).append(",")
                            .append(info.getDate()).append(",").append(info.getTime()).append(",")
                            .append(info.getTemperatureMin()).append(",").append(info.getTemperatureMax()).append(",")
                            .append(info.getDescription()).append(",").append(info.getHumidity()).append(",")
                            .append(info.getWindSpeed()).append(",").append(info.getUvIndex()).append(",")
                            .append(info.getVisibility()).append(",").append(info.getPressure()).append(",")
                            .append(info.getStopPoint()).append(",").append(info.getAirQuality()).append(",")
                            .append(info.getUrl()).append(",").append(ipAddress).append("\n");
                }
            }
        } catch (IOException e) {
            System.err.println("Error while writing to CSV file: " + e.getMessage());
        }
    }

    /**
     * Attempts to scrape weather data with retries on failure.
     *
     * @param url        URL to scrape for weather data
     * @param maxRetries Maximum number of retries for scraping
     * @param province   Name of the province
     * @param district   Name of the district
     * @param airQuality Air quality information
     * @return List of HourlyWeatherInfo objects containing weather data
     */
    private static List<HourlyWeatherInfo> scrapeWithRetry(String url, int maxRetries, String province, String district, String airQuality) {
        WebDriver driver = new ChromeDriver(createChromeOption());
        try {
            for (int attempt = 0; attempt < maxRetries; attempt++) {
                try {
                    return scrapeHourlyWeatherData3Days(driver, url, province, district, airQuality);
                } catch (TimeoutException e) {
                    System.err.println("Timeout khi thu thập dữ liệu từ URL: " + url + ". Thử lại lần " + (attempt + 1));
                    System.err.println(e.getMessage());
                } catch (Exception e) {
                    System.err.println("Lỗi khác khi thu thập dữ liệu từ URL: " + url + ". Thử lại lần " + (attempt + 1));
                    System.err.println(e.getMessage());
                }
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        } finally {
            driver.quit();
        }
        return new ArrayList<>();
    }


    /**
     * Initiates the process of scraping weather data for the specified region and then saves it to a CSV file.
     * This method manages the scraping tasks for each province and district within the region, handling them concurrently.
     * It aggregates the scraped weather data and air quality information from multiple sources.
     * After completing the scraping tasks, it compiles the data into a CSV file format and saves it.
     *
     * @param regionName    The name of the region for which to scrape weather data. It determines the scope of data collection.
     * @param directoryPath The file path of the directory where the CSV file will be saved.
     */
    public static void scrapeAndSaveToCsv(String regionName, String directoryPath) {
        if (isReadyToRun()) return;

        int dataFileId = insertToControlStartProcess();

        long startTime = System.currentTimeMillis();
        List<HourlyWeatherInfo> allWeatherData = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        AtomicInteger completedUrls = new AtomicInteger(0);

        List<ProvinceInfo> provinces = getAllProvinces(regionName);

        for (ProvinceInfo province : provinces) {
            // Submit separate tasks for each province
            executorService.submit(() -> {
                String airQuality = getAirQuality(province.getUrl(), 3);
                System.out.println("Finished URL: " + completedUrls.incrementAndGet() + "/" + TOTAL_URL_DONG_NAM_BO);

                List<HourlyWeatherInfo> provinceWeatherData = scrapeWithRetry(province.getUrlHour(), 3, province.getName(), "", airQuality);
                synchronized (allWeatherData) {
                    allWeatherData.addAll(provinceWeatherData);
                }
                System.out.println("Finished URL: " + completedUrls.incrementAndGet() + "/" + TOTAL_URL_DONG_NAM_BO);
            });

            // Process districts within each province
            List<DistrictInfo> districts = getDistrictsOfProvince(province.getUrl());

            for (DistrictInfo district : districts) {
                // Submit separate tasks for each district
                executorService.submit(() -> {
                    String airQuality = getAirQuality(district.getUrl(), 3);
                    System.out.println("Finished URL: " + completedUrls.incrementAndGet() + "/" + TOTAL_URL_DONG_NAM_BO);

                    List<HourlyWeatherInfo> districtWeatherData = scrapeWithRetry(district.getUrlHour(), 3, province.getName(), district.getName(), airQuality);
                    synchronized (allWeatherData) {
                        allWeatherData.addAll(districtWeatherData);
                    }
                    System.out.println("Finished URL: " + completedUrls.incrementAndGet() + "/" + TOTAL_URL_DONG_NAM_BO);
                });
            }
        }

        // Finalization of the scraping process
        finalizeScraping(executorService, allWeatherData, completedUrls, startTime, dataFileId, directoryPath);
    }

    /**
     * Checks if the scraping process is ready to run.
     * This method ensures that there is no ongoing scraping process and that a successful process hasn't been completed already on the same day.
     * It queries the control database to determine the readiness of the process.
     *
     * @return true if the scraping process can start, false otherwise.
     */
    private static boolean isReadyToRun() {
        try {
            ControlDatabaseManager dbManager = new ControlDatabaseManager("control");
            if (!dbManager.isReadyToRun()) {
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

    /**
     * Records the start of a scraping process in the control database.
     * This method logs the initiation of a scraping process by inserting a new record in the control database.
     * It includes information such as the current time, expected completion time, and initial status.
     *
     * @return The generated ID for the new data file record in the database, or -1 in case of an error.
     */
    public static int insertToControlStartProcess() {
        try {
            ControlDatabaseManager dbManager = new ControlDatabaseManager("control");
            Timestamp now = Timestamp.valueOf(LocalDateTime.now());
            Timestamp threeDaysLater = Timestamp.valueOf(LocalDateTime.now().plusDays(3));

            // Insert into data_files with status "SE"
            int fileId = dbManager.insertDataFile("", 0, null, "SE", now, now, threeDaysLater, "Scraping process started", now, 1, 1, false, null);

            dbManager.closeConnection();
            System.out.println("Scraping process started and insert to data_files success");
            return fileId;
        } catch (SQLException e) {
            e.printStackTrace();
            return -1; // Return an error indicator
        }
    }


    /**
     * Logs the successful completion of a scraping process in the control database.
     * This method updates the database to reflect the successful completion of the scraping process.
     * It includes details such as the final file name, row count, and configuration details.
     *
     * @param fileName     The name of the CSV file where data is saved.
     * @param dataFileId   The ID of the data file record in the database.
     * @param rowCount     The number of rows of data scraped.
     * @param scrapingTime The time at which the scraping was completed.
     */
    private static void insertToControlSuccessProcess(String fileName, String absolutePath, int dataFileId, int rowCount, LocalDateTime scrapingTime) {
        try {
            ControlDatabaseManager dbManager = new ControlDatabaseManager("control");
            Timestamp now = Timestamp.valueOf(scrapingTime);
            String code = convertFileNameToCode(fileName);
            // Insert into data_file_configs
            int configId = dbManager.insertDataFileConfig("WeatherDataScrapingConfig", code, "Configuration for scraping weather data"
                    , "https://thoitiet.vn", "localhost", "CSV", ",", "Province,District,Date,Time,TemperatureMin,TemperatureMax,Description,Humidity,WindSpeed,UVIndex,Visibility,Pressure,StopPoint,AirQuality,URL,IP", absolutePath, now, 1, 1, "/backup_path");

            // Update into data_files
            dbManager.updateDataFile(dataFileId, fileName, (long) rowCount, configId, "SU", now, true, "Successfully loaded 3-day weather data into CSV from thoitiet.vn");

            // Insert into data_checkpoints
            dbManager.insertDataCheckpoint("ScrapingCheckpoint", "Data Collection Completed", code, now, "Completed scraping of weather data", now, 1, 1);

            dbManager.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Converts a given file name to a standardized code format.
     * This method is used for database entries and removes special characters like dashes and underscores from the file name.
     * It also removes the file extension for a cleaner code representation.
     *
     * @param originalFileName The original file name to be converted.
     * @return The standardized code string derived from the file name.
     */
    private static String convertFileNameToCode(String originalFileName) {
        return originalFileName.trim().replace("-", "").replace("_", "").replace(".csv", "");
    }


    /**
     * Completes the scraping process by terminating the ExecutorService and summarizing the results.
     * This method waits for all scraping tasks to finish, summarizes the data collected, and then proceeds to save the data.
     * It also calculates the total time taken for the scraping process.
     *
     * @param executorService The ExecutorService managing the scraping tasks.
     * @param allWeatherData  A list of collected HourlyWeatherInfo objects.
     * @param completedUrls   A counter for the number of URLs successfully processed.
     * @param startTime       The timestamp marking the start of the scraping process.
     * @param dataFileId      The ID of the data file record in the database.
     * @param directoryPath   The file path of the directory where the CSV file will be saved.
     */
    private static void finalizeScraping(ExecutorService executorService, List<HourlyWeatherInfo> allWeatherData, AtomicInteger completedUrls, long startTime, int dataFileId, String directoryPath) {
        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Total URLs completed: " + completedUrls.get());
        System.out.println("Total data collected: " + allWeatherData.size());

        saveDataAndPrintSummary(allWeatherData, startTime, dataFileId, directoryPath);
    }

    /**
     * Saves the scraped weather data into a CSV file and prints a summary of the operation.
     * This method creates a unique filename for the CSV file based on the current time and the size of the data.
     * It then writes the collected weather data to this file and prints out the total runtime of the scraping process.
     *
     * @param allWeatherData The list of HourlyWeatherInfo objects to be saved.
     * @param startTime      The start time of the scraping process.
     * @param dataFileId     The ID of the data file record in the database.
     * @param directoryPath  The file path of the directory where the CSV file will be saved.
     */
    private static void saveDataAndPrintSummary(List<HourlyWeatherInfo> allWeatherData, long startTime, int dataFileId, String directoryPath) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm");
        String formattedDateTime = now.format(formatter);

        String fileName = formattedDateTime + "_" + allWeatherData.size() + ".csv";
        String absolutePath = directoryPath + File.separator + formattedDateTime + "_" + allWeatherData.size() + ".csv";
        saveToCSV(allWeatherData, absolutePath);
        System.out.println("Success save to " + fileName);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        System.out.println("Total runtime: " + duration + " ms");

        insertToControlSuccessProcess(fileName, absolutePath, dataFileId, allWeatherData.size(), now);
    }

    /**
     * Retrieves the public IP address of the current machine.
     * This method accesses an external service to obtain the public IP address of the machine running this application.
     * It is used for logging purposes in the scraping process.
     *
     * @return The public IP address as a String, or an error message if the address cannot be retrieved.
     */
    public static String getIPAddress() {
        try {
            URL whatIsMyIP = new URL("https://checkip.amazonaws.com");
            BufferedReader in = new BufferedReader(new InputStreamReader(whatIsMyIP.openStream()));
            String ipAddress = in.readLine().trim();
            in.close();
            return ipAddress;
        } catch (IOException e) {
            e.printStackTrace();
            return "Không thể lấy địa chỉ IP";
        }
    }

    /**
     * Counts and categorizes the different URLs for provinces and districts within a specified region.
     * This method is useful for assessing the total number of unique URLs that will be scraped for weather data.
     * It provides a breakdown of the number of provinces, districts, and air quality URLs.
     *
     * @param regionName The name of the region for which to count URLs.
     * @return The total number of unique URLs for the specified region.
     */
    private static int countUrl(String regionName) {

        Set<String> allUrls = new HashSet<>();
        int provinceCount = 0;
        int districtCount = 0;
        int urlForAirQualityCount = 0;

        List<ProvinceInfo> provinces = getAllProvinces(regionName);
        for (ProvinceInfo province : provinces) {
            provinceCount++;
            urlForAirQualityCount++;
            allUrls.add(province.getUrl());

            List<DistrictInfo> districts = getDistrictsOfProvince(province.getUrl());
            for (DistrictInfo district : districts) {
                districtCount++;
                urlForAirQualityCount++;
                allUrls.add(district.getUrl());
            }
        }

        System.out.println("Tổng số tỉnh: " + provinceCount);
        System.out.println("Tổng số URL chất lượng không khí: " + urlForAirQualityCount);
        System.out.println("Tổng số quận/huyện: " + districtCount);
        int totalUrl = allUrls.size() + urlForAirQualityCount;
        System.out.println("Tổng số URL: " + totalUrl);
        return totalUrl;
    }


    /**
     * The main method to start the scraping process.
     * This method sets up the necessary configurations and initiates the scraping and saving process.
     *
     * @param args Command-line arguments (not used in this application).
     */
    public static void main(String[] args) {
        Logger.getLogger("org.openqa.selenium").setLevel(Level.SEVERE);

        if (!setUTF8Output()) {
            return;
        }

        scrapeAndSaveToCsv(DongNamBo, "D:\\dataWeatherCsv");
//        countUrl(DongNamBo);
//        System.out.println(getAllProvinces("Đông Nam Bộ"));
//        System.out.println(getDistrictsOfProvince("https://thoitiet.vn/ho-chi-minh"));

    }
}