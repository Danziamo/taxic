package taxi.city.citytaxiclient;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.widget.Toast;

import taxi.city.citytaxiclient.core.User;
import taxi.city.citytaxiclient.fragments.CreateOrderActivityFragment;
import taxi.city.citytaxiclient.utils.Helper;


public class CreateOrderActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        User user = User.getInstance();
        if (user == null || user.id == 0) {
            finish();
        }

        setContentView(R.layout.activity_create_order);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment, new CreateOrderActivityFragment())
                    .commit();
        }
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
