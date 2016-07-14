package com.apnavaidya;


import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;


/**
 * Created by neeraj on 25/12/15.
 */
public class RecomendationAdapter extends FragmentPagerAdapter {

    public RecomendationAdapter(FragmentManager fm) {
        super(fm);
    }
    @Override
    public CharSequence getPageTitle(int position)
    {
       if(position==0)
           return "Food";
        else if(position==1)
           return  "Yoga";
        else
           return "Remedies";

    }
    @Override
    public int getCount() {
        return 3;
    }


    @Override
     public android.support.v4.app.Fragment getItem(int position) {

        if(position==0)
            return new FoodFragment();
        else if(position==1)
            return  new YogaFragment();
        else
            return new RemediesFragment();
    }
}
