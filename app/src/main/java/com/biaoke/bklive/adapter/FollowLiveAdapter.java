package com.biaoke.bklive.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.biaoke.bklive.R;
import com.biaoke.bklive.bean.FollowLiveBean;
import com.biaoke.bklive.utils.GlideUtis;
import com.xlibs.xrv.view.XRecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hasee on 2017/4/28.
 */

public class FollowLiveAdapter extends XRecyclerView.Adapter<FollowLiveAdapter.followliveItemViewHolder> {


    private List<FollowLiveBean> list;
    private Context context;
    XRecyclerView xRecyclerView;
    private GlideUtis glideutils;

    public FollowLiveAdapter(Context context, List<FollowLiveBean> list) {
        this.context = context;
        this.list = list;
        glideutils = new GlideUtis(context);
    }

    public void bind(List<FollowLiveBean> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public followliveItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.follow_live_item, parent, false);
        return new followliveItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final followliveItemViewHolder holder, final int position) {
        FollowLiveBean followBean = list.get(position);
        glideutils.glide(followBean.getSnapshotUrl(), holder.itemLiveThumbnail, true);//封面
        glideutils.glideCircle(followBean.getIconUrl(), holder.videoUserhead, true);
        if (followBean.getType().equals("live")) {
            holder.itemLiveState.setVisibility(View.VISIBLE);
        }
        holder.followTitle.setText(followBean.getTitle());
        holder.videoUserNickname.setText(followBean.getNickName());
        holder.addfollowNum.setText(followBean.getExp());
        //是直播的话就添加在线人数
        if (followBean.getType().equals("vod")) {
            holder.llOnline.setVisibility(View.GONE);
        } else {
            holder.peopleOnline.setText(followBean.getOnline());
        }

        //item点击事件
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(holder.itemView, position);
                }
            });
        }
        //添加喜欢的点击事件
        if (onItemFollowClickListener != null) {
            holder.addfollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemFollowClickListener.onItemSetFollowClick(holder.addfollow, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class followliveItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_live_Thumbnail)
        ImageView itemLiveThumbnail;
        @BindView(R.id.item_live_state)
        ImageView itemLiveState;
        @BindView(R.id.follow_title)
        TextView followTitle;
        @BindView(R.id.video_userhead)
        ImageView videoUserhead;
        @BindView(R.id.video_user_nickname)
        TextView videoUserNickname;
        @BindView(R.id.addfollow)
        ImageView addfollow;
        @BindView(R.id.people_online)
        TextView peopleOnline;
        @BindView(R.id.ll_online)
        LinearLayout llOnline;
        @BindView(R.id.addfollow_num)
        TextView addfollowNum;

        public followliveItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    // ###################################   item的点击事件（接口回调） ##############
    public interface OnItemClickListener {

        void onItemClick(View view, int postion);

    }

    public interface OnItemFollowClickListener {
        void onItemSetFollowClick(View view, int postion);
    }

    private OnItemFollowClickListener onItemFollowClickListener;
    private OnItemClickListener onItemClickListener;

    //对外提供一个监听的方法
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemFollowClickListener(OnItemFollowClickListener onItemClickListener) {
        this.onItemFollowClickListener = onItemClickListener;
    }
}
