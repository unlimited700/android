package com.apnavaidya;


import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by neeraj on 25/12/15.
 */
public class MainPagerAdapter extends FragmentPagerAdapter {

    Map<Integer, String> fragmentMap = new HashMap<>();

    public MainPagerAdapter(FragmentManager fm) {
        super(fm);

        fragmentMap = ProblemList.fragmentMap;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return (position == 0) ? "Yoga" : (position == 1) ? "Food" : "Remedies";
        //fragmentMap = ProblemList.fragmentMap;
        //return fragmentMap.get(position);
    }

    @Override
    public int getCount() {
        // return fragmentMap.size();
        return 3;
    }


    @Override
    public android.support.v4.app.Fragment getItem(int position) {

        return (position == 0) ? new YogaFragment() : (position == 1) ? new FoodFragment() : new RemediesFragment();
     /*   if (fragmentMap.get(position).equals("food")) {
            return new FoodFragment();
        } else if (fragmentMap.get(position).equals("yoga")) {
            return new YogaFragment();
        } else if (fragmentMap.get(position).equals("remedy")) {
            return new RemediesFragment();
        }
        return null;
    }*/
    }


}
