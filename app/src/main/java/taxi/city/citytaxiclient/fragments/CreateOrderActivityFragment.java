package taxi.city.citytaxiclient.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import taxi.city.citytaxiclient.App;
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
    SweetAlertDialog pDialog;
    Button btnMake;
    Button btnCancel;
    LinearLayout llFixedPrice;

    EditText etStopAddress;
    EditText etFixedPrice;
    EditText etDescription;
    EditText etStartAddress;
    EditText etPhone;
    CheckBox isFixed;

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
        order = Order.getInstance();
        user = User.getInstance();
        api = ApiService.getInstance();
        btnMake = (Button) rootView.findViewById(R.id.buttonOk);
        btnCancel = (Button) rootView.findViewById(R.id.buttonCancel);
        llFixedPrice = (LinearLayout) rootView.findViewById(R.id.linearLayoutFixedPrice);
        isFixed = (CheckBox) rootView.findViewById(R.id.checkBoxIsFixed);

        etStartAddress = (EditText) rootView.findViewById(R.id.editTextStartAddress);
        etStartAddress.setText(order.addressStartName);

        etPhone = (EditText) rootView.findViewById(R.id.editTextClientPhone);
        etPhone.setText(user.phone);

        etStopAddress = (EditText) rootView.findViewById(R.id.editTextEndAddress);
        etFixedPrice = (EditText) rootView.findViewById(R.id.editTextFixedPrice);
        etDescription = (EditText) rootView.findViewById(R.id.editTextDescription);

        etStartAddress.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    etPhone.requestFocus();
                    return true;
                }
                return false;
            }
        });

        etPhone.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    etDescription.requestFocus();
                    return true;
                }
                return false;
            }
        });

        btnMake.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        isFixed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isFixed.isChecked()) {
                    llFixedPrice.setVisibility(View.VISIBLE);
                } else {
                    llFixedPrice.setVisibility(View.GONE);
                }
            }
        });

        rootView.findViewById(R.id.textViewElements).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                return false;
            }
        });

        return rootView;
    }

    private void hideKeyboard() {
        // Check if no view has focus:
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
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

        App.getDefaultTracker().send(new HitBuilders.EventBuilder()
                .setCategory("order")
                .setLabel("create_order")
                .setAction("create")
                .build());

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

        if (isFixed.isChecked() &&  (fixedPrice.length() == 0 || Double.valueOf(fixedPrice) < 50)) {
            etFixedPrice.setError("Фиксированная сумма не меньше 50 сомов");
            etFixedPrice.requestFocus();
            return;
        }

        if (isFixed.isChecked() && (fixedPrice.length() == 0 || Double.valueOf(fixedPrice) > 999)) {
            etFixedPrice.setError("Фиксированная сумма не больше 999 сомов");
            etFixedPrice.requestFocus();
            return;
        }

        if (isFixed.isChecked() && addressEnd.length() < 3) {
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
        order.fixedPrice = isFixed.isChecked() ? Double.valueOf(fixedPrice) : 0;
        order.addressStopName = isFixed.isChecked() ? addressEnd : "";

        showProgress(true);
        mTask = new MakeOrderTask();
        mTask.execute((Void) null);
    }

    private class MakeOrderTask extends AsyncTask<Void, Void, JSONObject> {
        MakeOrderTask() {}

        @Override
        protected JSONObject doInBackground(Void... params) {

            JSONObject result = new JSONObject();
            JSONObject data = new JSONObject();
            try {
                if (order.id == 0) {
                    data = order.getOrderAsJson();
                    result = api.createOrderRequest(data, "orders/");
                } else {
                    result.put("status_code", HttpStatus.SC_BAD_REQUEST);
                }
            } catch (JSONException e) {
                result = null;
                e.printStackTrace();
            } catch (Exception e) {
                result = null;
            }
            return result;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            mTask = null;
            showProgress(false);
            try {
                mTask = null;
                if (Helper.isSuccess(result)) {
                    order.id = result.getInt("id");
                    Helper.saveOrderPreferences(getActivity(), order.id);
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
                } else if (Helper.isBadRequest(result) && result.getInt("status_code") == HttpStatus.SC_BAD_REQUEST) {
                    new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                            .setTitleText("Ошибка")
                            .setContentText("У вас уже есть активный заказ")
                            .setConfirmText("Ок")
                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                @Override
                                public void onClick(SweetAlertDialog sweetAlertDialog) {
                                    getActivity().finish();
                                    sweetAlertDialog.dismissWithAnimation();
                                }
                            })
                            .show();
                } else {
                    order.clear();
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
            } catch (JSONException ignored) {}
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
