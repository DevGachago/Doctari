package com.example.doctari.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.doctari.fragments.TabFiveFragment;
import com.example.doctari.fragments.TabFourFragment;
import com.example.doctari.fragments.TabOneFragment;
import com.example.doctari.fragments.TabThreeFragment;
import com.example.doctari.fragments.TabTwoFragment;

public class ViewPagerAdapter extends FragmentStateAdapter {
    private final Fragment[] fragments = new Fragment[]{
            new TabOneFragment(),
            new TabTwoFragment(),
            new TabThreeFragment(),
            new TabFourFragment(),
            new TabFiveFragment()
    };

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments[position];
    }

    @Override
    public int getItemCount() {
        return fragments.length;
    }

    public Fragment getFragment(int position) {
        return fragments[position];
    }
}
