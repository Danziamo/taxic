package taxi.city.citytaxiclient.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import taxi.city.citytaxiclient.R;
import taxi.city.citytaxiclient.adapters.OrderAdapter;
import taxi.city.citytaxiclient.models.GlobalSingleton;
import taxi.city.citytaxiclient.models.Order;
import taxi.city.citytaxiclient.models.OrderStatus;
import taxi.city.citytaxiclient.models.User;
import taxi.city.citytaxiclient.networking.RestClient;

public class HistoryOrderFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    SwipeRefreshLayout swipeLayout;
    private ArrayList<Order> orderList;
    private User user;

    RecyclerView rvOrders;
    OrderAdapter orderAdapter;

    private int limit = 15;

    public HistoryOrderFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history_order, container, false);
        user = GlobalSingleton.getInstance(getActivity()).currentUser;
        limit = 15;

        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);


        rvOrders = (RecyclerView)view.findViewById(R.id.rvOrders);
        //rvOrders.addItemDecoration(new RecyclerViewSimpleDivider(getActivity()));
        rvOrders.setHasFixedSize(true);

        orderAdapter = new OrderAdapter(orderList, R.layout.item_order_history, getActivity());

        rvOrders.setAdapter(orderAdapter);
        rvOrders.setItemAnimator(new DefaultItemAnimator());
        rvOrders.setLayoutManager(new LinearLayoutManager(getActivity()));

        fetchOrders();
        return view;
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                limit += 5;
                fetchOrders();
                swipeLayout.setRefreshing(false);
            }
        }, 1000);
    }

    private void fetchOrders() {
        RestClient.getOrderService().getAll(user.getId(), OrderStatus.FINISHED, "-id", limit, new Callback<ArrayList<Order> >() {
            @Override
            public void success(ArrayList<Order> orders, Response response) {
                orderAdapter.setDataset(orders);
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getActivity(), "Не удалось получить данные с сервера", Toast.LENGTH_SHORT).show();
            }
        });
    }

}