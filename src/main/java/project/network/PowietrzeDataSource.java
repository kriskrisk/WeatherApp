package project.network;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.reactivex.netty.RxNetty;
import rx.Observable;

import project.event.DustBasicEvent;

public class PowietrzeDataSource extends DataSource {

    private static final String BASE_URL = "http://powietrze.gios.gov.pl/pjp/current/getAQIDetailsList?param=AQI";

    private static final String PM10_JSON_KEY = "PM10";
    private static final String PM25_JSON_KEY = "PM2.5";
    private static final String ID_JSON_KEY = "stationId";
    private static final String VALUES_JSON_KEY = "values";
    private static final int ID_NUMBER = 544;

    @Override
    protected Observable<DustBasicEvent> makeRequest() {
        return RxNetty
                .createHttpRequest( // create an HTTP request using RxNetty
                        JsonHelper.withJsonHeader(prepareHttpGETRequest(BASE_URL)))
                // add an HTTP request header that instructs the server to
                // respond with JSON
                .compose(this::unpackResponse) // extract response body to a
                // string
                .map(JsonHelper::asJsonArray) // convert this string to a
                // JsonObject
                .map(jsonArray -> {
                    for (JsonElement o : jsonArray) {
                        JsonObject current = o.getAsJsonObject();
                        if (current.get(ID_JSON_KEY).getAsInt() == ID_NUMBER) {
                            JsonObject data = current.get(VALUES_JSON_KEY).getAsJsonObject();
                            return new DustBasicEvent(
                                    data.get(PM10_JSON_KEY).getAsDouble(),
                                    data.get(PM25_JSON_KEY).getAsDouble());
                        }
                    }

                    throw new IllegalStateException();
                });
    }

}

