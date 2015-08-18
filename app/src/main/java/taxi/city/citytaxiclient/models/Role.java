package taxi.city.citytaxiclient.models;

import com.google.gson.annotations.SerializedName;

public enum Role {
    @SerializedName("driver")
    DRIVER,
    @SerializedName("client")
    CLIENT;

    @Override
    public String toString() {
        switch (this) {
            case DRIVER:
                return "driver";
            case CLIENT:
                return "client";
            default:
                return null;
        }
    }
}
