package taxi.city.citytaxiclient;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import taxi.city.citytaxiclient.Core.Order;
import taxi.city.citytaxiclient.Core.User;
import taxi.city.citytaxiclient.Enums.OrderStatus;
import taxi.city.citytaxiclient.Service.ApiService;


public class MakeOrderActivity extends ActionBarActivity implements View.OnClickListener {

    private static final int MAKE_ORDER_ID = 1;
    private MakeOrderTask orderTask = null;
    private Order order = Order.getInstance();
    private ApiService api = ApiService.getInstance();
    private Button btn;
    EditText etAddress;
    EditText etPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_order);

        btn = (Button)findViewById(R.id.buttonMakeOrder);
        etAddress = (EditText) findViewById(R.id.editTextAddress);
        etPhone = (EditText) findViewById(R.id.editTextClientPhone);

        etAddress.setText(order.description);
        etPhone.setText(User.getInstance().phone);

        btn.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_make_order, menu);
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

    private void makeOrderRequest() {
        if (orderTask != null) {
            return;
        }

        String phone = etPhone.getText().toString();
        if (phone.length() != 13) {
            Toast.makeText(getApplicationContext(), "Телефон в неверном формате", Toast.LENGTH_LONG).show();
            return;
        }

        orderTask = new MakeOrderTask();
        orderTask.execute((Void) null);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.buttonMakeOrder:
                makeOrderRequest();
        }

    }

    public class MakeOrderTask extends AsyncTask<Void, Void, Integer> {

        MakeOrderTask() {}

        @Override
        protected Integer doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.
            // Simulate network access.

            order.status = OrderStatus.STATUS.NEW;
            order.clientPhone = etPhone.getText().toString();
            order.description = etAddress.getText().toString();
            JSONObject data = new JSONObject();
            try {
                data = order.getOrderAsJson();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return api.createOrderRequest(data, "orders/");
        }

        @Override
        protected void onPostExecute(Integer statusCode) {
            orderTask = null;
            if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED || statusCode == HttpStatus.SC_ACCEPTED)
            {
                Intent intent=new Intent();
                intent.putExtra("MESSAGE", "Заказ успешно создан");
                setResult(MAKE_ORDER_ID, intent);
                finish();

            } else {
                Intent intent=new Intent();
                intent.putExtra("MESSAGE", "Ошибка при создании запроса. Попробуйте ещё раз");
                setResult(MAKE_ORDER_ID, intent);
                finish();
            }
        }

        @Override
        protected void onCancelled() {
            orderTask = null;
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
