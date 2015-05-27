package taxi.city.citytaxiclient.core;

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
    public float rating;

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
        this.id = object.has("id") ? object.getString("id") : null;
        this.phone = object.has("phone") ? object.getString("phone") : null;
        this.firstName = object.has("first_name") ? object.getString("first_name") : null;
        this.lastName = object.has("last_name") ? object.getString("last_name") : null;
        String ratingSumString = object.getJSONObject("rating").getString("votes__sum");
        double ratingSum = ratingSumString == null || ratingSumString == "null" ? 0 : Double.valueOf(ratingSumString);
        int ratingCount = object.getJSONObject("rating").getInt("votes__count");
        this.rating = ratingCount == 0 ? 0 : (int)Math.round(ratingSum/ratingCount);
        if (object.has("cars") && object.getJSONArray("cars").length() > 0) {
            JSONObject carJSON = object.getJSONArray("cars").getJSONObject(0);
            this.carBrand = carJSON.has("brand") ? carJSON.getJSONObject("brand").getString("brand_name") : null;
            this.carModel = carJSON.has("brand_model") ? carJSON.getString("brand_model_name") : null;
            this.carColor = object.has("color") ? carJSON.getString("color") : null;
            this.carNumber = object.has("car_number") ? object.getString("car_number") : null;
        }
    }
}
