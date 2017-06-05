package project.event;

import java.time.LocalDateTime;

public class Event extends WeatherEvent {
    private final LocalDateTime timestamp;

    public Event() {
        timestamp = LocalDateTime.now();
    }

    public LocalDateTime getTimestamp() {
        return this.timestamp;
    }

    @java.lang.Override
    public java.lang.String toString() {
        return "Event(timestamp=" + this.getTimestamp() + ")";
    }

}
