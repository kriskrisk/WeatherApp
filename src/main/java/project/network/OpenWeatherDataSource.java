package project.network;

import com.google.gson.JsonObject;
import io.reactivex.netty.RxNetty;
import project.event.WeatherBasicEvent;
import rx.Observable;

import java.text.DecimalFormat;

//import static java.lang.Double.parseDouble;

public class OpenWeatherDataSource extends DataSource {

    private static final String URL = "http://api.openweathermap.org/data/2.5/weather?q=Warsaw&APPID=6e4867df339d71f8b66f2a5a84c17b2d";

    private static final String MAIN_JSON_KEY = "main";
    private static final String WIND_JSON_KEY = "wind";
    private static final String CLOUDS_JSON_KEY = "clouds";
    private static final String TEMPERATURE_JSON_KEY = "temp";
    private static final String PRESSURE_JSON_KEY = "pressure";
    private static final String CLOUDS_PERC_JSON_KEY = "all";
    private static final String WIND_SPEED_JSON_KEY = "speed";
    private static final String WIND_DIRECTION_JSON_KEY = "deg";
    private static final String HUMIDITY_JSON_KEY = "humidity";

    @Override
    protected Observable<WeatherBasicEvent> makeRequest() {

        return RxNetty
                .createHttpRequest( // create an HTTP request using RxNetty
                        JsonHelper.withJsonHeader(prepareHttpGETRequest(URL)))
                // add an HTTP request header that instructs the server to
                // respond with JSON
                .compose(this::unpackResponse) // extract response body to a
                // string
                .map(JsonHelper::asJsonObject) // convert this string to a
                // JsonObject
                .map(jsonObject -> {
                    JsonObject weatherJsonObject = jsonObject.get(MAIN_JSON_KEY).getAsJsonObject();
                    JsonObject windJsonObject = jsonObject.get(WIND_JSON_KEY).getAsJsonObject();
                    JsonObject cloudsJsonObject = jsonObject.get(CLOUDS_JSON_KEY).getAsJsonObject();
                    double temp = parseDouble(new DecimalFormat(".#").format(
                            weatherJsonObject.get(TEMPERATURE_JSON_KEY).getAsDouble() - 273.15));
                    return new WeatherBasicEvent(temp,
                            weatherJsonObject.get(PRESSURE_JSON_KEY).getAsDouble(),
                            cloudsJsonObject.get(CLOUDS_PERC_JSON_KEY).getAsDouble(),
                            windJsonObject.get(WIND_SPEED_JSON_KEY).getAsDouble(),
                            windJsonObject.get(WIND_DIRECTION_JSON_KEY).getAsDouble(),
                            weatherJsonObject.get(HUMIDITY_JSON_KEY).getAsDouble());
                });
    }

}
