package taxi.city.citytaxiclient.networking.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import taxi.city.citytaxiclient.models.Order;
import taxi.city.citytaxiclient.models.OrderStatus;
import taxi.city.citytaxiclient.models.Tariff;
import taxi.city.citytaxiclient.utils.Constants;

public class NOrder {

    @Expose
    @SerializedName("id")
    private int orderId;

    @Expose
    @SerializedName("client_phone")
    private String clientPhone;

    @Expose
    @SerializedName("order_time")
    private String orderTime;

    @Expose
    @SerializedName("status")
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
    @SerializedName("tariff")
    private int tariffId;

    @Expose
    @SerializedName("driver")
    private int driverId;

    //@Expose
    @SerializedName("client")
    private int clientId;

    @Expose
    @SerializedName("description")
    private String description;

    @Expose
    @SerializedName("order_travel_time")
    private String orderTravelTime;

    private long duration;
    private long pauseDuration;

    @Expose
    @SerializedName("order_sum")
    private double sum;

    @Expose
    @SerializedName("order_distance")
    private double distance;



    public NOrder() {}

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getClientPhone() {
        return clientPhone;
    }

    public void setClientPhone(String clientPhone) {
        this.clientPhone = clientPhone;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getStartName() {
        return this.startName;
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
        if (waitTime == null) {
            return "00:00:00";
        }
        return waitTime;
    }

    public void setWaitTime(String waitTime) {
        this.waitTime = waitTime;
    }

    public double getWaitTimePrice() {
        return this.waitTimePrice;
    }

    public void setWaitTimePrice(double waitTimePrice) {
        this.waitTimePrice = waitTimePrice;
    }

    public double getFixedPrice() {
        return fixedPrice;
    }

    public void setFixedPrice(double fixedPrice) {
        this.fixedPrice = fixedPrice;
    }

    public int getTariffId() {
        return tariffId;
    }

    public void setTariffId(int tariffId) {
        this.tariffId = tariffId;
    }

    public int getDriverId() {
        return driverId;
    }

    public void setDriverId(int driverId) {
        this.driverId = driverId;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOrderTravelTime() {
        return orderTravelTime;
    }

    public void setOrderTravelTime(String orderTravelTime) {
        this.orderTravelTime = orderTravelTime;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = (double)Math.round(distance*100)/100;
    }
    //End getter and setters

    //Helper methods
    public double getTotalSum() {
        return this.getTravelSum() + this.getWaitTimePrice();
    }

    public double getTravelSum() {
        return this.sum;
    }
}
