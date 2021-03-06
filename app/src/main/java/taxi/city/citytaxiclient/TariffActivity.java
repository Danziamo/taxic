package taxi.city.citytaxiclient;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;

import taxi.city.citytaxiclient.core.User;
import taxi.city.citytaxiclient.utils.Helper;

public class TariffActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        User user = User.getInstance();
        if (user == null || user.id == 0)
        {
            Helper.getPreferences(this);
            if (user == null || user.id == 0) {
                Toast.makeText(getApplicationContext(), "Сессия вышла, пожалуйста перезайдите", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, LoginActivityOld.class);
                startActivity(intent);
                finish();
            }
        }

        setContentView(R.layout.activity_tariff);

        App.getDefaultTracker().send(new HitBuilders.EventBuilder()
                .setCategory("ui_views")
                .setLabel("open_about")
                .build());

        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowTitleEnabled(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
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
}
