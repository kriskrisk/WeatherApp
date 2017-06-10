package project.event;

import java.time.LocalDateTime;

public final class RawWeatherEvent extends WeatherEvent {
    private final LocalDateTime timestamp;
    private final Double value;

    public RawWeatherEvent(final LocalDateTime timestamp, final Double value) {
        this.timestamp = timestamp;
        this.value = value;
    }

    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }

    public Double getValue() {
        return this.value;
    }

    @java.lang.Override
    public java.lang.String toString() {
        return "RawWeatherEvent(timestamp=" + this.getTimestamp() + ", value=" + this.getValue() + ")";
    }

}
