package taxi.city.citytaxiclient.core;

import com.google.android.gms.maps.model.LatLng;

public class DriverPosition {
    public String status;
    public LatLng position;

    public DriverPosition (String status, LatLng position) {
        this.status = status;
        this.position = position;
    }
}
