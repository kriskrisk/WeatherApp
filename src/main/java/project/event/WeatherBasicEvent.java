package project.event;

public class WeatherBasicEvent extends Event {
    private float temperature;
    private int pressure;
    private int clouds;
    private float windSpeed;
    private float windDirection;
    private int humidity;

    public WeatherBasicEvent(float temperature, int pressure, int clouds, float windSpeed, float windDirection, int humidity) {
        this.temperature = temperature;
        this.pressure = pressure;
        this.clouds = clouds;
        this.windSpeed = windSpeed;
        this.windDirection = windDirection;
        this.humidity = humidity;
    }

    public float getTemperature() {
        return temperature;
    }

    public int getPressure() {
        return pressure;
    }

    public float getClouds() {
        return clouds;
    }

    public float getWindSpeed() {
        return windSpeed;
    }

    public float getWindDirection() {
        return windDirection;
    }

    public int getHumidity() {
        return humidity;
    }

    public java.lang.String toString() {
        return "WeatherBasicEvent(temperaure=" + this.getTemperature() + ", pressure=" + this.getPressure() +
                ", clouds=" + this.getClouds() + ", wind speed=" + this.getWindSpeed() +
                ", wind direction=" + this.getWindDirection() + ", humidity=" + this.getHumidity() + ")";
    }
}
