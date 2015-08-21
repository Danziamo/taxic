package taxi.city.citytaxiclient.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rengwuxian.materialedittext.MaterialEditText;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import taxi.city.citytaxiclient.R;
import taxi.city.citytaxiclient.core.User;
import taxi.city.citytaxiclient.networking.RestClient;
import taxi.city.citytaxiclient.networking.model.AccountActivation;


public class AuthorizationFragment extends BaseFragment implements View.OnClickListener {

    private static final String ARG_PHONE = "PHONE_KEY";
    public static final String ARG_FROM_FORGET_PASSWORD = "FROM_FORGET_PASSWORD_KEY";
    public static final String ARG_PASSWORD = "PASSWORD_KEY";

    private String phone;
    private boolean isFromForgetPassword = false;
    private String password;

    private MaterialEditText metPassword;
    private MaterialEditText metSmscode;
    Button btnActivate;


    public static AuthorizationFragment newInstance(String phone, boolean isFromForgetPassword, String password){
        AuthorizationFragment fragment = new AuthorizationFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PHONE, phone);
        args.putBoolean(ARG_FROM_FORGET_PASSWORD, isFromForgetPassword);
        args.putString(ARG_PASSWORD, password);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle args = getArguments();
            phone = args.getString(ARG_PHONE);
            isFromForgetPassword = args.getBoolean(ARG_FROM_FORGET_PASSWORD, false);
            password = args.getString(ARG_PASSWORD, null);
        }
    }

    public AuthorizationFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_authorization, container, false);
        metPassword = (MaterialEditText)view.findViewById(R.id.metPassword);
        metSmscode = (MaterialEditText)view.findViewById(R.id.metCode);

        LinearLayout llPassword = (LinearLayout) view.findViewById(R.id.llPassword);
        if(isFromForgetPassword){
            llPassword.setVisibility(View.VISIBLE);
        }else{
            llPassword.setVisibility(View.INVISIBLE);
        }

        btnActivate = (Button)view.findViewById(R.id.btnActivate);
        btnActivate.setOnClickListener(this);

        TextView btnResendSms = (TextView) view.findViewById(R.id.btnResendSms);
        btnResendSms.setOnClickListener(this);


        return view;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        if(id == R.id.btnActivate){
            Toast.makeText(getActivity(), "Activate", Toast.LENGTH_LONG).show();
        }else if(id == R.id.btnResendSms){
            resendSmsRequest();
            activate();
        }
    }

    private void resendSmsRequest() {
        //@TODO show progress bar
        RestClient.getAccountService().forgotPasswordRequest(phone, new Callback<Object>() {
            @Override
            public void success(Object o, Response response) {
                //@TODO hide progress bar and maybe need to show text
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getActivity(), "Не удалось отправить данные на сервер", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePasswordRequest(){
        //@TODO show progress bar

        String password = metPassword.getText().toString();
        String smsCode = metSmscode.getText().toString();

        RestClient.getAccountService().updateForgotPassword(phone, password, smsCode, new Callback<Object>() {
            @Override
            public void success(Object o, Response response) {

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    private void activate(){
        String smsCode = metSmscode.getText().toString();

        AccountActivation activation = new AccountActivation();
        activation.activationCode = smsCode;
        activation.phone = phone;
        activation.password = password;
        RestClient.getAccountService().activate(activation, new Callback<User>() {
            @Override
            public void success(User user, Response response) {

            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }
}
