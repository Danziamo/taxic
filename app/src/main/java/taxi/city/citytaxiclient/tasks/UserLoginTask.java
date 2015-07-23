package taxi.city.citytaxiclient.tasks;

import android.os.AsyncTask;
import android.util.Log;

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
            JSONObject object = api.loginRequest(json, "login/");
            if (Helper.isSuccess(object)) {
                user.setUser(object);
                ApiService.getInstance().setToken(user.getToken());
                statusCode = 200;

                JSONObject onlineStatus = new JSONObject();
                onlineStatus.put("online_status", "online");
                onlineStatus.put("ios_token", JSONObject.NULL);
                onlineStatus.put("role", "user");
                onlineStatus = api.patchRequest(onlineStatus, "users/" + String.valueOf(user.id)+ "/");
                JSONObject orderResult = api.getArrayRequest("orders/?status=new&ordering=-id&limit=1&client=" + String.valueOf(user.id));
                if (Helper.isSuccess(orderResult) && orderResult.getJSONArray("result").length() > 0) {
                    Order.getInstance().id = orderResult.getJSONArray("result").getJSONObject(0).getInt("id");
                }
                orderResult = api.getArrayRequest("orders/?status=accepted&ordering=-id&limit=1&client=" + String.valueOf(user.id));
                if (Helper.isSuccess(orderResult) && orderResult.getJSONArray("result").length() > 0) {
                    Order.getInstance().id = orderResult.getJSONArray("result").getJSONObject(0).getInt("id");
                }
                orderResult = api.getArrayRequest("orders/?status=waiting&ordering=-id&limit=1&client=" + String.valueOf(user.id));
                if (Helper.isSuccess(orderResult) && orderResult.getJSONArray("result").length() > 0) {
                    Order.getInstance().id = orderResult.getJSONArray("result").getJSONObject(0).getInt("id");
                }
                orderResult = api.getArrayRequest("orders/?status=ontheway&ordering=-id&limit=1&client=" + String.valueOf(user.id));
                if (Helper.isSuccess(orderResult) && orderResult.getJSONArray("result").length() > 0) {
                    Order.getInstance().id = orderResult.getJSONArray("result").getJSONObject(0).getInt("id");
                }
                orderResult = api.getArrayRequest("orders/?status=pending&ordering=-id&limit=1&client=" + String.valueOf(user.id));
                if (Helper.isSuccess(orderResult) && orderResult.getJSONArray("result").length() > 0) {
                    Order.getInstance().id = orderResult.getJSONArray("result").getJSONObject(0).getInt("id");
                }
                orderResult = api.getArrayRequest("orders/?status=sos&ordering=-id&limit=1&client=" + String.valueOf(user.id));
                if (Helper.isSuccess(orderResult) && orderResult.getJSONArray("result").length() > 0) {
                    Order.getInstance().id = orderResult.getJSONArray("result").getJSONObject(0).getInt("id");
                }
            } else if (Helper.isBadRequest(object)) {
                String detail = "";
                if (object.has("detail")){
                    detail = object.getString("detail");
                }

                if (detail.contains("Account")) {
                    statusCode = NOT_ACTIVATED_ACCOUNT_STATUS_CODE;
                } else if(object.has("status_code")){
                    statusCode = object.getInt("status_code");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("LoginActivity", Arrays.toString(e.getStackTrace()));
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("LoginActivity", Arrays.toString(e.getStackTrace()));
        }
        return statusCode;
    }
}
