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
import taxi.city.citytaxiclient.OrderDetailsActivity;
import taxi.city.citytaxiclient.R;
import taxi.city.citytaxiclient.core.Order;
import taxi.city.citytaxiclient.core.OrderDetail;
import taxi.city.citytaxiclient.core.User;
import taxi.city.citytaxiclient.enums.OStatus;
import taxi.city.citytaxiclient.service.ApiService;

/**
 * A placeholder fragment containing a simple view.
 */
public class HistoryOrderFragment extends ListFragment implements SwipeRefreshLayout.OnRefreshListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    SwipeRefreshLayout swipeLayout;

    private ArrayList<OrderDetail> list = new ArrayList<>();
    private Order order = Order.getInstance();
    private ApiService api = ApiService.getInstance();
    private User user;
    private FetchOrderTask mFetchTask = null;
    private SweetAlertDialog pDialog;

    private OrderDetail orderDetail;
    ListView lvMain;
    private int limit = 15;

    public static HistoryOrderFragment newInstance(int position) {
        HistoryOrderFragment fragment = new HistoryOrderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, String.valueOf(position));
        fragment.setArguments(args);
        return fragment;
    }

    public HistoryOrderFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_history_order, container, false);
        user = User.getInstance();
        limit = 15;

        swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        lvMain = (ListView) rootView.findViewById(android.R.id.list);
        fetchData();
        return rootView;
    }

    private void goOrderDetails(OrderDetail detail) {
        Intent intent = new Intent(getActivity(), OrderDetailsActivity.class);
        intent.putExtra("DATA", detail);
        startActivity(intent);
    }

    private void InitListView(JSONArray array) {
        list.clear();
        ArrayList<String> alist = new ArrayList<>();
        try {
            for (int i=0; i < array.length(); ++i) {
                JSONObject row = array.getJSONObject(i);
                if (!row.has("status") || row.getString("status").equals(OStatus.CANCELED.toString()))
                    continue;
                OrderDetail details = new OrderDetail(row, user.id);
                alist.add("#" + String.valueOf(details.id) + " " +details.addressStart);
                list.add(details);

            }
            //OrderDetailsAdapter adapter = new OrderDetailsAdapter(getActivity(), list);
            ArrayAdapter adapter = new ArrayAdapter(getActivity(), R.layout.custom_simple_list_item, alist);
            setListAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void fetchData() {
        if (mFetchTask != null) {
            return;
        }

        //showProgress(true);
        mFetchTask = new FetchOrderTask();
        mFetchTask.execute((Void) null);

    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        //Toast.makeText(getActivity(), "Tada", Toast.LENGTH_LONG).show();

        /*String text = ((TextView) v.findViewById(R.id.orderId)).getText().toString();
        int orderId = Integer.valueOf(text);

        for (int i = list.size() - 1; i >= 0; i -= 1) {
            if (orderId == list.get(i).id) {
                orderDetail = list.get(i);
                break;
            }
        }
        goOrderDetails(orderDetail);*/

        String text = ((TextView) v).getText().toString();
        String[] textArray = text.split(" ");
        int orderId = Integer.valueOf(textArray[0].substring(1, textArray[0].length()));
        for (int i = list.size() - 1; i >= 0; i -= 1) {
            if (orderId == list.get(i).id) {
                orderDetail = list.get(i);
                break;
            }
        }
        goOrderDetails(orderDetail);
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override public void run() {
                limit += 5;
                fetchData();
                swipeLayout.setRefreshing(false);
            }
        }, 1000);
    }

    private class FetchOrderTask extends AsyncTask<Void, Void, JSONArray> {

        FetchOrderTask() {}

        @Override
        protected JSONArray doInBackground(Void... params) {

            JSONArray array = null;
            try {
                array = new JSONArray();
                JSONObject result = api.getArrayRequest("orders/?client=" + user.id + "&status=finished&ordering=-id&limit=" + limit);
                if (result.getInt("status_code") == HttpStatus.SC_OK) {
                    JSONArray tempArray = result.getJSONArray("result");
                    for (int i = 0; i < tempArray.length(); ++i) {
                        array.put(tempArray.getJSONObject(i));
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
                array = null;
            }

            return array;
        }

        @Override
        protected void onPostExecute(JSONArray result) {
            mFetchTask = null;
            if (result != null) {
                InitListView(result);
            } else {
                Toast.makeText(getActivity(), "Не удалось получить данные с сервера", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onCancelled() {
            mFetchTask = null;
        }
    }

}