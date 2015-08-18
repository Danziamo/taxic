package taxi.city.citytaxiclient;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.IntentCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import taxi.city.citytaxiclient.models.GlobalSingleton;
import taxi.city.citytaxiclient.models.OnlineStatus;
import taxi.city.citytaxiclient.models.User;
import taxi.city.citytaxiclient.networking.RestClient;
import taxi.city.citytaxiclient.utils.Helper;
import taxi.city.citytaxiclient.utils.SessionHelper;

public class AccountActivity extends ActionBarActivity implements ActionBar.TabListener {

    TabsPagerAdapter mPageAdapter;
    ViewPager mViewPager;
    private SweetAlertDialog pDialog;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        user = GlobalSingleton.getInstance(AccountActivity.this).currentUser;

        if (user == null || user.getId() == 0)
        {
            Helper.getPreferences(this);
            if (user == null || user.getId() == 0) {
                Toast.makeText(getApplicationContext(), "Сессия вышла, пожалуйста перезайдите", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        }

        setContentView(R.layout.activity_account);

        mPageAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                getSupportActionBar().setSelectedNavigationItem(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mViewPager.setAdapter(mPageAdapter);

        setUpTabs();
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    private void setUpTabs() {
        ActionBar ab = getSupportActionBar();
        ab.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        ab.setDisplayShowTitleEnabled(true);

        ab.addTab(ab.newTab().setText("Счет").setIcon(R.drawable.ic_action_account).setTabListener(this));
        ab.addTab(ab.newTab().setText("Кабинет").setIcon(R.drawable.ic_action_personal).setTabListener(this));
        ab.addTab(ab.newTab().setText("История").setIcon(R.drawable.ic_action_history).setTabListener(this));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_quit:
                signOut();
                return true;
            case R.id.action_about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
            case R.id.action_help:
                startActivity(new Intent(this, HelpActivity.class));
                return true;
            case R.id.action_share:
                shareLink();return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void shareLink(){
        String text = "Я пользуюсь Easy Taxi. Это удобное приложение для мгновенного вызова такси. \nhttp://onelink.to/r94mvp \nEasy Taxi\nНам с тобой по пути!";
        Intent intent = new Intent(Intent.ACTION_SEND);

        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
//        intent.putExtra(Intent.EXTRA_SUBJECT, "Я пользуюсь мобильным приложением Easy Taxi.");

        startActivity(Intent.createChooser(intent, "Поделиться"));
    }



    private void signOut() {
        SweetAlertDialog pDialog = new SweetAlertDialog(this, SweetAlertDialog.WARNING_TYPE);
        pDialog .setTitleText("Вы хотите выйти?")
                .setConfirmText("Выйти")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        logout();
                        sDialog.dismissWithAnimation();
                    }
                })
                .setCancelText("Отмена")
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();

    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {
        mViewPager.setCurrentItem(tab.getPosition());
        switch (tab.getPosition()) {
            case 0:
                break;
            case 1:
                break;
            case 2:
                break;
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction fragmentTransaction) {

    }

    private void logout() {
        showProgress(true);

        RestClient.getUserService().updateStatus(user.getId(), OnlineStatus.EXITED, new Callback<Object>() {
            @Override
            public void success(Object o, Response response) {
                showProgress(false);
                SessionHelper sessionHelper = new SessionHelper();
                sessionHelper.setPassword("");
                sessionHelper.setToken("");

                showProgress(false);
                Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
                ComponentName cn = intent.getComponent();
                Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);
                startActivity(mainIntent);
            }

            @Override
            public void failure(RetrofitError error) {
                showProgress(false);
                SessionHelper sessionHelper = new SessionHelper();
                sessionHelper.setPassword("");
                sessionHelper.setToken("");

                showProgress(false);
                Intent intent = new Intent(AccountActivity.this, LoginActivity.class);
                ComponentName cn = intent.getComponent();
                Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);
                startActivity(mainIntent);
            }
        });
    }

    public void showProgress(final boolean show) {
        if (show) {
            pDialog = new SweetAlertDialog(this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper()
                    .setBarColor(Color.parseColor("#A5DC86"));
            pDialog.setTitleText("Выход");
            pDialog.setCancelable(true);
            pDialog.show();
        } else {
            pDialog.dismissWithAnimation();
        }
    }
}
