package es.upm.miw.smarthome;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import java.util.List;

import es.upm.miw.smarthome.es.upm.miw.smarthome.weather.Weather;

public class Record extends Activity {

    List<Weather> listaHistorico;
    ListView lvHistorico;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        lvHistorico = findViewById(R.id.lvListadoHistorico);
//        repositorioPuntuaciones = new RepositorioPuntuaciones(this);
//        listaHistorico = repositorioPuntuaciones.getAllPuntuacionesOrderedByNFichas();
        lvHistorico.setAdapter(new RecordAdapter(this, listaHistorico, R.layout.record_list));
    }
}
