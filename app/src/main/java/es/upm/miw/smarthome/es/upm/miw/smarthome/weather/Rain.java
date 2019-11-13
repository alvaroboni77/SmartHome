package es.upm.miw.smarthome.es.upm.miw.smarthome.weather;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Rain {
    @SerializedName("1h")
    @Expose
    private Double oneh;

    public Double getOneh() {
        return oneh;
    }

    public void setOneh(Double oneh) {
        this.oneh = oneh;
    }
}
