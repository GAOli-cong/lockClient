package com.glc.lockclient.activity;

import android.os.Bundle;

import androidx.annotation.NonNull;

import com.glc.lockclient.R;
import com.glc.lockclient.adapter.UnlockRecordsAdapter;
import com.glc.lockclient.base.BaseActivity;
import com.glc.lockclient.bean.ApiResponseBean;
import com.glc.lockclient.bean.ResponseUnlockRecordsBean;
import com.glc.lockclient.databinding.ActivityUnlockRecordsBinding;
import com.glc.lockclient.http.RetrofitClient;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class UnlockRecordsActivity extends BaseActivity<ActivityUnlockRecordsBinding> {
    private UnlockRecordsAdapter recordsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        queryAllReason();
        binding.layoutTitle.imgBack.setOnClickListener(view -> {
            finish();
        });

        binding.layoutTitle.tvTitle.setText("开锁记录");

        recordsAdapter = new UnlockRecordsAdapter(R.layout.item_unlock__list,new ArrayList<>());
        binding.recyclerView.setAdapter(recordsAdapter);


        binding.swipeLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                queryAllReason();
            }
        });


    }

    private void queryAllReason(){
        RetrofitClient.getApiService().queryAllUnlockRecord()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ApiResponseBean<ResponseUnlockRecordsBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ApiResponseBean<ResponseUnlockRecordsBean> reasonBeanApiResponseBean) {
                        recordsAdapter.setNewInstance(reasonBeanApiResponseBean.getData().getList());
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        binding.swipeLayout.finishRefresh();
                    }
                });
    }
}