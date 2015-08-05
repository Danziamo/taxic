package taxi.city.citytaxiclient.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import taxi.city.citytaxiclient.core.Order;
import taxi.city.citytaxiclient.core.User;
import taxi.city.citytaxiclient.service.ApiService;
import taxi.city.citytaxiclient.utils.Helper;

/**
 * Created by mbt on 7/23/15.
 */
public abstract class UserLoginTask extends AsyncTask<Void, Void, Integer> {
    private final String mPhone;
    private final String mPassword;

    public static final int NOT_ACTIVATED_ACCOUNT_STATUS_CODE = 1000;

    public UserLoginTask(String phone, String password) {
        mPhone = phone;
        mPassword = password;
    }

    @Override
    protected Integer doInBackground(Void... params) {
        User user = User.getInstance();
        ApiService api = ApiService.getInstance();

        int statusCode = 500;
        try {
            JSONObject json = new JSONObject();
            user.phone = mPhone;
            user.password = mPassword;
            json.put("phone", mPhone);
            json.put("password", mPassword);
            JSONObject loginResult = api.loginRequest(json, "login/");
            if (Helper.isSuccess(loginResult)) {
                user.setUser(loginResult);
                ApiService.getInstance().setToken(user.getToken());
                statusCode = 200;

                JSONObject onlineStatus = new JSONObject();
                onlineStatus.put("online_status", "online");
                onlineStatus.put("ios_token", JSONObject.NULL);
                onlineStatus.put("role", "user");
                onlineStatus = api.patchRequest(onlineStatus, "users/" + String.valueOf(user.id)+ "/");
                if (loginResult.has("is_order_active") && loginResult.getJSONArray("is_order_active").length() > 0) {
                    JSONArray orderArray = loginResult.getJSONArray("is_order_active");
                    for (int i = 0; i < orderArray.length(); ++i) {
                        JSONObject orderObject = orderArray.getJSONObject(i);
                        if (orderObject.getString("client").equals("null")) continue;
                        if (orderObject.getInt("client") != user.id) continue;
                        Order.getInstance().id = orderObject.getInt("id");
                        Order.getInstance().status = Helper.getStatus(orderObject.getString("status"));
                        break;
                    }

                }
            } else if (Helper.isBadRequest(loginResult)) {
                String detail = "";
                if (loginResult.has("detail")){
                    detail = loginResult.getString("detail");
                }

                if (detail.contains("Account")) {
                    statusCode = NOT_ACTIVATED_ACCOUNT_STATUS_CODE;
                } else if(loginResult.has("status_code")){
                    statusCode = loginResult.getInt("status_code");
                }
            }
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
        return statusCode;
    }
}
