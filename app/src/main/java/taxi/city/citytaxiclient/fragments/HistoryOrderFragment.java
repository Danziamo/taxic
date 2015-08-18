package taxi.city.citytaxiclient.fragments;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import taxi.city.citytaxiclient.OrderDetailsActivity;
import taxi.city.citytaxiclient.R;
import taxi.city.citytaxiclient.core.OrderDetail;
import taxi.city.citytaxiclient.enums.OStatus;
import taxi.city.citytaxiclient.models.GlobalSingleton;
import taxi.city.citytaxiclient.models.Order;
import taxi.city.citytaxiclient.models.OrderStatus;
import taxi.city.citytaxiclient.models.User;
import taxi.city.citytaxiclient.networking.RestClient;
import taxi.city.citytaxiclient.service.ApiService;

public class HistoryOrderFragment extends ListFragment implements SwipeRefreshLayout.OnRefreshListener {

    SwipeRefreshLayout swipeLayout;

    private ArrayList<OrderDetail> list = new ArrayList<>();
    private ArrayList<Order> orderList;
    private User user;

    ListView lvMain;
    private int limit = 15;

    public HistoryOrderFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_history_order, container, false);
        user = GlobalSingleton.getInstance(getActivity()).currentUser;
        limit = 15;

        swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        lvMain = (ListView) rootView.findViewById(android.R.id.list);
        fetchOrders();
        return rootView;
    }

    private void goOrderDetails(Order order) {
        Intent intent = new Intent(getActivity(), OrderDetailsActivity.class);
        intent.putExtra("DATA", order);
        startActivity(intent);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        String text = ((TextView) v).getText().toString();
        String[] textArray = text.split(" ");
        Order order = new Order();
        int orderId = Integer.valueOf(textArray[0].substring(1, textArray[0].length()));
        for (int i = orderList.size() - 1; i >= 0; i -= 1) {
            if (orderId == orderList.get(i).getId()) {
                order = orderList.get(i);
                break;
            }
        }
        if (order != null) {
            goOrderDetails(order);
        }
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
        RestClient.getOrderService().getAll(user.getId(), OrderStatus.FINISHED, "-id", limit, new Callback<ArrayList<Order>>() {
            @Override
            public void success(ArrayList<Order> orders, Response response) {
                InitListView(orders);
                orderList = orders;
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getActivity(), "Не удалось получить данные с сервера", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void InitListView(ArrayList<Order> list) {
        ArrayList<String> alist = new ArrayList<>();
        for (int i=0; i < list.size(); ++i) {
            Order order = list.get(i);
            alist.add("#" + String.valueOf(order.getId()) + " " +order.getStartName());

        }
        ArrayAdapter adapter = new ArrayAdapter(getActivity(), R.layout.custom_simple_list_item, alist);
        setListAdapter(adapter);
    }

}