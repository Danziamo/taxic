package taxi.city.citytaxiclient.fragments;


import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import taxi.city.citytaxiclient.R;
import taxi.city.citytaxiclient.models.GlobalSingleton;
import taxi.city.citytaxiclient.models.OnlineStatus;
import taxi.city.citytaxiclient.models.Order;
import taxi.city.citytaxiclient.models.OrderStatus;
import taxi.city.citytaxiclient.models.Role;
import taxi.city.citytaxiclient.models.User;
import taxi.city.citytaxiclient.networking.RestClient;
import taxi.city.citytaxiclient.networking.model.NOrder;
import taxi.city.citytaxiclient.utils.Constants;

public class MapsFragment extends BaseFragment {

    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private User user;

    public MapsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        mMapView = (MapView) view.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);

        user = GlobalSingleton.getInstance(getActivity()).currentUser;

        mMapView.onResume();// needed to get the map to display immediately

        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }


        mGoogleMap = mMapView.getMap();
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.setPadding(0, getPixelFromDpi(48), 0, getPixelFromDpi(48));

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    private int getPixelFromDpi(float dp) {
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
        return (int)px;
    }

    public void showOnMap(Location position) {
        mGoogleMap.addMarker(new MarkerOptions()
                .title("Какой то маркер")
                .position(new LatLng(position.getLatitude(), position.getLongitude())));
    }

    private void createNewOrder() {
        NOrder newOrder = new NOrder();
        newOrder.client = user.getId();
        newOrder.clientPhone = user.getPhone();
        newOrder.startName = "Tamasha";
        newOrder.tariff = Constants.DEFAULT_ORDER_TARIFF;
        newOrder.status = OrderStatus.NEW;
        newOrder.startPoint = "POINT (13, 13)";

        RestClient.getOrderService().createOrder(newOrder, new Callback<Order>() {
            @Override
            public void success(Order order, Response response) {
                Toast.makeText(getActivity(), "SUCCESS NEW ORDER", Toast.LENGTH_SHORT).show();
                GlobalSingleton.getInstance(getActivity()).currentOrder = order;
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getActivity(), "FAIL NEW ORDER", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getOrder(int orderId) {
        RestClient.getOrderService().getById(orderId, new Callback<Order>() {
            @Override
            public void success(Order order, Response response) {
                GlobalSingleton.getInstance(getActivity()).currentOrder = order;
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getActivity(), "FAIL FETCH ORDER", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void canceldOrder(int orderId) {
        RestClient.getOrderService().updateStatus(orderId, OrderStatus.CANCELED, new Callback<Order>() {
            @Override
            public void success(Order order, Response response) {
                GlobalSingleton.getInstance(getActivity()).currentOrder = null;
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getActivity(), "FAIL CANCEL ORDER", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getDrivers() {
        RestClient.getUserService().getDrivers(OnlineStatus.ONLINE, Role.DRIVER, new Callback<ArrayList<User>>() {
            @Override
            public void success(ArrayList<User> users, Response response) {

            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getActivity(), "Error fetching drivers", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
