package taxi.city.citytaxiclient.networking.api;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.PATCH;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedFile;
import taxi.city.citytaxiclient.models.OnlineStatus;
import taxi.city.citytaxiclient.models.Role;
import taxi.city.citytaxiclient.models.User;
import taxi.city.citytaxiclient.networking.model.NUser;
import taxi.city.citytaxiclient.networking.model.UserStatus;

public interface UserApi {
    @POST("/users/")
    void add(@Body NUser user, Callback<User> cb);

    @GET("/users/{userId}")
    void getById(@Path("userId") int id, Callback<User> cb);

    @PATCH("/users/{userId}")
    void save(@Path("userId") int id, @Body User user, Callback<User> cb);

    @Multipart
    @POST("/users/{userId}/upload_picture")
    void uploadPicture(@Path("userId") int userId,
                       @Part("picture") TypedFile picture,
                       Callback<Object> cb);

    @PATCH("/users/{userId}/")
    void updateStatus(@Path("userId") int userId, @Body UserStatus user, Callback<User> cb);

    @FormUrlEncoded
    @PATCH("/users/{userId}/")
    void updateStatus(@Path("userId") int userId, @Field("online_status") OnlineStatus onlineStatus, Callback<Object> cb);

    @PATCH("/users/{userId}/")
    void updateUser(@Path("userId") int userId, @Body NUser user, Callback<User> callback);

    @FormUrlEncoded
    @PATCH("/users/{userId}/")
    Object updateToken(@Path("userId") int userId, @Field("android_token") String androidToken);

    @GET("/users/")
    void getDrivers(@Query("online_status") OnlineStatus status, @Query("role") Role role, Callback<ArrayList<User> > cb);
}
