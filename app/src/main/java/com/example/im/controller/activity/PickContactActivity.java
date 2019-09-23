package com.example.im.controller.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.im.R;
import com.example.im.controller.adapter.PickContactAdapter;
import com.example.im.model.Model;
import com.example.im.model.bean.PickContactInfo;
import com.example.im.model.bean.UserInfo;
import com.example.im.utils.Constant;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.List;

public class PickContactActivity extends Activity {
    private TextView tv_pick_save;
    private ListView lv_pick;
    private List<PickContactInfo> mPicks;
    private PickContactAdapter pickContactAdapter;
    private List<String> mExistMembers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_contact);
        getData();//获取传递过来的数据
        initView();
        initData();
        initListener();

    }

    private void getData() {
        String groupId = getIntent().getStringExtra(Constant.GROUP_ID);
        if (groupId !=null){
            EMGroup group = EMClient.getInstance().groupManager().getGroup(groupId);
            mExistMembers = group.getMembers();
        }
        if (mExistMembers == null){
            mExistMembers = new ArrayList<>();
        }
    }

    private void initListener() {
        //listView条目的点击事件
        lv_pick.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                //CheckBox的切换
                CheckBox cb_pick = view.findViewById(R.id.cb_pick);
                cb_pick.setChecked(!cb_pick.isChecked());

                //修改数据
                PickContactInfo pickContactInfo = mPicks.get(position);
                pickContactInfo.setChecked(cb_pick.isChecked());

                //刷新页面
                pickContactAdapter.notifyDataSetChanged();
            }
        });

        tv_pick_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //获取已选择的联系人
                List<String> names = pickContactAdapter.getPickContacts();
                //给启动页面返回数据
                Intent intent = new Intent();
                intent.putExtra("members",names.toArray(new String[0]));

                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }

    private void initData() {
        //获取数据库中的联系人信息
        // 获取所有联系人的数据

        List<UserInfo> contacts = Model.getInstance().getDbManager().getContactTableDao().getContacts();

        mPicks = new ArrayList<>();
        if (contacts != null && contacts.size()>=0){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(PickContactActivity.this,"成功获取联系人信息",Toast.LENGTH_SHORT).show();
                }
            });
            //转换
            for (UserInfo contact : contacts){
                PickContactInfo pickContactInfo = new PickContactInfo(contact, false);
                mPicks.add(pickContactInfo);
            }
        }else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(PickContactActivity.this,"获取联系人列表失败",Toast.LENGTH_SHORT).show();
                }
            });
        }

        pickContactAdapter = new PickContactAdapter(this, mPicks,mExistMembers);
        lv_pick.setAdapter(pickContactAdapter);
    }

    private void initView() {
        tv_pick_save= findViewById(R.id.tv_pick_save);
        lv_pick = findViewById(R.id.lv_pick);
    }
}
