package taxi.city.citytaxiclient.fragments;

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

import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import taxi.city.citytaxiclient.R;
import taxi.city.citytaxiclient.core.Order;
import taxi.city.citytaxiclient.core.User;
import taxi.city.citytaxiclient.enums.OStatus;
import taxi.city.citytaxiclient.service.ApiService;
import taxi.city.citytaxiclient.utils.Helper;


/**
 * A placeholder fragment containing a simple view.
 */
public class CreateOrderActivityFragment extends Fragment implements View.OnClickListener {

    private boolean isFixed = false;
    SweetAlertDialog pDialog;
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

        etStopAddress = (EditText) rootView.findViewById(R.id.editTextEndAddress);
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
                    btnFixed.setBackgroundResource(R.drawable.button_shape_dark_blue);
                    btnCounter.setBackgroundResource(R.drawable.button_shape_yellow);
                    btnFixed.setTextColor(Color.WHITE);
                    btnCounter.setTextColor(Color.BLACK);
                    llFixedPrice.setVisibility(View.VISIBLE);
                }
                isFixed = true;
                break;
            case R.id.buttonByCounter:
                if (isFixed) {
                    btnFixed.setBackgroundResource(R.drawable.button_shape_yellow);
                    btnCounter.setBackgroundResource(R.drawable.button_shape_dark_blue);
                    btnFixed.setTextColor(Color.BLACK);
                    btnCounter.setTextColor(Color.WHITE);
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
        String description = etDescription.getText().toString();
        String addressEnd = etStopAddress.getText().toString();
        String addressStart = etStartAddress.getText().toString();
        String fixedPrice = etFixedPrice.getText().toString();

        if (addressStart.length() < 3) {
            etStartAddress.setError("Минимум 3 символа");
            etStartAddress.requestFocus();
            return;
        }

        if (phone.length() != 13) {
            etPhone.setError("Телефон состоит из 13 символов");
            etPhone.requestFocus();
            return;
        }

        if (isFixed && Double.valueOf(fixedPrice) < 50) {
            etFixedPrice.setError("Фиксированная сумма не меньше 50 сомов");
            etFixedPrice.requestFocus();
            return;
        }

        if (isFixed && addressEnd.length() < 3) {
            etStopAddress.setError("Минимум 3 символа");
            etStopAddress.requestFocus();
            return;
        }

        order.clear();
        order.status = OStatus.NEW;
        order.clientPhone = phone;
        order.description = description;
        order.addressStartName = addressStart;
        order.clientId = user.id;
        order.fixedPrice = isFixed ? Double.valueOf(fixedPrice) : 0;
        order.addressStopName = isFixed ? addressEnd : "";

        showProgress(true);
        mTask = new MakeOrderTask();
        mTask.execute((Void) null);
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
            mTask = null;
            showProgress(false);
            try {
                order.id = result.getInt("id");
                mTask = null;
                if (Helper.isSuccess(result)) {
                    new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                            .setTitleText("Ваш заказ создан")
                            .setContentText("Ожидайте водителя")
                            .setConfirmText("Ок")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    getActivity().finish();
                                    sweetAlertDialog.dismissWithAnimation();
                                }
                            })
                            .show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
                new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("Ошибка")
                        .setContentText("Не удалось отправить данные на сервер")
                        .setConfirmText("Ок")
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                sweetAlertDialog.dismissWithAnimation();
                            }
                        })
                        .show();
            }
        }

        @Override
        protected void onCancelled() {
            mTask = null;
        }
    }

    public void showProgress(final boolean show) {
        if (show) {
            pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
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
