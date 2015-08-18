package taxi.city.citytaxiclient;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.analytics.HitBuilders;

import cn.pedant.SweetAlert.SweetAlertDialog;
import taxi.city.citytaxiclient.models.Order;
import taxi.city.citytaxiclient.service.ApiService;


/**
 * A placeholder fragment containing a simple view.
 */
public class OrderDetailsActivityFragment extends Fragment {

    private Order order;
    private SweetAlertDialog pDialog;
    Button btnNext;
    ApiService api = ApiService.getInstance();

    TextView tvDriverName;

    public OrderDetailsActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_order_details, container, false);

     //   getActivity().getSupportActionBar().setDisplayShowHomeEnabled(true);
        ((ActionBarActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getActivity().getIntent();
        order = (Order)intent.getExtras().getSerializable("DATA");

        TextView etAddressStart = (TextView)rootView.findViewById(R.id.editTextStartAddress);
        TextView tvFinishAddress = (TextView) rootView.findViewById(R.id.editTextFinishAddress);
        TextView tvWaitTime = (TextView)rootView.findViewById(R.id.textViewWaitingTime);
        TextView tvWaitSum = (TextView)rootView.findViewById(R.id.textViewWaitingSum);
        TextView tvDistance = (TextView)rootView.findViewById(R.id.textViewDistance);
        TextView tvSum = (TextView)rootView.findViewById(R.id.textViewSum);
        TextView tvTotalSum = (TextView)rootView.findViewById(R.id.textViewTotalSum);
        tvDriverName = (TextView)rootView.findViewById(R.id.textViewDriverName);
        btnNext = (Button) rootView.findViewById(R.id.buttonNext);

        btnNext.setVisibility(order.isActive() ? View.VISIBLE : View.GONE);

        if (order.isActive()) {
            App.getDefaultTracker().send(new HitBuilders.EventBuilder()
                    .setCategory("order")
                    .setAction("order details page")
                    .setLabel("Active order details page")
                    .build());
        } else {
            App.getDefaultTracker().send(new HitBuilders.EventBuilder()
                    .setCategory("order")
                    .setAction("order details page")
                    .setLabel("Order details page from history")
                    .build());
        }

        double waitSum = order.getWaitTimePrice();
        double sum = order.getSum();
        double totalSum = waitSum + sum;

        String waitTime = order.getWaitTime();
        if (waitTime.length() > 5) {
            waitTime = waitTime.substring(0, waitTime.length() - 3);
        }

        etAddressStart.setText(order.getStartName());
        tvFinishAddress.setText(order.getStopName());
        tvWaitTime.setText(waitTime);
        tvWaitSum.setText(String.valueOf((int)waitSum));
        tvDistance.setText(String.valueOf(order.getDistance()));
        tvSum.setText(String.valueOf((int)sum));
        tvTotalSum.setText(String.valueOf((int)totalSum));
        tvDriverName.setText(order.getDriver().getFullName());

        etAddressStart.setEnabled(false);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
                ft.replace(R.id.container, RatingFragment.newInstance(null, null), "rating");
                ft.commit();
            }
        });

        return rootView;
    }
}
