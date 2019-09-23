package com.example.im.model;

import android.content.Context;
import android.content.Intent;

import android.support.v4.content.LocalBroadcastManager;

import com.example.im.model.bean.GroupInfo;
import com.example.im.model.bean.InvationInfo;
import com.example.im.model.bean.UserInfo;
import com.example.im.utils.Constant;
import com.example.im.utils.SpUtils;
import com.hyphenate.EMContactListener;
import com.hyphenate.EMGroupChangeListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMucSharedFile;

import java.util.List;

//全局事件监听
public class EventListener {


    private Context mContext;
    private final LocalBroadcastManager mLBM;

    public EventListener(Context context) {
        mContext = context;
        //创建一个发送广播的管理者对象
        mLBM = LocalBroadcastManager.getInstance(mContext);
        //注册一个联系人变化的监听
        EMClient.getInstance().contactManager().setContactListener(emContactListener);
        //注册一个群信息变化的监听
        EMClient.getInstance().groupManager().addGroupChangeListener(emGroupChangeListener);
    }
    private final EMGroupChangeListener emGroupChangeListener = new EMGroupChangeListener() {
        //收到群邀请
        @Override
        public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) {
            //数据更新
            InvationInfo invitationInfo = new InvationInfo();
            invitationInfo.setGroup(new GroupInfo(groupName,groupId,inviter));
            invitationInfo.setReason(reason);
            invitationInfo.setStatus(InvationInfo.InvationStatus.NEW_GROUP_INVITE);
            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invitationInfo);
            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,true);
            //发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));

        }
        //收到群申请通知
        @Override
        public void onRequestToJoinReceived(String groupId, String groupName, String applicant, String reason) {
            //数据更新
            InvationInfo invitationInfo = new InvationInfo();
            invitationInfo.setReason(reason);
            invitationInfo.setGroup(new GroupInfo(groupName,groupId,applicant));
            invitationInfo.setStatus(InvationInfo.InvationStatus.NEW_GROUP_APPLICATION);
            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invitationInfo);
            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,true);
            //发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }
        //收到群申请被接受
        @Override
        public void onRequestToJoinAccepted(String groupId, String groupName, String accepter) {
            //更新数据
            InvationInfo invitationInfo = new InvationInfo();
            invitationInfo.setGroup(new GroupInfo(groupName,groupId,accepter));
            invitationInfo.setStatus(InvationInfo.InvationStatus.GROUP_ACCEPT_APPLICATION);
            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invitationInfo);
            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,true);
            //发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }
        //群申请被拒绝
        @Override
        public void onRequestToJoinDeclined(String groupId, String groupName, String decliner, String reason) {
            //更新数据
            InvationInfo invitation = new InvationInfo();
            invitation.setReason(reason);
            invitation.setGroup(new GroupInfo(groupName,groupId,decliner));
            invitation.setStatus(InvationInfo.InvationStatus.GROUP_APPLICATION_DECLINED);
            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invitation);
            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,true);
            //发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }
        //群邀请被同意
        @Override
        public void onInvitationAccepted(String groupId, String invitee, String reason) {
            //更新数据
            InvationInfo invitation = new InvationInfo();
            invitation.setReason(reason);
            invitation.setGroup(new GroupInfo(groupId,groupId,invitee));
            invitation.setStatus(InvationInfo.InvationStatus.GROUP_INVITE_ACCEPTED);
            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invitation);
            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,true);
            //发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }
        //群邀请被拒绝
        @Override
        public void onInvitationDeclined(String groupId, String invitee, String reason) {
            //更新数据
            InvationInfo invitation = new InvationInfo();
            invitation.setGroup(new GroupInfo(groupId,groupId,invitee));
            invitation.setStatus(InvationInfo.InvationStatus.GROUP_INVITE_DECLINED);
            invitation.setReason(reason);
            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invitation);
            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,true);
            //发送广播
            mLBM.sendBroadcast(new Intent(Constant.GROUP_INVITE_CHANGED));
        }
        //群成员被删除
        @Override
        public void onUserRemoved(String groupId, String groupName) {



        }
        //群解散
        @Override
        public void onGroupDestroyed(String groupId, String groupName) {

        }

        @Override
        public void onAutoAcceptInvitationFromGroup(String groupId, String inviter, String inviteMessage) {
            //update data
            InvationInfo invitation = new InvationInfo();
            invitation.setReason(inviteMessage);
            invitation.setGroup(new GroupInfo(groupId,groupId,inviter));
            invitation.setStatus(InvationInfo.InvationStatus.GROUP_INVITE_ACCEPTED);
            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invitation);
            //红点处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,true);
            //发送广播
            mLBM.sendBroadcast(new Intent(Constant.NEW_GROUP_INVITE));
        }

        @Override
        public void onMuteListAdded(String groupId, List<String> mutes, long muteExpire) {

        }

        @Override
        public void onMuteListRemoved(String groupId, List<String> mutes) {

        }

        @Override
        public void onAdminAdded(String groupId, String administrator) {

        }

        @Override
        public void onAdminRemoved(String groupId, String administrator) {

        }

        @Override
        public void onOwnerChanged(String groupId, String newOwner, String oldOwner) {

        }

        @Override
        public void onMemberJoined(String groupId, String member) {

        }

        @Override
        public void onMemberExited(String groupId, String member) {

        }

        @Override
        public void onAnnouncementChanged(String groupId, String announcement) {

        }

        @Override
        public void onSharedFileAdded(String groupId, EMMucSharedFile sharedFile) {

        }

        @Override
        public void onSharedFileDeleted(String groupId, String fileId) {

        }
    };
    private final EMContactListener emContactListener = new EMContactListener() {
        //联系人增加后执行
        @Override
        public void onContactAdded(String hxid) {
            //数据更新
            Model.getInstance().getDbManager().getContactTableDao().saveContact(new UserInfo(hxid),true);

            //发送联系人变化的广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_CHANGED));
        }
        //联系人删除后执行的方法
        @Override
        public void onContactDeleted(String hxid) {
            //数据更新
            Model.getInstance().getDbManager().getContactTableDao().deleteContactByHxId(hxid);
            Model.getInstance().getDbManager().getInviteTableDao().removeInvitation(hxid);
            //发送联系人变化的广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_CHANGED));

        }
        //接收到联系人新的邀请
        @Override
        public void onContactInvited(String hxid, String reason) {
            //数据更新
            InvationInfo invitationInfo = new InvationInfo();
            invitationInfo.setUser(new UserInfo(hxid));
            invitationInfo.setReason(reason);
            invitationInfo.setStatus(InvationInfo.InvationStatus.NEW_INVITE);
            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(invitationInfo);

            //红点的处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,true);

            //发送邀请信息变化的广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_INVITE_CHANGED));

        }
        //别人同意了你的好友邀请
        @Override
        public void onFriendRequestAccepted(String username) {
            InvationInfo InvitationInfo = new InvationInfo();
            //数据库更新
            Model.getInstance().getDbManager().getInviteTableDao().addInvitation(InvitationInfo);
            InvitationInfo.setUser(new UserInfo(username));
            InvitationInfo.setStatus(InvationInfo.InvationStatus.INVITE_ACCEPT_BY_PEER); //别人同意了你的邀请
            //红点的处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,true);

            //发送邀请信息变化的广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_INVITE_CHANGED));

        }
        //别人拒绝了你的好友邀请
        @Override
        public void onFriendRequestDeclined(String username) {
            //红点的处理
            SpUtils.getInstance().save(SpUtils.IS_NEW_INVITE,true);

            //发送邀请信息变化的广播
            mLBM.sendBroadcast(new Intent(Constant.CONTACT_INVITE_CHANGED));

        }
    };
}
