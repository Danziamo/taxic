package taxi.city.citytaxiclient;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import org.apache.http.HttpStatus;

import taxi.city.citytaxiclient.core.User;
import taxi.city.citytaxiclient.tasks.UserLoginTask;
import taxi.city.citytaxiclient.utils.SessionHelper;

/**
 * Created by Daniyar on 5/8/2015.
 */
public class SplashScreen extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        final SessionHelper sessionHelper = new SessionHelper();
        String phone = sessionHelper.getPhone();
        String password = sessionHelper.getPassword();

        if(!phone.isEmpty() && !password.isEmpty()){
            goToLoginActivity();
            /*new UserLoginTask(phone, password){
                @Override
                protected void onPostExecute(final Integer statusCode) {
                    super.onPostExecute(statusCode);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (statusCode == HttpStatus.SC_OK) {
                                User user = User.getInstance();
                                sessionHelper.save(user);
                                Intent intent = new Intent(SplashScreen.this, MapsActivity.class);
                                startActivity(intent);
                                finish();
                            } else if (statusCode == UserLoginTask.NOT_ACTIVATED_ACCOUNT_STATUS_CODE){
                                goToActivation();
                            } else {
                                goToLoginActivity();
                            }
                        }
                    }, SPLASH_TIME_OUT);

                }
            }.execute();*/
        }else {
            new Handler().postDelayed(new Runnable() {

                /*
                 * Showing splash screen with a timer. This will be useful when you
                 * want to show case your app logo / company
                 */

                @Override
                public void run() {
                    goToLoginActivity();
                }
            }, SPLASH_TIME_OUT);
        }
    }

    private void goToActivation() {
        SessionHelper sessionHelper = new SessionHelper();
        Intent intent = new Intent(SplashScreen.this, ConfirmSignUpActivity.class);
        intent.putExtra(ConfirmSignUpActivity.PHONE_KEY, sessionHelper.getPhone());
        intent.putExtra(ConfirmSignUpActivity.PASSWORD_KEY, sessionHelper.getPassword());
        startActivity(intent);
        finish();
    }

    private void goToLoginActivity(){
        Intent i = new Intent(SplashScreen.this, LoginActivity.class);
        startActivity(i);

        finish();
    }

}