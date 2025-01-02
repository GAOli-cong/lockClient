package com.glc.lockclient.http;

import com.glc.lockclient.bean.ApiResponseBean;
import com.glc.lockclient.bean.RequestUserBean;
import com.glc.lockclient.bean.ResponseUnlockRecordsBean;
import com.glc.lockclient.bean.StudentMessageBean;
import com.glc.lockclient.bean.UserMessageResponseBean;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;


public interface ApiService {
    @GET("student/queryAllStudentMessage")
    Observable<ApiResponseBean<StudentMessageBean>> queryAllStudentMessage();


    @GET("student/queryStudentMessageByName")
    Observable<ApiResponseBean<StudentMessageBean>> queryStudentMessageByName(@Query("name") String name);


    @POST("student/updateStudentMessage")
    Observable<ApiResponseBean> updateStudentMessage(@Body StudentMessageBean.ListDTO listDTO);

    @POST("user/login")
    Observable<ApiResponseBean> login(@Body RequestUserBean userBean);

    @GET("user/userMessage")
    Observable<ApiResponseBean<UserMessageResponseBean>> userMessage();

    /**
     * 添加原因
     * @param reasonBean
     * @return
     */
    @POST("unlock/insertUnlockRecord")
    Observable<ApiResponseBean> insertUnlockRecord(@Body ResponseUnlockRecordsBean.ListDTO reasonBean);

    /**
     * 查询所有开锁记录
     * @return
     */
    @GET("unlock/queryAllUnlockRecord")
    Observable<ApiResponseBean<ResponseUnlockRecordsBean>> queryAllUnlockRecord();

}
