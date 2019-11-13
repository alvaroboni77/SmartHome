package es.upm.miw.smarthome;

import android.app.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import es.upm.miw.smarthome.es.upm.miw.smarthome.weather.Weather;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends Activity {

    private static final String API_BASE_URL = "http://api.openweathermap.org";
    private TextView tvRespuesta;
    private WeatherRESTAPIService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvRespuesta = (TextView) findViewById(R.id.tvRespuesta);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(WeatherRESTAPIService.class);
    }

    public void obtenerInfoCiudad(View v) {

        Call<Weather> call_async = apiService.getCityByName();

        call_async.enqueue(new Callback<Weather>() {

            @Override
            public void onResponse(Call<Weather> call, Response<Weather> response) {
                Weather weather = response.body();
                if (null != weather) {
                tvRespuesta.append(weather.getName() + ": "+ weather.getWind()+"\n\n");
                } else {
                    tvRespuesta.setText("No se han recuperado datos");
                }
            }

            @Override
            public void onFailure(Call<Weather> call, Throwable t) {
                Toast.makeText(
                        getApplicationContext(),
                        "ERROR: " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }
}
