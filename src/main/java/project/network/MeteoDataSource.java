package project.network;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import io.reactivex.netty.RxNetty;
import project.event.WeatherBasicEvent;
//import project.event.WeatherBasicEvent;
import rx.Observable;
import rx.exceptions.Exceptions;

public class MeteoDataSource extends DataSource {

    private static final String URL = "http://www.meteo.waw.pl/";

    private class BitcoinRateNotFoundException extends Exception {
        private static final long serialVersionUID = 1L;
    };

    private static final Pattern TEMPERATURE_RE = Pattern.compile(
            "<span id=\"PARAM_0_TA\">([0-9,]*)</span>", Pattern.CASE_INSENSITIVE);
    private static final Pattern PRESSURE_RE = Pattern.compile(
            "<span id=\"PARAM_0_PR\">([0-9,]*)</span>", Pattern.CASE_INSENSITIVE);
    private static final Pattern WIND_SPEED_RE = Pattern.compile(
            "<span id=\"PARAM_0_WV\">([0-9,]*)</span>", Pattern.CASE_INSENSITIVE);
    private static final Pattern WIND_DIRECTION_RE = Pattern.compile(
            "<span id=\"PARAM_0_WV\">([NWES]*)</span>", Pattern.CASE_INSENSITIVE);
    private static final Pattern HUMIDITY_RE = Pattern.compile(
            "<span id=\"PARAM_0_RH\">([0-9,]*)</span>", Pattern.CASE_INSENSITIVE);

    @Override
    protected <T> Observable<WeatherBasicEvent> makeRequest() {

        return RxNetty.createHttpRequest(prepareHttpGETRequest(URL)).compose(this::unpackResponse).map(htmlSource -> {
            Matcher temperatureM = TEMPERATURE_RE.matcher(htmlSource);
            Matcher pressureM = PRESSURE_RE.matcher(htmlSource);
            Matcher windSpeedM = WIND_SPEED_RE.matcher(htmlSource);
            Matcher windDirectionM = WIND_DIRECTION_RE.matcher(htmlSource);
            Matcher humidityM = HUMIDITY_RE.matcher(htmlSource);

            boolean foundAll = true;

            float temperature = 0;
            int pressure = 0;
            int clouds = 0;//brak informacji o chmurach
            float windSpeed = 0;
            float windDirection = 0;
            int humidity = 0;

            if (temperatureM.find()) {
                temperature = Float.parseFloat(temperatureM.group(1).trim());
            } else {
                foundAll = false;
            }

            if (pressureM.find()) {
                pressure = (int)Float.parseFloat(pressureM.group(1).trim());
            } else {
                foundAll = false;
            }

            if (windSpeedM.find()) {
                windSpeed = Float.parseFloat(windSpeedM.group(1).trim());
            } else {
                foundAll = false;
            }

            if (windDirectionM.find()) {
                windDirection = Float.parseFloat(windDirectionM.group(1).trim());
            } else {
                foundAll = false;
            }

            if (humidityM.find()) {
                humidity = (int)Float.parseFloat(humidityM.group(1).trim());
            } else {
                foundAll = false;
            }

            if (foundAll) {
                return new WeatherBasicEvent(temperature, pressure, clouds, windSpeed, windDirection, humidity);
            }
            throw Exceptions.propagate(new BitcoinRateNotFoundException());
        });
    }
}

