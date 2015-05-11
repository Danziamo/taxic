package taxi.city.citytaxiclient;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;

import taxi.city.citytaxiclient.fragments.CreateOrderActivityFragment;


public class CreateOrderActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_order);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment, new CreateOrderActivityFragment())
                    .commit();
        }
    }
}
