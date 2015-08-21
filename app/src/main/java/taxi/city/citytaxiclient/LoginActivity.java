package taxi.city.citytaxiclient;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;
import taxi.city.citytaxiclient.core.User;
import taxi.city.citytaxiclient.models.GlobalSingleton;
import taxi.city.citytaxiclient.models.OnlineStatus;
import taxi.city.citytaxiclient.models.Order;
import taxi.city.citytaxiclient.models.Role;
import taxi.city.citytaxiclient.models.Session;
import taxi.city.citytaxiclient.networking.RestClient;
import taxi.city.citytaxiclient.networking.model.UserStatus;
import taxi.city.citytaxiclient.service.ApiService;
import taxi.city.citytaxiclient.tasks.UserLoginTask;
import taxi.city.citytaxiclient.utils.Helper;
import taxi.city.citytaxiclient.utils.SessionHelper;

public class LoginActivity extends Activity{

    private UserLoginTask mAuthTask = null;
    private ForgotPasswordTask mForgotTask = null;

    // UI references.
    private EditText mPhoneView;
    private TextView mPhoneExtraView;
    private TextView mForgotPassword;
    private EditText mPasswordView;
    private User user = User.getInstance();
    private ApiService api = ApiService.getInstance();
    private int statusCode;
    private String detail;
    private SweetAlertDialog pDialog;

    private SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        mPhoneView = (EditText) findViewById(R.id.login_phone);
        mForgotPassword =(TextView)findViewById(R.id.textViewForgotPassword);
        mPhoneExtraView = (TextView) findViewById(R.id.textViewPhoneExtra);
        //MaskedWatcher maskedWatcher = new MaskedWatcher("+996 (###) ###-###", mPhoneView);
        mPasswordView = (EditText) findViewById(R.id.login_password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login_phone || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        setSessionPreferences();

        Button mPhoneSignInButton = (Button) findViewById(R.id.btnSignIn);
        Button mSignUpButton = (Button) findViewById(R.id.btnSignUp);
        mForgotPassword.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            final Dialog dialog = new Dialog(LoginActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.forgot_password_alert);

                Window window = dialog.getWindow();
                WindowManager.LayoutParams wlp = window.getAttributes();

                wlp.gravity = Gravity.BOTTOM;
                wlp.dimAmount = 0.7f;
                wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
                window.setAttributes(wlp);

                final TextView tvExtraPhone = (TextView)dialog.findViewById(R.id.textViewForgotPhoneExtra);
                final EditText etPhone = (EditText)dialog.findViewById(R.id.etForgotPhone);

                Button btnOkDialog = (Button)dialog.findViewById(R.id.buttonOkDecline);
                Button btnCancelDialog = (Button)dialog.findViewById(R.id.buttonCancelDecline);

                btnOkDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String forgotPhone = tvExtraPhone.getText().toString() + etPhone.getText().toString();
                        if (forgotPhone.length() != 13) {
                            etPhone.requestFocus();
                            etPhone.setError("Неправильный формат");
                        }
                        forgotPassword(forgotPhone);
                        dialog.dismiss();
                    }
                });

                btnCancelDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.show();
            }
        });


        mPhoneSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mSignUpButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });

        findViewById(R.id.loginContainer).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                return false;
            }
        });
    }

    private void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void forgotPassword(String phone) {
        if (mForgotTask != null) return;

        showProgress(true);
        mForgotTask = new ForgotPasswordTask(phone);
        mForgotTask.execute((Void) null);
    }

    private void setSessionPreferences() {
        SessionHelper sessionHelper = new SessionHelper();

        String phone = sessionHelper.getPhone();
        if(!phone.isEmpty()){
            mPhoneView.setText(phone.substring(4, phone.length()));
        }

        mPasswordView.setText(sessionHelper.getPassword());
    }

    private void saveSessionPreferences(User user) {
        SessionHelper sessionHelper = new SessionHelper();
        sessionHelper.setPhone(user.phone);
        sessionHelper.setPassword(user.password);
        sessionHelper.setId(user.id);
        sessionHelper.setToken(user.getToken());
        api.setToken(user.getToken());
    }

    private void saveSessionPreferencesNew(taxi.city.citytaxiclient.models.User user) {
        SessionHelper sessionHelper = new SessionHelper();
        sessionHelper.setPhone(user.getPhone());
        sessionHelper.setPassword(mPasswordView.getText().toString());
        sessionHelper.setId(user.getId());
        sessionHelper.setToken(user.getToken());
        api.setToken(user.getToken());
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        if (!isNetworkAvailable()) {
            Toast.makeText(this, "Остутствует интернет", Toast.LENGTH_SHORT).show();
            return;
        }

        // Reset errors.
        mPhoneView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String phone = mPhoneExtraView.getText().toString() + mPhoneView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;


        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(phone)) {
            mPhoneView.setError(getString(R.string.error_field_required));
            focusView = mPhoneView;
            cancel = true;
        } else if (phone.length() != 13) {
            mPhoneView.setError(getString(R.string.error_invalid_phone));
            focusView = mPhoneView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            Session session = new Session();
            session.setPhone(phone);
            session.setPassword(password);
            RestClient.getSessionService().login(session, new Callback<taxi.city.citytaxiclient.models.User>() {
                @Override
                public void success(taxi.city.citytaxiclient.models.User user, Response response) {
                    GlobalSingleton.getInstance(LoginActivity.this).token = user.getToken();
                    GlobalSingleton.getInstance(LoginActivity.this).currentUser = user;
                    saveSessionPreferencesNew(user);

                    if (user.hasActiveOrder() && user.getActiveOrder() != null) {
                        GlobalSingleton.getInstance(LoginActivity.this).currentOrder = user.getActiveOrder();
                    }

                    UserStatus userStatus = new UserStatus();
                    userStatus.iosToken = null;
                    userStatus.onlineStatus = OnlineStatus.ONLINE;
                    userStatus.role = Role.DRIVER;
                    RestClient.getUserService().updateStatus(user.getId(), userStatus, new Callback<taxi.city.citytaxiclient.models.User>() {
                        @Override
                        public void success(taxi.city.citytaxiclient.models.User user, Response response) {
                            showProgress(false);
                            Intent intent = new Intent(LoginActivity.this, TestMapsActivity.class);
                            startActivity(intent);
                            finish();
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            showProgress(false);
                            Toast.makeText(LoginActivity.this, error.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, TestMapsActivity.class);
                            startActivity(intent);
                            finish();
                            Crashlytics.logException(error);
                        }
                    });
                    Toast.makeText(LoginActivity.this, "Success", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void failure(RetrofitError error) {
                    showProgress(false);
                    String message = "";
                    if (error.getKind() == RetrofitError.Kind.HTTP) {
                        String json = new String(((TypedByteArray) error.getResponse().getBody()).getBytes());
                        if (json.toLowerCase().contains("username or password")) {
                            message = "Телефон или пароль неверны";
                        } else if (json.toLowerCase().contains("account")) {
                            message = "Аккаунт не активирован";
                        }
                    } else {
                        message = "Не удалось подключится к серверу";
                    }
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            });
            /*mAuthTask = new UserLoginTask(phone, password) {
                @Override
                protected void onPostExecute(Integer statusCode) {
                    super.onPostExecute(statusCode);
                    mAuthTask = null;
                    showProgress(false);

                    if (statusCode == HttpStatus.SC_OK) {
                        saveSessionPreferences(user);
                        Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                        startActivity(intent);
                        finish();
                    } else if (statusCode == UserLoginTask.NOT_ACTIVATED_ACCOUNT_STATUS_CODE){
                        goToActivation();
                    } else if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
                        mPasswordView.setError(getString(R.string.error_incorrect_password));
                        mPasswordView.requestFocus();
                    } else {
                        Toast.makeText(getApplicationContext(), "Сервис недоступен", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                protected void onCancelled(Integer integer) {
                    super.onCancelled(integer);
                    mAuthTask = null;
                    showProgress(false);
                }
            };
            mAuthTask.execute();*/
        }
    }

    private boolean isPasswordValid(String password) {
        return password.length() >= 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */

    public void showProgress(final boolean show) {
        if (show) {
            pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper()
                    .setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Авторизация");
            pDialog.setCancelable(true);
            pDialog.show();
        } else {
            pDialog.dismissWithAnimation();
        }
    }

    private void goToActivation() {
        Intent intent = new Intent(LoginActivity.this, ConfirmSignUpActivity.class);
        intent.putExtra(ConfirmSignUpActivity.PHONE_KEY, mPhoneExtraView.getText().toString() + mPhoneView.getText().toString());
        intent.putExtra(ConfirmSignUpActivity.PASSWORD_KEY, mPasswordView.getText().toString());
        startActivity(intent);
    }

    private void signUp() {
        Intent intent = new Intent(this, UserDetailsActivity.class);
        intent.putExtra("NEW", true);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (data != null && data.getExtras() != null)
                Toast.makeText(getApplicationContext(), data.getExtras().getString("MESSAGE"), Toast.LENGTH_LONG).show();
        }
    }

    public class ForgotPasswordTask extends AsyncTask<Void, Void, JSONObject> {

        private final String mPhone;

        ForgotPasswordTask(String phone) {
            mPhone = phone;
        }

        @Override
        protected JSONObject doInBackground(Void... params) {
            return api.resetPasswordRequest("reset_password/?phone=" + mPhone.replace("+", "%2b"));
        }

        @Override
        protected void onPostExecute(final JSONObject result) {
            mForgotTask = null;
            showProgress(false);
            try {
                if (Helper.isSuccess(result)) {
                    Intent intent = new Intent(LoginActivity.this, ConfirmSignUpActivity.class);
                    intent.putExtra(ConfirmSignUpActivity.SIGNUP_KEY, false);
                    intent.putExtra(ConfirmSignUpActivity.PHONE_KEY, mPhone);
                    user.phone = mPhone;
                    startActivity(intent);
                } else if (Helper.isBadRequest(result)) {
                    Toast.makeText(LoginActivity.this, "Такого номера не существует", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "Сервис не доступен", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException ignored) {}
        }

        @Override
        protected void onCancelled() {
            mForgotTask = null;
            showProgress(false);
        }
    }
}



