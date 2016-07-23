package com.padc.aml.myanmarattractions.views.pods;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import com.padc.aml.myanmarattractions.R;
import com.padc.aml.myanmarattractions.controllers.UserController;
import com.padc.aml.myanmarattractions.data.models.UserModel;
import com.padc.aml.myanmarattractions.events.DataEvent;

/**
 * Created by aung on 7/6/16.
 */
public class ViewPodAccountControl extends FrameLayout {

    @BindView(R.id.vp_login_user)
    ViewPodLoginUser vpLoginUser;

    @BindView(R.id.vp_logout_user)
    ViewPodLogoutUser vpLogoutUser;

    public ViewPodAccountControl(Context context) {
        super(context);
    }

    public ViewPodAccountControl(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ViewPodAccountControl(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this, this);

        EventBus eventBus = EventBus.getDefault();
        if (!eventBus.isRegistered(this)) {
            eventBus.register(this);
        }

        refreshUserLoginStatus();
    }

    private void refreshUserLoginStatus() {
        boolean isUserLogin = UserModel.getInstance().isUserLogin();
        vpLogoutUser.setVisibility(isUserLogin ? View.GONE : View.VISIBLE);
        vpLoginUser.setVisibility(isUserLogin ? View.VISIBLE : View.GONE);

        if (isUserLogin) {
            vpLoginUser.setData(UserModel.getInstance().getLoginUser());
        }
    }

    public void setUserController(UserController userController) {
        vpLogoutUser.setController(userController);
        vpLoginUser.setController(userController);
    }

    public void onEventMainThread(DataEvent.RefreshUserLoginStatusEvent event) {
        refreshUserLoginStatus();
    }
}