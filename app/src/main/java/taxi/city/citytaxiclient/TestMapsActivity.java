package taxi.city.citytaxiclient;

import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.IntentCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.IOException;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import taxi.city.citytaxiclient.fragments.AccountDetailsActivityFragment;
import taxi.city.citytaxiclient.fragments.GarajActivityFragment;
import taxi.city.citytaxiclient.fragments.HistoryOrderFragment;
import taxi.city.citytaxiclient.fragments.LoginFragment;
import taxi.city.citytaxiclient.fragments.MapsFragment;
import taxi.city.citytaxiclient.interfaces.ConfirmCallback;
import taxi.city.citytaxiclient.models.GlobalSingleton;
import taxi.city.citytaxiclient.models.OnlineStatus;
import taxi.city.citytaxiclient.models.User;
import taxi.city.citytaxiclient.networking.RestClient;
import taxi.city.citytaxiclient.utils.Constants;
import taxi.city.citytaxiclient.utils.SessionHelper;

public class TestMapsActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final long DRAWER_CLOSE_DELAY_MS = 250;
    private static final String NAV_ITEM_ID = "navItemId";
    NavigationView navigationView;
    private final Handler mDrawerActionHandler = new Handler();
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private int mNavItemId;
    private User user;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    String mRegId;
    GoogleCloudMessaging gcm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_maps);

        user = GlobalSingleton.getInstance(TestMapsActivity.this).currentUser;

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.container, new MapsFragment())
                .commit();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(this);

        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {

            @Override
            public void onBackStackChanged() {
                Fragment f = getSupportFragmentManager().findFragmentById(R.id.container);
                if (f != null) {
                    updateTitleAndDrawer(f);
                }

            }
        });

        if (null == savedInstanceState) {
            mNavItemId = R.id.maps;
        } else {
            mNavItemId = savedInstanceState.getInt(NAV_ITEM_ID);
        }

        navigationView.getMenu().findItem(mNavItemId).setChecked(true);

        // set up the hamburger icon to open and close the menu_drawer
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.icon_history_text,
                R.string.icon_orders_text);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        navigate(mNavItemId);

        checkEnableGPS();
        setGooglePlayServices();
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            registerInBackground();
        }
        setLocationRequest();
    }

    private void checkEnableGPS(){
        String provider = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if(provider.equals("")){
            displayPromptForEnablingGPS();
        }
    }

    public void displayPromptForEnablingGPS()
    {
        SweetAlertDialog pDialog = new SweetAlertDialog(TestMapsActivity.this, SweetAlertDialog.NORMAL_TYPE);
        pDialog .setTitleText("Активируйте геолокацию")
                .setConfirmText("ОК")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
                        startActivity(new Intent(action));
                        sDialog.dismissWithAnimation();
                    }
                })
                .setCancelText("Отмена")
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        //mMap.animateCamera(CameraUpdateFactory.zoomTo(Integer.parseInt(MapsActivity.this.getString(R.string.map_default_zoom))));
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
    }

    private void setLocationRequest() {
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_LOW_POWER)
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

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        Constants.CONNECTION_FAILURE_RESOLUTION_REQUEST).show();
            } else {
                finish();
            }
            return false;
        }
        return true;
    }

    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }
                    mRegId = gcm.register(Constants.GCM_SENDER_ID);
                    Object object = RestClient.getUserService().updateToken(user.getId(), mRegId);
                } catch (IOException ex) {
                    msg = "err";
                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {}
        }.execute(null, null, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_test_maps, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void navigate(final int itemId) {
        // perform the actual navigation logic, updating the main content fragment etc
        switch (itemId) {
            case R.id.history:
                String backStateName = getSupportFragmentManager().getClass().getName();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new HistoryOrderFragment())
                        .addToBackStack(backStateName)
                        .commit();
                break;
            case R.id.tech_support:
                performState(HelpActivity.class);
                break;
            case R.id.tariff_info:
                performState(TariffActivity.class);
                break;
            case R.id.cabinet:
                String backName = getSupportFragmentManager().getClass().getName();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.container, new AccountDetailsActivityFragment())
                        .addToBackStack((backName))
                        .commit();
                break;
            case R.id.share:
                shareLink();break;
            case R.id.exit:
                showConfirmDialog(getString(R.string.logout_confirm_title), getString(R.string.logout_confirm_text), getString(R.string.logout_cancel_text), new ConfirmCallback() {
                    @Override
                    public void confirm() {
                        logout();
                    }

                    @Override
                    public void cancel() {

                    }
                });
                break;
            default:
        }
    }

    private void performState(Class<?> activity){
        Intent intent = new Intent(TestMapsActivity.this, activity);
        startActivity(intent);
    }

    private void shareLink(){
        String text = "Я пользуюсь Easy Taxi. Это удобное приложение для мгновенного вызова такси. \nhttp://onelink.to/r94mvp \nEasy Taxi\nНам с тобой по пути!";
        Intent intent = new Intent(Intent.ACTION_SEND);

        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
//        intent.putExtra(Intent.EXTRA_SUBJECT, "Я пользуюсь мобильным приложением Easy Taxi.");

        startActivity(Intent.createChooser(intent, "Поделиться"));
    }


    @Override
    public boolean onNavigationItemSelected(final MenuItem menuItem) {
        // update highlighted item in the navigation menu
        menuItem.setChecked(true);
        mNavItemId = menuItem.getItemId();

        // allow some time after closing the menu_drawer before performing real navigation
        // so the user can see what is happening
        mDrawerLayout.closeDrawer(GravityCompat.START);
        mDrawerActionHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                navigate(menuItem.getItemId());
            }
        }, DRAWER_CLOSE_DELAY_MS);
        return true;
    }

    @Override
    protected void onSaveInstanceState(final Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(NAV_ITEM_ID, mNavItemId);
    }

    public void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mNavItemId = savedInstanceState.getInt(NAV_ITEM_ID);
    }

    @Override
    protected void onStart() {
        super.onStart();
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
    protected void onResume() {
        super.onResume();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        startLocationUpdates();

        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (location != null) {
                //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15));
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
        //handleNewLocation(location);
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (result.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                result.startResolutionForResult(this, Constants.CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {

        }
    }

    private void handleNewLocation(Location location) {
        if (getSupportFragmentManager().findFragmentById(R.id.container) instanceof MapsFragment) {
            MapsFragment fragment = (MapsFragment) getSupportFragmentManager().findFragmentById(R.id.container);
            fragment.showOnMap(location);
        }
    }


    private void logout() {
        showProgress(getString(R.string.action_exit));

        RestClient.getUserService().updateStatus(user.getId(), OnlineStatus.EXITED, new Callback<Object>() {
            @Override
            public void success(Object o, Response response) {
                hideProgress();
                SessionHelper sessionHelper = new SessionHelper();
                sessionHelper.setPassword("");
                sessionHelper.setToken("");

                Intent intent = new Intent(TestMapsActivity.this, MainSplashActivity.class);
                ComponentName cn = intent.getComponent();
                Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);
                startActivity(mainIntent);
            }

            @Override
            public void failure(RetrofitError error) {
                hideProgress();
                SessionHelper sessionHelper = new SessionHelper();
                sessionHelper.setPassword("");
                sessionHelper.setToken("");

                Intent intent = new Intent(TestMapsActivity.this, MainSplashActivity.class);
                ComponentName cn = intent.getComponent();
                Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);
                startActivity(mainIntent);
            }
        });
    }

    private void updateTitleAndDrawer (Fragment fragment){
        String fragClassName = fragment.getClass().getName();

        if (fragClassName.equals(HistoryOrderFragment.class.getName())){
            setTitle ("История");
            //set selected item position, etc
        }
        else if (fragClassName.equals(AccountDetailsActivityFragment.class.getName())){
            setTitle ("Счёт");
            //set selected item position, etc
        }
        else if (fragClassName.equals(AccountDetailsActivityFragment.class.getName())){
            setTitle ("Счёт");
            //set selected item position, etc
        }
    }

}
