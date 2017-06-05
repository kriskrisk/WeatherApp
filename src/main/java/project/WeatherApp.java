package project;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import project.network.DataSource;
import project.network.MeteoDataSource;
import project.network.OpenWeatherDataSource;
import rx.Subscription;

import javafx.scene.control.Button;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;

import static project.event.EventStream.joinStream;

public class WeatherApp extends Application {
    private static final org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger(WeatherApp.class);
    private List<Subscription> sourceStreams = new LinkedList<>();
    private static final String FXML_MAIN_FORM_TEMPLATE = "/fxml/xchange-main.fxml";

    private void setupDataSources() {
        DataSource[] sources = { new OpenWeatherDataSource(),
                new MeteoDataSource() };
        for (DataSource source : sources) {
            sourceStreams.add(joinStream(source.dataSourceStream()));
        }
    }

    @Override
    public void start(Stage primaryStage) {
        log.info("Starting Weather application...");

        setupDataSources();

        Parent pane = FXMLLoader.load(WeatherApp.class.getResource(FXML_MAIN_FORM_TEMPLATE));

        primaryStage.setTitle("Hello World!");
        Button btn = new Button();
        btn.setText("Say 'Hello World'");
        btn.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                System.out.println("Hello World!");
            }
        });

        StackPane root = new StackPane();
        root.getChildren().add(btn);
        primaryStage.setScene(new Scene(root, 300, 250));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
