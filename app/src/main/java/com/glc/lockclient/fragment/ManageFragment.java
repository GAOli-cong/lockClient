package com.glc.lockclient.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.SPUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.glc.lockclient.R;
import com.glc.lockclient.activity.StudentDetailActivity;
import com.glc.lockclient.activity.UnlockRecordsActivity;
import com.glc.lockclient.adapter.StudentMessageAdapter;
import com.glc.lockclient.base.BaseFragment;
import com.glc.lockclient.bean.ApiResponseBean;
import com.glc.lockclient.bean.StudentMessageBean;
import com.glc.lockclient.databinding.FragmentManageBinding;
import com.glc.lockclient.http.RetrofitClient;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class ManageFragment extends BaseFragment<FragmentManageBinding> {

    private static final String TAG = "ManageFragment";
    private StudentMessageAdapter studentMessageAdapter;


    public ManageFragment() {
    }

    public static ManageFragment newInstance(String param1, String param2) {
        ManageFragment fragment = new ManageFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.unlockRecords.setOnClickListener(view1 -> {
            startActivity(new Intent(getActivity(), UnlockRecordsActivity.class));
        });

        studentMessageAdapter = new StudentMessageAdapter(R.layout.item_student_list, new ArrayList<>());
        binding.recyclerViewSearch.setAdapter(studentMessageAdapter);

        studentMessageAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(@NonNull BaseQuickAdapter<?, ?> adapter, @NonNull View view, int position) {
                Intent intent = new Intent(getActivity(), StudentDetailActivity.class);
                intent.putExtra("data",GsonUtils.toJson(studentMessageAdapter.getItem(position)));
                startActivity(intent);
            }
        });

        if(TextUtils.equals("adminNoLogin",SPUtils.getInstance().getString("username"))
                ||TextUtils.equals("admin",SPUtils.getInstance().getString("username"))){
            binding.unlockRecords.setVisibility(View.VISIBLE);
        }


        binding.search.setOnClickListener(view1 -> {
            String search = binding.editSearch.getText().toString();
            RetrofitClient.getApiService().queryStudentMessageByName(search)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ApiResponseBean<StudentMessageBean>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(ApiResponseBean<StudentMessageBean> studentMessageBeanApiResponseBean) {
                            studentMessageAdapter.setNewInstance(studentMessageBeanApiResponseBean.getData().getList());
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        });


        binding.swipeLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                getData();
            }
        });

        binding.swipeLayout.setEnableLoadMore(false);

        binding.swipeLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {

            }
        });

    }


    private void getData() {
        RetrofitClient.getApiService().queryAllStudentMessage()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ApiResponseBean<StudentMessageBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ApiResponseBean<StudentMessageBean> studentMessageBeanApiResponseBean) {
                        studentMessageAdapter.setNewInstance(studentMessageBeanApiResponseBean.getData().getList());
                    }

                    @Override
                    public void onError(Throwable e) {
                        binding.swipeLayout.finishRefresh();

                    }

                    @Override
                    public void onComplete() {
                        binding.swipeLayout.finishRefresh();
                    }
                });
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        getData();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.d(TAG, "setUserVisibleHint: "+isVisibleToUser);
        getData();
    }
}