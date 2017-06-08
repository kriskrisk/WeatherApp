package project.controller;

import static project.event.EventStream.binding;
import static project.event.EventStream.eventStream;
import static project.event.EventStream.joinStream;
import static project.event.EventStream.onEvent;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.text.MessageFormat;
import java.time.format.DateTimeFormatter;
import java.util.Currency;
import java.util.concurrent.TimeUnit;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import project.control.WeatherTooltip;
import pl.edu.mimuw.xchange.event.CurrencyRateEvent;
import project.WeatherApp;
import project.control.ValueControl;
import project.event.ErrorEvent;
import pl.edu.mimuw.xchange.event.GoodType;
import project.event.NetworkRequestFinishedEvent;
import project.event.NetworkRequestIssuedEvent;
import project.event.RawWeatherEvent;
import project.event.RefreshRequestEvent;
import project.event.SettingsRequestEvent;
import pl.edu.mimuw.xchange.event.TradeableGoodRateEvent;
import project.event.WeatherEvent;
import rx.Observable;
import rx.functions.Func1;
import rx.observables.JavaFxObservable;
import rx.schedulers.JavaFxScheduler;

public class WeatherAppController {

    private static final int ERROR_MSG_MAX_LENGTH = 400;
    private static final int ERROR_MSG_DURATION = 30; // Show error icon for 30 seconds

    @FXML
    private ValueControl temperatureControl;

    @FXML
    private ValueControl euroBidControl;

    @FXML
    private ValueControl usdAskControl;

    @FXML
    private ValueControl usdBidControl;

    @FXML
    private ValueControl bitcoinControl;

    @FXML
    private ValueControl goldControl;

    @FXML
    private Node errorIcon;

    @FXML
    private Node workingIcon;

    @FXML
    private Button refreshButton;

    @FXML
    private Button settingsButton;

    @FXML
    private void initialize() {
        initializeStatus();

        initalizeRefreshHandler();
        initializeSettingsHandler();

        initializeTooltips();

    }

    public Observable<RawWeatherEvent> getEuroAskPrice() {
        return getCurrencyStream("EUR", CurrencyRateEvent::getAsk);
    }

    public Observable<RawWeatherEvent> getEuroBidPrice() {
        return getCurrencyStream("EUR", CurrencyRateEvent::getBid);
    }

    public Observable<RawWeatherEvent> getUSDAskPrice() {
        return getCurrencyStream("USD", CurrencyRateEvent::getAsk);
    }

    public Observable<RawWeatherEvent> getUSDBidPrice() {
        return getCurrencyStream("USD", CurrencyRateEvent::getBid);
    }

    public Observable<RawWeatherEvent> getGoldPrice() {
        return getGoodsStream(GoodType.GOLD);
    }

    public Observable<RawWeatherEvent> getBitcoinPrice() {
        return getGoodsStream(GoodType.BITCOIN);
    }

    private void initalizeRefreshHandler() {
        joinStream(JavaFxObservable.actionEventsOf(refreshButton).map(e -> new RefreshRequestEvent()));
    }

    private void initializeSettingsHandler() {
        joinStream(JavaFxObservable.actionEventsOf(settingsButton).map(e -> new SettingsRequestEvent()));
    }

    private void initializeStatus() {
        Observable<WeatherEvent> events = eventStream().eventsInFx();

        // Basically, we keep track of the difference between issued requests
        // and completed requests
        // If this difference is > 0 we display the spinning icon...
        workingIcon.visibleProperty()
                .bind(binding(events.ofType(NetworkRequestIssuedEvent.class).map(e -> 1) // Every
                        // issued
                        // request
                        // contributes
                        // +1
                        .mergeWith(events.ofType(NetworkRequestFinishedEvent.class).map(e -> -1) // Every
                                // completed
                                // request
                                // contributes
                                // -1
                                .delay(2, TimeUnit.SECONDS, JavaFxScheduler.getInstance())) // We delay
                        // completion
                        // events for 2
                        // seconds so
                        // that the
                        // spinning icon
                        // is always
                        // displayed for
                        // at least 2
                        // seconds and
                        // it does not
                        // blink
                        .scan(0, (x, y) -> x + y).map(v -> v > 0))

                );

		/*
		 * This should show the error icon when an error event arrives and hides
		 * the icon after 30 seconds unless another error arrives
		 */
        Observable<ErrorEvent> errors = events.ofType(ErrorEvent.class);
        errorIcon.visibleProperty()
                .bind(onEvent(errors, true).andOn(
                        errors.throttleWithTimeout(ERROR_MSG_DURATION, TimeUnit.SECONDS, JavaFxScheduler.getInstance()),
                        false).toBinding());
    }

    private void initializeTooltips() {
        Tooltip.install(workingIcon, new Tooltip("Fetching data..."));

        Tooltip errorTooltip = new Tooltip();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        eventStream().eventsInFx().ofType(ErrorEvent.class).subscribe(e -> {
            ByteArrayOutputStream ostream = new ByteArrayOutputStream();
            e.getCause().printStackTrace(new PrintStream(ostream));
            String details = new String(ostream.toByteArray());
            if (details.length() > ERROR_MSG_MAX_LENGTH) {
                details = details.substring(0, ERROR_MSG_MAX_LENGTH) + "\u2026"; // Add
                // ellipsis
                // (...)
                // at
                // the
                // end
            }

            errorTooltip.setText(MessageFormat.format("An error has occurred ({0}):\n{1}",
                    e.getTimestamp().format(formatter), details));
        });
        Tooltip.install(errorIcon, errorTooltip);

        ValueControl[] weatherControls = { temperatureControl, euroBidControl, usdAskControl, usdBidControl,
                bitcoinControl, goldControl };
        for (ValueControl control : weatherControls) {
            Tooltip tooltipPopup = new Tooltip();
            WeatherTooltip tooltipContent = new WeatherTooltip(control.getSource(), control.getTitle());

            tooltipPopup.setGraphic(tooltipContent);

            Tooltip.install(control, tooltipPopup);
        }

    }

    private Observable<RawWeatherEvent> getCurrencyStream(String currencySymbol,
                                                       Func1<CurrencyRateEvent, Float> extractor) {
        return eventStream().eventsInFx().ofType(CurrencyRateEvent.class)
                .filter(e -> e.getCurrency().equals(Currency.getInstance(currencySymbol)))
                .map(e -> new RawWeatherEvent(e.getTimestamp(), extractor.call(e)));
    }

    private Observable<RawWeatherEvent> getGoodsStream(GoodType type) {
        return eventStream().eventsInFx().ofType(TradeableGoodRateEvent.class).filter(e -> e.getGood() == type)
                .map(e -> new RawWeatherEvent(e.getTimestamp(), e.getRate()));
    }

}
