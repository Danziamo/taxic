package taxi.city.citytaxiclient.fragments;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import taxi.city.citytaxiclient.R;
import taxi.city.citytaxiclient.models.GlobalSingleton;
import taxi.city.citytaxiclient.models.User;
import taxi.city.citytaxiclient.networking.RestClient;
import taxi.city.citytaxiclient.service.ApiService;
import taxi.city.citytaxiclient.utils.Helper;

public class AccountDetailsActivityFragment extends Fragment {

    TextView tvAccountNumber;
    TextView tvAccountBalance;
    private User user;

    public AccountDetailsActivityFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_account_details, container, false);

        user = GlobalSingleton.getInstance(getActivity()).currentUser;
        tvAccountNumber = (TextView)rootView.findViewById(R.id.textViewAccountNumber);
        tvAccountBalance = (TextView) rootView.findViewById(R.id.textViewAccountBalance);

        tvAccountNumber.setText(user.getPhone());
        tvAccountBalance.setText(String.valueOf((int)user.getBalance()) + "  сом*"); //Do not remove * symbol

        fetchTask();
        return rootView;
    }

    private void fetchTask(){
        RestClient.getUserService().getById(GlobalSingleton.getInstance(getActivity()).currentUser.getId(), new Callback<User>() {
            @Override
            public void success(User newUser, Response response) {
                user.setBalance(newUser.getBalance());
                tvAccountBalance.setText(String.valueOf((int) user.getBalance()) + "  сом*");
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getActivity(), "Failure account activity", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
