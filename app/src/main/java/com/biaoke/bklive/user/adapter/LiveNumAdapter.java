package com.biaoke.bklive.user.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.biaoke.bklive.R;
import com.biaoke.bklive.user.bean.LiveVideo_list;
import com.biaoke.bklive.utils.GlideUtis;
import com.xlibs.xrv.view.XRecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hasee on 2017/4/26.
 */

public class LiveNumAdapter extends XRecyclerView.Adapter<LiveNumAdapter.livenumViewHolder> {

    private List<LiveVideo_list> mList;
    private Context context;
    XRecyclerView xRecyclerView;
    GlideUtis glideUtis;

    public LiveNumAdapter(Context context, List<LiveVideo_list> mList) {
        this.context = context;
        this.mList = mList;
        glideUtis = new GlideUtis(context);
    }

    public void bindData(List<LiveVideo_list> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    @Override
    public livenumViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.user_livenum_item, parent, false);
        return new livenumViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final livenumViewHolder holder, final int position) {
        glideUtis.glide(mList.get(position).getSnapshotUrl(), holder.videoImage, true);
        holder.liveLooknum.setText(mList.get(position).getPv());
        holder.videoTitle.setText(mList.get(position).getTitle());
        holder.videoTime.setText(mList.get(position).getPubTime());
        //item的点击事件
        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(holder.itemView, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }


    class livenumViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.video_image)
        ImageView videoImage;
        @BindView(R.id.live_looknum)
        TextView liveLooknum;
        @BindView(R.id.video_title)
        TextView videoTitle;
        @BindView(R.id.video_time)
        TextView videoTime;

        public livenumViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    // ###################################   item的点击事件（接口回调） ##############
    public interface OnItemClickListener {

        void onItemClick(View view, int postion);

    }

    private LiveNumAdapter.OnItemClickListener onItemClickListener;

    //对外提供一个监听的方法
    public void setOnItemClickListener(LiveNumAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}
