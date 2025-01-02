package com.glc.lockclient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.blankj.utilcode.util.GsonUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.glc.lockclient.R;
import com.glc.lockclient.base.BaseActivity;
import com.glc.lockclient.bean.ApiResponseBean;
import com.glc.lockclient.bean.StudentMessageBean;
import com.glc.lockclient.databinding.ActivityStudentDetailBinding;
import com.glc.lockclient.http.RetrofitClient;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class StudentDetailActivity extends BaseActivity<ActivityStudentDetailBinding> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding.layoutTitle.tvTitle.setText("信息查看");
        binding.layoutTitle.imgBack.setOnClickListener(view -> {
            finish();
        });

        Intent intent = getIntent();

        String dataJson = intent.getStringExtra("data");
        StudentMessageBean.ListDTO listDTO = GsonUtils.fromJson(dataJson, StudentMessageBean.ListDTO.class);
        binding.stuName.setText(listDTO.getName());
        binding.editRemark.setText(listDTO.getRemark() + "");
        if (TextUtils.equals("已存入", listDTO.getPhoneStatus())) {
            binding.rbDeposited.setChecked(true);
        } else {
            binding.rbRemoved.setChecked(true);

        }


        //保存按钮
        binding.btnSave.setOnClickListener(view -> {
            RadioButton radioButton = findViewById(binding.rg.getCheckedRadioButtonId());
            String phone_status = radioButton.getText().toString();

            String remark = binding.editRemark.getText().toString().trim();
            listDTO.setPhoneStatus(phone_status);
            listDTO.setRemark(remark);


            RetrofitClient.getApiService().updateStudentMessage(listDTO)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ApiResponseBean>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(ApiResponseBean studentMessageBeanApiResponseBean) {
                            finish();
                            ToastUtils.showShort("保存成功");
                        }

                        @Override
                        public void onError(Throwable e) {
                            ToastUtils.showShort("保存失败"+e);
                        }

                        @Override
                        public void onComplete() {
                        }
                    });


        });
    }
}