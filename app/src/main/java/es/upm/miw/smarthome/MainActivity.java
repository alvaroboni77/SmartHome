package es.upm.miw.smarthome;

import android.app.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

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

        Call<City> call_async = apiService.getCityByName("Madrid", "2a52105ca37cda07e6341b5ef371c288");

        call_async.enqueue(new Callback<City>() {

            @Override
            public void onResponse(Call<City> call, Response<City> response) {
                City city = response.body();
                if (null != city) {
                tvRespuesta.append(city.getName() + "] "+city.getWind()+"\n\n");
                } else {
                    tvRespuesta.setText("No se han recuperado datos");
                }
            }

            @Override
            public void onFailure(Call<City> call, Throwable t) {
                Toast.makeText(
                        getApplicationContext(),
                        "ERROR: " + t.getMessage(),
                        Toast.LENGTH_LONG
                ).show();
            }
        });
    }
}
