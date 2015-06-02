package taxi.city.citytaxiclient;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.gms.common.api.Api;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import taxi.city.citytaxiclient.core.OrderDetail;
import taxi.city.citytaxiclient.core.User;
import taxi.city.citytaxiclient.service.ApiService;

public class RatingFragment extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private SweetAlertDialog pDialog;

    private String mParam1;
    private String mParam2;
    RatingBar ratingBar;
    OrderDetail orderDetail;

    private UpdateRatingTask mTask = null;

    public static RatingFragment newInstance(String param1, String param2) {
        RatingFragment fragment = new RatingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public RatingFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_rating, container, false);

        Intent intent = getActivity().getIntent();
        orderDetail = (OrderDetail)intent.getExtras().getSerializable("DATA");

        ratingBar = (RatingBar) rootView.findViewById(R.id.ratingBar);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (rating < 1.0f) {
                    ratingBar.setRating(1.0f);
                }
            }
        });

        Button btnOk = (Button) rootView.findViewById(R.id.buttonOk);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateRating();
            }
        });

        return rootView;
    }

    private void updateRating() {
        if (mTask != null) return;

        showProgress(true);
        mTask = new UpdateRatingTask();
        mTask.execute((Void) null);
    }

    private class UpdateRatingTask extends AsyncTask<Void, Void, JSONObject> {

        UpdateRatingTask() {}

        @Override
        protected JSONObject doInBackground(Void... params) {
            JSONObject res = new JSONObject();
            try {
                JSONObject data = new JSONObject();
                data.put("driver", orderDetail.driver);
                data.put("client", User.getInstance().id);
                data.put("order", orderDetail.id);
                data.put("votes", ratingBar.getRating());
                data.put("description", "");
                res = ApiService.getInstance().createOrderRequest(data, "rating/addvotes/");
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return res;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            mTask = null;
            showProgress(false);
            getActivity().finish();
        }

        @Override
        protected void onCancelled() {
            mTask = null;
            showProgress(false);
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
