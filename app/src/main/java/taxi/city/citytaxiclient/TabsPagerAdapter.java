package taxi.city.citytaxiclient;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.astuetz.PagerSlidingTabStrip;

public class TabsPagerAdapter extends FragmentPagerAdapter implements PagerSlidingTabStrip.IconTabProvider {

    private final String[] TITLES = { "Счёт", "Личные", "История" };
    private final int[] ICONS = {R.drawable.ic_action_account, R.drawable.ic_action_personal ,R.drawable.ic_action_history};
    private final int[] SELECTED_ICONS = {R.drawable.ic_action_account_selected, R.drawable.ic_action_personal_selected, R.drawable.ic_action_history_selected};

    public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return ICONS.length;
    }

    @Override
    public int getPageIconResId(int position) {
        return ICONS[position];
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return AccountDetailsActivityFragment.newInstance(position);
            case 1:
                return UserDetailsActivityFragment.newInstance(position);
            default:
                return AccountDetailsActivityFragment.newInstance(position);
        }

    }
}
