package com.biaoke.bklive.user.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.biaoke.bklive.R;
import com.biaoke.bklive.common.AvatarLoadOptions;
import com.biaoke.bklive.user.bean.LiveVideo_list;
import com.biaoke.bklive.utils.GlideUtis;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hasee on 2017/5/15.
 */

public class ShortVideoAdapter extends RecyclerView.Adapter<ShortVideoAdapter.VideoViewHolder> {

    private Context context;
    GlideUtis glideUtis;
    private List<LiveVideo_list> mList;

    public ShortVideoAdapter(Context context, List<LiveVideo_list> mList) {
        this.context = context;
        this.mList = mList;
        glideUtis = new GlideUtis(context);
    }

    public void bindData(List<LiveVideo_list> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.shortvideo_item, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final VideoViewHolder holder, final int position) {
        //封面
        ImageLoader.getInstance().displayImage(mList.get(position).getSnapshotUrl(), holder.shortvideoImage, AvatarLoadOptions.build_item());

        holder.liveLooknum.setText(mList.get(position).getPv());
        holder.videoTitle.setText(mList.get(position).getTitle());
        holder.videoTime.setText(mList.get(position).getPubTime());
        // ####################   item点击事件   #################
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(holder.itemView, position);
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    onItemClickListener.onItemLongClick(holder.itemView, position);
                    return true;
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    class VideoViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.shortvideo_image)
        ImageView shortvideoImage;
        @BindView(R.id.live_looknum)
        TextView liveLooknum;
        @BindView(R.id.video_title)
        TextView videoTitle;
        @BindView(R.id.video_time)
        TextView videoTime;

        public VideoViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    // ###################################   item的点击事件（接口回调） ##############
    public interface OnItemClickListener {
        void onItemClick(View view, int postion);

        void onItemLongClick(View view, int postion);
    }

    private ShortVideoAdapter.OnItemClickListener onItemClickListener;

    //对外提供一个监听的方法
    public void setOnItemClickListener(ShortVideoAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}
