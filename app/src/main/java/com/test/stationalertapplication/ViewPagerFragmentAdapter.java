package com.test.stationalertapplication;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

public class ViewPagerFragmentAdapter extends FragmentStatePagerAdapter {

    private CharSequence[] tabTitles = {"路線選択", "プリセット"};

    public ViewPagerFragmentAdapter(FragmentManager fm){
        super(fm);
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new MapsFragment();
            case 1:
                return new PresetFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }
}
