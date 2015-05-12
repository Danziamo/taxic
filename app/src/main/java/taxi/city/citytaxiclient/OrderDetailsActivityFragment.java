package taxi.city.citytaxiclient;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import cn.pedant.SweetAlert.SweetAlertDialog;
import taxi.city.citytaxiclient.core.Order;
import taxi.city.citytaxiclient.core.OrderDetail;


/**
 * A placeholder fragment containing a simple view.
 */
public class OrderDetailsActivityFragment extends Fragment {


    private OrderDetail orderDetail;
    private Order order;
    private SweetAlertDialog pDialog;

    public OrderDetailsActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_order_details, container, false);
        order = Order.getInstance();

        Intent intent = getActivity().getIntent();
        orderDetail = (OrderDetail)intent.getExtras().getSerializable("DATA");

        EditText etAddressStart = (EditText)rootView.findViewById(R.id.editTextStartAddress);
        TextView tvWaitTime = (TextView)rootView.findViewById(R.id.textViewWaitingTime);
        TextView tvWaitSum = (TextView)rootView.findViewById(R.id.textViewWaitingSum);
        TextView tvDistance = (TextView)rootView.findViewById(R.id.textViewDistance);
        TextView tvSum = (TextView)rootView.findViewById(R.id.textViewSum);
        TextView tvTotalSum = (TextView)rootView.findViewById(R.id.textViewTotalSum);

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
        tvWaitTime.setText(waitTime);
        tvWaitSum.setText(String.valueOf((int)waitSum));
        tvDistance.setText(orderDetail.distance);
        tvSum.setText(String.valueOf((int)sum));
        tvTotalSum.setText(String.valueOf((int)totalSum));

        etAddressStart.setEnabled(false);

        return rootView;
    }
}
