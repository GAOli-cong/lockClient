package com.glc.lockclient.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.SPUtils;
import com.glc.lockclient.R;
import com.glc.lockclient.base.BaseFragment;
import com.glc.lockclient.databinding.FragmentMyBinding;


public class MyFragment extends BaseFragment<FragmentMyBinding> {



    public MyFragment() {
    }

    public static MyFragment newInstance(String param1, String param2) {
        MyFragment fragment = new MyFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        binding.btnSignOut.setOnClickListener(view1 -> {
            SPUtils.getInstance().put("autoLogin",false);
            AppUtils.relaunchApp(true);
        });
    }
}