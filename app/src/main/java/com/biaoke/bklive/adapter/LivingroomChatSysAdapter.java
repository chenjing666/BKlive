package com.biaoke.bklive.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.biaoke.bklive.R;
import com.biaoke.bklive.bean.LivingroomChatListBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hasee on 2017/4/18.
 */

public class LivingroomChatSysAdapter extends RecyclerView.Adapter<LivingroomChatSysAdapter.ChatSysViewHolder> {
    private List<LivingroomChatListBean> mList;
    private Context context;

    public LivingroomChatSysAdapter(Context context, List<LivingroomChatListBean> mList) {
        this.context = context;
        this.mList = mList;
    }

    public void bind(List<LivingroomChatListBean> mList) {
        this.mList = mList;
        notifyDataSetChanged();
    }

    @Override
    public ChatSysViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_livingroom_sys_msg, parent, false);
        return new ChatSysViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatSysViewHolder holder, int position) {
        holder.sysNickname.setText(mList.get(position).getNickName());
        holder.sysMsg.setText(mList.get(position).getMessage());
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    class ChatSysViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.sys_nickname)
        TextView sysNickname;
        @BindView(R.id.sys_msg)
        TextView sysMsg;

        public ChatSysViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
