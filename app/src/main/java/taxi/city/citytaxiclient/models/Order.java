package taxi.city.citytaxiclient.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import taxi.city.citytaxiclient.utils.Constants;

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
    private String waitTimePrice;

    @Expose
    @SerializedName("fixed_price")
    private String fixedPrice;

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
    private String sum;

    @Expose
    @SerializedName("order_distance")
    private String distance;

    private boolean isActive;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getStartName() {
        return startName;
    }

    public void setStartName(String startName) {
        this.startName = startName;
    }

    public String getStopName() {
        return stopName;
    }

    public void setStopName(String stopName) {
        this.stopName = stopName;
    }

    public String getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(String startPoint) {
        this.startPoint = startPoint;
    }

    public String getStopPoint() {
        return stopPoint;
    }

    public void setStopPoint(String stopPoint) {
        this.stopPoint = stopPoint;
    }

    public String getWaitTime() {
        return waitTime;
    }

    public void setWaitTime(String waitTime) {
        this.waitTime = waitTime;
    }

    public String getWaitTimePrice() {
        //if (this.fixedPrice >= Constants.fixedPrice) return 0;
        return this.waitTimePrice;
    }

    public void setWaitTimePrice(String waitTimePrice) {
        this.waitTimePrice = waitTimePrice;
    }

    public String getFixedPrice() {
        return fixedPrice;
    }

    public void setFixedPrice(String fixedPrice) {
        this.fixedPrice = fixedPrice;
    }

    public Tariff getTariff() {
        return tariff;
    }

    public void setTariff(Tariff tariff) {
        this.tariff = tariff;
    }

    public User getDriver() {
        return driver;
    }

    public void setDriver(User driver) {
        this.driver = driver;
    }

    public User getClient() {
        return client;
    }

    public void setClient(User client) {
        this.client = client;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getSum() {
        return sum;
    }

    public void setSum(String sum) {
        this.sum = sum;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
    }

    public String getTotalSum() {
        //if (this.fixedPrice >= Constants.fixedPrice) return this.fixedPrice;
        return this.sum + this.waitTimePrice;
    }

    public String getTravelSum() {
        //if (this.fixedPrice >= Constants.fixedPrice) return fixedPrice;
        return this.sum;
    }

    public Order () {}
}
