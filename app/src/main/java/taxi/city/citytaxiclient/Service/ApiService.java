package taxi.city.citytaxiclient.Service;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Daniyar on 3/26/2015.
 */
public class ApiService {
    private static final String url = "http://81.88.192.37/api/v1/";
    private static final String TAG = "ApiService";
    private String token;

    private static ApiService mInstance = null;
    private ApiService() {
        this.token = "";
    }

    public static ApiService getInstance() {
        if (mInstance == null) {
            mInstance = new ApiService();
        }
        return mInstance;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public JSONObject loginRequest(JSONObject data, String apiUrl) {

        HttpClient httpclient = new DefaultHttpClient();
        JSONObject res;

        try {
            HttpPost request = new HttpPost(url + apiUrl);
            // Add your data
            request.addHeader("content-type", "application/json");

            StringEntity params = new StringEntity(data.toString());
            request.setEntity(params);

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();

            res = parseData(response ,statusCode);

        } catch (ClientProtocolException e) {
            res = null;
            // TODO Auto-generated catch block
        } catch (IOException e) {
            res = null;
            // TODO Auto-generated catch block
        } catch (Exception e) {
            e.printStackTrace();
            e.getMessage();
            res = null;
        }
        return res;
    }

    public int signUpRequest(JSONObject data, String apiUrl) {
        HttpClient httpclient = new DefaultHttpClient();
        int statusCode = -1;

        try {
            HttpPost request = new HttpPost(url + apiUrl);
            // Add your data
            request.addHeader("content-type", "application/json");

            StringEntity params = new StringEntity(data.toString(), HTTP.UTF_8);
            request.setEntity(params);

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(request);
            statusCode = response.getStatusLine().getStatusCode();

            Log.d(TAG, parseData(response, statusCode).toString());

        } catch (ClientProtocolException e) {
            statusCode = -1;
            // TODO Auto-generated catch block
        } catch (IOException e) {
            statusCode = -1;
            // TODO Auto-generated catch block
        } catch (Exception e) {
            e.printStackTrace();
            e.getMessage();
            statusCode = -1;
        }
        return statusCode;
    }

    public int createOrderRequest(JSONObject data, String apiUrl) {
        HttpClient httpclient = new DefaultHttpClient();
        int res;

        try {
            HttpPost request = new HttpPost(url + apiUrl);
            // Add your data
            request.addHeader("content-type", "application/json");
            request.setHeader("Authorization", "Token " + this.token);

            StringEntity entity = new StringEntity(data.toString(), HTTP.UTF_8);
            request.setEntity(entity);

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(request);
            res = response.getStatusLine().getStatusCode();
        } catch (ClientProtocolException e) {
            res = -1;
            // TODO Auto-generated catch block
        } catch (IOException e) {
            res = -1;
            // TODO Auto-generated catch block
        } catch (Exception e) {
            e.printStackTrace();
            e.getMessage();
            res = -1;
        }
        return res;
    }

    protected JSONObject parseData(HttpResponse response, int statusCode) {
        JSONObject result = null;
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }

            result = new JSONObject(sb.toString());
            result.put("status_code", statusCode);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    protected String getResponseMessage(HttpResponse response) {
        try {
            BufferedReader rd = null;
            rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            StringBuilder sb = new StringBuilder();
            String line = "";
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }

            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return e.getMessage();
        }
    }
}
