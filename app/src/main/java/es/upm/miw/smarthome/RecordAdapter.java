package es.upm.miw.smarthome;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.text.SimpleDateFormat;
import java.util.List;

import es.upm.miw.smarthome.es.upm.miw.smarthome.weather.Weather;

public class RecordAdapter extends ArrayAdapter<Weather> {

    public RecordAdapter(Context contexto, List<Weather> objects, int resourceId) {
        super(contexto, resourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // obtener o generar vista
        if (convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.record_list, parent, false);
        }

        Weather weather = getItem(position);
        if (weather != null) {

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            TextView tvWind= convertView.findViewById(R.id.windTextView);
            tvWind.setText(String.valueOf(weather.getWind()));

//            TextView tvRain= convertView.findViewById(R.id.rainTextView);
//            tvRain.setText(String.valueOf(weather.getRain()));
        }
        return convertView;
    }
}
