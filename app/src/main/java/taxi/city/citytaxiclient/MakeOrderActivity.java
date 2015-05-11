package taxi.city.citytaxiclient;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import taxi.city.citytaxiclient.core.Order;
import taxi.city.citytaxiclient.core.User;
import taxi.city.citytaxiclient.enums.OStatus;
import taxi.city.citytaxiclient.service.ApiService;


public class MakeOrderActivity extends ActionBarActivity implements View.OnClickListener {

    private static final int MAKE_ORDER_ID = 1;
    private MakeOrderTask orderTask = null;
    private Order order;
    private ApiService api;
    private User user;
    private Button btnMakeOrder;
    private Button btnFixedPrice;
    EditText etAddress;
    EditText etPhone;
    EditText etDescription;
    EditText etFixedPrice;
    EditText etAddressStop;

    TextView tvFixedPrice;
    TextView tvAddressStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_order);

        order = Order.getInstance();
        api = ApiService.getInstance();
        user = User.getInstance();
        btnMakeOrder = (Button)findViewById(R.id.buttonMakeOrder);
        btnFixedPrice = (Button)findViewById(R.id.buttonFixedPrice);
        etAddress = (EditText) findViewById(R.id.editTextAddress);
        etPhone = (EditText) findViewById(R.id.editTextClientPhone);
        etDescription = (EditText) findViewById(R.id.editTextDescription);

        tvFixedPrice = (TextView) findViewById(R.id.textViewFixedPrice);
        tvAddressStop = (TextView) findViewById(R.id.textViewStopAddress);
        etFixedPrice = (EditText) findViewById(R.id.editTextFixedPrice);
        etAddressStop = (EditText) findViewById(R.id.editTextStopAddress);

        etAddress.setText(order.addressStartName);
        etPhone.setText(User.getInstance().phone);

        btnMakeOrder.setOnClickListener(this);
        btnFixedPrice.setOnClickListener(this);
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

        order.clear();
        order.status = OStatus.NEW;
        order.clientPhone = etPhone.getText().toString();
        order.addressStartName = etAddress.getText().toString();
        order.description = etDescription.getText().toString();
        order.clientId = user.id;
        order.fixedPrice = 0;
        order.addressStopName = null;

        if (etFixedPrice.getVisibility() == View.VISIBLE) {
            order.addressStopName = etAddressStop.getText().toString();
            try {
                int price = Integer.parseInt(etFixedPrice.getText().toString());
                if (price < 50) {
                    throw new NumberFormatException("Сумма должна быть не меньше 50 сомов");
                }
                order.fixedPrice = price;
            } catch (NumberFormatException e) {
                etFixedPrice.setError("Фиксированная сумма неправильно указана." + e.getMessage());
                btnMakeOrder.setEnabled(true);
                return;
            }

            if (order.addressStopName == null || order.addressStopName.length() < 2) {
                etAddressStop.setError("Укажите куда собираетесь отправиться");
                btnMakeOrder.setEnabled(true);
                return;
            }
        }

        orderTask = new MakeOrderTask();
        orderTask.execute((Void) null);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.buttonMakeOrder:
                btnMakeOrder.setEnabled(false);
                makeOrderRequest();
                break;
            case R.id.buttonFixedPrice:
                if (tvAddressStop.getVisibility() != View.VISIBLE) {
                    tvAddressStop.setVisibility(View.VISIBLE);
                    tvFixedPrice.setVisibility(View.VISIBLE);
                    etFixedPrice.setVisibility(View.VISIBLE);
                    etAddressStop.setVisibility(View.VISIBLE);
                } else {
                    tvAddressStop.setVisibility(View.GONE);
                    tvFixedPrice.setVisibility(View.GONE);
                    etFixedPrice.setVisibility(View.GONE);
                    etAddressStop.setVisibility(View.GONE);
                }
                break;
        }
    }

    public class MakeOrderTask extends AsyncTask<Void, Void, JSONObject> {
        MakeOrderTask() {}

        @Override
        protected JSONObject doInBackground(Void... params) {

            JSONObject data = new JSONObject();
            try {
                data = order.getOrderAsJson();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return api.createOrderRequest(data, "orders/");
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            try {
                int statusCode = result.getInt("status_code");
                order.id = result.getInt("id");
                orderTask = null;
                if (statusCode == HttpStatus.SC_OK || statusCode == HttpStatus.SC_CREATED || statusCode == HttpStatus.SC_ACCEPTED) {
                    Intent intent = new Intent();
                    intent.putExtra("MESSAGE", "1");
                    setResult(MAKE_ORDER_ID, intent);
                    btnMakeOrder.setEnabled(true);
                    finish();

                } else {
                    Intent intent = new Intent();
                    intent.putExtra("MESSAGE", "2");
                    setResult(MAKE_ORDER_ID, intent);
                    btnMakeOrder.setEnabled(true);
                    finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Упс. внутренняя ошибка", Toast.LENGTH_LONG).show();
                btnMakeOrder.setEnabled(true);
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
