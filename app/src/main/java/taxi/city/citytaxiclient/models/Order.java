package taxi.city.citytaxiclient.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Order implements Serializable{

    @Expose
    private int id;

    @Expose
    @SerializedName("client_phone")
    private String clientPhone;

    @Expose
    private OrderStatus status;

    @Expose
    @SerializedName("address_start_name")
    private String startName;

    @Expose
    @SerializedName("address_stop_name")
    private String stopName;

    @Expose
    @SerializedName("address_start")
    private String startPoint;

    @Expose
    @SerializedName("address_stop")
    private String stopPoint;

    @Expose
    @SerializedName("wait_time")
    private String waitTime;

    @Expose
    @SerializedName("wait_time_price")
    private double waitTimePrice;

    @Expose
    @SerializedName("fixed_price")
    private double fixedPrice;

    @Expose
    private Tariff tariff;

    @Expose
    private User driver;

    @Expose
    private User client;

    @Expose
    private String description;

    @Expose
    @SerializedName("order_travel_time")
    private String duration;

    @Expose
    @SerializedName("order_sum")
    private double sum;

    @Expose
    @SerializedName("order_distance")
    private double distance;
}
