package project.event;

public class WeatherBasicEvent extends Event {
    private double temperature;
    private double pressure;
    private Double clouds;
    private double windSpeed;
    private double windDirection;
    private double humidity;

    public enum Source {
        OPEN_WEATHER_MAP, METEO
    }

    private final Source source;

    public WeatherBasicEvent(double temperature, double pressure, Double clouds, double windSpeed, double windDirection, double humidity, Source source) {
        this.temperature = temperature;
        this.pressure = pressure;
        this.clouds = clouds;
        this.windSpeed = windSpeed;
        this.windDirection = windDirection;
        this.humidity = humidity;
        this.source = source;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getPressure() {
        return pressure;
    }

    public Double getClouds() {
        return clouds;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public double getWindDirection() {
        return windDirection;
    }

    public double getHumidity() {
        return humidity;
    }

    public Source getSource() {
        return source;
    }

    public java.lang.String toString() {
        return "WeatherBasicEvent(temperaure=" + this.getTemperature() + ", pressure=" + this.getPressure() +
                ", clouds=" + this.getClouds() + ", wind speed=" + this.getWindSpeed() +
                ", wind direction=" + this.getWindDirection() + ", humidity=" + this.getHumidity() + ")";
    }
}
