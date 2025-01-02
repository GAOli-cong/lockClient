package com.glc.lockclient.dialog;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.glc.lockclient.R;

public class OpenDialogFragment extends DialogFragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 加载自定义布局
        View view = inflater.inflate(R.layout.dialog_open, container, false);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        // 获取屏幕宽高
        DisplayMetrics metrics = new DisplayMetrics();
        requireActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;

        // 设置对话框宽高为屏幕宽高的 80% 和 50%
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                    (int) (screenWidth * 0.8), // 宽度占屏幕 80%
                    (int) (screenHeight * 0.5) // 高度占屏幕 50%
            );
        }
        // 延时 2 秒关闭
        new Handler(Looper.getMainLooper()).postDelayed(this::dismiss, 3000);
    }

}
