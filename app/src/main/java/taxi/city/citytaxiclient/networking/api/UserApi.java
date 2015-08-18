package taxi.city.citytaxiclient.networking.api;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.PATCH;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.mime.TypedFile;
import taxi.city.citytaxiclient.models.OnlineStatus;
import taxi.city.citytaxiclient.models.User;
import taxi.city.citytaxiclient.networking.model.NUser;
import taxi.city.citytaxiclient.networking.model.UserStatus;

public interface UserApi {
    @POST("/users")
    void add(@Body User user, Callback<User> callback);

    @GET("/users/{userId}")
    void getById(@Path("userId") int id, Callback<User> callback);

    @PATCH("/users/{userId}")
    void save(@Path("userId") int id, @Body User user, Callback<User> callback);

    @Multipart
    @POST("/users/{userId}/upload_picture")
    void uploadPicture(@Path("userId") int userId,
                       @Part("picture") TypedFile picture,
                       Callback<Object> cb);

    @PATCH("/users/{userId}/")
    void updateStatus(@Path("userId") int userId, @Body UserStatus user, Callback<User> callback);

    @PATCH("/users/{userId}/")
    void updateStatus(@Path("userId") int userId, @Field("online_status") OnlineStatus onlineStatus, Callback<Object> callback);

    @PATCH("/users/{userId}/")
    void updateUser(@Path("userId") int userId, @Body NUser user, Callback<User> callback);
}
