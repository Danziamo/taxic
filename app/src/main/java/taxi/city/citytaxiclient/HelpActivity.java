package taxi.city.citytaxiclient;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;

import taxi.city.citytaxiclient.core.User;
import taxi.city.citytaxiclient.utils.Helper;

public class HelpActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        User user = User.getInstance();
        if (user == null || user.id == 0)
        {
            Helper.getPreferences(this);
            if (user == null || user.id == 0) {
                Toast.makeText(getApplicationContext(), "Сессия вышла, пожалуйста перезайдите", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }

        setContentView(R.layout.activity_help);

        App.getDefaultTracker().send(new HitBuilders.EventBuilder()
                .setCategory("ui_views")
                .setLabel("open_helper")
                .build());

        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowTitleEnabled(true);

        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            TextView info_version = (TextView) findViewById(R.id.info_version);
            String info_version_text = getString(R.string.info_version, pInfo.versionName);
            info_version.setText(info_version_text);
        }catch(Exception e){
            //silent
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }
}
