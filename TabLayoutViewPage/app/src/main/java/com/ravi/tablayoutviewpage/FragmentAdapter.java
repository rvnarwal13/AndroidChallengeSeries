package com.ravi.tablayoutviewpage;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;

public class FragmentAdapter extends FragmentStateAdapter {
    ArrayList<Fragment> fragments;
    ArrayList<String> titles;

    public FragmentAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
        this.fragments = new ArrayList<>();
        this.titles = new ArrayList<>();
    }

    public void addFragments(Fragment fragment, String title) {
        fragments.add(fragment);
        titles.add(title);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments.get(position);
    }

    @Override
    public int getItemCount() {
        return titles.size();
    }

    public String getPageTitle(int position) {
        return titles.get(position);
    }
}
