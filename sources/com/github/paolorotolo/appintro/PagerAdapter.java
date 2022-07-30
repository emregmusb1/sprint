package com.github.paolorotolo.appintro;

import android.util.SparseArray;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> fragments;
    private SparseArray<Fragment> retainedFragments = new SparseArray<>();

    public PagerAdapter(FragmentManager fm, @NonNull List<Fragment> fragments2) {
        super(fm);
        this.fragments = fragments2;
    }

    public Fragment getItem(int position) {
        if (this.fragments.isEmpty()) {
            return null;
        }
        if (this.retainedFragments.get(position) != null) {
            return this.retainedFragments.get(position);
        }
        return this.fragments.get(position);
    }

    public int getCount() {
        return this.fragments.size();
    }

    @NonNull
    public List<Fragment> getFragments() {
        return this.fragments;
    }

    @NonNull
    public Collection<Fragment> getRetainedFragments() {
        ArrayList<Fragment> retainedValues = new ArrayList<>(this.retainedFragments.size());
        for (int i = 0; i < this.retainedFragments.size(); i++) {
            retainedValues.add(this.retainedFragments.get(i));
        }
        return retainedValues;
    }

    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        this.retainedFragments.put(position, fragment);
        return fragment;
    }

    public void destroyItem(ViewGroup container, int position, Object object) {
        if (this.retainedFragments.get(position) != null) {
            this.retainedFragments.remove(position);
        }
        super.destroyItem(container, position, object);
    }
}
