package com.example.im.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.example.im.R;
import com.example.im.model.Model;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroupManager;
import com.hyphenate.chat.EMGroupOptions;
import com.hyphenate.exceptions.HyphenateException;

//创建新群
public class NewGroupActivity extends Activity {
    private EditText et_newgroup_name;
    private EditText et_newgroup_desc;
    private CheckBox cb_newgroup_public;
    private CheckBox cb_newgroup_invite;
    private Button bt_newgroup_create;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_group);
        initView();
        initListener();
    }

    private void initListener() {
        bt_newgroup_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NewGroupActivity.this, PickContactActivity.class);

                startActivityForResult(intent,1); //带回调参数
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK){ //成功获取到联系人
            createGroup(data.getStringArrayExtra("members"));
        }else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(NewGroupActivity.this,"未知错误",Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void createGroup(String[] members) {
        String groupName = et_newgroup_name.getText().toString();
        String groupDesc = et_newgroup_desc.getText().toString();
        Model.getInstance().getGlobalThreadPool().execute(new Runnable() {
            @Override
            public void run() {
                EMGroupOptions options = new EMGroupOptions();
                options.maxUsers = 200;
                options.inviteNeedConfirm = true;
                EMGroupManager.EMGroupStyle groupStyle = null;
                if (cb_newgroup_public.isChecked()){
                    if (cb_newgroup_invite.isChecked()){
                        groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePublicOpenJoin;
                    }else {
                        groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePublicJoinNeedApproval;
                    }
                }else {
                    if (cb_newgroup_invite.isChecked()){
                        groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePrivateMemberCanInvite;
                    }else {
                        groupStyle = EMGroupManager.EMGroupStyle.EMGroupStylePrivateOnlyOwnerInvite;
                    }
                }

                options.style = groupStyle;//创建群类型

                try {
                    EMClient.getInstance().groupManager().createGroup(groupName,groupDesc,members,"申请加入",options);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(NewGroupActivity.this,"创建成功",Toast.LENGTH_SHORT).show();

                            finish();
                        }
                    });
                } catch (HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(NewGroupActivity.this,"创建失败，原因"+e.toString(),Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void initView() {
        et_newgroup_name = findViewById(R.id.et_newgroup_name);
        et_newgroup_desc = findViewById(R.id.et_newgroup_desc);
        cb_newgroup_public = findViewById(R.id.cb_newgroup_public);
        cb_newgroup_invite = findViewById(R.id.cb_newgroup_invite);
        bt_newgroup_create = findViewById(R.id.bt_newgroup_create);
    }
}
