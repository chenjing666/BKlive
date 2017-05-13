package com.biaoke.bklive.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.biaoke.bklive.R;
import com.biaoke.bklive.bean.HeadBean;
import com.biaoke.bklive.utils.GlideUtis;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hasee on 2017/4/4.
 */

public class VideoHeadImgAdapter extends RecyclerView.Adapter<VideoHeadImgAdapter.ViewHoldeer> {

    private Context context;
    private List<HeadBean> mList;
    GlideUtis glideUtis;

    public VideoHeadImgAdapter(Context context, List<HeadBean> mList) {
        this.context = context;
        this.mList = mList;
        glideUtis = new GlideUtis(context);
    }

    public VideoHeadImgAdapter(Context context) {
        this.context = context;
    }

    public void bindData(List<HeadBean> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    @Override
    public ViewHoldeer onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_plvodeo_view, parent, false);
        return new ViewHoldeer(view);
    }

    @Override
    public void onBindViewHolder(final ViewHoldeer holder, final int position) {
        if (mList.get(position).getImgUrl() == null) {
            //获取不到就显示默认
            glideUtis.glideCircle("http://server-test.bk5977.com:8800/BK/logo/logo-72.png", holder.itemHeadImgIv, true);
        } else {
            glideUtis.glideCircle(mList.get(position).getImgUrl(), holder.itemHeadImgIv, true);
        }
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

    class ViewHoldeer extends RecyclerView.ViewHolder {
        @BindView(R.id.item_head_img_iv)
        ImageView itemHeadImgIv;

        public ViewHoldeer(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    // ###################################   item的点击事件（接口回调） ##############
    public interface OnItemClickListener {

        void onItemClick(View view, int postion);

    }

    private VideoHeadImgAdapter.OnItemClickListener onItemClickListener;

    //对外提供一个监听的方法
    public void setOnItemClickListener(VideoHeadImgAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
