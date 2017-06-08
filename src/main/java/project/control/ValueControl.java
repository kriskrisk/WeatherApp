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

    private FontIcon upIcon;
    private FontIcon downIcon;
    private FontIcon currentIcon;

    private FontIcon noDataIcon = new FontIcon(WeatherIcons.NA);

    private HBox innerContainer;

    private Text prefixLabel;
    private Text suffixLabel;
    private Text textControl;

    private ObjectProperty<Observable<RawWeatherEvent>> sourceProperty = new SimpleObjectProperty<>();

    private String formatPattern = "0.000";
    private DecimalFormat format = new DecimalFormat(formatPattern);

    private StringProperty prefixProperty = new SimpleStringProperty();
    private StringProperty suffixProperty = new SimpleStringProperty("PLN");
    private StringProperty titleProperty = new SimpleStringProperty("-");

    public Observable<RawWeatherEvent> getSource() {
        return sourceProperty.get();
    }

    public void setSource(Observable<RawWeatherEvent> source) {
        source.subscribe(e -> {
            if (innerContainer == null) {
                createContentControls();
            }

            textControl.setText(format.format(e.getValue()));
        });

        source.distinctUntilChanged().buffer(2, 1).map(buffer -> {
            if (buffer.size() < 2) {
                return null;
            }

            float current = buffer.get(1).getValue();
            float prev = buffer.get(0).getValue();

            if (prev < current) {
                return upIcon;
            } else if (prev > current) {
                return downIcon;
            }

            return currentIcon;
        }).subscribe(icon -> {
            if (currentIcon == icon) {
                return;
            }
            if (currentIcon != null) {
                removeTrendIcon();
            }
            currentIcon = icon;
            if (currentIcon != null) {
                addTrendIcon(currentIcon);
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

    public void setSuffix(String sufix) {
        suffixProperty.set(sufix);
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
        suffixLabel.getStyleClass().add("helper-label");

        innerContainer = new HBox();
        innerContainer.getStyleClass().add("value-container");
        innerContainer.getChildren().addAll(prefixLabel, textControl, suffixLabel);

        getChildren().add(innerContainer);

        upIcon = new FontIcon(FontAwesome.CHEVRON_UP);
        upIcon.getStyleClass().add("chevron-up");
        downIcon = new FontIcon(FontAwesome.CHEVRON_DOWN);
        downIcon.getStyleClass().add("chevron-down");
    }

    private void removeTrendIcon() {
        getChildren().remove(currentIcon);
    }

    private void addTrendIcon(Node icon) {
        getChildren().add(icon);
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

        if (currentIcon != null) {
            currentIcon.relocate(
                    suffixLabel.getBoundsInParent().getMinX() + innerContainer.getBoundsInParent().getMinX(),
                    suffixLabel.getBoundsInParent().getMinY() + innerContainer.getBoundsInParent().getMinY()
                            - currentIcon.getLayoutBounds().getHeight() + 2);
        }

    }

}

