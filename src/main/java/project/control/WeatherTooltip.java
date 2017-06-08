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
        chartBox.getChildren().addAll(chartIcon, createChart(title));

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

    private Node createChart(String title) {
        NumberAxis xAxis = new NumberAxis();
        NumberAxis yAxis = new NumberAxis();

        xAxis.setTickLabelFormatter(new StringConverter<Number>() {

            @Override
            public String toString(Number object) {
                LocalDateTime dt = LocalDateTime.ofInstant(Instant.ofEpochMilli(object.longValue()),
                        ZoneId.systemDefault());
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                return dt.format(formatter);
            }

            @Override
            public Number fromString(String string) {
                return 0d;
            }
        });

        xAxis.setAutoRanging(false);
        yAxis.setAutoRanging(false);

        AreaChart<Number, Number> chart = new AreaChart<>(xAxis, yAxis);

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(title);
        chart.getData().add(series);

        addDataToSeries(series, xAxis, yAxis);

        return chart;
    }

    private void addDataToSeries(XYChart.Series<Number, Number> series, NumberAxis xAxis, NumberAxis yAxis) {
        source.distinctUntilChanged(e -> (int) (e.getValue() * 1000)).subscribe(e -> {
            //removeAgingData(series);

            series.getData().add(new XYChart.Data<Number, Number>(
                    toMilis(e.getTimestamp()), e.getValue()));

            double min = series.getData().stream().mapToDouble(data -> data.getYValue().doubleValue()).min().orElse(0)
                    * 0.9d;
            double max = series.getData().stream().mapToDouble(data -> data.getYValue().doubleValue()).max()
                    .orElse(10000d) * 1.1d;

            yMin = min;
            yAxis.setLowerBound(yMin);

            yMax = max;
            yAxis.setUpperBound(yMax);

            yAxis.setTickUnit((yMax - yMin) / 5);

            min = series.getData().get(0).getXValue().doubleValue();
            max = toMilis(LocalDateTime.now()).doubleValue() + 1000;

            xAxis.setLowerBound(min);
            xAxis.setUpperBound(max);
            xAxis.setTickUnit((max - min) / 2);

            if (series.getData().size() > 1 && !container.getChildren().contains(chartBox)) {
                container.getChildren().add(chartBox);
            }
        });
    }

    private Number toMilis(LocalDateTime timestamp) {
        return timestamp.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

	/*
	private void removeAgingData(Series<Number, Number> series) {

	}
	*/

}


