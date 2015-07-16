package taxi.city.citytaxiclient;

import android.app.Application;
import android.content.Context;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;

import io.fabric.sdk.android.Fabric;

public class App extends Application {
    private static Context mContext;
    public static GoogleAnalytics analytics;
    public static Tracker tracker;



    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());

        analytics = GoogleAnalytics.getInstance(this);
        //analytics.setLocalDispatchPeriod(1800);

        tracker = analytics.newTracker("UA-64277044-5"); // Replace with actual tracker/property Id
        tracker.enableExceptionReporting(true);
        tracker.enableAdvertisingIdCollection(true);
        tracker.enableAutoActivityTracking(true);

        mContext = this;
    }

    public static Tracker getDefaultTracker() {
        return tracker;
    }


    public static Context getContext() {
        return mContext;
    }
}
