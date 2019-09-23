package com.example.im.controller.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.im.R;
import com.example.im.controller.activity.LoginActivity;
import com.example.im.model.Model;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;

public class SettingFragment extends Fragment {
    private Button bt_setting_loginout;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_setting, null);
        initView(view);
        return view;
    }

    private void initView(View view) {
        bt_setting_loginout = (Button) view.findViewById(R.id.bt_setting_loginout);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initData();
    }

    private void initData() {
        //在button上显示当前用户名
        bt_setting_loginout.setText("退出登录("+ EMClient.getInstance().getCurrentUser() + ")");
        //退出登录
        bt_setting_loginout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                    @Override
                    public void run() {
                        //登录环信服务器退出登录
                        EMClient.getInstance().logout(false, new EMCallBack() {
                            @Override
                            public void onSuccess() {
                                //关闭数据库DBHelper
                                Model.getInstance().getDbManager().close();
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        //更新UI，弹出Toast
                                        Toast.makeText(getActivity(),"退出成功",Toast.LENGTH_SHORT).show();
                                        //back to the loginActivity
                                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                                        startActivity(intent);
                                        getActivity().finish();
                                    }
                                });

                            }

                            @Override
                            public void onError(int code, String error) {
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(getActivity(),"退出失败"+error,Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }

                            @Override
                            public void onProgress(int progress, String status) {

                            }
                        });
                    }
                });
            }
        });
    }
}
