package com.example.im.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;

import com.example.im.R;
import com.example.im.model.Model;
import com.example.im.model.bean.UserInfo;
import com.hyphenate.chat.EMClient;

//欢迎页面
public class SplashActivity extends Activity {
    private Handler handler = new Handler(){
      public void handleMessage(Message msg){
          if (isFinishing()){ //如果当前Activity已经退出，那么就不处理handler中的消息
              return;
          }
          //判断进入主页还是登录页
          toMainOrLogin();
      }

        private void toMainOrLogin() {
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    if(EMClient.getInstance().isLoggedInBefore()){ //如果登录过
                        //获取当前登录用户的信息
                        UserInfo account = Model.getInstance().getUserAccountDao().getAccountByHxId(EMClient.getInstance().getCurrentUser());
                        if(account == null){
                            //跳转到登录页
                            Intent intent=new Intent(SplashActivity.this,LoginActivity.class);
                            startActivity(intent);
                        }else {
                            //登陆成功后的方法
                            Model.getInstance().loginSuccess(account);
                            //跳转主页面
                            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                        //跳转主页面
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                    }else{//没登录过
                        //跳转到登录页
                        Intent intent=new Intent(SplashActivity.this,LoginActivity.class);
                        startActivity(intent);
                    }

                    //结束当前页面
                    finish();
                }
            });
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //发送延时2S消息
        handler.sendMessageDelayed(Message.obtain(),2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁消息
        handler.removeCallbacksAndMessages(null);
    }
}
