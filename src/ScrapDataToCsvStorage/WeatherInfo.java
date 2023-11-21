package ScrapDataToCsvStorage;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

public class WeatherInfo {
    String day;
    String temperature;
    String description;
    String humidity;
    String windSpeed;
    
	public WeatherInfo(String day, String temperature, String description, String humidity, String windSpeed) {
		super();
		this.day = day;
		this.temperature = temperature;
		this.description = description;
		this.humidity = humidity;
		this.windSpeed = windSpeed;
	}
	
	public WeatherInfo() {
		super();
	}

	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public String getTemperature() {
		return temperature;
	}
	public void setTemperature(String temperature) {
		this.temperature = temperature;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getHumidity() {
		return humidity;
	}
	public void setHumidity(String humidity) {
		this.humidity = humidity;
	}
	public String getWindSpeed() {
		return windSpeed;
	}
	public void setWindSpeed(String windSpeed) {
		this.windSpeed = windSpeed;
	}
	@Override
	public String toString() {

        return String.format(
                "Ngày: %s\n" +
                "Nhiệt độ: %s\n" +
                "Mô tả thời tiết: %s\n" +
                "Độ ẩm: %s\n" +
                "Tốc độ gió: %s\n" +
                "-----------------------------",
                day, temperature, description, humidity, windSpeed
            );
	}

    
}