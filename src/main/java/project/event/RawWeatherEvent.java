package project.event;

import java.time.LocalDateTime;

public final class RawWeatherEvent <T> extends WeatherEvent {
    private final LocalDateTime timestamp;
    private final T value;

    public RawWeatherEvent(final LocalDateTime timestamp, final T value) {
        this.timestamp = timestamp;
        this.value = value;
    }

    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }

    public T getValue() {
        return this.value;
    }

    @java.lang.Override
    public java.lang.String toString() {
        return "RawWeatherEvent(timestamp=" + this.getTimestamp() + ", value=" + this.getValue() + ")";
    }

}
