package com.biaoke.bklive.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.biaoke.bklive.R;
import com.biaoke.bklive.bean.live_item;
import com.biaoke.bklive.common.AvatarLoadOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.pkmmte.view.CircularImageView;
import com.xlibs.xrv.view.XRecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hasee on 2017/4/4.
 */

public class liveItemAdapter_samecity extends XRecyclerView.Adapter<liveItemAdapter_samecity.liveItemViewHolder> {

    private List<live_item> list;
    private Context context;

    public liveItemAdapter_samecity(Context context) {
        this.context = context;
        //通知更新
        notifyDataSetChanged();
    }

    public void bind(List<live_item> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public liveItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_live, parent, false);
        return new liveItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final liveItemViewHolder holder, final int position) {
        live_item bean = list.get(position);
        String IconUrl = bean.getIconUrl();//获取头像地址
        String SnapshotUrl = bean.getSnapshotUrl();
        //头像图片，图片加载
        ImageLoader.getInstance()
                .displayImage(IconUrl, holder.itemLiveHead, AvatarLoadOptions.build_item());
        //封面
        ImageLoader.getInstance()
                .displayImage(SnapshotUrl, holder.itemLiveThumbnail, AvatarLoadOptions.build_item());


        if (bean.getType().equals("live")) {
            holder.itemLiveState.setVisibility(View.VISIBLE);
        }

        holder.itemLiveDescription.setText(bean.getTitle());
        holder.itemLiveNickName.setText(bean.getNickName());
        holder.itemLivePeople.setText(bean.getExp());

        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(holder.itemView, position);
                }
            });
        }
        holder.itemLiveHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(holder.itemView, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class liveItemViewHolder extends XRecyclerView.ViewHolder {
        @BindView(R.id.item_live_state)
        ImageView itemLiveState;
        @BindView(R.id.item_live_Thumbnail)
        ImageView itemLiveThumbnail;
        @BindView(R.id.item_live_description)
        TextView itemLiveDescription;
        @BindView(R.id.item_live_head)
        CircularImageView itemLiveHead;
        @BindView(R.id.item_live_nickName)
        TextView itemLiveNickName;
        @BindView(R.id.item_live_people)
        TextView itemLivePeople;

        public liveItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    // ###################################   item的点击事件（接口回调） ##############
    public interface OnsamecityItemClickListener {

        void onItemClick(View view, int postion);

    }

    private OnsamecityItemClickListener onItemClickListener;

    //对外提供一个监听的方法
    public void setOnItemClickListener(OnsamecityItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


}
