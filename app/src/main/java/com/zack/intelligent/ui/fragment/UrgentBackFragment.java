package com.zack.intelligent.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.yanzhenjie.nohttp.rest.Response;
import com.zack.intelligent.R;
import com.zack.intelligent.adapter.BackDataAdapter;
import com.zack.intelligent.bean.DataBean;
import com.zack.intelligent.bean.GunCabsBean;
import com.zack.intelligent.bean.MembersBean;
import com.zack.intelligent.bean.OperBean;
import com.zack.intelligent.bean.SubCabsBean;
import com.zack.intelligent.bean.TaskItemsBean;
import com.zack.intelligent.http.HttpClient;
import com.zack.intelligent.http.HttpListener;
import com.zack.intelligent.ui.UrgentGoActivity;
import com.zack.intelligent.ui.widget.NoSrcollViewPage;
import com.zack.intelligent.utils.LogUtil;
import com.zack.intelligent.utils.SharedUtils;
import com.zack.intelligent.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class UrgentBackFragment extends Fragment {

    private static final String TAG = "UrgentBackFragment";
    Unbinder unbinder;
    @BindView(R.id.urgent_back_tab_layout)
    TabLayout urgentBackTabLayout;
    @BindView(R.id.urgent_back_view_pager)
    NoSrcollViewPage urgentBackViewPager;

    private List<Fragment> fragments;
    private ShortPagerAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_urgent_back, container, false);
        unbinder = ButterKnife.bind(this, view);
        fragments = new ArrayList<>();
        fragments.add(new UrgentBackGunsFragment());
        fragments.add(new UrgentBackAmmosFragment());

        urgentBackTabLayout.setupWithViewPager(urgentBackViewPager);
        adapter = new ShortPagerAdapter(getChildFragmentManager());
        urgentBackViewPager.setAdapter(adapter);
        return view;
    }

    private class ShortPagerAdapter extends FragmentPagerAdapter {
        public String[] mTilte;

        public ShortPagerAdapter(FragmentManager fm) {
            super(fm);
            mTilte = new String[]{"归还枪支", "归还弹药"};
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTilte[position];
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

}
