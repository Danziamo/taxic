package taxi.city.citytaxiclient.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class OrderUser {
    @Expose
    private String phone;

    @Expose
    private int id;

    @Expose
    @SerializedName("first_name")
    private String firstName;
    @Expose
    @SerializedName("last_name")
    private String lastName;

    @Expose
    private Rating rating;

    @Expose
    private ArrayList<Car> cars;

    @Expose
    @SerializedName("cur_position")
    private String curPosition;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }

    public ArrayList<Car> getCars() {
        return cars;
    }

    public void setCars(ArrayList<Car> cars) {
        this.cars = cars;
    }

    public String getCurPosition() {
        return curPosition;
    }

    public void setCurPosition(String curPosition) {
        this.curPosition = curPosition;
    }

    public String getFullName() {
        return this.lastName + " " + this.firstName;
    }
}
