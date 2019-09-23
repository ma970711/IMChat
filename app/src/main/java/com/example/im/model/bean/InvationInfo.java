package com.example.im.model.bean;

public class InvationInfo {
    private UserInfo user;
    private GroupInfo group;
    private String reason;//邀请原因
    private InvationStatus status;//邀请状态

    public UserInfo getUser() {
        return user;
    }

    public void setUser(UserInfo user) {
        this.user = user;
    }

    public GroupInfo getGroup() {
        return group;
    }

    public void setGroup(GroupInfo group) {
        this.group = group;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public InvationStatus getStatus() {
        return status;
    }

    public void setStatus(InvationStatus status) {
        this.status = status;
    }

    public InvationInfo() {

    }

    public InvationInfo(UserInfo user, GroupInfo group, String reason, InvationStatus status) {
        this.user = user;
        this.group = group;
        this.reason = reason;
        this.status = status;
    }

    public enum InvationStatus{
        NEW_INVITE, //新邀请
        INVITE_ACCEPT, //接受邀请
        INVITE_ACCEPT_BY_PEER, //邀请被接受
        //以下是群组邀请信息状态

        //收到邀请去加入群
        NEW_GROUP_INVITE,

        //收到申请群加入
        NEW_GROUP_APPLICATION,

        //群邀请被接受
        GROUP_INVITE_ACCEPTED,

        //群申请被批准
        GROUP_APPLICATION_ACCEPTED,

        //接受了群邀请
        GROUP_ACCEPT_INVITE,

        //批准的群加入申请
        GROUP_ACCEPT_APPLICATION,

        //拒绝了群邀请
        GROUP_REJECT_INVITE,

        //拒绝了群申请加入
        GROUP_REJECT_APPLICATION,

        //群邀请被对方拒绝
        GROUP_INVITE_DECLINED,

        //群申请被拒绝
        GROUP_APPLICATION_DECLINED
    }

    @Override
    public String toString() {
        return "InvationInfo{" +
                "user=" + user +
                ", group=" + group +
                ", reason='" + reason + '\'' +
                ", status=" + status +
                '}';
    }
}
