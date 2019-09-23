package com.example.im.controller.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.im.R;
import com.example.im.model.bean.InvationInfo;
import com.example.im.model.bean.UserInfo;

import java.util.ArrayList;
import java.util.List;

import static com.example.im.model.bean.InvationInfo.InvationStatus.GROUP_ACCEPT_APPLICATION;
import static com.example.im.model.bean.InvationInfo.InvationStatus.GROUP_ACCEPT_INVITE;
import static com.example.im.model.bean.InvationInfo.InvationStatus.GROUP_APPLICATION_ACCEPTED;
import static com.example.im.model.bean.InvationInfo.InvationStatus.GROUP_APPLICATION_DECLINED;
import static com.example.im.model.bean.InvationInfo.InvationStatus.GROUP_INVITE_ACCEPTED;
import static com.example.im.model.bean.InvationInfo.InvationStatus.GROUP_INVITE_DECLINED;
import static com.example.im.model.bean.InvationInfo.InvationStatus.NEW_GROUP_APPLICATION;
import static com.example.im.model.bean.InvationInfo.InvationStatus.NEW_GROUP_INVITE;

//邀请信息页面的适配器
public class InviteAdapter extends BaseAdapter {
    private Context mContext;
    private List<InvationInfo> mInvitationInfos = new ArrayList<>();
    private OnInviteListener mOnInviteListener;
    public InviteAdapter(Context context,OnInviteListener onInviteListener) {
        mContext = context;
        mOnInviteListener = onInviteListener;
    }
    //刷新数据的方法
    public void refresh(List<InvationInfo> invationInfos){
        if(invationInfos != null && invationInfos.size() >= 0){
            mInvitationInfos.clear();
            mInvitationInfos.addAll(invationInfos);

            notifyDataSetChanged();
        }

    }



    @Override
    public int getCount() {
        return mInvitationInfos == null?0:mInvitationInfos.size();
    }

    @Override
    public Object getItem(int i) {
        return mInvitationInfos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        //1 获取或创建ViewHolder
        ViewHolder holder = null;
        if (view == null){
            holder = new ViewHolder();

            view = View.inflate(mContext, R.layout.item_invite,null);
            holder.name   = view.findViewById(R.id.tv_invite_name);
            holder.reason = view.findViewById(R.id.tv_invite_reason);
            holder.accpet = view.findViewById(R.id.bt_invite_accept);
            holder.reject = view.findViewById(R.id.bt_invite_reject);

            view.setTag(holder);

        }else {
            holder = (ViewHolder) view.getTag();
        }
        //2 获取当前item数据
        InvationInfo invationInfo = mInvitationInfos.get(i);
        //3 显示当前item数据
        UserInfo user = invationInfo.getUser();
        if (user != null){//当前是联系人的邀请
            holder.name.setText(invationInfo.getUser().getName());
            holder.accpet.setVisibility(View.GONE);
            holder.reject.setVisibility(View.GONE);
            //原因
            if (invationInfo.getStatus() == InvationInfo.InvationStatus.NEW_INVITE){// 新的邀请
                if (invationInfo.getReason() == null){
                    holder.reason.setText("添加好友");

                }else {
                    holder.reason.setText(invationInfo.getReason());
                }
                holder.accpet.setVisibility(View.VISIBLE);
                holder.reject.setVisibility(View.VISIBLE);

            }else if (invationInfo.getStatus() == InvationInfo.InvationStatus.INVITE_ACCEPT){//接受邀请
                if (invationInfo.getReason() == null){
                    holder.reason.setText("接受邀请");
                }else {
                    holder.reason.setText(invationInfo.getReason());
                }

            }else if (invationInfo.getStatus() == InvationInfo.InvationStatus.INVITE_ACCEPT_BY_PEER){//邀请被接受
                if (invationInfo.getReason() == null){
                    holder.reason.setText("邀请被接受");
                }else {
                    holder.reason.setText(invationInfo.getReason());
                }

            }
            //按钮的处理
            holder.accpet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnInviteListener.onAccept(invationInfo);

                }
            });
            //拒绝按钮的点击事件处理
            holder.reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnInviteListener.onReject(invationInfo);
                }
            });
        }else { //群组的邀请信息
            //显示名称
            holder.name.setText(invationInfo.getGroup().getInvatePerson());
            holder.accpet.setVisibility(View.GONE);
            holder.reject.setVisibility(View.GONE);
            //显示原因
            switch(invationInfo.getStatus()){
                // 您的群申请请已经被接受
                case GROUP_APPLICATION_ACCEPTED:
                    holder.reason.setText("您的群申请已经被接受");

                    break;
                //  您的群邀请已经被接收
                case GROUP_INVITE_ACCEPTED:
                    holder.reason.setText("您的群邀请已经被接受");
                    break;
                // 你的群申请已经被拒绝
                case GROUP_APPLICATION_DECLINED:
                    holder.reason.setText("你的群申请已经被拒绝");
                    break;
                // 您的群邀请已经被拒绝
                case GROUP_INVITE_DECLINED:
                    holder.reason.setText("您的群邀请已经被拒绝");
                    break;
                // 您收到了群邀请
                case NEW_GROUP_INVITE:
                    holder.reason.setText("您收到了群邀请");
                    holder.accpet.setVisibility(View.VISIBLE);
                    holder.reject.setVisibility(View.VISIBLE);
                    //接受邀请
                    holder.accpet.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mOnInviteListener.onInviteAccept(invationInfo);
                        }
                    });
                    //拒绝邀请
                    holder.reject.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mOnInviteListener.onInviteReject(invationInfo);
                        }
                    });
                    break;
                // 您收到了群申请
                case NEW_GROUP_APPLICATION:
                    holder.reason.setText("您收到了群申请");
                    holder.accpet.setVisibility(View.VISIBLE);
                    holder.reject.setVisibility(View.VISIBLE);
                    //接受申请
                    holder.accpet.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mOnInviteListener.onApplicationAccept(invationInfo);
                        }
                    });
                    //拒绝申请
                    holder.reject.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mOnInviteListener.onApplicationReject(invationInfo);

                        }
                    });
                    break;
                // 你接受了群邀请
                case GROUP_ACCEPT_INVITE:
                    holder.reason.setText("你接受了群邀请");
                    break;
                // 您批准了群加入
                case GROUP_ACCEPT_APPLICATION:
                    holder.reason.setText("您批准了群加入");
                    break;
                case GROUP_REJECT_INVITE:
                    holder.reason.setText("您拒绝了群邀请");
            }
        }
        //4 返回view

        return view;
    }

    private class ViewHolder{
        private TextView name;
        private TextView reason;
        private Button accpet;
        private Button reject;

    }

    public interface OnInviteListener{
        //联系人接收按钮的点击事件
        void onAccept(InvationInfo invationInfo);
        //联系人拒绝按钮的点击事件
        void onReject(InvationInfo invationInfo);
        //接受邀请按钮处理
        void onInviteAccept(InvationInfo invationInfo);
        //拒绝邀请按钮处理
        void onInviteReject(InvationInfo invationInfo);
        //接受申请按钮处理
        void onApplicationAccept(InvationInfo invationInfo);
        //拒绝申请按钮处理
        void onApplicationReject(InvationInfo invationInfo);
    }
}
