package com.example.war.ximalayaradio.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.war.ximalayaradio.utils.FragmentCreator;

public class MainContentAdapter extends FragmentPagerAdapter {
    public MainContentAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int index) {
        return FragmentCreator.getFragment(index);
    }

    @Override
    public int getCount() {
        return FragmentCreator.PAGE_COUNT;
    }
}