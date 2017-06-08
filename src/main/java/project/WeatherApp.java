package project;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import project.event.SettingsRequestEvent;
import project.event.WeatherEvent;
import project.network.DataSource;
import project.network.MeteoDataSource;
import project.network.OpenWeatherDataSource;
import rx.Observable;
import rx.Subscription;

import javafx.scene.control.Button;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;

import static project.event.EventStream.eventStream;
import static project.event.EventStream.joinStream;

public class WeatherApp extends Application {
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(WeatherApp.class);
    private List<Subscription> sourceStreams = new LinkedList<>();
    private static final String FXML_MAIN_FORM_TEMPLATE = "/fxml/xchange-main.fxml";
    //private Stage mainStage;


    private void setupDataSources() {
        DataSource[] sources = { new OpenWeatherDataSource(),
                new MeteoDataSource() };
        for (DataSource source : sources) {
            sourceStreams.add(joinStream(source.dataSourceStream()));
        }
    }

    private void setupEventHandler() {
        Observable<WeatherEvent> events = eventStream().events();

        events.subscribe(log::info);
        //.ofType(Event.class) - niepotrzebne

        //events.ofType(SettingsRequestEvent.class).subscribe(e -> onSettingsRequested());
    }

    @Override
    public void start(Stage primaryStage) {
        log.info("Starting Weather application...");

        //mainStage = primaryStage;

        setupDataSources();

        setupEventHandler();

    }

    public static void main(String[] args) {
        launch(args);
    }
}
