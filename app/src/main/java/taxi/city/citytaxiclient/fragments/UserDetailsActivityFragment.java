package taxi.city.citytaxiclient.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
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
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import taxi.city.citytaxiclient.ConfirmSignUpActivity;
import taxi.city.citytaxiclient.R;
import taxi.city.citytaxiclient.models.GlobalSingleton;
import taxi.city.citytaxiclient.models.User;
import taxi.city.citytaxiclient.networking.RestClient;
import taxi.city.citytaxiclient.networking.model.NUser;
import taxi.city.citytaxiclient.service.ApiService;
import taxi.city.citytaxiclient.utils.Helper;

/**
 * A placeholder fragment containing a simple view.
 */
public class UserDetailsActivityFragment extends Fragment implements View.OnClickListener {

    private EditText etLastName;
    private EditText etFirstName;
    private EditText etPhone;
    private EditText etPhoneExtra;
    private EditText etDoB;
    //private TextView tvTitle;
    private EditText etPassword;
    private EditText etEmail;
    private boolean isNew = false;
    private LinearLayout llPhone;
    //private SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy");

    private SweetAlertDialog pDialog;
    private DatePickerDialog datePickerDialog;
    private SimpleDateFormat dateFormatter;

    Button btnSave;
    Button btnBack;

    private User user;
    private UserUpdateTask mTask = null;

    public UserDetailsActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_user_details, container, false);
        isNew = getActivity().getIntent().getBooleanExtra("NEW", false);
        dateFormatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        user = GlobalSingleton.getInstance(getActivity()).currentUser;

        etLastName = (EditText)rootView.findViewById(R.id.editTextLastName);
        etFirstName = (EditText)rootView.findViewById(R.id.editTextFirstName);
        etEmail = (EditText)rootView.findViewById(R.id.editTextEmail);
        etPhone = (EditText) rootView.findViewById(R.id.textViewPhone);
        etPassword = (EditText) rootView.findViewById(R.id.editTextPassword);
        etPhoneExtra = (EditText) rootView.findViewById(R.id.textViewExtra);
        //tvTitle = (TextView) rootView.findViewById(R.id.textViewTitle);
        etDoB = (EditText) rootView.findViewById(R.id.editTextDoB);
        etDoB.setInputType(InputType.TYPE_NULL);
        llPhone = (LinearLayout) rootView.findViewById(R.id.linearLayoutPhone);

        btnSave = (Button)rootView.findViewById(R.id.buttonSave);
        btnBack = (Button)rootView.findViewById(R.id.buttonBack);

        if (isNew) {
            btnBack.setVisibility(View.VISIBLE);
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
            etLastName.setText(user.getLastName());
            etFirstName.setText(user.getFirstName());
            etPassword.setText(user.getPassword());
            etDoB.setText(user.getDateOfBirth());
            llPhone.setVisibility(View.GONE);
        }

        btnSave.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        updateView();
        setDateTimePicker();
        rootView.findViewById(R.id.userContainer).setOnTouchListener(new View.OnTouchListener() {
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
    private void setDateTimePicker() {
        etDoB.setOnClickListener(this);

        Calendar newCalendar = Calendar.getInstance();
        datePickerDialog = new DatePickerDialog(getActivity(), android.R.style.Theme_Holo_Dialog ,new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                etDoB.setText(dateFormatter.format(newDate.getTime()));
            }
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonSave:
                updateTask();
                break;
            case R.id.editTextDoB:
                datePickerDialog.show();
                break;
            default:
                getActivity().finish();
                break;
        }
    }

    private void updateView() {
        if (isNew) {
            btnSave.setText("Подтвердить");
            //tvTitle.setText("Регистарция");
            etPhone.setEnabled(true);
            etPhoneExtra.setEnabled(true);
        } else {
            //tvTitle.setVisibility(View.GONE);
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
        String dob = etDoB.getText().toString();

        if (!isNew) {

            if (firstName.length() < 2) {
                etFirstName.setError("Имя неправильно задано");
                etFirstName.requestFocus();
                return;
            }

            if (lastName.length() < 2) {
                etLastName.setError("Фамилия неправильно задано");
                etLastName.requestFocus();
                return;
            }

            if (email.length() != 0 && !Helper.isValidEmailAddress(email)) {
                etEmail.setError("Email неправильно задано");
                etEmail.requestFocus();
                return;
            }
        }

        if (isNew && phone.length() != 13) {
            etPhone.setError("Телефон должен состоять из 13 символов");
            etPhone.requestFocus();
            return;
        }

        if (password.length() < 4) {
            etPassword.setError("Пароль неправильно задан");
            etPassword.requestFocus();
            return;
        }

        if (isNew) {
            JSONObject json = new JSONObject();
            try {
                if (isNew) json.put("phone", phone);
                //json.put("role", "user");
                json.put("first_name", !isNew ? firstName : "Имя");
                json.put("last_name", !isNew ? lastName : "Фамилия");
                json.put("email", !isNew ? email : null);
                json.put("date_of_birth", !isNew ? dob : null);
                json.put("password", password);
            } catch (JSONException e) {
                e.printStackTrace();
            }

            if (json.length() < 1) return;

            showProgress(true);
            mTask = new UserUpdateTask(json);
            mTask.execute((Void) null);
        } else {
            NUser nuser = new NUser();
            nuser.firstName = firstName;
            nuser.lastName = lastName;
            nuser.password = password;
            if (!dob.isEmpty())
                nuser.dob = dob;
            if (!email.isEmpty())
                nuser.email = email;

            RestClient.getUserService().updateUser(user.getId(), nuser, new Callback<User>() {
                @Override
                public void success(User newUser, Response response) {
                    user.setFirstName(newUser.getFirstName());
                    user.setLastName(newUser.getLastName());
                    user.setPassword(etPassword.getText().toString());
                    user.setDateOfBirth(newUser.getDateOfBirth());
                    user.setEmail(newUser.getEmail());
                    Toast.makeText(getActivity(), "Success updating profile", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void failure(RetrofitError error) {
                    Toast.makeText(getActivity(), "Failure updating profile", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private class UserUpdateTask extends AsyncTask<Void, Void, JSONObject> {

        private JSONObject mJson;

        UserUpdateTask(JSONObject json) {
            mJson = json;
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            if (isNew) return ApiService.getInstance().signUpRequest(mJson, "users/");
            return ApiService.getInstance().patchRequest(mJson, "users/" + user.getId() + "/");
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
        user.setPhone(etPhoneExtra.getText().toString() + etPhone.getText().toString());
        user.setFirstName(etFirstName.getText().toString());
        user.setLastName(etLastName.getText().toString());
        user.setPassword(etPassword.getText().toString());
        user.setDateOfBirth(etDoB.getText().toString());
        if (isNew) {
            try {
                //user.setUser(object);
                if (object.has("token")) ApiService.getInstance().setToken(object.getString("token"));
                goToActivation();
            } catch (JSONException ignored) {}
        }
    }

    private void goToActivation() {
        Intent intent = new Intent(getActivity(), ConfirmSignUpActivity.class);
        intent.putExtra("PHONE", etPhoneExtra.getText().toString() + etPhone.getText().toString());
        intent.putExtra("PASS", etPassword.getText().toString());
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
