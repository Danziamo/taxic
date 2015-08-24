package taxi.city.citytaxiclient.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import taxi.city.citytaxiclient.core.OrderDetail;
import taxi.city.citytaxiclient.R;

public class OrderDetailsAdapter extends ArrayAdapter<OrderDetail> {

    private static class ViewHolder {
        TextView infotext;
        TextView address;
        TextView ordersum;
        TextView distance;
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
            viewHolder.infotext = (TextView) convertView.findViewById(R.id.tvInfoText);
            viewHolder.address = (TextView) convertView.findViewById(R.id.tvAddress);
            viewHolder.ordersum = (TextView) convertView.findViewById(R.id.tvOrderSum);
            viewHolder.distance = (TextView) convertView.findViewById(R.id.tvDistance);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

       /* viewHolder.id.setText(String.valueOf(orderDetail.id));
        viewHolder.address.setText("#" + String.valueOf(orderDetail.id) + ": " + orderDetail.addressStart);*/

        return convertView;
    }
}
