package taxi.city.citytaxiclient;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import taxi.city.citytaxiclient.core.Driver;
import taxi.city.citytaxiclient.core.Order;
import taxi.city.citytaxiclient.core.User;
import taxi.city.citytaxiclient.enums.OStatus;
import taxi.city.citytaxiclient.service.ApiService;
import taxi.city.citytaxiclient.utils.Helper;

public class MapsActivity extends ActionBarActivity  implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, View.OnClickListener {

    private static final String TAG = "MapsActivity";
    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private static final int MAKE_ORDER_ID = 1;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private Order order;
    private ApiService api;
    private User user;

    private Location currLocation = null;
    private CheckOrderStatusTask task = null;
    private DeclineOrderTask declineTask = null;
    private static final String PREFS_NAME = "MyPrefsFile";

    Button btnOk;
    Button btnRefresh;
    Button btnSettings;

    TextView tvAddress;
    TextView tvOrderStatus;
    ImageView ivIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        order = Order.getInstance();
        api = ApiService.getInstance();
        user = User.getInstance();

        if (user == null || user.id == 0)
        {
            Toast.makeText(getApplicationContext(), "Сессия вышла, пожалуйста перезайдите", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
        }

        CheckEnableGPS();

        setGooglePlayServices();
        setUpMapIfNeeded();

        tvAddress = (TextView) findViewById(R.id.textViewAddress);
        tvOrderStatus = (TextView) findViewById(R.id.textViewOrderStatus);
        ivIcon = (ImageView) findViewById(R.id.imageViewSearchIcon);
        ivIcon.setVisibility(View.GONE);
        btnOk = (Button) findViewById(R.id.buttonOk);
        btnOk.setOnClickListener(this);

        btnRefresh = (Button) findViewById(R.id.buttonRefresh);
        btnRefresh.setOnClickListener(this);

        btnSettings = (Button) findViewById(R.id.buttonSettings);
        btnSettings.setOnClickListener(this);

        setLocationRequest();
        CheckPreviousSession();
    }

    private void CheckPreviousSession() {
        if (order.id != 0) {
            if (task != null) {
                return;
            }

            task = new CheckOrderStatusTask();
            task.execute((Void) null);
        } else {
            btnOk.setEnabled(true);
            ivIcon.setVisibility(View.VISIBLE);
        }
    }

    private void CheckEnableGPS(){
        String provider = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if(!provider.equals("")){
            //GPS Enabled
            Toast.makeText(this, "GPS Enabled: " + provider,
                    Toast.LENGTH_LONG).show();
        }else{
            displayPromptForEnablingGPS();
        }
    }

    public void displayPromptForEnablingGPS()
    {

        final AlertDialog.Builder builder =
                new AlertDialog.Builder(MapsActivity.this);
        final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
        final String message = "Активируйте геолокацию.";

        builder.setMessage(message)
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                MapsActivity.this.startActivity(new Intent(action));
                                d.dismiss();
                            }
                        })
                .setNegativeButton("Отмена",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                d.cancel();
                            }
                        });
        builder.create().show();
    }

    private void setLocationRequest() {
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setSmallestDisplacement(10)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1000); // 1 second, in milliseconds
    }

    private void setGooglePlayServices() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        CheckPreviousSession();
        mGoogleApiClient.connect();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                mMap.setMyLocationEnabled(true);
                mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                    public void onCameraChange(CameraPosition arg0) {
                        if (order.id == 0 || order.status == OStatus.FINISHED || order.status == OStatus.CANCELED) {
                            LatLng cameraPosition = new LatLng(arg0.target.latitude, arg0.target.longitude);
                            String displayText = "Поиск адреса...";
                            order.addressStartName = null;
                            if (currLocation != null) {
                                Location cameraLocation = new Location("someprovider");
                                cameraLocation.setLatitude(arg0.target.latitude);
                                cameraLocation.setLongitude(arg0.target.longitude);
                                if (currLocation.distanceTo(cameraLocation) > 30 * 1000) {
                                    order.addressStart = new LatLng(currLocation.getLatitude(), currLocation.getLongitude());
                                } else {
                                    order.addressStart = cameraPosition;
                                }
                            } else
                                order.addressStart = cameraPosition;
                            tvAddress.setText(displayText);
                            LocationAddress.getAddressFromLocation(arg0.target.latitude, arg0.target.longitude,
                                    getApplicationContext(), new GeocoderHandler());
                        } else {
                            tvAddress.setText(null);
                        }
                    }
                });
            }
        }
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            Bundle bundle = message.getData();
            locationAddress = bundle.getString("address");
            switch (message.what) {
                case 1:
                    order.addressStartName = locationAddress;
                    break;
                default:
                    order.addressStartName = null;
            }
            tvAddress.setText(locationAddress);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        CheckPreviousSession();
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        // Connected to Google Play services!
        // The good stuff goes here.
        Log.i(TAG, "Location services connected.");
        startLocationUpdates();

        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location != null) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
            handleNewLocation(location);
        }
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (result.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                result.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + result.getErrorCode());
        }
    }

    private void handleNewLocation(Location location) {
        if (location != null) {
            currLocation = location;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonOk:
                if (order.status != OStatus.NEW && order.status != OStatus.WAITING && order.status != OStatus.ACCEPTED) {
                    MakeOrder();
                } else {
                    DeclineOrder();
                }
                break;
            case R.id.buttonSettings:
                goToSettings();
                break;
            case R.id.buttonRefresh:
                CheckPreviousSession();
                break;
            default:
                break;
        }
    }

    private void goToSettings() {
        Intent intent = new Intent(this, AccountActivity.class);
        startActivity(intent);
    }

    private void DeclineOrder() {
        final AlertDialog.Builder builder =
                new AlertDialog.Builder(MapsActivity.this);
        //final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
        final String title = "Напишите причину отказа";
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setTitle(title)
                .setView(input)
                .setPositiveButton("ОК",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {

                            }
                        })
                .setNegativeButton("Отмена",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                d.cancel();
                            }
                        });
        //builder.create().show();
        final AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = input.getText().toString();
                if (text.trim().length() < 6) {
                    input.setError("Должно быть 6 символов");
                } else {
                    mMap.clear();
                    DeclineTask(text);
                    dialog.dismiss();
                }
            }

        });
    }

    private void DeclineTask(String reason) {
        if (declineTask != null) {
            declineTask = null;
        }

        declineTask = new DeclineOrderTask(reason);
        declineTask.execute((Void) null);
    }

    private void MakeOrder() {
        Intent intent = new Intent(this, CreateOrderActivity.class);
        startActivityForResult(intent, MAKE_ORDER_ID);
    }

    private void updateViews() {
        if (order.status == null) {
            tvOrderStatus.setText(null);
        }
        else if (order.status == OStatus.NEW || order.status == OStatus.ACCEPTED
                || order.status == OStatus.CANCELED)
            tvOrderStatus.setText("Статус: " + order.id + " - " + order.status);
        else
            tvOrderStatus.setText("Статус: " + order.id + " - " + order.status +
                    "\nВремя ождания: " + order.waitTime +
                    "\nСумма ожидания: " + order.waitSum +
                    "\nСумма поездки: " + String.valueOf(order.sum - order.waitSum) +
                    "\nВремя поездки: " + order.time +
                    "\nОбщая сумма:" + String.valueOf(order.sum) +
                    "\nПуть: "+ order.distance);

        if (order.status == OStatus.NEW || order.status == OStatus.ACCEPTED || order.status == OStatus.WAITING) {
            btnOk.setBackgroundResource(R.drawable.button_shape_red);
            btnOk.setText("Отмена");
            btnOk.setVisibility(View.VISIBLE);
        } else if (order.status == OStatus.PENDING || order.status == OStatus.ONTHEWAY) {
            btnOk.setVisibility(View.INVISIBLE);
        } else {
            btnOk.setBackgroundResource(R.drawable.button_shape_azure);
            btnOk.setText("Заказать");
            btnOk.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MAKE_ORDER_ID) {
            if (data != null && data.getExtras() != null) {
                int code = Integer.valueOf(data.getExtras().getString("MESSAGE"));

                if (code == 1) {
                    updateViews();
                    savePreferencesOrder(order);
                    ivIcon.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "Заказ успешно создан", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Ошибка при создании запроса. Попробуйте ещё раз", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private void savePreferencesOrder(Order mOrder) {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("orderIdKey", String.valueOf(mOrder.id));
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory( Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    private class CheckOrderStatusTask extends AsyncTask<Void, Void, JSONObject> {

        CheckOrderStatusTask() {}

        @Override
        protected JSONObject doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            // Simulate network access.
            JSONObject result = api.getOrderRequest("orders/" + order.id + "/");
            try {
                if (result != null && result.getInt("status_code") == HttpStatus.SC_OK && result.has("driver")) {
                    JSONObject driver = api.getOrderRequest("users/" + result.getInt("driver") + "/");
                    if (driver != null && driver.getInt("status_code") == HttpStatus.SC_OK && driver.has("phone")) {
                        JSONObject driverCar = api.getArrayRequest("usercars/driver="+result.getString("driver"));
                        result.put("driver_phone", driver.getString("phone"));
                        result.put("driver_first_name", driver.getString("first_name"));
                        result.put("driver_last_name", driver.getString("last_name"));
                        if (driverCar != null && Helper.isSuccess(driverCar) && driverCar.has("result")) {
                            JSONArray array = driverCar.getJSONArray("result");
                            if (array.length() > 0) {
                                JSONObject car = array.getJSONObject(0);
                                result.put("driver_brand", car.getString("brand"));
                                result.put("driver_model", car.getString("brand_model"));
                                result.put("driver_number", car.getString("car_number"));
                                result.put("driver_color", car.getString("color"));
                            }
                        }
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(final JSONObject result) {
            task = null;
            int statusCode;
            String status;
            if (result != null) {
                try {
                    statusCode = result.getInt("status_code");
                    if (statusCode == HttpStatus.SC_OK) {
                        order.status = Helper.getStatus(result.getString("status"));
                        order.id = result.getInt("id");
                        order.sum = result.getDouble("order_sum");
                        order.distance = result.getDouble("order_distance");
                        order.waitTime = result.getString("wait_time");
                        order.time = result.getString("order_travel_time");
                        order.waitSum = getFormattedDouble(result.getString("wait_time_price"));
                        if (result.has("driver_phone")) {
                            order.driverPhone = result.getString("driver_phone");
                            order.driver = new Driver(result);
                        }
                        displayDriverOnMap(stringToLatLng(result.getString("address_stop")));
                        updateViews();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(getApplicationContext(), "Ошибка при попытке подключения к серверу", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            task = null;
        }
    }

    private double getFormattedDouble(String s) {
        double r = 0;
        try {
            r = Double.valueOf(s);
        } catch (Exception e) {

        }
        return r;
    }

    private void displayDriverOnMap(LatLng position) {
        if (order.status == OStatus.FINISHED) {
            mMap.clear();
            return;
        }
        if (position == null)  {
            return;
        }

        if (order.status == OStatus.ACCEPTED || order.status == OStatus.WAITING || order.status == OStatus.ONTHEWAY) {
            mMap.clear();
            String markerTitle = order.driverPhone == null ? "Ваш водитель" : order.driverPhone;
            mMap.addMarker(new MarkerOptions().position(position).title(markerTitle));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    final AlertDialog.Builder builder =
                            new AlertDialog.Builder(MapsActivity.this);
                    //final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
                    final String message = "Вы уверены что хотите позвонить?";
                    final String title = order.clientPhone;

                    builder.setMessage(message)
                            .setTitle(title)
                            .setPositiveButton("Позвонить",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface d, int id) {
                                            Intent callIntent = new Intent(Intent.ACTION_CALL);
                                            callIntent.setData(Uri.parse("tel:" + order.clientPhone));
                                            startActivity(callIntent);
                                            d.dismiss();
                                        }
                                    })
                            .setNegativeButton("Отмена",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface d, int id) {
                                            d.cancel();
                                        }
                                    });
                    builder.create().show();
                }
            });
        } else {
            mMap.clear();
        }
    }

    private LatLng stringToLatLng(String s) {
        if (s == null || s.equals("null"))
            return null;
        String[] geo = s.replace("(", "").replace(")", "").split(" ");

        double latitude = Double.valueOf(geo[1].trim());
        double longitude = Double.valueOf(geo[2].trim());
        return new LatLng(latitude, longitude);
    }

    private void setStatus(String s) {
        switch (s) {
            case "new":
                order.status = OStatus.NEW;
                break;
            case "accepted":
                order.status = OStatus.ACCEPTED;
                break;
            case "sos":
                order.status = OStatus.SOS;
                break;
            case "canceled":
                order.status = OStatus.CANCELED;
                break;
            case "finished":
                order.status = OStatus.FINISHED;
                break;
            case "ontheway":
                order.status = OStatus.ONTHEWAY;
                break;
            case "waiting":
                order.status = OStatus.WAITING;
                break;
            case "pending":
                order.status = OStatus.PENDING;
                break;
        }
    }

    private class DeclineOrderTask extends AsyncTask<Void, Void, JSONObject> {
        private String mReason;

        DeclineOrderTask(String reason) {
            mReason = reason;
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            // Simulate network access.
            JSONObject object = new JSONObject();
            try {
                object.put("status", OStatus.CANCELED.toString());
                object.put("driver", order.driver);
                object.put("description", mReason);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return api.patchRequest(object, "orders/" + order.id + "/");
        }

        @Override
        protected void onPostExecute(final JSONObject result) {
            declineTask = null;
            int statusCode;
            if (result != null) {
                try {
                    statusCode = result.getInt("status_code");
                    if (statusCode == HttpStatus.SC_OK) {
                        order.clear();
                        updateViews();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                //Toast.makeText(this, "Ошибка при попытке подключения к серверу", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            declineTask = null;
        }
    }
}
