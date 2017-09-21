package com.lvqingyang.wifisharing.Wifi.share;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.lvqingyang.wifisharing.BuildConfig;
import com.lvqingyang.wifisharing.R;
import com.lvqingyang.wifisharing.base.BaseFragment;
import com.lvqingyang.wifisharing.base.MyDialog;
import com.lvqingyang.wifisharing.bean.ConnectDevice;
import com.skyfishjy.library.RippleBackground;

import java.util.ArrayList;
import java.util.List;

import frame.tool.SolidRVBaseAdapter;

/**
 * Author：LvQingYang
 * Date：2017/8/26
 * Email：biloba12345@gamil.com
 * Github：https://github.com/biloba123
 * Info：
 */
public class ShareHotspotFragment extends BaseFragment {

    /**
     * view
     */


     /**
     * fragment
     */


     /**
     * data
     */
     private WifiHotUtil mWifiHotUtil;

    /**
     * tag
     */
    private static final String TAG = "ShareHotspotFragment";
    private Button mBtnOpenHotspot;
    private LinearLayout mLlClosed;
    private RelativeLayout mLlOpening;
    private RippleBackground mRbLeft;
    private RippleBackground mRbRight;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mTvName;
    private LinearLayout mLlNoDevice;
    private RecyclerView mRvDevice;
    private List<ConnectDevice> mConnectDeviceList=new ArrayList<ConnectDevice>();
    private SolidRVBaseAdapter mAdapter;


    public static ShareHotspotFragment newInstance() {
        
        Bundle args = new Bundle();
        
        ShareHotspotFragment fragment = new ShareHotspotFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void showConfigHotspotDialog(){
        View view = LayoutInflater.from(getActivity())
                .inflate(R.layout.dialog_config_hotspot, null);
        final LinearLayout llpwd = (LinearLayout) view.findViewById(R.id.ll_pwd);
        final Switch swshare = (Switch) view.findViewById(R.id.sw_share);
        final EditText etpwd = (EditText) view.findViewById(R.id.et_pwd);
        final Switch swpwd = (Switch) view.findViewById(R.id.sw_pwd);
        final EditText etname = (EditText) view.findViewById(R.id.et_name);
        llpwd.setVisibility(View.GONE);

        swshare.setChecked(true);

        etname.setText(mWifiHotUtil.getValidApSsid());
        etpwd.setText(mWifiHotUtil.getValidPassword());

        MyDialog dialog = new MyDialog(getActivity())
                .setTitle(R.string.personal_hotspot)
                .setView(view)
                .setPosBtn(R.string.create, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String psd = etpwd.getText().toString();
                        String name = etname.getText().toString();
                        if (swpwd.isChecked()) {
                            //有密码
                            mWifiHotUtil.turnOnWifiAp(name, psd, WifiHotUtil.WifiSecurityType.WIFICIPHER_WPA2);
                            if (swshare.isChecked()) {

                            }
                        } else {
                            mWifiHotUtil.turnOnWifiAp(name, null, WifiHotUtil.WifiSecurityType.WIFICIPHER_NOPASS);
                        }
                    }
                })
                .setNegBtn(android.R.string.cancel, null);
        dialog.show(getFragmentManager());

        final TextView tvPos = dialog.getTvpos();
        swpwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    llpwd.setVisibility(View.VISIBLE);
                    enablePosTv(etpwd, tvPos, 8);
                } else {
                    llpwd.setVisibility(View.GONE);
                    tvPos.setEnabled(true);
                    tvPos.setTextColor(getResources().getColor(R.color.colorAccent));
                }
            }
        });

        etpwd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                enablePosTv(etpwd, tvPos, 8);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        etname.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                enablePosTv(etname, tvPos, 1);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public boolean checkSystemWritePermission() {
        boolean retVal = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            retVal = Settings.System.canWrite(getActivity());
            Log.d(TAG, "Can Write Settings: " + retVal);
            if(!retVal){
                openAndroidPermissionsMenu();
            }
        }
        return retVal;
    }

    @Override
    protected View initContentView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_share_hotspot,container,false);
        return view;
    }

    @Override
    protected void initView(View view) {
        mLlClosed = (LinearLayout) view.findViewById(R.id.ll_closed);
        mBtnOpenHotspot = (Button) view.findViewById(R.id.btn_open_hotspot);
        mLlOpening = (RelativeLayout) view.findViewById(R.id.ll_opening);
        mRbLeft = (RippleBackground) view.findViewById(R.id.rb_left);
        mRbRight = (RippleBackground) view.findViewById(R.id.rb_right);
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.srl);
        mTvName=view.findViewById(R.id.tv_name);
        mLlNoDevice = (LinearLayout) view.findViewById(R.id.ll_no_device);
        mRvDevice = (RecyclerView) view.findViewById(R.id.rv_device);
    }

    @Override
    protected void setListener() {
        WiFiAPService.startService(getActivity());
        WiFiAPService.addWiFiAPListener(new WiFiAPListener() {
            @Override
            public void stateChanged(int state) {
                switchState(state);
            }
        });

        mBtnOpenHotspot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkSystemWritePermission()) {
                    showConfigHotspotDialog();
                }
            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                showConnectDevice();
            }
        });

    }

    @Override
    protected void initData() {
        mWifiHotUtil=WifiHotUtil.getInstance(getActivity());

        mAdapter=new SolidRVBaseAdapter<ConnectDevice>(getActivity(), mConnectDeviceList) {
            @Override
            protected void onBindDataToView(SolidCommonViewHolder holder, ConnectDevice bean) {
                holder.setText(R.id.tv_ip,bean.getIp());
            }

            @Override
            public int getItemLayoutID(int viewType) {
                return R.layout.item_device;
            }

            @Override
            protected void onItemClick(int position, ConnectDevice bean) {
                super.onItemClick(position, bean);
                new MyDialog(getActivity())
                        .setTitle(getString(R.string.device_info))
                        .setMessage("IP: "+bean.getIp()+"\n\n"
                            +"Mac: "+bean.getMac())
                        .setPosBtn(android.R.string.ok,null)
                        .show(getFragmentManager());
            }
        };


    }

    @Override
    protected void setData() {
        switchState(mWifiHotUtil.getWifiAPState());

        mRvDevice.setAdapter(mAdapter);
        mRvDevice.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    @Override
    protected void getBundleExtras(Bundle bundle) {

    }

    private void enablePosTv(EditText et,TextView tvPos,int limit){
        if (et.getText().toString().length()<limit) {
            tvPos.setEnabled(false);
            tvPos.setTextColor(getResources().getColor(android.R.color.darker_gray));
        }else {
            tvPos.setEnabled(true);
            tvPos.setTextColor(getResources().getColor(R.color.colorAccent));
        }
    }

    private void openAndroidPermissionsMenu() {
        Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
        intent.setData(Uri.parse("package:" + getActivity().getPackageName()));
        startActivity(intent);
    }

    private void hotspotClosed(){
        mSwipeRefreshLayout.setVisibility(View.GONE);
        mLlOpening.setVisibility(View.GONE);
        mRbLeft.stopRippleAnimation();
        mRbRight.stopRippleAnimation();

        mLlClosed.setVisibility(View.VISIBLE);
    }

    private void hotspotOpening(){
        mSwipeRefreshLayout.setVisibility(View.GONE);
        mLlClosed.setVisibility(View.GONE);

        mLlOpening.setVisibility(View.VISIBLE);
        mRbLeft.startRippleAnimation();
        mRbRight.startRippleAnimation();
    }

    private void hotspotOpened(){
        mLlClosed.setVisibility(View.GONE);
        mLlOpening.setVisibility(View.GONE);
        mRbLeft.stopRippleAnimation();
        mRbRight.stopRippleAnimation();

        mSwipeRefreshLayout.setVisibility(View.VISIBLE);
        mTvName.setText(mWifiHotUtil.getValidApSsid());

        showConnectDevice();
    }

    private void switchState(int state){
        switch (state) {
            case WiFiAPListener.WIFI_AP_CLOSE_SUCCESS:{
                if (BuildConfig.DEBUG) Log.d(TAG, "stateChanged: close-------------------------");
                hotspotClosed();
                break;
            }
            case WiFiAPListener.WIFI_AP_CLOSEING:{
                if (BuildConfig.DEBUG) Log.d(TAG, "stateChanged: closing-----------------------");
                break;
            }
            case WiFiAPListener.WIFI_AP_OPEN_SUCCESS:{
                if (BuildConfig.DEBUG) Log.d(TAG, "stateChanged: open-------------------------");
                hotspotOpened();
                break;
            }
            case WiFiAPListener.WIFI_AP_OPENING:{
                if (BuildConfig.DEBUG) Log.d(TAG, "stateChanged: opening-------------------------");
                hotspotOpening();
                break;
            }
        }
    }

    private void showConnectDevice(){
        mAdapter.clearAllItems();
        mAdapter.addItems(mWifiHotUtil.getConnectedDevices());
        if (mConnectDeviceList.size()>0) {
            mLlNoDevice.setVisibility(View.GONE);
            mRvDevice.setVisibility(View.VISIBLE);
        }else {
            mRvDevice.setVisibility(View.GONE);
            mLlNoDevice.setVisibility(View.VISIBLE);
        }

        mSwipeRefreshLayout.setRefreshing(false);
    }
}