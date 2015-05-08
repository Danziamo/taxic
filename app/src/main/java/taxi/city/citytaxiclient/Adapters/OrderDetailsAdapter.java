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
        TextView fixedPrice;
        View separator;
    }

    public OrderDetailsAdapter(Context context, ArrayList<OrderDetail> clients) {
        super(context, R.layout.activity_order_list_item, clients);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        OrderDetail client = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.activity_order_list_item, parent, false);
            viewHolder.id = (TextView) convertView.findViewById(R.id.orderId);
            viewHolder.address = (TextView) convertView.findViewById(R.id.orderAddress);
            viewHolder.fixedPrice = (TextView) convertView.findViewById(R.id.orderFixedPrice);
            viewHolder.separator = (View)convertView.findViewById(R.id.viewSeparator);


            switch (client.status) {
                case "finished":
                case "canceled":
                    viewHolder.id.setTextColor(Color.RED);
                    break;
                case "new":
                    viewHolder.id.setTextColor(Color.GREEN);
                    break;
                default:
                    viewHolder.id.setTextColor(Color.rgb(160,32,240));
                    break;
            }
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        double mFixedPrice = 0;
        try {
            mFixedPrice = Double.valueOf(client.fixedPrice);
        } catch (Exception e) {
            mFixedPrice = 0;
        }

        viewHolder.id.setText(String.valueOf(client.id));
        viewHolder.address.setText(client.addressStart);

        if (mFixedPrice >= 50) {
            viewHolder.separator.setVisibility(View.VISIBLE);
            viewHolder.fixedPrice.setText(String.valueOf((int)mFixedPrice) + "сом");
            viewHolder.fixedPrice.setTextColor(this.getContext().getResources().getColor(R.color.red));

        }
        return convertView;
    }
}
