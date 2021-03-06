package taxi.city.citytaxiclient.fragments;


import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.SwitchCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import taxi.city.citytaxiclient.LocationAddress;
import taxi.city.citytaxiclient.R;
import taxi.city.citytaxiclient.models.GlobalSingleton;
import taxi.city.citytaxiclient.models.OnlineStatus;
import taxi.city.citytaxiclient.models.Order;
import taxi.city.citytaxiclient.models.OrderStatus;
import taxi.city.citytaxiclient.models.Role;
import taxi.city.citytaxiclient.models.User;
import taxi.city.citytaxiclient.networking.RestClient;
import taxi.city.citytaxiclient.networking.model.CreateOrder;
import taxi.city.citytaxiclient.networking.model.NOrder;
import taxi.city.citytaxiclient.utils.Constants;
import taxi.city.citytaxiclient.utils.Helper;

public class MapsFragment extends BaseFragment {

    View view;

    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private User user;
    private Order mOrder;
    private LatLng cameraLocation;
    private String mAddressText;

    SwitchCompat orderTypeSwitcher;
    Button mainFunctionalButton;
    Button searchButton;
    Button cancelButton;
    Button falseMovementButton;

    YoYo.YoYoString animation;
    View animView;
    View createOrderPanel;
    View additionalPanel;
    View bottomPanel;
    View falseLayout;
    View searchViews;
    View onTheWayPanel;
    View globalView;
    View toolsPanel;
    View bigLinear;
    View easyLinear;
    View vipLinear;
    View chooseCarTypePanel;

    EditText etAddress;
    EditText etPhone;
    EditText etDescription;
    EditText etFixedPrice;
    EditText etEndAddress;
    TextView waitingTextView;
    TextView waitingPriceTextView;
    TextView distanceTextView;
    TextView distancePriceTextView;

    ImageView carSelector1;
    ImageView carSelector2;
    ImageView carSelector3;


    public static int ANIMATION_SPEED = 450;
    public static int CREATE_ORDER_START_POINT = 10;
    public static int TYPE_SWITCHER_START_POINT = 350;

    public MapsFragment() {

    }

    Handler globalTimerHandler = new Handler();
    Runnable globalTimerRunnable = new Runnable() {

        @Override
        public void run() {
            getDrivers();
            if (mOrder == null) return;
            getOrder(mOrder.getId());
            globalTimerHandler.postDelayed(this, 30 * 1000);
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_maps, container, false);

        animView            = view.findViewById(R.id.map_main_navigation_panel);
        createOrderPanel    = view.findViewById(R.id.main_order_panel);
        additionalPanel     = view.findViewById(R.id.additional_panel);
        searchViews         = view.findViewById(R.id.search_views);
        bottomPanel         = view.findViewById(R.id.bottom_panel);
        falseLayout         = view.findViewById(R.id.false_layout);
        onTheWayPanel       = view.findViewById(R.id.on_the_way_panel);
        globalView          = view.findViewById(R.id.global_view);
        toolsPanel          = view.findViewById(R.id.tools_panel);
        bigLinear           = view.findViewById(R.id.big_linear);
        easyLinear          = view.findViewById(R.id.easy_linear);
        vipLinear           = view.findViewById(R.id.vip_linear);
        chooseCarTypePanel  = view.findViewById(R.id.choose_car_type_panel);

        mainFunctionalButton = (Button)view.findViewById(R.id.main_functioanl_button);
        searchButton         = (Button)view.findViewById(R.id.search_button);
        orderTypeSwitcher    = (SwitchCompat)view.findViewById(R.id.etOrderTypeSwitcher);
        cancelButton         = (Button)view.findViewById(R.id.main_cancel_button);
        falseMovementButton  = (Button)view.findViewById(R.id.false_movement_button);

        etAddress = (EditText)view.findViewById(R.id.etAddress);
        etPhone = (EditText)view.findViewById(R.id.etClientPhone);
        etEndAddress = (EditText)view.findViewById(R.id.etEndLocation);
        etFixedPrice = (EditText)view.findViewById(R.id.etFixedPrice);
        etDescription = (EditText)view.findViewById(R.id.etDopInfo);

        waitingTextView = (TextView)view.findViewById(R.id.waitingTextView);
        waitingPriceTextView = (TextView)view.findViewById(R.id.waitingPriceTextView);
        distanceTextView = (TextView)view.findViewById(R.id.distanceTextView);
        distancePriceTextView = (TextView)view.findViewById(R.id.distancePriceTextView);

        carSelector1 = (ImageView)view.findViewById(R.id.car_selector1);
        carSelector2 = (ImageView)view.findViewById(R.id.car_selector2);
        carSelector3 = (ImageView)view.findViewById(R.id.car_selector3);

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

        mGoogleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                if (mOrder != null) return;
                setAddressText(null);
                cameraLocation = new LatLng(cameraPosition.target.latitude, cameraPosition.target.longitude);
                LocationAddress.getAddressFromLocation(cameraPosition.target.latitude, cameraPosition.target.longitude,
                        getActivity(), new GeocoderHandler());
            }
        });

        mainFunctionalButton.setOnClickListener(mainFunctionalButtonListener);

        orderTypeSwitcher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    additionalPanel.setVisibility(View.VISIBLE);
                    animation = YoYo.with(Techniques.SlideInUp)
                            .duration(ANIMATION_SPEED)
                          //  .startPoint(toolsPanel.getMeasuredHeight() + chooseCarTypePanel.getMeasuredHeight() + 10)
                            .startPoint(toolsPanel.getMeasuredHeight() + bottomPanel.getMeasuredHeight()/3)
                            .playOn(animView);
                } else {
                    additionalPanel.setVisibility(View.GONE);
                }
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // cancelOrder(mOrder.getId());
                cancelOrderAnimation();
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

        mOrder = GlobalSingleton.getInstance(getActivity()).currentOrder;

        etPhone.setText(user.getPhone());
        updateViews();
        globalTimerHandler.postDelayed(globalTimerRunnable, 0);



        bigLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchLinearBachground(R.id.big_linear);
            }
        });
        easyLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchLinearBachground(R.id.easy_linear);
            }
        });
        vipLinear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchLinearBachground(R.id.vip_linear);
            }
        });

        return view;
    }

    public void switchLinearBachground(int id){
        switch(id){
            case R.id.big_linear:
                bigLinear.setBackgroundColor(getResources().getColor(R.color.startBtnFinish));
                easyLinear.setBackgroundColor(getResources().getColor(R.color.addressBar));
                vipLinear.setBackgroundColor(getResources().getColor(R.color.addressBar));
                carSelector1.setVisibility(View.VISIBLE);
                carSelector2.setVisibility(View.INVISIBLE);
                carSelector3.setVisibility(View.INVISIBLE);
                break;
            case R.id.easy_linear:
                bigLinear.setBackgroundColor(getResources().getColor(R.color.addressBar));
                easyLinear.setBackgroundColor(getResources().getColor(R.color.startBtnFinish));
                vipLinear.setBackgroundColor(getResources().getColor(R.color.addressBar));
                carSelector1.setVisibility(View.INVISIBLE);
                carSelector2.setVisibility(View.VISIBLE);
                carSelector3.setVisibility(View.INVISIBLE);
                break;
            case R.id.vip_linear:
                bigLinear.setBackgroundColor(getResources().getColor(R.color.addressBar));
                easyLinear.setBackgroundColor(getResources().getColor(R.color.addressBar));
                vipLinear.setBackgroundColor(getResources().getColor(R.color.startBtnFinish));
                carSelector1.setVisibility(View.INVISIBLE);
                carSelector2.setVisibility(View.INVISIBLE);
                carSelector3.setVisibility(View.VISIBLE);
                break;
        }
    }
    private void setAddressText(String text) {
        mAddressText = text;
        if (text == null)
            etAddress.setText("Поиск адреса...");
        else
            etAddress.setText(text);
    }

    @Override
    public void onStart(){
        super.onStart();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //setFalseMovementButtonParams(-(mainFunctionalButton.getMeasuredHeight()/2));
            }
        },1);

    }

    public void invalidateAnimation(){
        createOrderPanel.setVisibility(View.GONE);
        additionalPanel.setVisibility(View.GONE);
        orderTypeSwitcher.setChecked(false);
        falseLayout.setVisibility(View.INVISIBLE);
        mainFunctionalButton.setText(getResources().getString(R.string.order_taxi));
    }

    int falseMovementButtonHeight = 0;
    int movementDistance = 0;
    View.OnClickListener mainFunctionalButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(mainFunctionalButton.getText().equals(getResources().getString(R.string.order_taxi))){
                performAnimation();
            }
            if(mainFunctionalButton.getText().equals(getResources().getString(R.string.issue_taxi))) {
               // createNewOrder();
                animateAfterCreationOfOrder();

            }
        }
    };

    public void setFalseMovementButtonParams(final int bottomMargin){
        falseMovementButton.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)falseMovementButton.getLayoutParams();
//                params.addRule(RelativeLayout.CENTER_HORIZONTAL);
//                params.addRule(RelativeLayout.ABOVE, R.id.map_main_navigation_panel);
                params.setMargins(0, 0, 0, getPixelFromDpi(bottomMargin));
                falseMovementButton.setLayoutParams(params);
            }
        },5);
    }



    public void performAnimation(){
        createOrderPanel.setVisibility(View.VISIBLE);
        animation = YoYo.with(Techniques.SlideInUp)
                .duration(ANIMATION_SPEED)
                .startPoint(CREATE_ORDER_START_POINT)
                .withListener(new com.nineoldandroids.animation.Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(com.nineoldandroids.animation.Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(com.nineoldandroids.animation.Animator animation) {
                        mainFunctionalButton.setText(getResources().getString(R.string.issue_taxi));
                        falseLayout.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onAnimationCancel(com.nineoldandroids.animation.Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(com.nineoldandroids.animation.Animator animation) {

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
        if (mGoogleMap != null) {
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(position.getLatitude(), position.getLongitude()), 15));
        }
    }

    private void animateAfterCreationOfOrder() {
        animView.setVisibility(View.INVISIBLE);
        falseLayout.setVisibility(View.VISIBLE);
        falseMovementButton.setVisibility(View.VISIBLE);

        falseMovementButtonHeight = mainFunctionalButton.getMeasuredHeight();

        movementDistance = (view.getMeasuredHeight()/2)
                - toolsPanel.getMeasuredHeight()
                - (falseMovementButton.getMeasuredHeight()/2);

        TranslateAnimation anim = new TranslateAnimation( 0, 0, 0, - movementDistance );
        anim.setDuration(800);
        anim.setFillAfter(true);
        anim.setInterpolator(new LinearInterpolator());
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                searchViews.setVisibility(View.VISIBLE);
                cancelButton.setVisibility(View.VISIBLE);
                falseLayout.setVisibility(View.INVISIBLE);
                falseMovementButton.clearAnimation();
                falseMovementButton.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        falseMovementButton.startAnimation(anim);
    }

    private void updateViews() {
        if (mOrder == null) {
            cancelButton.setVisibility(View.INVISIBLE);
            searchViews.setVisibility(View.INVISIBLE);
            animView.setVisibility(View.VISIBLE);
        } else if (mOrder.getStatus() == OrderStatus.NEW) {
            cancelButton.setVisibility(View.VISIBLE);
            searchViews.setVisibility(View.VISIBLE);
            animView.setVisibility(View.INVISIBLE);
        } else if (mOrder.getStatus() == OrderStatus.ACCEPTED) {

        } else if (mOrder.getStatus() == OrderStatus.WAITING) {
            onTheWayPanel.setVisibility(View.VISIBLE);
        } else if (mOrder.getStatus() == OrderStatus.ONTHEWAY) {
            onTheWayPanel.setVisibility(View.VISIBLE);
        } else if (mOrder.getStatus() == OrderStatus.PENDING) {
            onTheWayPanel.setVisibility(View.VISIBLE);
        } else {
            onTheWayPanel.setVisibility(View.VISIBLE);
        }
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            Bundle bundle = message.getData();
            locationAddress = bundle.getString("address");
            switch (message.what) {
                case 1:
                    setAddressText(locationAddress);
                    break;
                default:
                    setAddressText(null);
            }
        }
    }

    private void createNewOrder() {
        CreateOrder newOrder = new CreateOrder();
        newOrder.client = user.getId();
        newOrder.clientPhone = etPhone.getText().toString();
        newOrder.startName = mAddressText;
        newOrder.tariff = Constants.DEFAULT_ORDER_TARIFF;
        newOrder.status = OrderStatus.NEW;
        newOrder.startPoint = Helper.getFormattedLatLng(cameraLocation);
        newOrder.description = etDescription.getText().toString();

        String fixedPrice = etFixedPrice.getText().toString();
        if (fixedPrice.isEmpty())
            newOrder.fixedPrice = 0;
        else
            newOrder.fixedPrice = Double.valueOf(fixedPrice);
        newOrder.stopName = etEndAddress.getText().toString();

        showProgress("Создание");

        RestClient.getOrderService().createOrder(newOrder, new Callback<NOrder>() {
            @Override
            public void success(NOrder order, Response response) {
                hideProgress();
                animateAfterCreationOfOrder();
                Toast.makeText(getActivity(), "SUCCESS NEW ORDER", Toast.LENGTH_SHORT).show();
                mOrder = new Order(order);
                GlobalSingleton.getInstance(getActivity()).currentOrder = mOrder;
                updateViews();
            }

            @Override
            public void failure(RetrofitError error) {
                hideProgress();
                Toast.makeText(getActivity(), "FAIL NEW ORDER", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getOrder(int orderId) {
        RestClient.getOrderService().getById(orderId, new Callback<Order>() {
            @Override
            public void success(Order order, Response response) {
                if (order.getStatus() != OrderStatus.FINISHED && order.getStatus() != OrderStatus.CANCELED) {
                    GlobalSingleton.getInstance(getActivity()).currentOrder = order;
                    mOrder = order;
                    setCounter(order);
                } else {
                    mOrder = null;
                    GlobalSingleton.getInstance(getActivity()).currentOrder = null;
                    setCounter(null);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getActivity(), "FAIL FETCH ORDER", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setCounter(Order order) {
        if (order != null) {
            waitingTextView.setText(order.getWaitTime());
            waitingPriceTextView.setText(String.valueOf((int) order.getWaitTimePrice()));
            distanceTextView.setText(String.valueOf(order.getDistance()));
            distancePriceTextView.setText(String.valueOf((int) order.getTravelSum()));
        } else {
            waitingTextView.setText(null);
            waitingPriceTextView.setText(null);
            distanceTextView.setText(null);
            distancePriceTextView.setText(null);
        }
    }

    private void cancelOrderAnimation() {
        animView.setVisibility(View.VISIBLE);
        searchViews.setVisibility(View.INVISIBLE);
        cancelButton.setVisibility(View.GONE);
        invalidateAnimation();
        falseMovementButton.setVisibility(View.INVISIBLE);
       // setFalseMovementButtonParams(-(falseMovementButtonHeight / 2));
    }

    private void cancelOrder(int orderId) {
        showProgress("Отмена");
        RestClient.getOrderService().updateStatus(orderId, OrderStatus.CANCELED, new Callback<Order>() {
            @Override
            public void success(Order order, Response response) {
                hideProgress();
                cancelOrderAnimation();
                GlobalSingleton.getInstance(getActivity()).currentOrder = null;
                mOrder = null;
                updateViews();
            }

            @Override
            public void failure(RetrofitError error) {
                hideProgress();
                Toast.makeText(getActivity(), "FAIL CANCEL ORDER", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getDrivers() {
        if (mOrder == null || mOrder.getStatus() == OrderStatus.NEW) {
            RestClient.getUserService().getDrivers(OnlineStatus.ONLINE, Role.DRIVER, new Callback<ArrayList<User>>() {
                @Override
                public void success(ArrayList<User> users, Response response) {
                    if (users.size() == 0) return;
                    if (mGoogleMap == null) return;
                    mGoogleMap.clear();
                    for (int i = 0; i < users.size(); ++i) {
                        User driver = users.get(i);
                        LatLng position = Helper.getLatLng(driver.getCurPosition());
                        if (driver.getOnlineStatus() == OnlineStatus.EXITED || position == null)
                            continue;
                        mGoogleMap.addMarker(new MarkerOptions()
                                .position(position)
                                .icon(driver.getOnlineStatus() == OnlineStatus.ONLINE
                                        ? BitmapDescriptorFactory.fromResource(R.drawable.car)
                                        : BitmapDescriptorFactory.fromResource(R.drawable.car_offline)));
                    }
                }

                @Override
                public void failure(RetrofitError error) {
                    Toast.makeText(getActivity(), "Error fetching drivers", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
