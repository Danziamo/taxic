package taxi.city.citytaxiclient.fragments;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import org.w3c.dom.Text;

import cn.pedant.SweetAlert.SweetAlertDialog;
import taxi.city.citytaxiclient.R;
import taxi.city.citytaxiclient.core.Order;
import taxi.city.citytaxiclient.enums.OStatus;
import taxi.city.citytaxiclient.utils.Helper;

//import static taxi.city.citytaxiclient.R.id.imageButtonCallDriver;


/**
 * A placeholder fragment containing a simple view.
 */
public class DriverDetailsFragment extends Fragment /*implements View.OnClickListener*/ /*implements View.OnClickListener/*/ {

    TextView tvLastName;
    TextView tvFirstName;
    TextView tvPhone;
    TextView tvCarBrand;
    TextView tvCarModel;
    TextView tvCarColor;
    TextView tvCarNumber;
    TextView tvRating;
    RatingBar ratingBar;
    ImageButton imgBtnCallDriver;
    Order order = Order.getInstance();

    public DriverDetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_driver_details, container, false);


        tvFirstName = (TextView) rootView.findViewById(R.id.textViewFirstName);
        tvLastName = (TextView) rootView.findViewById(R.id.textViewLastName);
        tvPhone = (TextView) rootView.findViewById(R.id.textViewDriverPhone);
        tvCarBrand = (TextView) rootView.findViewById(R.id.textViewCarBrand);
      //  tvCarModel = (TextView) rootView.findViewById(R.id.textViewCarModel);
        tvCarColor = (TextView) rootView.findViewById(R.id.textViewCarColor);
        tvCarNumber = (TextView) rootView.findViewById(R.id.textViewCarNumber);
       // imgBtnCallDriver = (ImageButton) rootView.findViewById(imageButtonCallDriver);
        ratingBar = (RatingBar)rootView.findViewById(R.id.ratingBarDriver);
       // tvRating = (TextView) rootView.findViewById(R.id.textViewRating);

        tvFirstName.setText(order.driver.firstName);
        tvLastName.setText(order.driver.lastName);
        tvPhone.setText(order.driver.phone);
        tvCarBrand.setText(order.driver.carBrand);
        tvCarModel.setText(order.driver.carModel);
        tvCarNumber.setText(order.driver.carNumber);
        tvCarColor.setText(order.driver.carColor);
       // imgBtnCallDriver.setOnClickListener(this);
        ratingBar.setRating(order.driver.rating);
        tvRating.setText(Helper.getRatingText(order.driver.rating));
        return rootView;
    }

  /*  @Override
    public void onClick(View v) {
        switch (v.getId()){
            case imageButtonCallDriver:
                callDriver();
                break;
        }
    }*/

    private void callDriver() {
        SweetAlertDialog pDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE);
        pDialog.setTitleText("Вы хотите позвонить?")
                .setContentText(order.driver.phone)
                .setConfirmText("Позвонить")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + order.driver.phone));
                        startActivity(callIntent);
                        sDialog.dismissWithAnimation();
                    }
                })
                .setCancelText("Отмена")
                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();
    }
}

