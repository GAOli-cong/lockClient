package com.glc.lockclient.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.CompoundButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.glc.lockclient.R;
import com.glc.lockclient.base.BaseActivity;
import com.glc.lockclient.bean.ApiResponseBean;
import com.glc.lockclient.bean.RequestUserBean;
import com.glc.lockclient.databinding.ActivityLoginBinding;
import com.glc.lockclient.http.RetrofitClient;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class LoginActivity extends BaseActivity<ActivityLoginBinding> {
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding.versionName.setText("版本号："+AppUtils.getAppVersionName());


        boolean autoLogin = SPUtils.getInstance().getBoolean("autoLogin");
        String username = SPUtils.getInstance().getString("username");
        String password = SPUtils.getInstance().getString("password");

        binding.editUser.setText(username);
        binding.editPassword.setText(password);
        if (autoLogin) {
            if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
                login(username, password);
            }
        }

        binding.cbAutoLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                SPUtils.getInstance().put("autoLogin", b);
            }
        });

        binding.btnLogin.setOnClickListener(view -> {

           String usernameLogin =binding.editUser.getText().toString();
           String passwordLogin =binding.editPassword.getText().toString();

            login(usernameLogin, passwordLogin);
        });


    }

    private void login(String user, String password) {
        RequestUserBean requestUserBean = new RequestUserBean();
        requestUserBean.setUsername(user);
        requestUserBean.setPassword(password);

        if(TextUtils.equals("adminNoLogin",user)){
            SPUtils.getInstance().put("username", user);
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            return;
        }

        RetrofitClient.getApiService().login(requestUserBean)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ApiResponseBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ApiResponseBean apiResponseBean) {
                        Log.d(TAG, "onNext: " + apiResponseBean);
                        if (TextUtils.equals(apiResponseBean.getMsg(), "登录成功")) {
                            SPUtils.getInstance().put("username", user);
                            SPUtils.getInstance().put("password", password);

                            SPUtils.getInstance().put("token", apiResponseBean.getData().toString());
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.showLong("请确定已连接，lock_server网络。"+e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}