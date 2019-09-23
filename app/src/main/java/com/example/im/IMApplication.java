package com.example.im;

import android.app.Application;
import android.content.Context;

import com.example.im.model.Model;
import com.hyphenate.chat.EMOptions;
import com.hyphenate.easeui.EaseUI;

public class IMApplication extends Application {
    private static Context mContext;
    @Override
    public void onCreate() {
        super.onCreate();
        //初始化环信EasyUI
        EMOptions options = new EMOptions();
        options.setAcceptInvitationAlways(false);//经过同意后才接受邀请
        options.setAutoAcceptGroupInvitation(false);//经过同意后才接受群邀请
        EaseUI.getInstance().init(this,options);

        //初始化数据模型层类
        Model.getInstance().init(this);
        //初始化全局上下文对象
        mContext = this;

    }
    //全局上下文
    public static Context getGlobalApplication(){
        return mContext;
    }
}
