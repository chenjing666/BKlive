package com.biaoke.bklive.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.biaoke.bklive.R;
import com.biaoke.bklive.bean.live_item;
import com.biaoke.bklive.common.CommonUtil;
import com.biaoke.bklive.common.LoadImage;
import com.biaoke.bklive.common.LogUtil;
import com.pkmmte.view.CircularImageView;
import com.xlibs.xrv.view.XRecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hasee on 2017/4/4.
 */

public class liveItemAdapter extends XRecyclerView.Adapter<liveItemAdapter.liveItemViewHolder> {

    private Bitmap defaultBitmap;
    private List<live_item> list;
    private Context context;
    XRecyclerView xRecyclerView;
    private LoadImage loadImage;
    private LoadImage.ImageLoadListener listener = new LoadImage.ImageLoadListener() {
        /** *回调方法 *@parambitmap 请求回来的 bitmap *@paramurl 图片请求地址 */
        public void imageLoadOk(Bitmap bitmap, String url) {
            // 类似于 findviewById 得到每个 listview 的图片通过异步加载显示图 片
            ImageView iv = (ImageView) xRecyclerView.findViewWithTag(CommonUtil.NETPATH + url);
            LogUtil.d(url);
            if (iv != null) {
                LogUtil.d(" 异步加载得到图片的 url=" + url);
                iv.setImageBitmap(bitmap);
            }
        }
    };

    public liveItemAdapter(Context context, XRecyclerView xRecyclerView) {
        this.context = context;
        this.xRecyclerView = xRecyclerView;
        loadImage = new LoadImage(context, listener);
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
        defaultBitmap = BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher);
        live_item bean = list.get(position);
        String IconUrl = bean.getIconUrl();//获取头像地址
        String SnapshotUrl = bean.getSnapshotUrl();
        holder.itemLiveHead.setTag(CommonUtil.NETPATH + IconUrl);
        holder.itemLiveThumbnail.setTag(CommonUtil.NETPATH + SnapshotUrl);
        holder.itemLiveThumbnail.setMaxHeight((250 + (position % 3) * 30));
        Bitmap bitmap_head = loadImage.getBitmap(IconUrl);
        Bitmap bitmap_main = loadImage.getBitmap(SnapshotUrl);

        if (bitmap_head != null) {
            holder.itemLiveHead.setImageBitmap(bitmap_head);
        }
        if (bitmap_main != null) {
            holder.itemLiveThumbnail.setImageBitmap(bitmap_main);
        }

        if (bean.getType().equals("live")) {
            holder.itemLiveState.setText("直播中");
        } else {
            holder.itemLiveState.setText("短视频");
        }

//        holder.itemLiveThumbnail.setImageBitmap(defaultBitmap);//设置默认图片封面
        holder.itemLiveDescription.setText(bean.getTitle());
//        holder.itemLiveHead.setImageBitmap(defaultBitmap);//设置默认图片头像
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
        TextView itemLiveState;
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
    public interface OnItemClickListener {

        void onItemClick(View view, int postion);

    }

    private OnItemClickListener onItemClickListener;

    //对外提供一个监听的方法
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }


}
