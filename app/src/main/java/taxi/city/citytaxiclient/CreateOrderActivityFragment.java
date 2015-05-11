package taxi.city.citytaxiclient;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import taxi.city.citytaxiclient.Core.Order;
import taxi.city.citytaxiclient.Core.User;
import taxi.city.citytaxiclient.Enums.OStatus;
import taxi.city.citytaxiclient.Service.ApiService;


/**
 * A placeholder fragment containing a simple view.
 */
public class CreateOrderActivityFragment extends Fragment implements View.OnClickListener {

    private boolean isFixed = false;
    Button btnMake;
    Button btnCancel;
    Button btnFixed;
    Button btnCounter;
    LinearLayout llFixedPrice;

    EditText etStopAddress;
    EditText etFixedPrice;
    EditText etDescription;
    EditText etStartAddress;
    EditText etPhone;

    private MakeOrderTask mTask = null;
    Order order;
    User user;
    ApiService api;


    public CreateOrderActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create_order, container, false);
        isFixed = false;
        order = Order.getInstance();
        user = User.getInstance();
        api = ApiService.getInstance();
        btnFixed = (Button) rootView.findViewById(R.id.buttonByFixed);
        btnCounter = (Button) rootView.findViewById(R.id.buttonByCounter);
        btnMake = (Button) rootView.findViewById(R.id.buttonOk);
        btnCancel = (Button) rootView.findViewById(R.id.buttonCancel);
        llFixedPrice = (LinearLayout) rootView.findViewById(R.id.linearLayoutFixedPrice);

        etStartAddress = (EditText) rootView.findViewById(R.id.editTextStartAddress);
        etStartAddress.setText(order.addressStartName);

        etPhone = (EditText) rootView.findViewById(R.id.editTextClientPhone);
        etPhone.setText(user.phone);

        etStopAddress = (EditText) rootView.findViewById(R.id.editTextStopAddress);
        etFixedPrice = (EditText) rootView.findViewById(R.id.editTextFixedPrice);
        etDescription = (EditText) rootView.findViewById(R.id.editTextDescription);

        btnMake.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnCounter.setOnClickListener(this);
        btnFixed.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonByFixed:
                if (!isFixed) {
                    btnFixed.setBackgroundResource(R.drawable.button_shape_yellow);
                    btnCounter.setBackgroundResource(R.drawable.button_shape_dark_blue);
                    btnFixed.setTextColor(Color.BLACK);
                    btnCounter.setTextColor(Color.WHITE);
                    llFixedPrice.setVisibility(View.VISIBLE);
                }
                isFixed = true;
                break;
            case R.id.buttonByCounter:
                if (isFixed) {
                    btnFixed.setBackgroundResource(R.drawable.button_shape_dark_blue);
                    btnCounter.setBackgroundResource(R.drawable.button_shape_yellow);
                    btnFixed.setTextColor(Color.WHITE);
                    btnCounter.setTextColor(Color.BLACK);
                    llFixedPrice.setVisibility(View.GONE);
                }
                isFixed = false;
                break;
            case R.id.buttonOk:
                makeOrder();
                break;
            case R.id.buttonCancel:
                getActivity().finish();
                break;
            default:
        }
    }

    private void makeOrder() {
        if (mTask != null) return;

        String phone = etPhone.getText().toString();
        if (phone.length() != 13) {
            etPhone.setError("Телефон состоит из 13 символов");
            etPhone.requestFocus();
            return;
        }

        order.clear();
        order.status = OStatus.NEW;
        order.clientPhone = etPhone.getText().toString();
        order.description = etDescription.getText().toString();
        order.clientId = user.id;
        order.fixedPrice = 0;
        order.addressStopName = null;

        /*mTask = new MakeOrderTask();
        mTask.execute((Void) null);*/
        return;
    }

    private class MakeOrderTask extends AsyncTask<Void, Void, JSONObject> {
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
            /*try {
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
                Toast.makeText(getActivity(), "Не удалось", Toast.LENGTH_LONG).show();
                btnMakeOrder.setEnabled(true);
            }*/
        }

        @Override
        protected void onCancelled() {
            mTask = null;
        }
    }
}
