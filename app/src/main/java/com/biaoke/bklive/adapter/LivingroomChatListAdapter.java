package com.biaoke.bklive.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.biaoke.bklive.R;
import com.biaoke.bklive.bean.LivingroomChatListBean;
import com.biaoke.bklive.utils.GlideUtis;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hasee on 2017/4/17.
 */

public class LivingroomChatListAdapter extends RecyclerView.Adapter<LivingroomChatListAdapter.ChatListViewHolder> {

    private List<LivingroomChatListBean> mList;
    private Context context;
    GlideUtis glideUtis;
    private int intLevel;

    public LivingroomChatListAdapter(Context context, List<LivingroomChatListBean> mList) {
        this.context = context;
        this.mList = mList;
        glideUtis = new GlideUtis(context);
    }

    public void bind(List<LivingroomChatListBean> mList) {
        this.mList = mList;
        notifyDataSetChanged();
    }

    @Override
    public ChatListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_livingroom_message, parent, false);
        return new ChatListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ChatListViewHolder holder, int position) {
        glideUtis.glideCircle(mList.get(position).getImageUrl(), holder.ivUserHeadLiving, true);
        holder.tvLevelLiving.setText("lv." + mList.get(position).getLevel());
        //根据用户等级设置等级背景颜色
        if (mList.get(position).getLevel().isEmpty()) {
            intLevel = 1;
        } else {
            intLevel = Integer.parseInt(mList.get(position).getLevel());
        }
        //数据强转为int类型，不能为空，切记切记
        if (intLevel <= 6) {
            holder.levelImageColor.setImageResource(R.drawable.zhibojian_huang);
        } else if (intLevel > 6) {
            holder.levelImageColor.setImageResource(R.drawable.zhibojian_lan);
        }

        holder.tvNicknameLiving.setText(mList.get(position).getNickName());
        holder.tvMessageLivingroom.setText(mList.get(position).getMessage());
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    class ChatListViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_user_head_living)
        ImageView ivUserHeadLiving;
        @BindView(R.id.level_image_color)
        ImageView levelImageColor;
        @BindView(R.id.tv_level_living)
        TextView tvLevelLiving;
        @BindView(R.id.tv_nickname_living)
        TextView tvNicknameLiving;
        @BindView(R.id.tv_message_livingroom)
        TextView tvMessageLivingroom;

        public ChatListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
