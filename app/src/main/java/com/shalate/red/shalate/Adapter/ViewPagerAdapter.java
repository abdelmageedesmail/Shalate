package com.shalate.red.shalate.Adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.shalate.red.shalate.Fragment.ChatFragment;
import com.shalate.red.shalate.Fragment.FilterationF;
import com.shalate.red.shalate.Fragment.GudidFragment;
import com.shalate.red.shalate.Fragment.MyAccount;
import com.shalate.red.shalate.R;
import com.shalate.red.shalate.Utilities.PrefrencesStorage;

import java.util.HashMap;
import java.util.Map;

public class ViewPagerAdapter extends FragmentPagerAdapter {

    private boolean isFirstTime;
    private FragmentManager mFragmentManager;
    private Map<Integer, String> mFragmentTags;
    private String[] title;
    PrefrencesStorage storage;

    public ViewPagerAdapter(FragmentManager paramFragmentManager, Context paramContext, boolean paramBoolean) {
        super(paramFragmentManager);
        this.mFragmentManager = paramFragmentManager;
        this.mFragmentTags = new HashMap();
        storage = new PrefrencesStorage(paramContext);
        isFirstTime = paramBoolean;
        title = paramContext.getResources().getStringArray(R.array.tabLayoutTitle);
        storage = new PrefrencesStorage(paramContext);

    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return title[position];
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new FilterationF();
            case 1:
                return new MyAccount();
            case 2:
                return new GudidFragment();
            case 3:
                return new ChatFragment();
            default:
                return null;
        }
    }


    public Fragment getFragment(int paramInt) {
        Fragment localFragment = null;
        String str = (String) this.mFragmentTags.get(Integer.valueOf(paramInt));
        if (str != null) {
            localFragment = this.mFragmentManager.findFragmentByTag(str);
        }
        return localFragment;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        return super.instantiateItem(container, position);
    }

    @Override
    public int getCount() {
        return 4;
    }


}