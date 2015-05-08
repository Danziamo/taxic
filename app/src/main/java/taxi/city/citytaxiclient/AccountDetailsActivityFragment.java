package taxi.city.citytaxiclient;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import taxi.city.citytaxiclient.Core.User;
import taxi.city.citytaxiclient.Service.ApiService;
import taxi.city.citytaxiclient.Utils.Helper;


/**
 * A placeholder fragment containing a simple view.
 */
public class AccountDetailsActivityFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    TextView tvAccountNumber;
    TextView tvAccountBalance;
    private FetchAccountTask mTask = null;

    // TODO: Rename and change types of parameters
    private String mParam1;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param position Parameter 1.
     * @return A new instance of fragment AccountFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AccountDetailsActivityFragment newInstance(int position) {
        AccountDetailsActivityFragment fragment = new AccountDetailsActivityFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, String.valueOf(position));
        fragment.setArguments(args);
        return fragment;
    }

    public AccountDetailsActivityFragment() {
        // Required empty public constructor
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
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_account_details, container, false);

        tvAccountNumber = (TextView)rootView.findViewById(R.id.textViewAccountNumber);
        tvAccountBalance = (TextView) rootView.findViewById(R.id.textViewAccountBalance);

        tvAccountNumber.setText(User.getInstance().phone);
        tvAccountBalance.setText(String.valueOf((int)User.getInstance().balance) + "  сом");

        /*Button buttonBack = (Button)rootView.findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });*/

        fetchTask();
        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    private void fetchTask(){
        if (mTask != null) return;

        mTask = new FetchAccountTask();
        mTask.execute((Void) null);
    }

    private class FetchAccountTask extends AsyncTask<Void, Void, JSONObject> {

        FetchAccountTask() {}

        @Override
        protected JSONObject doInBackground(Void... params) {
            return ApiService.getInstance().getOrderRequest("users/" + User.getInstance().id + "/");
        }

        @Override
        protected void onPostExecute(final JSONObject result) {
            mTask = null;
            int statusCode = -1;
            try {
                if(Helper.isSuccess(result)) {
                    statusCode = result.getInt("status_code");
                }
                if (Helper.isSuccess(statusCode)) {
                    fillForms(result);
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

    private void fillForms(JSONObject object) throws JSONException {
        String balance = object.getString("balance");
        double b = balance == null ? 0 : Double.valueOf(balance);
        tvAccountBalance.setText(String.valueOf((int)b) + "  сом");
        User.getInstance().balance = b;
    }
}
