package taxi.city.citytaxiclient.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import taxi.city.citytaxiclient.OrderDetailsActivity;
import taxi.city.citytaxiclient.R;
import taxi.city.citytaxiclient.models.Order;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private ArrayList<Order> items;
    private int itemLayout;
    private final Context mContext;

    public OrderAdapter(ArrayList<Order> items, int layout, Context context) {
        this.items = items;
        this.itemLayout = layout;
        this.mContext = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mIdView;
        public TextView mAddressView;

        public ViewHolder(final View v) {
            super(v);
            mIdView = (TextView) itemView.findViewById(R.id.tv_order_id);
            mAddressView = (TextView) itemView.findViewById(R.id.tv_order_address);

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Order order = (Order) v.getTag();
                    Intent intent = new Intent(mContext, OrderDetailsActivity.class);
                    intent.putExtra("DATA", order);
                    mContext.startActivity(intent);
                }
            });
        }
    }

    @Override
    public OrderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        Order item = items.get(position);
        holder.mAddressView.setText(item.getStartName());
        holder.mIdView.setText("#" + item.getId());
        holder.itemView.setTag(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void setDataset(ArrayList<Order> dataset) {
        items = dataset;
        // This isn't working
        notifyItemRangeInserted(0, items.size());
        notifyDataSetChanged();
    }
}