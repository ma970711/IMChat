package com.example.im.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.example.im.R;
import com.example.im.controller.adapter.GroupDetailAdapter;
import com.example.im.model.Model;
import com.example.im.model.bean.UserInfo;
import com.example.im.utils.Constant;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCursorResult;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.List;

//群详情页面
public class GroupDetailActivity extends Activity {
    private GridView gv_groupdetail;
    private Button bt_groupdetail_out;
    private EMGroup mGroup;
    private GroupDetailAdapter.OnGroupDetailListener mOnGroupDetailListener = new GroupDetailAdapter.OnGroupDetailListener() {
        @Override
        public void onAddMembers() {
            Intent intent = new Intent(GroupDetailActivity.this, PickContactActivity.class);
            intent.putExtra(Constant.GROUP_ID,mGroup.getGroupId());
            startActivityForResult(intent,2);
        }

        @Override
        public void onDeleteMembers(UserInfo user) {
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().groupManager().removeUserFromGroup(mGroup.getGroupId(),user.getHxid());

                        //更新页面
                        getMemberFromHxServer();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this,"删除失败，原因"+e.toString(),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });


        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){
            //获取返回的群成员信息
            String[] members = data.getStringArrayExtra("members");
            Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        //去环信服务器发送邀请信息
                        EMClient.getInstance().groupManager().addUsersToGroup(mGroup.getGroupId(),members);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this,"发送邀请成功",Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this,"发送邀请失败，原因"+e.toString(),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            });
        }
    }

    private List<UserInfo> mUsers;
    private GroupDetailAdapter groupDetailAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);
        initView();
        getData();
        initData();
        initListener();
    }

    private void initListener() {
        gv_groupdetail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        //判断是否删除模式
                        if (groupDetailAdapter.ismIsDeleteModel()){
                            groupDetailAdapter.setmIsDeleteModel(false);//切换模式
                            groupDetailAdapter.notifyDataSetChanged();
                        }
                     break;
                }
                return false;
            }
        });
    }

    private void initData() {
        //初始化button显示
        initButtonDisplay();
        //初始化GridView
        initGridView();
        //从环信服务器获取所有群成员
        getMemberFromHxServer();
    }

    private void getMemberFromHxServer() {
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    //从环信服务器获取所有成员信息
                    List<String> members =new ArrayList<>();
                    EMCursorResult<String> mResult = null;
//                    members.add(emGroup.getOwner());
                    do {
                        mResult = EMClient.getInstance().groupManager().fetchGroupMembers(mGroup.getGroupId(),
                                mResult != null ? mResult.getCursor() : "", 1);
                        members.addAll(mResult.getData());
                    } while (!TextUtils.isEmpty(mResult.getCursor()) && mResult.getData().size() == 1);

                    mUsers = new ArrayList<>();
                    if (members != null && members.size()>=0){

                        //转换
                        for (String member:members){
                            UserInfo userInfo = new UserInfo(member);
                            mUsers.add(userInfo);
                        }
                        //更新页面
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                groupDetailAdapter.refresh(mUsers); //刷新适配器

                            }
                        });
                    }else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(GroupDetailActivity.this,"未知错误",Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                } catch (HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(GroupDetailActivity.this,"获取群信息失败,原因"+e.toString(),Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void initGridView() {
        boolean isCanModify = EMClient.getInstance().getCurrentUser().equals(mGroup.getOwner());
        groupDetailAdapter = new GroupDetailAdapter(this,isCanModify,mOnGroupDetailListener);
        gv_groupdetail.setAdapter(groupDetailAdapter);
    }

    private void initButtonDisplay() {
        if (EMClient.getInstance().getCurrentUser().equals(mGroup.getOwner())){//当前用户为群主
            bt_groupdetail_out.setText("解散群");
            bt_groupdetail_out.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                EMClient.getInstance().groupManager().destroyGroup(mGroup.getGroupId());
                                exitGroupBroatCast();//发送退群广播
                                runOnUiThread(new Runnable() { //更新页面
                                    @Override
                                    public void run() {
                                        Toast.makeText(GroupDetailActivity.this,"解散成功",Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                            } catch (HyphenateException e) {
                                e.printStackTrace();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(GroupDetailActivity.this,"解散失败，原因"+e.toString(),Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                }
            });

        }else {
            bt_groupdetail_out.setText("退群");
            bt_groupdetail_out.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                EMClient.getInstance().groupManager().leaveGroup(mGroup.getGroupId());
                                //发送退群广播
                                exitGroupBroatCast();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(GroupDetailActivity.this,"退群成功",Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                });
                            } catch (HyphenateException e) {
                                e.printStackTrace();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(GroupDetailActivity.this,"退群失败，原因"+e.toString(),Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                }
            });

        }
    }

    private void exitGroupBroatCast() {
        LocalBroadcastManager mLBM = LocalBroadcastManager.getInstance(GroupDetailActivity.this);
        Intent intent = new Intent(Constant.EXIT_GROUP);
        intent.putExtra(Constant.GROUP_ID,mGroup.getGroupId());
        mLBM.sendBroadcast(intent);

    }

    //获取传递过来的数据
    private void getData() {
        Intent intent = getIntent();
        String groupId = intent.getStringExtra(Constant.GROUP_ID);
        if (groupId == null){
            return;
        }else {
             mGroup = EMClient.getInstance().groupManager().getGroup(groupId);


        }
    }

    private void initView() {
        gv_groupdetail = findViewById(R.id.gv_groupdetail);
        bt_groupdetail_out = findViewById(R.id.bt_groupdetail_out);
    }
}
