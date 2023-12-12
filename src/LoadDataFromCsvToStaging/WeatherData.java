package LoadDataFromCsvToStaging;

public class WeatherData {
    private int id;
    private String date;
    private String time;
    private String province;
    private String wards;
    private String district;
    private String temperature;
    private String feeling;
    private String status;
    private String humidity;
    private String vision;
    private String windSpeed;
    private String stopPoint;
    private String uvIndex;
    private String airQuality;
    private String lastUpdateTime;
    private String breadcrumb;
    private String url;
    private String path;
    private String dtrequest;
    private String request;
    private String method;
    private String protocols;
    private String statusCode;
    private String host;
    private String server;
    private String ip;

    public WeatherData() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getWards() {
        return wards;
    }

    public void setWards(String wards) {
        this.wards = wards;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getFeeling() {
        return feeling;
    }

    public void setFeeling(String feeling) {
        this.feeling = feeling;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getVision() {
        return vision;
    }

    public void setVision(String vision) {
        this.vision = vision;
    }

    public String getWindSpeed() {
        return windSpeed;
    }

    public void setWindSpeed(String windSpeed) {
        this.windSpeed = windSpeed;
    }

    public String getStopPoint() {
        return stopPoint;
    }

    public void setStopPoint(String stopPoint) {
        this.stopPoint = stopPoint;
    }

    public String getUvIndex() {
        return uvIndex;
    }

    public void setUvIndex(String uvIndex) {
        this.uvIndex = uvIndex;
    }

    public String getAirQuality() {
        return airQuality;
    }

    public void setAirQuality(String airQuality) {
        this.airQuality = airQuality;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getBreadcrumb() {
        return breadcrumb;
    }

    public void setBreadcrumb(String breadcrumb) {
        this.breadcrumb = breadcrumb;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDtrequest() {
        return dtrequest;
    }

    public void setDtrequest(String dtrequest) {
        this.dtrequest = dtrequest;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public String getProtocols() {
        return protocols;
    }

    public void setProtocols(String protocols) {
        this.protocols = protocols;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public String toString() {
        return "WeatherData{" +
                "id=" + id +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", province='" + province + '\'' +
                ", wards='" + wards + '\'' +
                ", district='" + district + '\'' +
                ", temperature='" + temperature + '\'' +
                ", feeling='" + feeling + '\'' +
                ", status='" + status + '\'' +
                ", humidity='" + humidity + '\'' +
                ", vision='" + vision + '\'' +
                ", windSpeed='" + windSpeed + '\'' +
                ", stopPoint='" + stopPoint + '\'' +
                ", uvIndex='" + uvIndex + '\'' +
                ", airQuality='" + airQuality + '\'' +
                ", lastUpdateTime='" + lastUpdateTime + '\'' +
                ", breadcrumb='" + breadcrumb + '\'' +
                ", url='" + url + '\'' +
                ", path='" + path + '\'' +
                ", dtrequest='" + dtrequest + '\'' +
                ", request='" + request + '\'' +
                ", method='" + method + '\'' +
                ", protocols='" + protocols + '\'' +
                ", statusCode='" + statusCode + '\'' +
                ", host='" + host + '\'' +
                ", server='" + server + '\'' +
                ", ip='" + ip + '\'' +
                '}';
    }
}
