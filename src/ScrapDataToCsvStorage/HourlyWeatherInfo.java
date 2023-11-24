package ScrapDataToCsvStorage;

import java.time.LocalDate;

public class HourlyWeatherInfo {
	private String date;
	private String time;
	private String temperatureMin;
	private String temperatureMax;
	private String description;
	private String humidity;
	private String windSpeed;
	private String uvIndex;
	private String visibility;
	private String pressure;
	private String stopPoint;
	private String province;
	private String district;
	private String url;
	private String airQuality;

	public String getAirQuality() {
		return airQuality;
	}

	public void setAirQuality(String airQuality) {
		this.airQuality = airQuality;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getStopPoint() {
		return stopPoint;
	}

	public void setStopPoint(String stopPoint) {
		this.stopPoint = removeUnits(stopPoint);
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTemperatureMin() {
		return temperatureMin;
	}

	public String getTemperatureMax() {
		return temperatureMax;
	}

	public String getHumidity() {
		return humidity;
	}

	public String getWindSpeed() {
		return windSpeed;
	}

	public String getUvIndex() {
		return uvIndex;
	}

	public String getVisibility() {
		return visibility;
	}

	public String getPressure() {
		return pressure;
	}

	public void setTemperatureMin(String temperatureMin) {
		this.temperatureMin = removeUnits(temperatureMin);
	}

	public void setTemperatureMax(String temperatureMax) {
		this.temperatureMax = removeUnits(temperatureMax);
	}

	public void setHumidity(String humidity) {
		this.humidity = removeUnits(humidity);
	}

	public void setWindSpeed(String windSpeed) {
		this.windSpeed = removeUnits(windSpeed);
	}

	public void setUvIndex(String uvIndex) {
		this.uvIndex = removeUnits(uvIndex);
	}

	public void setVisibility(String visibility) {
		this.visibility = removeUnits(visibility);
	}

	public void setPressure(String pressure) {
		this.pressure = removeUnits(pressure);
	}

	private String removeUnits(String value) {
		if (value == null)
			return null;
		return value.replace("°C", "").replace("%", "").replace(" km/giờ", "").replace(" km", "").replace(" mb", "")
				.trim();
	}

	@Override
	public String toString() {
		return "Province='" + province + '\'' + ", District='" + district + '\'' + ", Date='" + date + '\'' + ", Time='"
				+ time + '\'' + ", Min Temperature='" + temperatureMin + '\'' + ", Max Temperature='" + temperatureMax
				+ '\'' + ", Description='" + description + '\'' + ", Humidity='" + humidity + '\'' + ", Wind Speed='"
				+ windSpeed + '\'' + ", UV Index='" + uvIndex + '\'' + ", Visibility='" + visibility + '\''
				+ ", Pressure='" + pressure + '\'' + ", Air Quality='" + airQuality + '\'' + // Dòng mới
				", StopPoint=" + stopPoint + ", Url=" + url;
	}
}
