package project.event;

public class DustBasicEvent extends Event {
    private double PM10Level;
    private double PM25Level;

    public DustBasicEvent(double PM10Level, double PM25Level) {
        this.PM10Level = PM10Level;
        this.PM25Level = PM25Level;
    }

    public double getPM10Level() {
        return PM10Level;
    }

    public double getPM25Level() {
        return PM25Level;
    }

    public java.lang.String toString() {
        return "DustBasicEvent(PM10 level=" + this.getPM10Level() + ", PM2.5 level=" + this.getPM25Level() + ")";
    }
}
