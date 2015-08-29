package taxi.city.citytaxiclient.networking.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import taxi.city.citytaxiclient.models.OrderStatus;
import taxi.city.citytaxiclient.models.Tariff;

public class NOrder {

    @Expose
    @SerializedName("client_phone")
    public String clientPhone;

    public OrderStatus status;

    @Expose
    public int tariff;

    @Expose
    public int client;

    @Expose
    @SerializedName("fixed_price")
    public double fixedPrice;

    @Expose
    @SerializedName("address_start_name")
    public String  startName;

    @Expose
    @SerializedName("address_stop_name")
    public String stopName;

    @Expose
    @SerializedName("address_start")
    public String startPoint;

    @Expose
    public String description;
}
