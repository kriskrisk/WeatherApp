package project.event;

import java.time.LocalDateTime;

public class RateEvent extends WeatherEvent {
    private final LocalDateTime timestamp;

    public RateEvent() {
        timestamp = LocalDateTime.now();
    }

    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }

    @java.lang.Override
    public java.lang.String toString() {
        return "RateEvent(timestamp=" + this.getTimestamp() + ")";
    }

}
