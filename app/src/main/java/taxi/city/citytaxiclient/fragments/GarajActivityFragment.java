package taxi.city.citytaxiclient.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import taxi.city.citytaxiclient.R;


/**
 * A placeholder fragment containing a simple view.
 */
public class GarajActivityFragment extends Fragment {

    public GarajActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_garaj, container, false);

        return rootView;
    }
}
