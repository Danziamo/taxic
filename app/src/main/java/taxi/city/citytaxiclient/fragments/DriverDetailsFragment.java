package taxi.city.citytaxiclient.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;

import taxi.city.citytaxiclient.R;
import taxi.city.citytaxiclient.core.Order;
import taxi.city.citytaxiclient.enums.OStatus;

import static taxi.city.citytaxiclient.R.id.imageButtonCallDriver;


/**
 * A placeholder fragment containing a simple view.
 */
public class DriverDetailsFragment extends Fragment implements View.OnClickListener /*implements View.OnClickListener/*/ {

    TextView tvLastName;
    TextView tvFirstName;
    TextView tvPhone;
    TextView tvCarBrand;
    TextView tvCarModel;
    TextView tvCarColor;
    TextView tvCarNumber;
    ImageButton imgBtnCallDriver;

    public DriverDetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_driver_details, container, false);
        Order order = Order.getInstance();

        tvFirstName = (TextView) rootView.findViewById(R.id.textViewFirstName);
        tvLastName = (TextView) rootView.findViewById(R.id.textViewLastName);
        tvPhone = (TextView) rootView.findViewById(R.id.textViewDriverPhone);
        tvCarBrand = (TextView) rootView.findViewById(R.id.textViewCarBrand);
        tvCarModel = (TextView) rootView.findViewById(R.id.textViewCarModel);
        tvCarColor = (TextView) rootView.findViewById(R.id.textViewCarColor);
        tvCarNumber = (TextView) rootView.findViewById(R.id.textViewCarNumber);
        imgBtnCallDriver = (ImageButton) rootView.findViewById(imageButtonCallDriver);

        tvFirstName.setText(order.driver.firstName);
        tvLastName.setText(order.driver.lastName);
        tvPhone.setText(order.driver.phone);
        tvCarBrand.setText(order.driver.carBrand);
        tvCarModel.setText(order.driver.carModel);
        tvCarNumber.setText(order.driver.carNumber);
        tvCarColor.setText(order.driver.carColor);
        imgBtnCallDriver.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {

    }
    /*private void updateViews() {
        if imgBtnCallDriver.setVisibility(View.GONE)
    }
    else if (mClient.statusequals(OStatus.ACCEPTED.toString())) {
        imgBtnCallDriver.setVisibility(View.VISIBLE);}
    else {
            imgBtnCallDriver.setVisibility(View.VISIBLE);
        }
    public void onClick(View v){

    }
*/
   /* switch (v.getId()){
        case imageButtonCallDriver:
        imgBtnCallDriver();
        break;
    }*/


}
