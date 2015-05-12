package taxi.city.citytaxiclient.fragments;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import taxi.city.citytaxiclient.ConfirmSignUpActivity;
import taxi.city.citytaxiclient.MapsActivity;
import taxi.city.citytaxiclient.R;
import taxi.city.citytaxiclient.core.User;
import taxi.city.citytaxiclient.service.ApiService;
import taxi.city.citytaxiclient.utils.Helper;

/**
 * A placeholder fragment containing a simple view.
 */
public class UserDetailsActivityFragment extends Fragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;

    private EditText etLastName;
    private EditText etFirstName;
    private EditText etPhone;
    private EditText etPhoneExtra;
    private EditText etDoB;
    private TextView tvTitle;
    private EditText etPassword;
    private EditText etEmail;
    private boolean isNew = false;
    //private SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");

    private SweetAlertDialog pDialog;
    private DatePickerDialog datePickerDialog;

    Button btnSave;
    Button btnBack;

    private User user;
    private UserUpdateTask mTask = null;

    public static UserDetailsActivityFragment newInstance(int position) {
        UserDetailsActivityFragment fragment = new UserDetailsActivityFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, String.valueOf(position));
        fragment.setArguments(args);
        return fragment;
    }

    public UserDetailsActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_details, container, false);
        isNew = getActivity().getIntent().getBooleanExtra("NEW", false);

        user = User.getInstance();

        etLastName = (EditText)rootView.findViewById(R.id.editTextLastName);
        etFirstName = (EditText)rootView.findViewById(R.id.editTextFirstName);
        etEmail = (EditText)rootView.findViewById(R.id.editTextEmail);
        etPhone = (EditText) rootView.findViewById(R.id.textViewPhone);
        etPassword = (EditText) rootView.findViewById(R.id.editTextPassword);
        etPhoneExtra = (EditText) rootView.findViewById(R.id.textViewExtra);
        tvTitle = (TextView) rootView.findViewById(R.id.textViewTitle);
        etDoB = (EditText) rootView.findViewById(R.id.editTextDoB);

        if (isNew) {
            etLastName.setVisibility(View.GONE);
            etFirstName.setVisibility(View.GONE);
            etEmail.setVisibility(View.GONE);
            etDoB.setVisibility(View.GONE);
        }

        ImageButton btnShowPassword = (ImageButton)rootView.findViewById(R.id.imageButtonShowPassword);

        btnShowPassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    etPassword.setSelection(etPassword.length());
                    return true;
                }
                if(event.getAction() == MotionEvent.ACTION_UP){
                    etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    etPassword.setSelection(etPassword.length());
                    return true;
                }

                return false;
            }
        });

        if (!isNew) {
            etLastName.setText(user.lastName);
            etFirstName.setText(user.firstName);
            etPassword.setText(user.password);
            etEmail.setText(user.email);
            String extra = user.phone.substring(0, 4);
            String phone = user.phone.substring(4);
            etPhone.setText(phone);
            etPhoneExtra.setText(extra);
        }

        btnSave = (Button)rootView.findViewById(R.id.buttonSave);
        btnBack = (Button)rootView.findViewById(R.id.buttonBack);

        btnSave.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        updateView();

        return rootView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonSave:
                updateTask();
                break;
            default:
                getActivity().finish();
                break;
        }
    }

    private void updateView() {
        if (isNew) {
            btnSave.setText("Подтвердить");
            tvTitle.setText("Регистарция");
            etPhone.setEnabled(true);
            etPhoneExtra.setEnabled(true);
        } else {
            tvTitle.setText("Настройки пользователя");
            btnSave.setText("Сохранить");
            etPhone.setEnabled(false);
            etPhoneExtra.setEnabled(false);
        }
    }

    private void updateTask() {
        if (mTask != null) return;

        String firstName = etFirstName.getText().toString();
        String lastName = etLastName.getText().toString();
        String phone = etPhoneExtra.getText().toString() + etPhone.getText().toString();
        String password = etPassword.getText().toString();
        String email = etEmail.getText().toString();

        if (!isNew) {

            if (firstName == null || firstName.length() < 2) {
                etFirstName.setError("Имя неправильно задано");
                etFirstName.requestFocus();
                return;
            }

            if (lastName == null || lastName.length() < 2) {
                etLastName.setError("Фамилия неправильно задано");
                etLastName.requestFocus();
                return;
            }

            if (email != null && !Helper.isValidEmailAddress(email)) {
                etEmail.setError("Email неправильно задано");
                etEmail.requestFocus();
                return;
            }
        }

        if (phone.length() != 13) {
            etPhone.setError("Телефон должен состоять из 13 символов");
            etPhone.requestFocus();
            return;
        }

        if (password == null || password.length() < 3) {
            etPassword.setError("Пароль неправильно задан");
            etPassword.requestFocus();
            return;
        }


        JSONObject json = new JSONObject();
        try {
            json.put("phone", phone);
            json.put("activation_code", "11111");
            //json.put("role", "user");
            json.put("first_name", !isNew ? firstName : "Имя");
            json.put("last_name", !isNew ? lastName : "Фамиля");
            json.put("password", password);

            if (!isNew) {
                json.put("email", email);
                json.put("date_of_birth", null);
            }
        } catch (JSONException e)  {
            e.printStackTrace();
        }

        if (json.length() < 1) return;

        showProgress(true);
        mTask = new UserUpdateTask(json);
        mTask.execute((Void) null);
    }

    private class UserUpdateTask extends AsyncTask<Void, Void, JSONObject> {

        private JSONObject mJson;

        UserUpdateTask(JSONObject json) {
            mJson = json;
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            if (isNew) return ApiService.getInstance().signUpRequest(mJson, "users/");
            return ApiService.getInstance().patchRequest(mJson, "users/" + user.id + "/");
        }

        @Override
        protected void onPostExecute(final JSONObject result) {
            mTask = null;
            showProgress(false);
            int statusCode = -1;
            try {
                if(result != null && result.has("status_code")) {
                    statusCode = result.getInt("status_code");
                }
                if (Helper.isSuccess(statusCode)) {
                    confirmUpdate(result);
                } else if (statusCode == HttpStatus.SC_BAD_REQUEST) {
                    if (result.has("phone")) {
                        etPhone.setError("Пользователь с таким номером уже существует");
                        etPhone.requestFocus();
                    }
                } else {
                    Toast.makeText(getActivity(), "Сервис недоступен", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onCancelled() {
            mTask = null;
        }
    }

    private void confirmUpdate(JSONObject object) {
        user.phone = etPhoneExtra.getText().toString() + etPhone.getText().toString();
        user.firstName = etFirstName.getText().toString();
        user.lastName = etLastName.getText().toString();
        user.password = etPassword.getText().toString();
        user.email = etEmail.getText().toString();
        if (isNew) {
            try {
                user.setUser(object);
                if (object.has("token")) ApiService.getInstance().setToken(object.getString("token"));
                goToMapsActivity();
            } catch (JSONException ignored) {}
        }
    }

    private void goToMapsActivity() {
        Intent intent = new Intent(getActivity(), MapsActivity.class);
        intent.putExtra("finish", true);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // To clean up all activities
        startActivity(intent);
        getActivity().finish();
    }

    public void showProgress(final boolean show) {
        if (show) {
            pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper()
                    .setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Сохранение");
            pDialog.setCancelable(true);
            pDialog.show();
        } else {
            pDialog.dismissWithAnimation();
        }
    }
}
