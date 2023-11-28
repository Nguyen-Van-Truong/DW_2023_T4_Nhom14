package ScrapDataToCsvStorage;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileWriter;
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
import java.time.format.DateTimeParseException;
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
 */
public class WeatherScrapingHourlyToStorage {
	/**
	 * Sets the System output to UTF-8 encoding.
	 * 
	 * @return true if successful, false otherwise
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
	 * @param driver WebDriver instance to use for scraping
	 * @return List of ProvinceInfo objects containing province names and URLs
	 */
	public static List<ProvinceInfo> getAllProvinces(WebDriver driver) {
		List<ProvinceInfo> provinces = new ArrayList<>();
		getWithRetry(driver, "https://thoitiet.vn", 3);

		List<WebElement> provinceElements = driver.findElements(By.cssSelector(".dropdown-menu .mega-submenu a"));

		for (WebElement provinceElement : provinceElements) {
			String provinceName = provinceElement.getAttribute("title");
			String provinceUrl = provinceElement.getAttribute("href");
			provinces.add(new ProvinceInfo(provinceName, provinceUrl));
		}

		return provinces;
	}

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
	 * @param driver      WebDriver instance to use for scraping
	 * @param provinceUrl URL of the province page
	 * @return List of DistrictInfo objects containing district names and URLs
	 */
	public static List<DistrictInfo> getDistrictsOfProvince(WebDriver driver, String provinceUrl) {
		List<DistrictInfo> districts = new ArrayList<>();
		getWithRetry(driver, provinceUrl, 3);

		List<WebElement> districtElements = driver.findElements(By.cssSelector(".khu-vuc-lan-can a"));

		for (WebElement districtElement : districtElements) {
			String districtName = districtElement.getText();
			String districtUrl = districtElement.getAttribute("href");
			districts.add(new DistrictInfo(districtName, districtUrl));
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
	 * @param driver     WebDriver instance to use for scraping
	 * @param url        URL to scrape for air quality data
	 * @param maxRetries Maximum number of retries for scraping
	 * @return Air quality as a string
	 */
	private static String getAirQuality(WebDriver driver, String url, int maxRetries) {
		getWithRetry(driver, url, maxRetries);
		try {
			WebElement airQualityElement = driver.findElement(By.cssSelector(".air-rules .air-active"));
			return airQualityElement.getText().trim();
		} catch (NoSuchElementException e) {
			System.err.println("Air quality information not found.");
			return "Không rõ";
		}
	}

	/**
	 * Creates and configures ChromeOptions for WebDriver. ChromeOptions are used to
	 * set various properties and arguments for the ChromeDriver.
	 *
	 * @return Configured ChromeOptions object.
	 */
	private static ChromeOptions createChromeOption() {
		ChromeOptions chromeOptions = new ChromeOptions();
		chromeOptions.addArguments("--headless");
		chromeOptions.addArguments("--no-sandbox");
		chromeOptions.addArguments("--disable-dev-shm-usage");
		return chromeOptions;
	}

	/**
	 * Scrapes hourly weather data for the next 3 days.
	 * 
	 * @param driver     WebDriver instance to use for scraping
	 * @param url        URL to scrape for weather data
	 * @param province   Name of the province
	 * @param district   Name of the district
	 * @param airQuality Air quality information
	 * @return List of HourlyWeatherInfo objects containing weather data
	 */
	public static List<HourlyWeatherInfo> scrapeHourlyWeatherData3Days(WebDriver driver, String url, String province,
			String district, String airQuality) {
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
	 * 
	 * @param weatherData List of HourlyWeatherInfo objects to save
	 * @param filePath    Path to the CSV file
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
	 * @param driver     WebDriver instance to use for scraping
	 * @param url        URL to scrape for weather data
	 * @param maxRetries Maximum number of retries for scraping
	 * @param province   Name of the province
	 * @param district   Name of the district
	 * @param airQuality Air quality information
	 * @return List of HourlyWeatherInfo objects containing weather data
	 */
	private static List<HourlyWeatherInfo> scrapeWithRetry(WebDriver driver, String url, int maxRetries,
			String province, String district, String airQuality) {
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
		return new ArrayList<>();
	}

	/**
	 * Initiates the scraping process and saves data to CSV.
	 */
	public static void scrapeAndSaveToCsv() {
		long startTime = System.currentTimeMillis();
		List<HourlyWeatherInfo> allWeatherData = new ArrayList<>();
		ExecutorService executorService = Executors.newFixedThreadPool(4);
		AtomicInteger completedUrls = new AtomicInteger(1);

		WebDriver driverForGetProvinces = new ChromeDriver(createChromeOption());
		List<ProvinceInfo> provinces = getAllProvinces(driverForGetProvinces);

		for (int i = 0; i < provinces.size(); i++) {
			ProvinceInfo province = provinces.get(i);
			executorService.submit(() -> {
				WebDriver driverForProvince = new ChromeDriver(createChromeOption());
				WebDriver driverForAirQuality = new ChromeDriver(createChromeOption());
				try {
					// get air quality data from another url
					String airQuality = getAirQuality(driverForAirQuality, province.getUrl(), 3);

					List<HourlyWeatherInfo> provinceWeatherData = scrapeWithRetry(driverForProvince,
							province.getUrlHour(), 3, province.getName(), "", airQuality);
					synchronized (allWeatherData) {
						allWeatherData.addAll(provinceWeatherData);
					}
				} finally {
					driverForProvince.quit();
					completedUrls.incrementAndGet();
					System.out.println("Finished URL: " + completedUrls.get());
					completedUrls.incrementAndGet();
					System.out.println("Finished URL: " + completedUrls.get());
				}
			});

			WebDriver driverForGetDistricts = new ChromeDriver(createChromeOption());
			List<DistrictInfo> districts = getDistrictsOfProvince(driverForGetDistricts, province.getUrl());
			for (int j = 0; j < districts.size(); j++) {
				DistrictInfo district = districts.get(j);
				executorService.submit(() -> {
					WebDriver driverForDistrict = new ChromeDriver(createChromeOption());
					WebDriver driverForAirQuality = new ChromeDriver(createChromeOption());
					try {
						String airQuality = getAirQuality(driverForAirQuality, district.getUrl(), 3);

						List<HourlyWeatherInfo> districtWeatherData = scrapeWithRetry(driverForDistrict,
								district.getUrlHour(), 3, province.getName(), district.getName(), airQuality);
						synchronized (allWeatherData) {
							allWeatherData.addAll(districtWeatherData);
						}
					} finally {
						driverForDistrict.quit();
						completedUrls.incrementAndGet();
						System.out.println("Finished URL: " + completedUrls.get());
						completedUrls.incrementAndGet();
						System.out.println("Finished URL: " + completedUrls.get());
					}
				});
			}
		}

		executorService.shutdown();
		try {
			executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Total URLs completed: " + completedUrls.get());
		System.out.println("Total data collected: " + allWeatherData.size());

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
	 * Counts and prints the number of different URLs for provinces and districts.
	 * 
	 * @param driver WebDriver instance to use for scraping
	 */
	private static void countUrl(WebDriver driver) {
		Set<String> allUrls = new HashSet<>();
		int provinceCount = 0;
		int districtCount = 0;
		int urlForAirQualityCount = 0;
		List<ProvinceInfo> provinces = getAllProvinces(driver);
		for (ProvinceInfo province : provinces) {
			provinceCount++;
			urlForAirQualityCount++;
			allUrls.add(province.getUrl());

			List<DistrictInfo> districts = getDistrictsOfProvince(driver, province.getUrl());
			for (DistrictInfo district : districts) {
				districtCount++;
				urlForAirQualityCount++;
				allUrls.add(district.getUrl());
			}
		}

		System.out.println("Tổng số tỉnh: " + provinceCount);
		System.out.println("Tổng số URL chất lượng không khí: " + urlForAirQualityCount);
		System.out.println("Tổng số quận/huyện: " + districtCount);
		System.out.println("Tổng số URL: " + (int) (allUrls.size() + urlForAirQualityCount));
	}

	/**
	 * The main method to start the scraping process.
	 * 
	 * @param args Command-line arguments (not used)
	 */
	public static void main(String[] args) {
		if (!setUTF8Output()) {
			return;
		}

		scrapeAndSaveToCsv();
	}
}