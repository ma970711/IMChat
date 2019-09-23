package com.example.im.controller.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.example.im.R;
import com.example.im.controller.adapter.InviteAdapter;
import com.example.im.model.Model;
import com.example.im.model.bean.InvationInfo;
import com.example.im.utils.Constant;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;

import java.util.List;

//邀请信息列表界面
public class InviteActivity extends Activity {
    private ListView lv_invite;
    private InviteAdapter.OnInviteListener mOnInviteListener = new InviteAdapter.OnInviteListener() {
        @Override
        public void onAccept(InvationInfo invationInfo) {
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().contactManager().acceptInvitation(invationInfo.getUser().getHxid());
                        //数据库更新
                        Model.getInstance().getDbManager().getInviteTableDao().updateInvitationStatus(InvationInfo.InvationStatus.INVITE_ACCEPT,invationInfo.getUser().getHxid());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //页面更新
                                Toast.makeText(InviteActivity.this,"接受邀请成功",Toast.LENGTH_SHORT).show();
                                //刷新页面
                                refresh();
                            }
                        });
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this,"接受邀请失败",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });

        }

        @Override
        public void onReject(InvationInfo invationInfo) {
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().contactManager().declineInvitation(invationInfo.getUser().getHxid());
                        //数据库变化
                        Model.getInstance().getDbManager().getInviteTableDao().removeInvitation(invationInfo.getUser().getHxid());
                        //页面更新
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this,"拒绝成功",Toast.LENGTH_SHORT).show();
                                refresh();
                            }
                        });

                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this,"拒绝失败",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });

        }
        //接受邀请按钮处理
        @Override
        public void onInviteAccept(InvationInfo invationInfo) {
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {

                        EMClient.getInstance().groupManager().acceptInvitation(invationInfo.getGroup().getGroupId(),invationInfo.getGroup().getInvatePerson());
                        //本地数据库更新
                        invationInfo.setStatus(InvationInfo.InvationStatus.GROUP_ACCEPT_INVITE);
                        Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invationInfo);
                        //内存数据变化
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this,"接受邀请成功",Toast.LENGTH_SHORT).show();
                                refresh();
                            }
                        });
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this,"接受邀请失败，原因"+e.toString(),Toast.LENGTH_SHORT).show();
                                refresh();
                            }
                        });
                    }
                }
            });

        }

        @Override
        public void onInviteReject(InvationInfo invationInfo) {
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        //通知环信服务器拒绝邀请
                        EMClient.getInstance().groupManager().declineInvitation(invationInfo.getGroup().getGroupId(),invationInfo.getGroup().getInvatePerson(),"拒绝邀请");
                        //本地数据库更新
                        invationInfo.setStatus(InvationInfo.InvationStatus.GROUP_REJECT_INVITE);
                        Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invationInfo);
                        //更新内存的数据
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this,"拒绝成功",Toast.LENGTH_SHORT).show();
                                refresh();
                            }
                        });
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this,"拒绝失败,原因"+e.toString(),Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                }
            });

        }

        @Override
        public void onApplicationAccept(InvationInfo invationInfo) {
            //通知环信服务器
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().groupManager().acceptApplication(invationInfo.getGroup().getGroupId(),invationInfo.getGroup().getInvatePerson());
                        invationInfo.setStatus(InvationInfo.InvationStatus.GROUP_ACCEPT_APPLICATION);
                        Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invationInfo);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this,"接受申请",Toast.LENGTH_SHORT).show();
                                refresh();
                            }
                        });
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this,"接受申请失败，原因"+e.toString(),Toast.LENGTH_SHORT).show();

                            }
                        });
                    }
                }
            });

        }

        @Override
        public void onApplicationReject(InvationInfo invationInfo) {
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().groupManager().declineApplication(invationInfo.getGroup().getGroupId(),invationInfo.getGroup().getInvatePerson(),"拒绝");
                        invationInfo.setStatus(InvationInfo.InvationStatus.GROUP_APPLICATION_DECLINED);
                        Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invationInfo);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this,"拒绝申请",Toast.LENGTH_SHORT).show();
                                refresh();
                            }
                        });
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(InviteActivity.this,"拒绝申请失败，原因"+e.toString(),Toast.LENGTH_SHORT).show();

                            }
                        });
                    }

                }
            });

        }
    };
    private InviteAdapter inviteAdapter;
    private LocalBroadcastManager mLBM;
    private BroadcastReceiver InviteChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            refresh();//刷新页面
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);
        initView();

        initData();
    }

    private void initData() {
        //初始化ListView
        inviteAdapter = new InviteAdapter(this,mOnInviteListener);

        lv_invite.setAdapter(inviteAdapter);

        //刷新方法
        refresh();
        //注册邀请信息变化广播
        mLBM = LocalBroadcastManager.getInstance(this);
        mLBM.registerReceiver(InviteChangedReceiver,new IntentFilter(Constant.CONTACT_INVITE_CHANGED));
        mLBM.registerReceiver(InviteChangedReceiver,new IntentFilter(Constant.GROUP_INVITE_CHANGED));
    }

    private void refresh() {
        //获取数据库中的所有邀请信息
        List<InvationInfo> invationInfos = Model.getInstance().getDbManager().getInviteTableDao().getInvationInfo();
        //刷新适配器
        inviteAdapter.refresh(invationInfos);
    }

    private void initView() {
       lv_invite = findViewById(R.id.lv_invite);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLBM.unregisterReceiver(InviteChangedReceiver);
    }
}
