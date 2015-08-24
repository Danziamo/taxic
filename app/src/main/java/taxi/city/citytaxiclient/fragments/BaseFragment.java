package taxi.city.citytaxiclient.fragments;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.app.Fragment;

import cn.pedant.SweetAlert.SweetAlertDialog;
import taxi.city.citytaxiclient.BaseActivity;
import taxi.city.citytaxiclient.interfaces.ConfirmCallback;

public class BaseFragment extends Fragment {
    public void showProgress(String msg){
        BaseActivity activity = (BaseActivity) getActivity();
        activity.showProgress(msg);
    }

    public void hideProgress(){
        BaseActivity activity = (BaseActivity) getActivity();
        activity.hideProgress();
    }

    public void showConfirmDialog(String titleText, String confirmText, String cancelText, final ConfirmCallback callback){
        BaseActivity activity = (BaseActivity) getActivity();
        activity.showConfirmDialog(titleText, confirmText, cancelText, callback);
    }
}
