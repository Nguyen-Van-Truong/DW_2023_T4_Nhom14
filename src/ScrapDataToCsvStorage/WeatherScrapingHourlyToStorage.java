package ScrapDataToCsvStorage;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
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

    /** The default region name used when no specific region is specified. */
    public static final String All = "";
    /** The name of the Đông Nam Bộ region. */
    public static final String DongNamBo = "Đông Nam Bộ";

    /** The total number of URLs for the Đông Nam Bộ region. */
    public static final int TOTAL_URL_DONG_NAM_BO = 156;

    /** The total number of URLs to be processed for all regions. */
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
            driver.quit(); // Close the WebDriver
        }

        return provinces;
    }


    /**
     * Attempts to load a page with a given URL, retrying up to a specified number of times.
     * This method is used to handle intermittent network or server issues by retrying the page load.
     *
     * @param driver    The WebDriver instance used to load the page.
     * @param url       The URL to be loaded.
     * @param maxRetries The maximum number of retry attempts.
     * @return true if the page is successfully loaded, false otherwise.
     */
    private static boolean getWithRetry(WebDriver driver, String url, int maxRetries) {
        for (int attempt = 0; attempt < maxRetries; attempt++) {
            try {
                driver.get(url);
                return true; // Successfully loaded the page
            } catch (Exception e) {
                System.err.println("Error accessing URL: " + url + ". Retry attempt " + (attempt + 1));
                try {
                    Thread.sleep(10000); // Wait for 5 seconds before retrying
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        return false; // Failed to load the page after retries
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
            driver.quit(); // Ensure the WebDriver is closed
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

            info.setTemperatureMin(
                    safelyGetText(detail, By.cssSelector(".summary-temperature-min"), "Không rõ nhiệt độ thấp nhất"));
            info.setTemperatureMax(safelyGetText(detail, By.cssSelector(".summary-temperature-max-value"),
                    "Không rõ nhiệt độ cao nhất"));
            info.setDescription(safelyGetText(detail, By.cssSelector(".summary-description-detail"), "Không rõ mô tả"));
            info.setHumidity(safelyGetText(detail, By.cssSelector(".summary-humidity > span:last-child"),
                    "Không có dữ liệu Độ ẩm"));
            info.setWindSpeed(safelyGetText(detail, By.cssSelector(".summary-speed > span:last-child"),
                    "Không có dữ liệu Tốc độ gió"));
            info.setUvIndex(safelyGetText(detail, By.cssSelector(".weather-content-item .op-8.fw-bold"),
                    "Không có dữ liệu UV"));
            info.setVisibility(
                    safelyGetText(detail, By.xpath(".//h6[contains(text(), 'Tầm nhìn')]/following-sibling::div/span"),
                            "Không có dữ liệu Tầm nhìn"));
            info.setPressure(
                    safelyGetText(detail, By.xpath(".//h6[contains(text(), 'Áp suất')]/following-sibling::div/h3"),
                            "Không có dữ liệu Áp suất"));
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
     * The method writes the weather data in CSV format to the specified file path.
     *
     * @param weatherData The list of HourlyWeatherInfo objects to be saved.
     * @param filePath    The path of the CSV file where the data will be saved.
     */
    public static void saveToCSV(List<HourlyWeatherInfo> weatherData, String filePath) {
        try (OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(filePath),
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
                        .append(info.getUrl()).append(",").append(ipAddress).append("\n"); // Thêm giá trị IP
            }
        } catch (IOException e) {
            System.err.println("Lỗi khi ghi vào tệp CSV: " + e.getMessage());
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
     * @param regionName The name of the region for which to scrape weather data. It determines the scope of data collection.
     */
    public static void scrapeAndSaveToCsv(String regionName) {
        long startTime = System.currentTimeMillis();
        List<HourlyWeatherInfo> allWeatherData = new ArrayList<>();
        ExecutorService executorService = Executors.newFixedThreadPool(2); // Adjust thread pool size as needed
        AtomicInteger completedUrls = new AtomicInteger(0);

        List<ProvinceInfo> provinces = getAllProvinces(regionName);

        for (ProvinceInfo province : provinces) {
            // Submit separate tasks for each province
            executorService.submit(() -> {
                String airQuality = getAirQuality(province.getUrl(), 3);
                System.out.println("Finished URL: " + completedUrls.incrementAndGet() + "/" + TOTAL_URL_DONG_NAM_BO);

                List<HourlyWeatherInfo> provinceWeatherData = scrapeWithRetry(province.getUrlHour(), 3,
                        province.getName(), "", airQuality);
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

                    List<HourlyWeatherInfo> districtWeatherData = scrapeWithRetry(district.getUrlHour(), 3,
                            province.getName(), district.getName(), airQuality);
                    synchronized (allWeatherData) {
                        allWeatherData.addAll(districtWeatherData);
                    }
                    System.out.println("Finished URL: " + completedUrls.incrementAndGet() + "/" + TOTAL_URL_DONG_NAM_BO);
                });
            }
        }

        // Finalization of the scraping process
        finalizeScraping(executorService, allWeatherData, completedUrls, startTime);
    }


    /**
     * Finalizes the scraping process by shutting down the ExecutorService and summarizing the results.
     * This method waits for all tasks to complete, then prints the total number of URLs processed and the size of the collected data.
     * Finally, it calls the method to save the data and print a summary.
     *
     * @param executorService The ExecutorService used for managing scraping tasks.
     * @param allWeatherData  The list of all collected HourlyWeatherInfo objects.
     * @param completedUrls   A counter of the completed URLs.
     * @param startTime       The start time of the scraping process for calculating the total duration.
     */
    private static void finalizeScraping(ExecutorService executorService, List<HourlyWeatherInfo> allWeatherData,
                                         AtomicInteger completedUrls, long startTime) {
        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Total URLs completed: " + completedUrls.get());
        System.out.println("Total data collected: " + allWeatherData.size());

        saveDataAndPrintSummary(allWeatherData, startTime);
    }

    /**
     * Saves the collected weather data to a CSV file and prints a summary of the operation.
     * The method formats the current time to create a unique filename for the CSV, saves the data,
     * and then prints out a summary including the total runtime of the scraping process.
     *
     * @param allWeatherData The list of HourlyWeatherInfo objects to be saved.
     * @param startTime      The start time of the scraping process for calculating the total duration.
     */
    private static void saveDataAndPrintSummary(List<HourlyWeatherInfo> allWeatherData, long startTime) {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm");
        String formattedDateTime = now.format(formatter);

        String fileName = formattedDateTime + "_" + allWeatherData.size() + ".csv";
        saveToCSV(allWeatherData, fileName);
        System.out.println("Success save to " + fileName);

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        System.out.println("Total runtime: " + duration + " ms");
    }

    /**
     * Retrieves the public IP address of the machine.
     *
     * @return Public IP address as a string
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
     * Counts and prints the number of different URLs for provinces and districts within a specified region.
     * This method is useful for understanding the scope of data collection and ensures that all relevant URLs are accounted for.
     *
     * @param regionName The name of the region to retrieve URLs for.
     * @return The total number of unique URLs found for the given region.
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
        if (!setUTF8Output()) {
            return;
        }

        scrapeAndSaveToCsv(DongNamBo);
//        countUrl(DongNamBo);
//        System.out.println(getAllProvinces("Đông Nam Bộ"));
//        System.out.println(getDistrictsOfProvince("https://thoitiet.vn/ho-chi-minh"));

    }
}