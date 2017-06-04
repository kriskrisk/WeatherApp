package project.network;

import com.google.gson.JsonObject;
import io.reactivex.netty.RxNetty;
import project.event.WeatherBasicRateEvent;
import rx.Observable;

public class OpenWeatherRateEvent extends RateDataSource{

    private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/weather?q=Warsaw";

    private static final String RATES_JSON_KEY = "rates";
    private static final String BID_JSON_KEY = "bid";
    private static final String ASK_JSON_KEY = "ask";

    @Override
    protected Observable<WeatherBasicRateEvent> makeRequest() {
        String url = BASE_URL;

        return RxNetty
                .createHttpRequest( // create an HTTP request using RxNetty
                        JsonHelper.withJsonHeader(prepareHttpGETRequest(url)))
                // add an HTTP request header that instructs the server to
                // respond with JSON
                .compose(this::unpackResponse) // extract response body to a
                // string
                .map(JsonHelper::asJsonObject) // convert this string to a
                // JsonObject
                .map(jsonObject -> { // extract ask/ bid prices
                    JsonObject ratesJsonObject = jsonObject.get(RATES_JSON_KEY).getAsJsonArray().get(0)
                            .getAsJsonObject();
                    return new WeatherBasicRateEvent(ratesJsonObject.get(BID_JSON_KEY).getAsFloat(),
                            ratesJsonObject.get(BID_JSON_KEY).getAsInt(),
                            ratesJsonObject.get(BID_JSON_KEY).getAsFloat(),
                            ratesJsonObject.get(BID_JSON_KEY).getAsFloat(),
                            ratesJsonObject.get(BID_JSON_KEY).getAsFloat(),
                            ratesJsonObject.get(ASK_JSON_KEY).getAsInt());
                });
    }

}
