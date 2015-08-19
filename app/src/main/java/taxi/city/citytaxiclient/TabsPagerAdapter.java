package taxi.city.citytaxiclient;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

import taxi.city.citytaxiclient.fragments.AccountDetailsActivityFragment;
import taxi.city.citytaxiclient.fragments.HistoryOrderFragment;
import taxi.city.citytaxiclient.fragments.UserDetailsActivityFragment;

public class TabsPagerAdapter extends FragmentStatePagerAdapter {

    private final String[] TITLES = { "Счёт", "Личные", "История" };
    private final int[] ICONS = {R.drawable.ic_action_account, R.drawable.ic_action_personal ,R.drawable.ic_action_history};
    private final int[] SELECTED_ICONS = {R.drawable.ic_action_account_selected, R.drawable.ic_action_personal_selected, R.drawable.ic_action_history_selected};
    SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return ICONS.length;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new AccountDetailsActivityFragment();
            case 1:
                return new UserDetailsActivityFragment();
            default:
                return new HistoryOrderFragment();
        }

    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        registeredFragments.put(position, fragment);
        return fragment;
    }

    public Fragment getRegisteredFragment(int position) {
        return registeredFragments.get(position);
    }
}
