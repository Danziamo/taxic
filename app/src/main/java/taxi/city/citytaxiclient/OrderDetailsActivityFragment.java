package taxi.city.citytaxiclient;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.AsyncTask;
import android.app.Fragment;
import android.os.Bundle;
import android.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;
import taxi.city.citytaxiclient.core.Order;
import taxi.city.citytaxiclient.core.OrderDetail;
import taxi.city.citytaxiclient.service.ApiService;
import taxi.city.citytaxiclient.utils.Helper;


/**
 * A placeholder fragment containing a simple view.
 */
public class OrderDetailsActivityFragment extends Fragment {


    private OrderDetail orderDetail;
    private Order order;
    private SweetAlertDialog pDialog;
    private FetchOrderTask mFetchTask = null;
    Button btnNext;
    ApiService api = ApiService.getInstance();

    TextView tvDriverName;

    public OrderDetailsActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_order_details, container, false);
        order = Order.getInstance();

        Intent intent = getActivity().getIntent();
        orderDetail = (OrderDetail)intent.getExtras().getSerializable("DATA");

        TextView etAddressStart = (TextView)rootView.findViewById(R.id.editTextStartAddress);
        TextView tvFinishAddress = (TextView) rootView.findViewById(R.id.textViewFinishAddress);
        TextView tvWaitTime = (TextView)rootView.findViewById(R.id.textViewWaitingTime);
        TextView tvWaitSum = (TextView)rootView.findViewById(R.id.textViewWaitingSum);
        TextView tvDistance = (TextView)rootView.findViewById(R.id.textViewDistance);
        TextView tvSum = (TextView)rootView.findViewById(R.id.textViewSum);
        TextView tvTotalSum = (TextView)rootView.findViewById(R.id.textViewTotalSum);
        tvDriverName = (TextView)rootView.findViewById(R.id.textViewDriverName);
        btnNext = (Button) rootView.findViewById(R.id.buttonNext);

        btnNext.setVisibility(orderDetail.active ? View.VISIBLE : View.GONE);

        double totalSum = 0;
        double waitSum = 0;
        double sum = 0;
        try {
            waitSum = Double.valueOf(orderDetail.waitSum);
            sum = Double.valueOf(orderDetail.sum);
            totalSum = waitSum + sum;
        } catch (Exception e) {
            totalSum = 0;
        }

        String waitTime = orderDetail.waitTime;
        if (waitTime.length() > 5) {
            waitTime = waitTime.substring(0, waitTime.length() - 3);
        }

        etAddressStart.setText(orderDetail.addressStart);
        tvFinishAddress.setText(!orderDetail.addressEnd.equals("null") ? orderDetail.addressEnd : null);
        tvWaitTime.setText(waitTime);
        tvWaitSum.setText(String.valueOf((int)waitSum));
        tvDistance.setText(orderDetail.distance);
        tvSum.setText(String.valueOf((int)sum));
        tvTotalSum.setText(String.valueOf((int)totalSum));
        tvDriverName.setText(orderDetail.driverName);

        etAddressStart.setEnabled(false);
        if (orderDetail.driverName == null) fetch();

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
                ft.replace(R.id.fragment, RatingFragment.newInstance(null, null), "rating");
// Start the animated transition.
                ft.commit();
            }
        });

        return rootView;
    }

    private void fetch() {
        if (mFetchTask != null) return;

        mFetchTask = new FetchOrderTask();
        mFetchTask.execute((Void) null);
    }

    private class FetchOrderTask extends AsyncTask<Void, Void, JSONObject> {

        FetchOrderTask() {}

        @Override
        protected JSONObject doInBackground(Void... params) {

            return api.getOrderRequest("/users/" + String.valueOf(orderDetail.driver) + "/");
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            mFetchTask = null;
            try {
                if (Helper.isSuccess(result)) {
                    tvDriverName.setText(result.getString("last_name") + " " + result.getString("first_name"));
                }
            } catch (JSONException ignored) {}
        }

        @Override
        protected void onCancelled() {
            mFetchTask = null;
        }
    }
}
