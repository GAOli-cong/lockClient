package com.glc.lockclient.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.viewholder.BaseViewHolder;
import com.glc.lockclient.R;
import com.glc.lockclient.bean.ResponseUnlockRecordsBean;

import java.util.List;

public class UnlockRecordsAdapter extends BaseQuickAdapter<ResponseUnlockRecordsBean.ListDTO, BaseViewHolder> {
    public UnlockRecordsAdapter(int layoutResId, @Nullable List<ResponseUnlockRecordsBean.ListDTO> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder baseViewHolder, ResponseUnlockRecordsBean.ListDTO reasonBean) {
        baseViewHolder.setText(R.id.reason,reasonBean.getReason());
        baseViewHolder.setText(R.id.time,reasonBean.getTime());
        baseViewHolder.setText(R.id.username,"开锁用户："+reasonBean.getUsername());

    }
}
