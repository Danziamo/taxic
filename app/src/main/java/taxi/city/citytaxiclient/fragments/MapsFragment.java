package taxi.city.citytaxiclient.fragments;


import android.content.res.Resources;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.SwitchCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nineoldandroids.animation.Animator;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

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

    View view;

    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private User user;

    SwitchCompat orderTypeSwitcher;
    Button mainFunctionalButton;
    Button searchButton;
    Button cancelButton;

    YoYo.YoYoString animation;
    View animView;
    View createOrderPanel;
    View additionalPanel;
    View bottomPanel;
    View falseLayout;
    View searchViews;
    View onTheWayPanel;

    public static int ANIMATION_SPEED = 450;
    public static int CREATE_ORDER_START_POINT = 10;
    public static int TYPE_SWITCHER_START_POINT = 350;

    public MapsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_maps, container, false);

        animView         = view.findViewById(R.id.map_main_navigation_panel);
        createOrderPanel = view.findViewById(R.id.main_order_panel);
        additionalPanel  = view.findViewById(R.id.additional_panel);
        searchViews      = view.findViewById(R.id.search_views);
        bottomPanel      = view.findViewById(R.id.bottom_panel);
        falseLayout      = view.findViewById(R.id.false_layout);
        onTheWayPanel     = view.findViewById(R.id.on_the_way_panel);

        mainFunctionalButton = (Button)view.findViewById(R.id.main_functioanl_button);
        searchButton = (Button)view.findViewById(R.id.search_button);
        orderTypeSwitcher = (SwitchCompat)view.findViewById(R.id.etOrderTypeSwitcher);
        cancelButton = (Button)view.findViewById(R.id.main_cancel_button);

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

        mainFunctionalButton.setOnClickListener(mainFunctionalButtonListener);

        orderTypeSwitcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    additionalPanel.setVisibility(View.VISIBLE);
                    animation = YoYo.with(Techniques.SlideInUp)
                            .duration(ANIMATION_SPEED)
                            .startPoint(TYPE_SWITCHER_START_POINT)
                            .playOn(animView);
                } else {
                    additionalPanel.setVisibility(View.GONE);
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animView.setVisibility(View.VISIBLE);
                searchViews.setVisibility(View.INVISIBLE);
                cancelButton.setVisibility(View.GONE);
            }
        });

        bottomPanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invalidateAnimation();
            }
        });

        falseLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                invalidateAnimation();
            }
        });

        animView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return view;
    }

    public void invalidateAnimation(){
        createOrderPanel.setVisibility(View.GONE);
        additionalPanel.setVisibility(View.GONE);
        orderTypeSwitcher.setChecked(false);
        falseLayout.setVisibility(View.INVISIBLE);
        mainFunctionalButton.setText(getResources().getString(R.string.order_taxi));
    }


    View.OnClickListener mainFunctionalButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(mainFunctionalButton.getText().equals(getResources().getString(R.string.order_taxi))){
//                animView.setVisibility(View.INVISIBLE);
//                animation = YoYo.with(Techniques.SlideInUp)
//                        .duration(0)
//                        .interpolate(new AccelerateDecelerateInterpolator())
//                        .startPoint(CREATE_ORDER_START_POINT)
//                        .withListener(new Animator.AnimatorListener() {
//                            @Override
//                            public void onAnimationStart(Animator animation) {
//
//                            }
//
//                            @Override
//                            public void onAnimationEnd(Animator animation) {
//                                animView.setVisibility(View.VISIBLE);
//                                performAnimation();
//                            }
//
//                            @Override
//                            public void onAnimationCancel(Animator animation) {
//
//                            }
//
//                            @Override
//                            public void onAnimationRepeat(Animator animation) {
//
//                            }
//                        })
//                        .playOn(animView);
                performAnimation();
            }
            if(mainFunctionalButton.getText().equals(getResources().getString(R.string.issue_taxi))) {
                invalidateAnimation();
                animView.setVisibility(View.GONE);
                searchViews.setVisibility(View.VISIBLE);
                cancelButton.setVisibility(View.VISIBLE);
            }

        }
    };

    public void performAnimation(){
        createOrderPanel.setVisibility(View.VISIBLE);
        animation = YoYo.with(Techniques.SlideInUp)
                .duration(ANIMATION_SPEED)
                .startPoint(CREATE_ORDER_START_POINT)
                .withListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mainFunctionalButton.setText(getResources().getString(R.string.issue_taxi));
                        falseLayout.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                })
                .playOn(animView);
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

    private void cancelOrder(int orderId) {
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
