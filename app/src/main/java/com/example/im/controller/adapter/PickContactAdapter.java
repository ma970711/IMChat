package com.example.im.controller.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.example.im.R;
import com.example.im.model.bean.PickContactInfo;

import java.util.ArrayList;
import java.util.List;

public class PickContactAdapter extends BaseAdapter {

    private Context mContext;

    private List<PickContactInfo> mPicks = new ArrayList<>();

    private List<String> mExistingMembers  = new ArrayList<>();

    public PickContactAdapter(Context mContext, List<PickContactInfo> mPicks) {
        this.mContext = mContext;
        this.mPicks = mPicks;
    }

    public PickContactAdapter(Context context , List<PickContactInfo> picks, List<String> existingMembers) {

        mContext = context;

        if(picks != null && picks.size() >= 0) {

            mPicks.clear();

            mPicks.addAll(picks);

        }

        // 接受群中已经存在的群成员的环信id

        if(existingMembers != null && existingMembers.size() >=0 ) {

            mExistingMembers.clear();

            mExistingMembers.addAll(existingMembers);

        }

    }

    // 获取选中的联系人

    public List<String> getAddMembers(){

        // 准备一个要返回的数据集合

        List<String> names = new ArrayList<>();

        // 遍历集合 选择出选中状态的联系人

        for (PickContactInfo pick: mPicks){

            if(pick.isChecked()) {

                names.add(pick.getUser().getName());

            }

        }

        return names;

    }

    @Override

    public int getCount() {

        return mPicks == null? 0:mPicks.size();

    }

    @Override

    public Object getItem(int position) {

        return mPicks.get(position);

    }

    @Override

    public long getItemId(int position) {

        return position;

    }



    @Override

    public View getView(int position, View convertView, ViewGroup parent) {

        // 创建或获取viewholder

        ViewHolder holder  = null;

        if(convertView == null) {

            holder = new ViewHolder();

            convertView = View.inflate(mContext, R.layout.item_pick, null);

            holder.tv_name = (TextView) convertView.findViewById(R.id.tv_pick_name);

            holder.cb_checked = (CheckBox) convertView.findViewById(R.id.cb_pick);

            convertView.setTag(holder);

        }else {

            holder = (ViewHolder) convertView.getTag();

        }

        // 获取当前item数据

        PickContactInfo pickContactInfo = mPicks.get(position);

        // 显示数据

        holder.tv_name.setText(pickContactInfo.getUser().getName());

        holder.cb_checked.setChecked(pickContactInfo.isChecked());

        if(mExistingMembers.contains(pickContactInfo.getUser().getHxid())) {

            holder.cb_checked.setChecked(true);

            pickContactInfo.setChecked(true);

        }

        // 返回view
        if (mExistingMembers.contains(pickContactInfo.getUser().getHxid())){
            holder.cb_checked.setChecked(true);
            pickContactInfo.setChecked(true);
        }

        return convertView;

    }

    public List<String> getPickContacts() { //获取选择的联系人
        List<String> picks = new ArrayList<>();
        for (PickContactInfo pick : mPicks){
            if (pick.isChecked()){  //判断是否选中
                picks.add(pick.getUser().getName());
            }
        }
        return picks;
    }

    static class ViewHolder{

        TextView tv_name;

        CheckBox cb_checked;

    }

}