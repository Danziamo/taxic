package taxi.city.citytaxiclient;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class StartFragment1 extends Fragment {

    AppCompatSpinner spinner;

    public StartFragment1() {
        // Required empty public constructor

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_start_fragment1, container, false);

        String[] ITEMS = {"+996", "+7", "+998"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, ITEMS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner = (AppCompatSpinner) view.findViewById(R.id.spCodNumber);
        spinner.setAdapter(adapter);
        return view;
    }


}
