package ScrapDataToCsvStorage;

/**
 * Represents information about a province, including its name and URL for
 * weather data.
 */
public class ProvinceInfo {
	private String name;
	private String url;

	/**
	 * Constructs a new ProvinceInfo object.
	 *
	 * @param name The name of the province.
	 * @param url  The URL to fetch weather data for the province.
	 */
	public ProvinceInfo(String name, String url) {
		this.name = name;
		this.url = url;
	}

	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}

	public String getUrlHour() {
		return url + "/theo-gio";
	}

	@Override
	public String toString() {
		return name + " - " + url;
	}
}
