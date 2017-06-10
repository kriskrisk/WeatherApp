package project.event;

import project.network.DataSource;

public final class ChangeSourceRequestEvent extends WeatherEvent {
    private DataSource currentSource;

    public ChangeSourceRequestEvent(DataSource currentSource) {
        this.currentSource = currentSource;
    }

    @java.lang.Override
    public java.lang.String toString() {
        return "ChangeSourceRequestEvent()";
    }

}
