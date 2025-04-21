package com.glc.lockclient.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.glc.lockclient.BaseApplication;
import com.glc.lockclient.R;
import com.glc.lockclient.activity.UnlockRecordsActivity;
import com.glc.lockclient.base.BaseFragment;
import com.glc.lockclient.bean.ApiResponseBean;
import com.glc.lockclient.bean.ResponseUnlockRecordsBean;
import com.glc.lockclient.bean.UserMessageResponseBean;
import com.glc.lockclient.databinding.FragmentHomeBinding;
import com.glc.lockclient.dialog.OpenDialogFragment;
import com.glc.lockclient.http.RetrofitClient;
import com.glc.lockclient.utils.HeartbeatManager;
import com.glc.lockclient.utils.RabbitMQConnectionUtil;
import com.glc.slidetoggleview.SlideToggleView;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;
import java.util.Locale;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class HomeFragment extends BaseFragment<FragmentHomeBinding> {
    private static final String TAG = "HomeFragment";
    Channel channelLock;
    Channel consumerChannel;


    public HomeFragment() {
    }


    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        try {
            connect();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        binding.userName.setText("你好,"+SPUtils.getInstance().getString("username"));

        binding.userName.setOnClickListener(view1 -> {
          if(TextUtils.equals("adminNoLogin",SPUtils.getInstance().getString("username"))){
              send();
              ToastUtils.showLong("已点击开锁");
          }
        });


        binding.slide.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    Log.d(TAG, "onTouch: ACTION_DOWN");

                    if (TextUtils.equals("橱柜离线", binding.status.getText().toString())) {
                        Toast.makeText(getActivity(), "橱柜离线，无法解锁", Toast.LENGTH_SHORT).show();
                        binding.slide.closeToggle();
                        return true; // 表示事件已被消费
                    }

                    if (TextUtils.isEmpty(binding.editReason.getText().toString())) {
                        Toast.makeText(getActivity(), "请填写开锁原因", Toast.LENGTH_SHORT).show();
                        binding.slide.closeToggle();
                        return true;
                    }
                }
                return false; // 继续传递事件
            }
        });


        /**
         * 滑动按钮
         */
        binding.slide.setSlideToggleListener(new SlideToggleView.SlideToggleListener() {
            @Override
            public void onBlockPositionChanged(SlideToggleView view, int left, int total,
                                               int slide) {
                String content = String.format(Locale.CHINESE, "left: %d - total: %d - slide: %d",
                        left, total, slide);
                Log.d(TAG, "onBlockPositionChanged: " + content);

            }

            @Override
            public void onSlideOpen(SlideToggleView view) {
                String reason  = binding.editReason.getText().toString();
                ResponseUnlockRecordsBean.ListDTO listDTO = new ResponseUnlockRecordsBean.ListDTO();
                listDTO.setReason(reason);
                listDTO.setTime(TimeUtils.getNowString());
                listDTO.setUsername(SPUtils.getInstance().getString("username"));
                insertUnlockRecords(listDTO);



            }
        });


        /**
         * 监听断开
         */
        BaseApplication.getInstance().getHeartbeatManager().setListener(new HeartbeatManager.Listener() {
            @Override
            public void off() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setOnLineStyle(false);
                    }
                });
            }
        });


        RetrofitClient.getApiService().userMessage()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ApiResponseBean<UserMessageResponseBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ApiResponseBean<UserMessageResponseBean> apiResponseBean) {
                        UserMessageResponseBean userMessageResponseBean = apiResponseBean.getData();
                        binding.userName.setText("你好,"+userMessageResponseBean.getUsername());
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    private void insertUnlockRecords(ResponseUnlockRecordsBean.ListDTO reasonBean){
        RetrofitClient.getApiService().insertUnlockRecord(reasonBean)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ApiResponseBean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ApiResponseBean apiResponseBean) {
                        if(apiResponseBean.isSuccess()){
                            //发送开锁消息
                            send();

                            //恢复关闭
                            binding.slide.closeToggle();
                            binding.editReason.setText("");
                            //弹出开锁动画
                            OpenDialogFragment customDialogFragment = new OpenDialogFragment();
                            customDialogFragment.show(getChildFragmentManager(), "CustomDialog");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.showShort("请求失败");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    /**
     * 连接服务
     *
     * @throws Exception
     */
    void connect() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //1. 获取连接对象
                    Connection connection = RabbitMQConnectionUtil.getConnection();

                    //2. 构建Channel
                    channelLock = connection.createChannel();

                    //接收的channel
                    consumerChannel = connection.createChannel();

                    //3. 构建队列
                    channelLock.queueDeclare("to_lock_server", false, false, false, null);

                    basicConsume();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }

    /**
     * 发送消息到lockServer
     *
     * @throws Exception
     */
    private void send() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //4. 发布消息
                    String message = "Hello World!";
                    AMQP.BasicProperties properties = new AMQP.BasicProperties.Builder()
                            .expiration("1000") // 设置消息的 TTL 为 1 秒  超过1s没有被消费则丢弃当前消息
                            .build();
                    channelLock.basicPublish("", "to_lock_server", properties, message.getBytes());
                    System.out.println("消息发送成功！");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();


    }


    /**
     * 收消息 (用来检查服务端软件的在线状态)
     */
    private void basicConsume() {
        //实现Consumer的最简单方法是将便捷类DefaultConsumer子类化。可以在basicConsume 调用上传递此子类的对象以设置订阅：
        try {
            consumerChannel.basicConsume("server_heart", false, new DefaultConsumer(consumerChannel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    super.handleDelivery(consumerTag, envelope, properties, body);

                    String msg = new String(body);
                    long deliveryTag = envelope.getDeliveryTag();
                    consumerChannel.basicAck(deliveryTag, false);
                    Log.d("收到心跳", "handleDelivery: " + msg);
                    String[] parts = msg.split(":");
                    if (parts.length == 2) {
                        String deviceId = parts[0];
                        String status = parts[1];
                        if ("ONLINE".equals(status)) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setOnLineStyle(true);
                                }
                            });
                            BaseApplication.getInstance().getHeartbeatManager().receiveHeartbeat(deviceId);
                        }
                    } else {
                        System.err.println("Invalid message format: " + msg);
                    }
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    /**
     * 设置在线状态
     *
     * @param status
     */
    private void setOnLineStyle(boolean status) {
        if (status) {
            binding.status.setText("橱柜在线");
            binding.status.setTextColor(Color.parseColor("#0099ff"));
            binding.cabinet.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_cabinet_on));
            binding.slide.setBackground(getResources().getDrawable(R.drawable.shape_blue));
            binding.slide.setText("滑动解锁橱柜");

        } else {
            binding.status.setText("橱柜离线");
            binding.status.setTextColor(Color.parseColor("#000000"));
            binding.cabinet.setBackgroundDrawable(getResources().getDrawable(R.drawable.ic_cabinet));
            binding.slide.setBackground(getResources().getDrawable(R.drawable.shape_d0d0d0));
            binding.slide.setText("橱柜离线无法开锁");

        }

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: 页面被重建");
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.d(TAG, "setUserVisibleHint: " + isVisibleToUser);
    }
}