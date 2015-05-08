package taxi.city.citytaxiclient.Core;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

import taxi.city.citytaxiclient.Enums.OStatus;

/**
 * Created by Daniyar on 3/27/2015.
 */
public class Order {
    private static Order mInstance = null;
    public int id;
    public String orderTime;
    public LatLng addressStart;
    public LatLng addressStop;
    public int clientId;
    public String clientPhone;
    public OStatus status;
    public String waitTime;
    public int tariff;
    public String description;
    public String addressStartName;
    public String addressStopName;
    public String driverPhone;
    public Driver driver;

    public String time;
    public double sum;
    public double distance;
    public double waitSum;
    public double fixedPrice;

    private Order() {

    }

    public static Order getInstance() {
        if (mInstance == null) {
            mInstance = new Order();
        }
        return mInstance;
    }

    private String LatLngToString(LatLng point) {
        if (point != null) {
            String res = point.toString();
            return res.replace("lat/lng", "POINT").replace(",", " ").replace(":", " ");
        } else {
            return null;
        }
    }

    private String getTimeFromLong(long seconds) {
        int hr = (int)seconds/3600;
        int rem = (int)seconds%3600;
        int mn = rem/60;
        int sec = rem%60;
        String hrStr = (hr<10 ? "0" : "")+hr;
        String mnStr = (mn<10 ? "0" : "")+mn;
        String secStr = (sec<10 ? "0" : "")+sec;
        return hrStr + mnStr + secStr;
    }

    private String getTimeNow() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }

    public JSONObject getOrderAsJson() throws JSONException {
        JSONObject obj = new JSONObject();
        //obj.put("id", this.id);
        obj.put("client_phone", this.clientPhone);
        obj.put("status", this.status);
        obj.put("address_start", LatLngToString(this.addressStart));
        obj.put("wait_time", "00:00:00");
        obj.put("tariff", 1);
        obj.put("order_time", getTimeNow());
        obj.put("order_sum", 0);
        obj.put("order_distance", 0);
        obj.put("order_travel_time", "00:00:00");
        obj.put("address_start_name", this.addressStartName);
        obj.put("address_stop_name", this.addressStopName == null ? JSONObject.NULL : this.addressStopName);
        obj.put("description", this.description);
        obj.put("client", this.clientId);
        obj.put("fixed_price", this.fixedPrice);
        return obj;
    }

    public void clear() {
        this.id = 0;
        this.waitTime = null;
        //this.addressStart = null;
        this.addressStop = null;
        this.status = null;
        this.tariff = 1;
        this.driver = null;
        this.orderTime = null;
        this.clientPhone = null;
        this.distance = 0;
        this.time = "00:00:00";
        this.waitSum = 0;
        this.sum = 0;
    }
}
