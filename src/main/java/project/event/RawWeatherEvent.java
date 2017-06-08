package project.event;

import java.time.LocalDateTime;

public final class RawWeatherEvent extends WeatherEvent {
    private final LocalDateTime timestamp;
    private final double value;

    public RawWeatherEvent(final LocalDateTime timestamp, final double value) {
        this.timestamp = timestamp;
        this.value = value;
    }

    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }

    public double getValue() {
        return this.value;
    }

    @java.lang.Override
    public java.lang.String toString() {
        return "RawWeatherEvent(timestamp=" + this.getTimestamp() + ", value=" + this.getValue() + ")";
    }

}
