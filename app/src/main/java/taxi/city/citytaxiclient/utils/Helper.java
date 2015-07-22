package taxi.city.citytaxiclient.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpStatus;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import taxi.city.citytaxiclient.core.Order;
import taxi.city.citytaxiclient.core.User;
import taxi.city.citytaxiclient.enums.OStatus;
import taxi.city.citytaxiclient.service.ApiService;

/**
 * Created by Daniyar on 4/16/2015.
 */
public class Helper {
    private static String regexPattern = "\\d+\\.?\\d*";
    private static DecimalFormat df = new DecimalFormat("#.##");

    /**
     * Returns string representation of given LatLng to save or send to remote server
     */
    public static String getFormattedLatLng(LatLng location) {
        if (location != null) {
            return "POINT (" + location.latitude + " " + location.longitude + ")";
        }
        return null;
    }

    /**
     * Returns LatLng representation from string
     */
    public static LatLng getLatLng(String s) {
        if (s == null || s.equals("null"))
            return null;
        List<String> geo = new ArrayList<>();
        Matcher m = Pattern.compile(regexPattern).matcher(s);
        while(m.find()) {
            geo.add(m.group());
        }
        if (geo.size() != 2)
            return null;
        double latitude = Double.valueOf(geo.get(0).trim());
        double longitude = Double.valueOf(geo.get(1).trim());
        return new LatLng(latitude, longitude);
    }

    /**
     * Return proper Enum from string
     */
    public static OStatus getStatus(String s) {
        if (s.equals(OStatus.NEW.toString())) return OStatus.NEW;
        if (s.equals(OStatus.ACCEPTED.toString())) return OStatus.ACCEPTED;
        if (s.equals(OStatus.PENDING.toString())) return OStatus.PENDING;
        if (s.equals(OStatus.CANCELED.toString())) return OStatus.CANCELED;
        if (s.equals(OStatus.FINISHED.toString())) return OStatus.FINISHED;
        if (s.equals(OStatus.ONTHEWAY.toString())) return OStatus.ONTHEWAY;
        if (s.equals(OStatus.SOS.toString())) return OStatus.SOS;
        if (s.equals(OStatus.WAITING.toString())) return OStatus.WAITING;
        return null;
    }

    /**
     * Returns formatted time value from long as string "hh:mm:ss"
     */
    public static String getTimeFromLong(long seconds) {
        int hr = (int)seconds/3600;
        int rem = (int)seconds%3600;
        int mn = rem/60;
        int sec = rem%60;
        String hrStr = (hr<10 ? "0" : "")+hr;
        String mnStr = (mn<10 ? "0" : "")+mn;
        String secStr = (sec<10 ? "0" : "")+sec;
        return String.format("%s:%s:%s", hrStr, mnStr, secStr);
    }

    public static String getTimeFromLong(long seconds, OStatus status) {
        if (status == OStatus.NEW || status == OStatus.ACCEPTED || status == OStatus.WAITING) {
            return "00:00:00";
        }
        return getTimeFromLong(seconds);
    }

    /**
     * Returning formatted string representation of distance like #.##
     */
    public static String getFormattedDistance(double distance) {
        return df.format(distance);
    }

    /**
     * Getting long representation of time from string (from hh:mm:ss to seconds)
     */
    public static Long getLongFromString(String s) {
        long res = 0;
        try {
            String[] list = s.split(":");
            res += 60*60*Integer.valueOf(list[0]) + 60*Integer.valueOf(list[1]) +Integer.valueOf(list[2]);
        } catch (Exception e) {
            res = 0;
        }
        return res;

    }

    public double getDoubleFromObject(String s) {
        double res;
        try {
            res = Double.valueOf(s);
        } catch (Exception e) {
            res = 0;
        }
        return res;
    }

    public static boolean isSuccess(int status) {
        if (status == HttpStatus.SC_OK) return true;
        if (status == HttpStatus.SC_ACCEPTED) return true;
        if (status == HttpStatus.SC_CREATED) return true;
        return false;
    }

    public static boolean isSuccess (JSONObject object) throws JSONException {
        if (object == null) return false;
        if (!object.has("status_code")) return false;
        if (!isSuccess(object.getInt("status_code"))) return false;
        return true;
    }

    public static boolean isBadRequest(int status) {
        if (status == HttpStatus.SC_FORBIDDEN) return true;
        if (status == HttpStatus.SC_NOT_FOUND) return true;
        if (status == HttpStatus.SC_NOT_ACCEPTABLE) return true;
        if (status == HttpStatus.SC_METHOD_NOT_ALLOWED) return true;
        if (status == HttpStatus.SC_UNAUTHORIZED) return true;
        if (status == HttpStatus.SC_BAD_REQUEST) return true;
        return false;
    }

    public static boolean isBadRequest(JSONObject object) throws  JSONException {
        if (object == null) return false;
        if (!object.has("status_code")) return false;
        if (!isBadRequest(object.getInt("status_code"))) return false;
        return true;
    }

    public static double getDouble(String number) {
        double result = 0;
        try {
            result = Double.valueOf(number);
        } catch (Exception e) {
            result = 0;
        }
        return result;
    }

    public static String getStringFromJson(JSONObject object, String key) throws JSONException{
        if (!object.has(key)) return null;
        String value = object.getString(key);
        if (value == null || value.equals("null")) return null;
        return value;
    }

    public static boolean isOrderActive(Order order) {
        if (order == null || order.id == 0) return false;
        OStatus status = order.status;
        if (status == null) return false;
        if (status == OStatus.NEW) return false;
        if (status == OStatus.FINISHED) return false;
        if (status == OStatus.CANCELED) return false;
        return true;
    }

    public static boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    public static boolean isYearValid(String s) {
        int year = 0;
        try {
            year = Integer.valueOf(s);
        } catch (Exception e) {
            return false;
        }
        return !(year < 1900 || year > Calendar.getInstance().get(Calendar.YEAR));
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public static String getRatingText(float rating) {
        if (rating < 1 ) return null;
        if (rating == 1) return "1 звезда";
        if (rating == 5) return "5 звезд";
        return String.valueOf(rating) + " звезды";
    }

    public static void getPreferences(Context context) {
        final String PREFS_NAME = "MyPrefsFile";
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        if (settings.contains("phoneKey")) {
            User.getInstance().phone = settings.getString("phoneKey", "");
        }
        if (settings.contains("passwordKey")) {
            User.getInstance().password = settings.getString("passwordKey", "");
        }
        if (settings.contains("idKey")) {
            User.getInstance().id = settings.getInt("idKey", 0);
        }
        if (settings.contains("tokenKey")) {
            User.getInstance().token = settings.getString("tokenKey", null);
            ApiService.getInstance().setToken(User.getInstance().token);
        }
        if (settings.contains("orderIdKey")) {
            Order.getInstance().id = settings.getInt("orderIdKey", 0);
        }
    }

    public static void saveOrderPreferences(Context context, int id) {
        final String PREFS_NAME = "MyPrefsFile";
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt("orderIdKey", id);
        editor.apply();
    }

    public static void removeOrderPreferences(Context context) {
        final String PREFS_NAME = "MyPrefsFile";
        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.remove("orderIdKey");
        editor.apply();
    }
}
