package project.event;

public class WeatherBasicEvent extends Event {
    private double temperature;
    private double pressure;
    private int clouds;
    private double windSpeed;
    private int windDirection;
    private double humidity;

    public WeatherBasicEvent(double temperature, double pressure, int clouds, double windSpeed, int windDirection, double humidity) {
        this.temperature = temperature;
        this.pressure = pressure;
        this.clouds = clouds;
        this.windSpeed = windSpeed;
        this.windDirection = windDirection;
        this.humidity = humidity;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getPressure() {
        return pressure;
    }

    public int getClouds() {
        return clouds;
    }

    public double getWindSpeed() {
        return windSpeed;
    }

    public int getWindDirection() {
        return windDirection;
    }

    public double getHumidity() {
        return humidity;
    }

    public java.lang.String toString() {
        return "WeatherBasicEvent(temperaure=" + this.getTemperature() + ", pressure=" + this.getPressure() +
                ", clouds=" + this.getClouds() + ", wind speed=" + this.getWindSpeed() +
                ", wind direction=" + this.getWindDirection() + ", humidity=" + this.getHumidity() + ")";
    }
}
