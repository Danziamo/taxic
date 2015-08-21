package taxi.city.citytaxiclient;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import org.apache.http.HttpStatus;

import taxi.city.citytaxiclient.core.User;
import taxi.city.citytaxiclient.tasks.UserLoginTask;
import taxi.city.citytaxiclient.utils.SessionHelper;

public class MainSplashActivity extends ActionBarActivity {

    View animContainer;
    View bottomMiniPanel;
    private YoYo.YoYoString animation;

    private static int SPLASH_TIME_OUT = 1000;
    private static int MIN_TIME_OUT    = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_splash);

        animContainer   = findViewById(R.id.main_navigation_panel);
        bottomMiniPanel = findViewById(R.id.anim_panel);





        final SessionHelper sessionHelper = new SessionHelper();
        String phone = sessionHelper.getPhone();
        String password = sessionHelper.getPassword();

        if(!phone.isEmpty() && !password.isEmpty()){
          //  goToLoginActivity();
            new UserLoginTask(phone, password){
                @Override
                protected void onPostExecute(final Integer statusCode) {
                    super.onPostExecute(statusCode);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (statusCode == HttpStatus.SC_OK) {
                                User user = User.getInstance();
                                sessionHelper.save(user);
                                Intent intent = new Intent(MainSplashActivity.this, MapsActivity.class);
                                startActivity(intent);
                                finish();
                            } else if (statusCode == UserLoginTask.NOT_ACTIVATED_ACCOUNT_STATUS_CODE){
                                goToActivation();
                            } else {
                                //goToLoginActivity();
                                openAnimation();
                            }
                        }
                    }, SPLASH_TIME_OUT);

                }
            }.execute();

        }else {
            openAnimation();
        }


    }

    private void openAnimation(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                performAnim(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        performAnim(false);
                    }
                },SPLASH_TIME_OUT);


            }
        },MIN_TIME_OUT);
    }



    public void performAnim(Boolean flag){
        if(flag)
            animContainer.setVisibility(View.INVISIBLE);
        else
            animContainer.setVisibility(View.VISIBLE);

        bottomMiniPanel.setVisibility(View.VISIBLE);
        animation = YoYo.with(Techniques.SlideInUp)
                .duration(800)
                .playOn(animContainer);
    }

    private void goToActivation() {
        SessionHelper sessionHelper = new SessionHelper();
        Intent intent = new Intent(MainSplashActivity.this, ConfirmSignUpActivity.class);
        intent.putExtra(ConfirmSignUpActivity.PHONE_KEY, sessionHelper.getPhone());
        intent.putExtra(ConfirmSignUpActivity.PASSWORD_KEY, sessionHelper.getPassword());
        startActivity(intent);
        finish();
    }

    private void goToLoginActivity(){
        Intent i = new Intent(MainSplashActivity.this, LoginActivity.class);
        startActivity(i);

        finish();
    }

}
