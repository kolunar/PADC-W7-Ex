package com.padc.aml.myanmarattractions.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.util.Pair;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.greenrobot.event.EventBus;
import com.padc.aml.myanmarattractions.MyanmarAttractionsApp;
import com.padc.aml.myanmarattractions.R;
import com.padc.aml.myanmarattractions.adapters.AttractionAdapter;
import com.padc.aml.myanmarattractions.controllers.UserController;
import com.padc.aml.myanmarattractions.data.models.AttractionModel;
import com.padc.aml.myanmarattractions.data.models.UserModel;
import com.padc.aml.myanmarattractions.data.persistence.AttractionsContract;
import com.padc.aml.myanmarattractions.data.vos.AttractionVO;
import com.padc.aml.myanmarattractions.dialogs.SharedDialog;
import com.padc.aml.myanmarattractions.events.DataEvent;
import com.padc.aml.myanmarattractions.utils.MMFontUtils;
import com.padc.aml.myanmarattractions.utils.MyanmarAttractionsConstants;
import com.padc.aml.myanmarattractions.views.holders.AttractionViewHolder;
import com.padc.aml.myanmarattractions.views.pods.ViewPodAccountControl;
import com.padc.aml.myanmarattractions.views.pods.ViewPodLogoutUser;

public class HomeActivity extends AppCompatActivity
        implements AttractionViewHolder.ControllerAttractionItem,
        UserController,
        LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.rv_attractions)
    RecyclerView rvAttractions;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.fab)
    FloatingActionButton fab;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;

    @BindView(R.id.navigation_view)
    NavigationView navigationView;

    private AttractionAdapter mAttractionAdapter;

    private ViewPodAccountControl vpAccountControl;

    private BroadcastReceiver mDataLoadedBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            //TODO instructions when the new data is ready.
            String extra = intent.getStringExtra("key-for-extra");
            Toast.makeText(getApplicationContext(), "Extra : " + extra, Toast.LENGTH_SHORT).show();

            List<AttractionVO> newAttractionList = AttractionModel.getInstance().getAttractionList();
            mAttractionAdapter.setNewData(newAttractionList);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this, this);

        setSupportActionBar(toolbar);

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu_black_24dp);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Menu leftMenu = navigationView.getMenu();
        MMFontUtils.applyMMFontToMenu(leftMenu);

        vpAccountControl = (ViewPodAccountControl) navigationView.getHeaderView(0);
        vpAccountControl.setUserController(this);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        UserModel.getInstance().init();

        List<AttractionVO> attractionList = AttractionModel.getInstance().getAttractionList();
        mAttractionAdapter = new AttractionAdapter(attractionList, this);
        rvAttractions.setAdapter(mAttractionAdapter);

        int gridColumnSpanCount = getResources().getInteger(R.integer.attraction_list_grid);
        rvAttractions.setLayoutManager(new GridLayoutManager(getApplicationContext(), gridColumnSpanCount));

        getSupportLoaderManager().initLoader(MyanmarAttractionsConstants.ATTRACTION_LIST_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        switch (id){
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            case R.id.action_settings:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(mDataLoadedBroadcastReceiver, new IntentFilter(AttractionModel.BROADCAST_DATA_LOADED));

        EventBus eventBus = EventBus.getDefault();
        if (!eventBus.isRegistered(this)) {
            eventBus.register(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mDataLoadedBroadcastReceiver);

        EventBus eventBus = EventBus.getDefault();
        eventBus.unregister(this);
    }

    @Override
    public void onTapAttraction(AttractionVO attraction, ImageView ivAttraction) {
        Intent intent = AttractionDetailActivity.newIntent(attraction.getTitle());
        ActivityOptionsCompat activityOptions = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                new Pair(ivAttraction, getString(R.string.attraction_list_detail_transition_name)));
        ActivityCompat.startActivity(this, intent, activityOptions.toBundle());
    }

    public void onEventMainThread(DataEvent.AttractionDataLoadedEvent event) {
        String extra = event.getExtraMessage();
        Toast.makeText(getApplicationContext(), "Extra : " + extra, Toast.LENGTH_SHORT).show();

        List<AttractionVO> newAttractionList = AttractionModel.getInstance().getAttractionList();
        //List<AttractionVO> newAttractionList = event.getAttractionList();
        mAttractionAdapter.setNewData(newAttractionList);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this,
                AttractionsContract.AttractionEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        List<AttractionVO> attractionList = new ArrayList<>();
        if (data != null && data.moveToFirst()) {
            do {
                AttractionVO attraction = AttractionVO.parseFromCursor(data);
                attraction.setImages(AttractionVO.loadAttractionImagesByTitle(attraction.getTitle()));
                attractionList.add(attraction);
            } while (data.moveToNext());
        }

        Log.d(MyanmarAttractionsApp.TAG, "Retrieved attractions : "+attractionList.size());
        mAttractionAdapter.setNewData(attractionList);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onTapLogin() {
        Toast.makeText(MyanmarAttractionsApp.getContext(), "HomeActivity::onTapLogin", Toast.LENGTH_LONG).show();
        Intent intent = AccountControlActivity.newIntent(AccountControlActivity.NAVIGATE_TO_LOGIN);
        startActivityForResult(intent, AccountControlActivity.RC_ACCOUNT_CONTROL_LOGIN);
    }

    @Override
    public void onTapRegister() {
        Intent intent = AccountControlActivity.newIntent(AccountControlActivity.NAVIGATE_TO_REGISTER);
        //startActivity(intent);
        startActivityForResult(intent, AccountControlActivity.RC_ACCOUNT_CONTROL_REGISTER);
    }

    @Override
    public void onTapLogout() {
        SharedDialog.confirmYesNoWithTheme(this, getString(R.string.msg_confirm_logout), new SharedDialog.YesNoConfirmDelegate() {
            @Override
            public void onConfirmYes() {
                UserModel.getInstance().logout();
            }

            @Override
            public void onConfirmNo() {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AccountControlActivity.RC_ACCOUNT_CONTROL_REGISTER) {
            if (resultCode == RESULT_OK) {
                boolean isRegisterSuccess = data.getBooleanExtra(AccountControlActivity.IR_IS_REGISTER_SUCCESS, false);
                if (isRegisterSuccess) {
                    SharedDialog.promptMsgWithTheme(this, getString(R.string.msg_welcome_new_user));

                    DataEvent.RefreshUserLoginStatusEvent event = new DataEvent.RefreshUserLoginStatusEvent();
                    EventBus.getDefault().post(event);
                }
            }
        }
        else if (requestCode == AccountControlActivity.RC_ACCOUNT_CONTROL_LOGIN) {
            if (resultCode == RESULT_OK) {
                boolean isLoginSuccess = data.getBooleanExtra(AccountControlActivity.IR_IS_LOGIN_SUCCESS, false);
                if (isLoginSuccess) {
                    //SharedDialog.promptMsgWithTheme(this, getString(R.string.msg_welcome_new_user));

                    DataEvent.RefreshUserLoginStatusEvent event = new DataEvent.RefreshUserLoginStatusEvent();
                    EventBus.getDefault().post(event);
                }
            }
        }
    }
}