package es.upm.miw.smarthome.es.upm.miw.smarthome.weather;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Weather {


    @SerializedName("wind")
    @Expose
    private Wind wind;

//    @SerializedName("rain")
//    @Expose
//    private List<Rain> rain=null;

    public Weather(){

    }

    public void setWind(Wind wind) {
        this.wind = wind;
    }

    public Wind getWind() {
        return wind;
    }
}
