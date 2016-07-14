package com.apnavaidya;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;

import com.astuetz.PagerSlidingTabStrip;

public class Accordion extends AppCompatActivity {

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accordion);
        //imageView=(ImageView)findViewById(R.id.imageView2);
        /*ActionBar actionBar=getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);*/
        ActiveTools();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      /*  switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }*/
        return true;
    }



    void ActiveTools()
    {   final Button button=(Button)findViewById(R.id.button);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        ViewPager pager = (ViewPager) findViewById(R.id.mainpager);
        pager.setAdapter(new MainPagerAdapter(getSupportFragmentManager()));
        PagerSlidingTabStrip tabs = (PagerSlidingTabStrip) findViewById(R.id.maintabs);
        tabs.setIndicatorColor(Color.BLUE);
        tabs.setIndicatorHeight(3);
        tabs.setDividerPadding(width /3);
        tabs.setShouldExpand(true);
        tabs.setViewPager(pager);

        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                  /*Toast.makeText(getApplicationContext(),
                        "page scrolled",
                        Toast.LENGTH_SHORT).show();*/
            }
            @Override
            public void onPageSelected(int position) {

                if(position==0){
                   // imageView.setImageResource(R.drawable.yog_word);

                }
                else if(position==1)
                {
                    //imageView.setImageResource(R.drawable.food_wor);
                }
                else
                {
                    //imageView.setImageResource(R.drawable.remedy);
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }



}