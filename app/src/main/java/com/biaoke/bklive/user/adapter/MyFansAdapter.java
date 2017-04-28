package com.biaoke.bklive.user.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.biaoke.bklive.R;
import com.biaoke.bklive.user.bean.MyFansBean;
import com.biaoke.bklive.utils.GlideUtis;
import com.xlibs.xrv.view.XRecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hasee on 2017/4/26.
 */

public class MyFansAdapter extends XRecyclerView.Adapter<MyFansAdapter.livenumViewHolder> {

    private List<MyFansBean> mList;
    private Context context;
    XRecyclerView xRecyclerView;
    GlideUtis glideUtis;

    public MyFansAdapter(Context context, List<MyFansBean> mList) {
        this.context = context;
        this.mList = mList;
        glideUtis = new GlideUtis(context);
    }

    public void bindData(List<MyFansBean> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    @Override
    public livenumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.fans_item, parent, false);
        return new livenumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final livenumViewHolder holder, final int position) {
        glideUtis.glideCircle(mList.get(position).getIconUrl(), holder.fansMessageHead, true);
        holder.fansLevel.setText(mList.get(position).getLevel());
        holder.fansNickname.setText(mList.get(position).getNickName());
        holder.fansSignture.setText(mList.get(position).getSignture());
        if (mList.get(position).getSex().equals("男")) {
            holder.fansSex.setImageResource(R.drawable.man);
        } else {
            holder.fansSex.setImageResource(R.drawable.female);
        }
        if (holder.btnFollow.isSelected()) {
            holder.btnFollow.setText("取消关注");
        }
        if (!holder.btnFollow.isSelected()) {
            holder.btnFollow.setText("关注");
        }

        //item的点击事件
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(holder.itemView, position);
                }
            });
        }
        //关注按钮的点击事件
        if (onItemSetFollowClickListener != null) {
            holder.btnFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemSetFollowClickListener.onItemSetFollowClick(holder.btnFollow, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }


    class livenumViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.fans_message_head)
        ImageView fansMessageHead;
        @BindView(R.id.fanslevel_imagecolor)
        ImageView fanslevelImagecolor;
        @BindView(R.id.fans_level)
        TextView fansLevel;
        @BindView(R.id.fans_nickname)
        TextView fansNickname;
        @BindView(R.id.fans_sex)
        ImageView fansSex;
        @BindView(R.id.fans_signture)
        TextView fansSignture;
        @BindView(R.id.btn_follow)
        Button btnFollow;

        public livenumViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    // ###################################   item的点击事件（接口回调） ##############
    public interface OnItemClickListener {

        void onItemClick(View view, int postion);

    }

    public interface OnItemSetFollowClickListener {
        void onItemSetFollowClick(View view, int postion);
    }

    private MyFansAdapter.OnItemSetFollowClickListener onItemSetFollowClickListener;
    private MyFansAdapter.OnItemClickListener onItemClickListener;

    //对外提供一个监听的方法
    public void setOnItemClickListener(MyFansAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemSetFollowClickListener(MyFansAdapter.OnItemSetFollowClickListener onItemClickListener) {
        this.onItemSetFollowClickListener = onItemClickListener;
    }

}
