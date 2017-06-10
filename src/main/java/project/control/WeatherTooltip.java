package project.control;

import javafx.scene.Node;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;
import org.ocpsoft.prettytime.PrettyTime;
import org.ocpsoft.prettytime.impl.ResourcesTimeFormat;
import org.ocpsoft.prettytime.units.JustNow;
import rx.Observable;
import rx.schedulers.JavaFxScheduler;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import project.event.RawWeatherEvent;

public class WeatherTooltip extends StackPane {

    private Observable<RawWeatherEvent> source;

    private RawWeatherEvent lastEvent;
    private Date lastDate;

    private double yMin;
    private double yMax;

    private VBox chartBox;
    private VBox container;

    public WeatherTooltip(Observable<RawWeatherEvent> source, String title) {
        this.source = source;

        getStyleClass().add("tooltip");

        FontIcon clockIcon = new FontIcon(FontAwesome.CLOCK_O);
        Text timestampText = createTimestampText(source);

        HBox timestampBox = new HBox();
        timestampBox.getChildren().addAll(clockIcon, timestampText);

        FontIcon chartIcon = new FontIcon(FontAwesome.LINE_CHART);

        chartBox = new VBox();
        chartBox.getChildren().addAll(chartIcon);

        container = new VBox();
        container.getChildren().addAll(timestampBox);

        getChildren().add(container);

    }

    private Text createTimestampText(Observable<RawWeatherEvent> source) {
        Text timestampText = new Text("No data");

        PrettyTime pt = new PrettyTime();
        JustNow unit = pt.getUnit(JustNow.class);
        pt.removeUnit(JustNow.class);
        unit.setMaxQuantity(5 * 1000L);
        pt.registerUnit(unit, new ResourcesTimeFormat(unit));

        source.subscribe(e -> {
            lastEvent = e;
            lastDate = Date.from(lastEvent.getTimestamp().atZone(ZoneId.systemDefault()).toInstant());
        });

        Observable.interval(5, TimeUnit.SECONDS, JavaFxScheduler.getInstance()).subscribe(ignore -> {
            if (lastEvent != null) {
                timestampText.setText("Last update: " + pt.format(lastDate));
            }
        });
        return timestampText;
    }

}


