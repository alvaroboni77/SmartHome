package es.upm.miw.smarthome;

import es.upm.miw.smarthome.es.upm.miw.smarthome.weather.Weather;
import retrofit2.Call;
import retrofit2.http.GET;

@SuppressWarnings("Unused")
interface WeatherRESTAPIService {

    // http://api.openweathermap.org/data/2.5/weather?q=Madrid&APPID=2a52105ca37cda07e6341b5ef371c288
    @GET("/data/2.5/weather?q=Madrid&APPID=2a52105ca37cda07e6341b5ef371c288")
    Call<Weather> getCityByName();
}
