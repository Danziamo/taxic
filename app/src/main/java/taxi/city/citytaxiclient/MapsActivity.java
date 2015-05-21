package taxi.city.citytaxiclient;

import android.app.Dialog;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import taxi.city.citytaxiclient.core.Driver;
import taxi.city.citytaxiclient.core.Order;
import taxi.city.citytaxiclient.core.OrderDetail;
import taxi.city.citytaxiclient.core.User;
import taxi.city.citytaxiclient.enums.OStatus;
import taxi.city.citytaxiclient.service.ApiService;
import taxi.city.citytaxiclient.utils.Helper;

public class MapsActivity extends ActionBarActivity  implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, View.OnClickListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private static final int MAKE_ORDER_ID = 1;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    SweetAlertDialog pDialog;

    private Order order;
    private ApiService api;
    private User user;
    private boolean isFirstFetch = true;

    private Location currLocation = null;
    private CheckOrderStatusTask task = null;
    private DeclineOrderTask declineTask = null;

    LinearLayout llMain;
    LinearLayout llOrderStatus;
    LinearLayout llOrderWaitTime;
    LinearLayout llOrderWaitSum;
    LinearLayout llOrderDistance;
    LinearLayout llOrderSum;
    LinearLayout llOrderTotalSum;

    Button btnOk;
    Button btnRefresh;
    Button btnSettings;

    TextView tvAddress;
    TextView tvOrderStatus;
    TextView tvOrderWaitTime;
    TextView tvOrderWaitSum;
    TextView tvOrderDistance;
    TextView tvOrderTravelSum;
    TextView tvOrderTotalSum;

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

        llMain = (LinearLayout) findViewById(R.id.mainLayout);
        llOrderStatus = (LinearLayout) findViewById(R.id.linearLayoutOrderStatus);
        llOrderWaitTime = (LinearLayout) findViewById(R.id.linearLayoutOrderWaitTime);
        llOrderWaitSum = (LinearLayout) findViewById(R.id.linearLayoutOrderWaitSum);
        llOrderDistance = (LinearLayout) findViewById(R.id.linearLayoutOrderDistance);
        llOrderSum = (LinearLayout) findViewById(R.id.linearLayoutOrderTravelSum);
        llOrderTotalSum = (LinearLayout) findViewById(R.id.linearLayoutTotalSum);

        tvAddress = (TextView) findViewById(R.id.textViewAddress);
        tvOrderStatus = (TextView) findViewById(R.id.textViewOrderStatus);
        tvOrderDistance = (TextView) findViewById(R.id.textViewOrderDistance);
        tvOrderTotalSum = (TextView) findViewById(R.id.textViewOrderTotalSum);
        tvOrderTravelSum = (TextView) findViewById(R.id.textViewOrderTravelSum);
        tvOrderWaitTime = (TextView)findViewById(R.id.textViewOrderWaitTime);
        tvOrderWaitSum = (TextView) findViewById(R.id.textViewOrderWaitSum);
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
        SweetAlertDialog pDialog = new SweetAlertDialog(MapsActivity.this, SweetAlertDialog.NORMAL_TYPE);
        pDialog .setTitleText("Активируйте геолокацию")
                .setContentText(order.clientPhone)
                .setConfirmText("ОК")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
                        MapsActivity.this.startActivity(new Intent(action));
                        sDialog.dismissWithAnimation();
                    }
                })
                .setCancelText("Отмена")
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
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
                    cancelOrder();
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

    private void cancelOrder() {

        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.alertdialog_decline_order);

        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.dimAmount = 0.7f;
        wlp.gravity = Gravity.BOTTOM;
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        window.setAttributes(wlp);

        final EditText reason = (EditText) dialog.findViewById(R.id.editTextDeclineReason);
        Button btnOkDialog = (Button) dialog.findViewById(R.id.buttonOkDecline);
        Button btnCancelDialog = (Button) dialog.findViewById(R.id.buttonCancelDecline);
        // if button is clicked, close the custom dialog
        btnOkDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                order.status = OStatus.NEW;
                String reasonText = reason.getText().toString();
                if (reasonText.length() < 6) {
                    reason.setError("Нужно как минимум 6 символов");
                    reason.requestFocus();
                    return;
                }
                DeclineTask(reasonText);
                updateViews();
                dialog.dismiss();
            }
        });

        btnCancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    private void DeclineTask(String reason) {
        if (declineTask != null) {
            declineTask = null;
        }

        showProgress(true);
        declineTask = new DeclineOrderTask(reason);
        declineTask.execute((Void) null);
    }

    private void MakeOrder() {
        Intent intent = new Intent(this, CreateOrderActivity.class);
        startActivityForResult(intent, MAKE_ORDER_ID);
    }

    private void updateViews() {

        tvOrderDistance.setText(String.valueOf(order.distance));
        tvOrderWaitSum.setText(String.valueOf(order.getWaitSum()));
        tvOrderWaitTime.setText(order.waitTime);
        tvOrderStatus.setText(order.getStatusName());
        tvOrderTravelSum.setText(String.valueOf(order.getTravelSum()));
        tvOrderTotalSum.setText(String.valueOf(order.getTotalSum()));

        if (order.status == OStatus.NEW || order.status == OStatus.ACCEPTED || order.status == OStatus.WAITING) {
            llMain.setVisibility(View.VISIBLE);
            llOrderTotalSum.setVisibility(View.GONE);
            llOrderSum.setVisibility(View.GONE);
            llOrderDistance.setVisibility(View.GONE);
            llOrderWaitSum.setVisibility(View.GONE);
            llOrderWaitTime.setVisibility(View.GONE);
            llOrderStatus.setVisibility(View.VISIBLE);
            btnOk.setText("Отмена");
            btnOk.setBackgroundResource(R.drawable.button_shape_red);
            ivIcon.setVisibility(View.GONE);
        } else if (order.status == OStatus.PENDING || order.status == OStatus.ONTHEWAY){
            llMain.setVisibility(View.VISIBLE);
            llOrderTotalSum.setVisibility(View.VISIBLE);
            llOrderSum.setVisibility(View.VISIBLE);
            llOrderDistance.setVisibility(View.VISIBLE);
            llOrderWaitSum.setVisibility(View.VISIBLE);
            llOrderWaitTime.setVisibility(View.VISIBLE);
            llOrderStatus.setVisibility(View.VISIBLE);
            btnOk.setVisibility(View.INVISIBLE);
            ivIcon.setVisibility(View.GONE);
        } else {
            mMap.clear();
            llMain.setVisibility(View.GONE);
            llOrderTotalSum.setVisibility(View.GONE);
            btnOk.setVisibility(View.VISIBLE);
            btnOk.setText("Вызвать");
            btnOk.setBackgroundResource(R.drawable.button_shape_dark_blue);
            ivIcon.setVisibility(View.VISIBLE);
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
                    ivIcon.setVisibility(View.GONE);
                    Toast.makeText(this, "Заказ успешно создан", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Ошибка при создании запроса. Попробуйте ещё раз", Toast.LENGTH_SHORT).show();
                }
            }
        }
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
            JSONObject result = api.getOrderRequest("orders/" + order.id + "/");
            try {
                if (Helper.isSuccess(result) && result.has("driver")) {
                    if (order.driver == null) {
                        JSONObject driver = api.getOrderRequest("users/" + result.getInt("driver") + "/");
                        if (driver != null && driver.getInt("status_code") == HttpStatus.SC_OK && driver.has("phone")) {
                            result.put("driver_phone", driver.getString("phone"));
                            result.put("driver_first_name", driver.getString("first_name"));
                            result.put("driver_last_name", driver.getString("last_name"));
                            String ratingSumString = driver.getJSONObject("rating").getString("votes__sum");
                            double ratingSum = ratingSumString == null || ratingSumString == "null" ? 0 : Double.valueOf(ratingSumString);
                            int ratingCount = driver.getJSONObject("rating").getInt("votes__count");
                            result.put("driver_rating", ratingCount == 0 ? 0 : (int)Math.round(ratingSum/ratingCount));
                            JSONArray driverCar = driver.getJSONArray("cars");
                            if (driverCar.length() > 0) {
                                JSONObject car = driverCar.getJSONObject(0);
                                result.put("driver_brand", car.getJSONObject("brand").getJSONObject("brand_name"));
                                result.put("driver_model", car.getJSONObject("brand_model").getJSONObject("brand_model_name"));
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
                        if (order.status == OStatus.FINISHED && !isFirstFetch) {
                            showOrderDetails();
                            order.clear();
                        }
                        isFirstFetch = false;
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

    private void showOrderDetails() {
        Intent intent = new Intent(MapsActivity.this, OrderDetailsActivity.class);
        intent.putExtra("DATA", new OrderDetail(order));
        startActivity(intent);
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
            mMap.addMarker(new MarkerOptions()
                    .position(position)
                    .title(markerTitle)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.car)));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 15));
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    /*SweetAlertDialog pDialog = new SweetAlertDialog(MapsActivity.this, SweetAlertDialog.WARNING_TYPE);
                    pDialog .setTitleText("Вы хотите позвонить?")
                            .setContentText(order.clientPhone)
                            .setConfirmText("Позвонить")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                                    callIntent.setData(Uri.parse("tel:" + order.clientPhone));
                                    startActivity(callIntent);
                                    sDialog.dismissWithAnimation();
                                }
                            })
                            .setCancelText("Отмена")
                            .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sDialog) {
                                    sDialog.dismissWithAnimation();
                                }
                            })
                            .show();*/
                    Intent intent = new Intent(MapsActivity.this, DriverDetails.class);
                    startActivity(intent);
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

    private class DeclineOrderTask extends AsyncTask<Void, Void, JSONObject> {
        private String mReason;

        DeclineOrderTask(String reason) {
            mReason = reason;
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            JSONObject object = new JSONObject();
            try {
                object.put("status", OStatus.CANCELED.toString());
                object.put("driver", order.driver == null ? JSONObject.NULL : order.driver.id);
                object.put("description", mReason);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return api.patchRequest(object, "orders/" + order.id + "/");
        }

        @Override
        protected void onPostExecute(final JSONObject result) {
            showProgress(false);
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

    public void showProgress(final boolean show) {
        if (show) {
            pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper()
                    .setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Обновление");
            pDialog.setCancelable(true);
            pDialog.show();
        } else {
            if (pDialog != null) pDialog.dismissWithAnimation();
        }
    }
}
