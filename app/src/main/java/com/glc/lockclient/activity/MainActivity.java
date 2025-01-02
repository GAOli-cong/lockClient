package com.glc.lockclient.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.glc.lockclient.R;
import com.glc.lockclient.base.BaseActivity;
import com.glc.lockclient.databinding.ActivityMainBinding;
import com.glc.lockclient.fragment.HomeFragment;
import com.glc.lockclient.fragment.ManageFragment;
import com.glc.lockclient.fragment.MyFragment;
import com.google.android.material.tabs.TabLayout;
import com.gyf.immersionbar.ImmersionBar;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity<ActivityMainBinding> {
    private String[] titles = {"首页","管理","我的"};
    private List<Fragment> fragments = new ArrayList<>();
    private int[] unSele = {R.drawable.ic_home_un, R.drawable.ic_manage_un,R.drawable.ic_my_un};
    private int[] onSele = { R.drawable.ic_home_on,R.drawable.ic_manage_on,R.drawable.ic_my_on};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ImmersionBar
                .with(this)
                .statusBarColor(R.color.white)
                .statusBarDarkFont(true)
                .fitsSystemWindows(true)
                .init();
        initData();





    }

    private void initData() {
        fragments.add(new HomeFragment());
        fragments.add(new ManageFragment());
        fragments.add(new MyFragment());

        MainTabAdapter mainTabAdapter = new MainTabAdapter(getSupportFragmentManager());
        binding.viewpager.setAdapter(mainTabAdapter);
        binding.tab.setupWithViewPager(binding.viewpager);

        binding.viewpager.setOffscreenPageLimit(3); // 设置缓存的页面数量
        for (int i = 0; i < binding.tab.getTabCount(); i++) {
            TabLayout.Tab tabAt = binding.tab.getTabAt(i);
            tabAt.setCustomView(getView(i));
        }


        binding.tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                View view = tab.getCustomView();
                ImageView img = view.findViewById(R.id.img);
                TextView tv = view.findViewById(R.id.name);
                String title = tv.getText().toString();
                if (title.equals("首页")) {
                    img.setImageResource(onSele[0]);
                } else if (title.equals("管理")) {
                    img.setImageResource(onSele[1]);
                } else if (title.equals("我的")) {
                    img.setImageResource(onSele[2]);
                }



            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                View view = tab.getCustomView();
                ImageView img = view.findViewById(R.id.img);
                TextView tv = view.findViewById(R.id.name);
                String title = tv.getText().toString();
                if (title.equals("首页")) {
                    img.setImageResource(unSele[0]);
                } else if (title.equals("管理")) {
                    img.setImageResource(unSele[1]);
                } else if (title.equals("我的")) {
                    img.setImageResource(unSele[2]);
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    public class MainTabAdapter extends FragmentPagerAdapter {

        public MainTabAdapter(@NonNull FragmentManager fm) {
            super(fm);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }


    }



    public View getView(int position) {
        View inflate = View.inflate(this, R.layout.item_tab, null);
        TextView tv = inflate.findViewById(R.id.name);
        ImageView img = inflate.findViewById(R.id.img);
        if (binding.tab.getTabAt(position).isSelected()) {
            img.setImageResource(onSele[position]);
        } else {
            img.setImageResource(unSele[position]);
        }
        tv.setText(titles[position]);
        tv.setTextColor(binding.tab.getTabTextColors());
        return inflate;
    }

}