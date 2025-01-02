package com.glc.lockclient.adapter;

import android.text.TextUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.glc.lockclient.R;
import com.glc.lockclient.bean.StudentMessageBean;

import java.util.List;

public class StudentMessageAdapter extends BaseQuickAdapter<StudentMessageBean.ListDTO, BaseViewHolder> {

    public StudentMessageAdapter(int layoutResId, @Nullable List<StudentMessageBean.ListDTO> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, StudentMessageBean.ListDTO studentMessageBean) {
            baseViewHolder.setText(R.id.name,studentMessageBean.getName());
            baseViewHolder.setText(R.id.status,studentMessageBean.getPhoneStatus());
            ImageView imgStatus = baseViewHolder.findView(R.id.imgStatus);
        if (TextUtils.equals("已存入", studentMessageBean.getPhoneStatus())) {
            imgStatus.setBackgroundDrawable(getContext().getDrawable(R.drawable.ic_save_phone_on));
        }else {
            imgStatus.setBackgroundDrawable(getContext().getDrawable(R.drawable.ic_save_phone_un));
        }
    }
}
