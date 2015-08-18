package taxi.city.citytaxiclient.networking.api;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;
import taxi.city.citytaxiclient.models.Order;
import taxi.city.citytaxiclient.models.OrderStatus;

public interface OrderApi {
    @GET("/info_orders/")
    void getAll(@Query("client") int userId, @Query("status") OrderStatus status, @Query("ordering") String type, @Query("limit") int limit, Callback<ArrayList<Order>> cb);

    @GET("/orders/{orderId}/")
    void getById(@Path("orderId") int orderId, Callback<Order> cb);
}
