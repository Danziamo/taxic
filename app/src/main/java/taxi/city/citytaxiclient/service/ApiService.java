package taxi.city.citytaxiclient.service;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import taxi.city.citytaxiclient.requestMethods.HttpPatch;

/**
 * Created by Daniyar on 3/26/2015.
 */
public class ApiService {
    private static final String url = "http://81.88.192.37/api/v1/";
    private static final int CONNECTION_TIMEOUT = 5000;
    private static final int SOCKET_TIMEOUT = 10000;
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

    private HttpClient getHttpClient() {
        HttpParams httpParameters = new BasicHttpParams();
        HttpConnectionParams.setConnectionTimeout(httpParameters, CONNECTION_TIMEOUT);
        HttpConnectionParams.setSoTimeout(httpParameters, SOCKET_TIMEOUT);
        return new DefaultHttpClient(httpParameters);
    }

    public void setToken(String token) {
        this.token = token;
    }

    public JSONObject loginRequest(JSONObject data, String apiUrl) {

        HttpClient httpClient = getHttpClient();
        JSONObject res;

        try {
            HttpPost request = new HttpPost(url + apiUrl);
            // Add your data
            request.addHeader("content-type", "application/json");

            StringEntity params = new StringEntity(data.toString());
            request.setEntity(params);

            // Execute HTTP Post Request
            HttpResponse response = httpClient.execute(request);
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

    public JSONObject logoutRequest(JSONObject data, String apiUrl) {
        HttpClient httpClient = getHttpClient();
        JSONObject res;

        try {
            HttpPost request = new HttpPost(url + apiUrl);
            // Add your data
            request.addHeader("content-type", "application/json");
            request.addHeader("Authorization", "Token " + this.token);

            StringEntity params = new StringEntity(data.toString());
            request.setEntity(params);

            // Execute HTTP Post Request
            HttpResponse response = httpClient.execute(request);
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

    public JSONObject signUpRequest(JSONObject data, String apiUrl) {
        HttpClient httpClient = getHttpClient();
        JSONObject json = new JSONObject();

        try {
            HttpPost request = new HttpPost(url + apiUrl);
            // Add your data
            request.addHeader("content-type", "application/json");

            StringEntity params = new StringEntity(data.toString(), HTTP.UTF_8);
            request.setEntity(params);

            // Execute HTTP Post Request
            HttpResponse response = httpClient.execute(request);
            json = parseData(response);

        } catch (IOException e) {
            json = null;
            // TODO Auto-generated catch block
        } catch (Exception e) {
            e.printStackTrace();
            e.getMessage();
            json = null;
        }
        return json;
    }

    public JSONObject activateRequest(JSONObject data, String apiUrl) {
        HttpClient httpClient = getHttpClient();
        JSONObject json = new JSONObject();

        try {
            HttpPost request = new HttpPost(url + apiUrl);

            request.addHeader("content-type", "application/json");

            StringEntity params = new StringEntity(data.toString(), HTTP.UTF_8);
            request.setEntity(params);

            // Execute HTTP Post Request
            HttpResponse response = httpClient.execute(request);
            json = parseData(response);

        } catch (IOException e) {
            json = null;
            // TODO Auto-generated catch block
        } catch (Exception e) {
            e.printStackTrace();
            e.getMessage();
            json = null;
        }
        return json;
    }

    public JSONObject putRequest(JSONObject data, String apiUrl) {
        HttpClient httpClient = getHttpClient();
        JSONObject json;

        try {
            HttpPut request = new HttpPut(url + apiUrl);
            // Add your data
            request.addHeader("content-type", "application/json");
            //request.addHeader("Authorization", "Token " + this.token);

            StringEntity params = new StringEntity(data.toString(), HTTP.UTF_8);
            request.setEntity(params);

            // Execute HTTP Post Request
            HttpResponse response = httpClient.execute(request);
            json = parseData(response);

        } catch (IOException e) {
            json = null;
            // TODO Auto-generated catch block
        } catch (Exception e) {
            e.printStackTrace();
            e.getMessage();
            json = null;
        }
        return json;
    }

    public JSONObject patchRequest(JSONObject data, String apiUrl) {
        HttpClient httpClient = getHttpClient();
        JSONObject json = new JSONObject();

        try {
            HttpPatch request = new HttpPatch(url + apiUrl);
            // Add your data
            request.addHeader("content-type", "application/json");
            request.addHeader("Authorization", "Token " + this.token);

            StringEntity params = new StringEntity(data.toString(), HTTP.UTF_8);
            request.setEntity(params);

            // Execute HTTP Post Request
            HttpResponse response = httpClient.execute(request);
            json = parseData(response);

        } catch (IOException e) {
            json = null;
            // TODO Auto-generated catch block
        } catch (Exception e) {
            e.printStackTrace();
            e.getMessage();
            json = null;
        }
        return json;
    }

    public JSONObject resetPasswordRequest(String apiUrl) {
        HttpClient httpClient = getHttpClient();
        JSONObject result = new JSONObject();
        try {
            HttpGet request = new HttpGet(url + apiUrl);
            // Add your data
            //request.addHeader("content-type", "application/json");
            //request.setHeader("Authorization", "Token " + this.token);

            HttpResponse response = httpClient.execute(request);
            result = parseData(response);
        } catch (ClientProtocolException e) {
            result = null;
        } catch (IOException e) {
            result = null;
        }
        return result;
    }


    public JSONObject getOrderRequest(String apiUrl) {
        HttpClient httpClient = getHttpClient();
        JSONObject res;

        try {
            HttpGet request = new HttpGet(url + apiUrl);
            // Add your data
            request.addHeader("content-type", "application/json");
            request.setHeader("Authorization", "Token " + this.token);

            // Execute HTTP Post Request
            HttpResponse response = httpClient.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            res = parseData(response, statusCode);
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

    public JSONObject getArrayRequest(String apiUrl) {
        JSONObject result = new JSONObject();
        HttpClient httpClient = getHttpClient();
        try {
            HttpGet request = new HttpGet(url + apiUrl);
            // Add your data
            request.addHeader("content-type", "application/json");
            request.setHeader("Authorization", "Token " + this.token);

            // Execute HTTP Post Request
            HttpResponse response = httpClient.execute(request);
            int statusCode = response.getStatusLine().getStatusCode();
            result.put("status_code", statusCode);

            JSONArray object = parseDataArray(response);
            result.put("result", object);
        } catch (ClientProtocolException e) {
            result = null;
            // TODO Auto-generated catch block
        } catch (IOException e) {
            result = null;
            // TODO Auto-generated catch block
        } catch (JSONException e) {
            result = null;
            e.printStackTrace();
        }
        return result;
    }

    public JSONObject createOrderRequest(JSONObject data, String apiUrl) {
        HttpClient httpClient = getHttpClient();
        JSONObject res;

        try {
            HttpPost request = new HttpPost(url + apiUrl);
            // Add your data
            request.addHeader("content-type", "application/json");
            request.setHeader("Authorization", "Token " + this.token);

            StringEntity entity = new StringEntity(data.toString(), HTTP.UTF_8);
            request.setEntity(entity);

            // Execute HTTP Post Request
            HttpResponse response = httpClient.execute(request);
            res = parseData(response);
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

    protected JSONObject parseData(HttpResponse response) {
        JSONObject result = new JSONObject();
        try {
            result.put("status_code", response.getStatusLine().getStatusCode());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }

            result = new JSONObject(sb.toString());
            result.put("status_code", response.getStatusLine().getStatusCode());
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    protected JSONArray parseDataArray(HttpResponse response) {
        JSONArray result = null;
        try {
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            StringBuilder sb = new StringBuilder();
            String line = "";
            while ((line = rd.readLine()) != null) {
                sb.append(line);
            }
            result = new JSONArray(sb.toString());
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

        return result;
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
