package com.example.im.controller.fragment;

import android.content.Intent;
import android.support.v4.app.Fragment;

import com.example.im.controller.activity.ChatActivity;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.EaseConstant;
import com.hyphenate.easeui.EaseUI;
import com.hyphenate.easeui.ui.EaseConversationListFragment;

import java.util.List;

//会话列表页面
public class ChatFragment extends EaseConversationListFragment {


    @Override
    protected void initView() {
        super.initView();
        //跳转到会话详情页面
        setConversationListItemClickListener(new EaseConversationListItemClickListener() {
            @Override
            public void onListItemClicked(EMConversation conversation) {
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                intent.putExtra(EaseConstant.EXTRA_USER_ID,conversation.conversationId());
                //是否群聊
                if (conversation.getType() == EMConversation.EMConversationType.GroupChat){
                    intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE,EaseConstant.CHATTYPE_GROUP);
                }
                startActivity(intent);
            }
        });

        conversationList.clear();//清空集合,针对4.4及以下版本BUG修复
        //监听会话消息
        EMClient.getInstance().chatManager().addMessageListener(emMessageListener);
    }
    private EMMessageListener emMessageListener = new EMMessageListener() {
        @Override
        public void onMessageReceived(List<EMMessage> messages) {
            //设置数据
            EaseUI.getInstance().getNotifier().notify(messages);

            //刷新页面
            refresh();
        }

        @Override
        public void onCmdMessageReceived(List<EMMessage> messages) {

        }

        @Override
        public void onMessageRead(List<EMMessage> messages) {

        }

        @Override
        public void onMessageDelivered(List<EMMessage> messages) {

        }

        @Override
        public void onMessageRecalled(List<EMMessage> messages) {

        }

        @Override
        public void onMessageChanged(EMMessage message, Object change) {

        }
    };
}
