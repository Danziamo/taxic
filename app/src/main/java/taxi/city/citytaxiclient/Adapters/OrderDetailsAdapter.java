package taxi.city.citytaxiclient.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import taxi.city.citytaxiclient.Core.OrderDetail;
import taxi.city.citytaxiclient.R;

public class OrderDetailsAdapter extends ArrayAdapter<OrderDetail> {

    private static class ViewHolder {
        TextView id;
        TextView address;
    }

    public OrderDetailsAdapter(Context context, ArrayList<OrderDetail> orderDetails) {
        super(context, R.layout.activity_order_list_item, orderDetails);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        OrderDetail orderDetail = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.activity_order_list_item, parent, false);
            viewHolder.id = (TextView) convertView.findViewById(R.id.orderId);
            viewHolder.address = (TextView) convertView.findViewById(R.id.orderAddress);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.id.setText(String.valueOf(orderDetail.id));
        viewHolder.address.setText("#" + String.valueOf(orderDetail.id) + ": " + orderDetail.addressStart);

        return convertView;
    }
}
