package com.zack.intelligent.ui.fragment;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.app.Fragment;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.zack.intelligent.R;
import com.zack.intelligent.utils.SharedUtils;
import com.zack.intelligent.utils.Utils;
import com.zkteco.android.biometric.ZKLiveFaceService;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 */
public class AboutFragment extends Fragment {
    private static final String TAG = "AboutFragment";

    Unbinder unbinder;
    @BindView(R.id.about_edt_mac)
    EditText aboutEdtMac;
    @BindView(R.id.about_edt_total_storage)
    EditText aboutEdtTotalStorage;
    @BindView(R.id.about_edt_avail_storage)
    EditText aboutEdtAvailStorage;
    @BindView(R.id.about_edt_used_storage)
    EditText aboutEdtUsedStorage;
    @BindView(R.id.about_edt_total_memory)
    EditText aboutEdtTotalMemory;
    @BindView(R.id.about_edt_avail_memory)
    EditText aboutEdtAvailMemory;
    @BindView(R.id.about_edt_used_memory)
    EditText aboutEdtUsedMemory;
    @BindView(R.id.about_edt_server_ip)
    EditText aboutEdtServerIp;
    @BindView(R.id.about_edt_server_port)
    EditText aboutEdtServerPort;
    @BindView(R.id.about_edt_local_ip)
    EditText aboutEdtLocalIp;
    @BindView(R.id.about_edt_subnet_mask)
    EditText aboutEdtSubnetMask;
    @BindView(R.id.about_edt_gateway)
    EditText aboutEdtGateway;
    @BindView(R.id.about_edt_server_status)
    EditText aboutEdtServerStatus;
    @BindView(R.id.about_edt_system_version)
    EditText aboutEdtSystemVersion;
    @BindView(R.id.about_edt_version_no)
    EditText aboutEdtVersionNo;

    public AboutFragment() {
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        unbinder = ButterKnife.bind(this, view);

        ActivityManager activityManager = (ActivityManager) getContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        long availMem = memoryInfo.availMem;//可用内存
        long totalMem = memoryInfo.totalMem;//总内存
        aboutEdtTotalMemory.setText(totalMem / 1024 / 1024 + "MB");
        aboutEdtAvailMemory.setText(availMem / 1024 / 1024 + "MB");
        aboutEdtUsedMemory.setText((totalMem - availMem) / 1024 / 1024 + "MB");

        File datapath = Environment.getDataDirectory();
        StatFs datastatFs = new StatFs(datapath.getPath());
        long blockSizeLong = datastatFs.getBlockSizeLong();
        long blockCountLong = datastatFs.getBlockCountLong();
        long availableBlocksLong = datastatFs.getAvailableBlocksLong();
        String romTotalSize = Formatter.formatFileSize(getContext(), blockSizeLong * blockCountLong);
        String romAvailableSize = Formatter.formatFileSize(getContext(), blockSizeLong * availableBlocksLong);
        String romUsedSize = Formatter.formatFileSize(getContext(), blockSizeLong * (blockCountLong - availableBlocksLong));

        aboutEdtTotalStorage.setText(romTotalSize);
        aboutEdtAvailStorage.setText(romAvailableSize);
        aboutEdtUsedStorage.setText(romUsedSize);

        aboutEdtMac.setText(getHardwareId());
        aboutEdtServerIp.setText(SharedUtils.getServerIp());
        aboutEdtServerPort.setText(String.valueOf(SharedUtils.getServerPort()));
        aboutEdtLocalIp.setText(Utils.getHostIP());
//        aboutEdtSubnetMask.setText();
//        aboutEdtGateway.setText();

        PackageManager packageManager = getContext().getPackageManager();
        String packageName = getContext().getPackageName();
        int versionCode = 0;
        String versionName = null;
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, 0);
            versionCode = packageInfo.versionCode;
            versionName = packageInfo.versionName;
            aboutEdtSystemVersion.setText(versionName);
            aboutEdtVersionNo.setText(versionCode + "");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return view;
    }

    private String getHardwareId() {
        byte[] hwid = new byte[256];
        int[] size = new int[1];
        size[0] = 256;
        if (0 == ZKLiveFaceService.getHardwareId(hwid, size)) {
            String hwidStr = new String(hwid, 0, size[0]);
            Log.i(TAG, "getHardwareId 硬件码:" + hwidStr);
            return hwidStr;
        }
        return "";
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
