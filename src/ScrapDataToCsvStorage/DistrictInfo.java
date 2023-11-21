package ScrapDataToCsvStorage;

public class DistrictInfo {
	private String name;
	private String url;

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
