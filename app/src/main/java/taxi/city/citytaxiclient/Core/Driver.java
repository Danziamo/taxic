package taxi.city.citytaxiclient.Core;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Daniyar on 5/8/2015.
 */
public class Driver {
    public String id;
    public String firstName;
    public String lastName;
    public String carBrand;
    public String carModel;
    public String carColor;
    public String phone;
    public String carNumber;

    public Driver (String id, String phone, String firstName, String lastName, String carBrand, String carModel, String carColor, String carNumber) {
        this.id = id;
        this.phone = phone;
        this.firstName = firstName;
        this.lastName = lastName;
        this.carBrand = carBrand;
        this.carModel = carModel;
        this.carColor = carColor;
        this.carNumber = carNumber;
    }

    public Driver (JSONObject object) throws JSONException {
        this.id = object.has("driver") ? object.getString("driver") : null;
        this.phone = object.has("driver_phone") ? object.getString("driver_phone") : null;
        this.firstName = object.has("driver_first_name") ? object.getString("driver_first_name") : null;
        this.lastName = object.has("driver_last_name") ? object.getString("driver_last_name") : null;
        this.carBrand = object.has("driver_brand") ? object.getString("driver_brand") : null;
        this.carModel = object.has("driver_model") ? object.getString("driver_model") : null;
        this.carColor = object.has("driver_color") ? object.getString("driver_color") : null;
        this.carNumber = object.has("driver_number") ? object.getString("driver_number") : null;
    }
}
