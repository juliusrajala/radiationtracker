package com.example.radiationtracker;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;


public class MyActivity extends FragmentActivity {

    FragmentPagerAdapter adapterViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);
        ViewPager vpPager = (ViewPager)findViewById(R.id.vpPager);
        adapterViewPager = new MyPagerAdapter(getSupportFragmentManager());
        vpPager.setAdapter(adapterViewPager);

//        vpPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
//            //This method will be invoked when the current page is scrolled
//            @Override
//            public void onPageScrolled(int position, float position, int positionOffSetPixels) {
//                //Code goes here
//            }
//
//            //Called when the scroll state changes.
//            @Override
//            public void onPageScrollStateChanged(int state){
//                //Code goes here
//            }
//
//            @Override
//            public void onPageSelected(int i) {
//
//            }
//
//            @Override
//            public void onPageScrollStateChanged(int i) {
//
//            }
//        });
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static class MyPagerAdapter extends FragmentPagerAdapter{

        private static int NUM_ITEMS = 4;

        public MyPagerAdapter(FragmentManager fragmentManager){
            super(fragmentManager);
        }

        @Override
        public int getCount(){
            return NUM_ITEMS;
        }
        @Override
        public Fragment getItem(int position){
            switch(position){
                case 0: return FirstFragment.newInstance(0, "Page #1");
                case 1: return FirstFragment.newInstance(1, "Page #2");
                case 2: return SecondFragment.newInstance(2, "Page #3");
                case 3: return ThirdFragment.newInstance(3, "Page #4");
                default: return null;
            }
        }
        @Override
        public CharSequence getPageTitle(int position){
            return "Page" + position;
        }

    }
}
