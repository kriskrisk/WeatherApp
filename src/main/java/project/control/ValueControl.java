package project.control;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import org.kordamp.ikonli.fontawesome.FontAwesome;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.weathericons.WeatherIcons;
import project.event.RawWeatherEvent;
import rx.Observable;
import java.text.DecimalFormat;

public class ValueControl extends Pane {
    private FontIcon noDataIcon = new FontIcon(WeatherIcons.NA);

    private HBox innerContainer;

    private Text prefixLabel;
    private Text textControl;
    private Text suffixLabel;

    private ObjectProperty<Observable<RawWeatherEvent>> sourceProperty = new SimpleObjectProperty<>();

    private String formatPattern = "0.0";
    private DecimalFormat format = new DecimalFormat(formatPattern);

    private StringProperty prefixProperty = new SimpleStringProperty();
    private StringProperty titleProperty = new SimpleStringProperty("-");
    private StringProperty suffixProperty = new SimpleStringProperty();

    public Observable<RawWeatherEvent> getSource() {
        return sourceProperty.get();
    }

    public void setSource(Observable<RawWeatherEvent> source) {
        source.subscribe(e -> {
            if (innerContainer == null) {
                createContentControls();
            }

            if (e.getValue() == null) {
                textControl.setText("-");
            } else {
                textControl.setText(format.format(e.getValue()));
            }
        });

        sourceProperty.set(source);
    }

    public String getFormat() {
        return formatPattern;
    }

    public void setFormat(String pattern) {
        formatPattern = pattern;
        format = new DecimalFormat(pattern);
    }

    public String getPrefix() {
        return prefixProperty.get();
    }

    public void setPrefix(String prefix) {
        prefixProperty.set(prefix);
    }

    public String getSuffix() {
        return suffixProperty.get();
    }

    public void setSuffix(String suffix) {
        suffixProperty.set(suffix);
    }

    public String getTitle() {
        return titleProperty.get();
    }

    public void setTitle(String title) {
        titleProperty.set(title);
    }

    public ValueControl() {
        noDataIcon.getStyleClass().add("no-data");
        getChildren().add(noDataIcon);
    }

    private void createContentControls() {
        textControl = new Text();

        getChildren().remove(noDataIcon);

        textControl = new Text();
        textControl.getStyleClass().add("rate-value");

        prefixLabel = new Text();
        prefixLabel.textProperty().bind(prefixProperty);
        prefixLabel.getStyleClass().add("helper-label");

        suffixLabel = new Text();
        suffixLabel.textProperty().bind(suffixProperty);
        suffixLabel.getStyleClass().add("rate-value");

        innerContainer = new HBox();
        innerContainer.getStyleClass().add("value-container");
        innerContainer.getChildren().addAll(prefixLabel, textControl, suffixLabel);

        getChildren().add(innerContainer);
    }

    @Override
    protected void layoutChildren() {
		/* Custom children positioning */
        super.layoutChildren();

        if (noDataIcon.isVisible()) {
            noDataIcon.relocate((getWidth() - noDataIcon.getLayoutBounds().getWidth()) / 2,
                    (getHeight() - noDataIcon.getLayoutBounds().getHeight()) / 2);
        }

        if (innerContainer != null) {
            innerContainer.relocate((getWidth() - innerContainer.getLayoutBounds().getWidth()) / 2,
                    (getHeight() - innerContainer.getLayoutBounds().getHeight()) / 2);
        }
    }

}

