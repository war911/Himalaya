package com.example.war.ximalayaradio.utils;

import com.example.war.ximalayaradio.base.BaseFragment;
import com.example.war.ximalayaradio.fragments.HistoryFragment;
import com.example.war.ximalayaradio.fragments.RecommendFragment;
import com.example.war.ximalayaradio.fragments.SubscriptionFragment;

import java.util.HashMap;
import java.util.Map;

public class FragmentCreator {
    private static Map<Integer, BaseFragment> sCache = new HashMap<>();
    public final static int INDEX_RECOMMEND = 0;
    public final static int INDEX_SUBSCRIPTION = 1;
    public final static int INDEX_HISTROY = 2;

    public final static int PAGE_COUNT = 3;

    public static BaseFragment getFragment(int index) {
        BaseFragment baseFragment = sCache.get(index);
        if (baseFragment != null) {
            return baseFragment;
        }

        switch (index) {
            case INDEX_RECOMMEND:
                baseFragment = new RecommendFragment();
                break;
            case INDEX_SUBSCRIPTION:
                baseFragment = new SubscriptionFragment();
                break;
            case INDEX_HISTROY:
                baseFragment = new HistoryFragment();
                break;
            default:
                break;
        }
        sCache.put(index, baseFragment);
        return baseFragment;
    }
}
