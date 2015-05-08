package taxi.city.citytaxiclient;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cn.pedant.SweetAlert.SweetAlertDialog;
import taxi.city.citytaxiclient.Adapters.OrderDetailsAdapter;
import taxi.city.citytaxiclient.Core.Order;
import taxi.city.citytaxiclient.Core.OrderDetail;
import taxi.city.citytaxiclient.Core.User;
import taxi.city.citytaxiclient.Enums.OStatus;
import taxi.city.citytaxiclient.Service.ApiService;

/**
 * A placeholder fragment containing a simple view.
 */
public class HistoryOrderFragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

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
    private int limit = 1;

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
        limit = 1;

        lvMain = (ListView) rootView.findViewById(R.id.orderList);

        swipeLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                try {
                    String text = ((TextView) view.findViewById(R.id.orderId)).getText().toString();
                    int orderId = Integer.valueOf(text);

                    for (int i = list.size() - 1; i >= 0; i -= 1) {
                        if (orderId == list.get(i).id) {
                            orderDetail = list.get(i);
                            break;
                        }
                    }
                    goOrderDetails(orderDetail);
                }
                catch (Exception e) {
                }
            }
        });
        fetchData();
        return rootView;
    }

    private void goOrderDetails(OrderDetail detail) {

    }

    private void InitListView(JSONArray array) {
        list.clear();
        try {
            for (int i=0; i < array.length(); ++i) {
                JSONObject row = array.getJSONObject(i);
                if (!row.has("status") || row.getString("status").equals(OStatus.CANCELED.toString()))
                    continue;
                OrderDetail details = new OrderDetail(row, user.id);
                list.add(details);

            }
            OrderDetailsAdapter adapter = new OrderDetailsAdapter(getActivity(), list);
            lvMain.setAdapter(adapter);
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
    public void onClick(View v) {

    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override public void run() {
                limit += 1;
                fetchData();
                swipeLayout.setRefreshing(false);
            }
        }, 5000);
    }

    public class FetchOrderTask extends AsyncTask<Void, Void, JSONArray> {

        FetchOrderTask() {}

        @Override
        protected JSONArray doInBackground(Void... params) {

            JSONArray array = null;
            try {
                array = new JSONArray();
                JSONObject result = api.getArrayRequest("orders/?client=" + user.id + "&status=finished&ordering=-id&limit=" + limit);
                if (result.getInt("status_code") == HttpStatus.SC_OK) {
                    JSONArray tempArray = result.getJSONArray("result");
                    for (int i = 0; i < tempArray.length() && i < 10; ++i) {
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


/*

public class MainActivity extends Activity implements OnRefreshListener {

    SwipeRefreshLayout swipeLayout;
    ListView orderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        orderList = (ListView) findViewById(R.id.orderList);

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    @Override
    public void onRefresh() {
        new Handler().postDelayed(new Runnable() {
            @Override public void run() {
                swipeLayout.setRefreshing(false);
            }
        }, 5000);
    }
}*/
