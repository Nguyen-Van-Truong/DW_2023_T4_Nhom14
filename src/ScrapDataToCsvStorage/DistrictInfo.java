package ScrapDataToCsvStorage;
/**
 * Represents information about a district, including its name and URL for weather data.
 */
public class DistrictInfo {
    private String name;
    private String url;

    /**
     * Constructs a new DistrictInfo object.
     *
     * @param name The name of the district.
     * @param url  The URL to fetch weather data for the district.
     */
    public DistrictInfo(String name, String url) {
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
