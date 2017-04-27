package com.biaoke.bklive.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.biaoke.bklive.R;
import com.biaoke.bklive.bean.PrivateMessageBean;
import com.biaoke.bklive.utils.GlideUtis;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hasee on 2017/4/25.
 */

public class PrivateMessageAdapter extends RecyclerView.Adapter<PrivateMessageAdapter.PrivateMessageViewHolder> {

    private List<PrivateMessageBean> mList;
    private Context context;
    GlideUtis glideUtis;
    private int intLevel;

    public PrivateMessageAdapter(Context context, List<PrivateMessageBean> mList) {
        this.context = context;
        this.mList = mList;
        glideUtis = new GlideUtis(context);
    }

    public void bind(List<PrivateMessageBean> mList) {
        this.mList = mList;
        notifyDataSetChanged();
    }

    @Override
    public PrivateMessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.privatemessage_style, parent, false);
        return new PrivateMessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PrivateMessageViewHolder holder, final int position) {
        if (mList.get(position).getImageUrl().isEmpty()) {
            //头像地址为空，设置默认头像
            glideUtis.glideCircle("http://server-test.bk5977.com:8800/BK/icon.png", holder.userMessageHead, true);
        } else {
            glideUtis.glideCircle(mList.get(position).getImageUrl(), holder.userMessageHead, true);
        }
        holder.userLevel.setText("lv." + mList.get(position).getLevel());
        //根据用户等级设置等级背景颜色
        if (mList.get(position).getLevel().isEmpty()) {
            intLevel = 1;
        } else {
            intLevel = Integer.parseInt(mList.get(position).getLevel());
        }
        //数据强转为int类型，不能为空，切记切记
        if (intLevel <= 6) {
            holder.levelImagecolor.setImageResource(R.drawable.zhibojian_huang);
        } else if (intLevel > 6) {
            holder.levelImagecolor.setImageResource(R.drawable.zhibojian_lan);
        }
        holder.userNickname.setText(mList.get(position).getNickName());
        holder.endMessage.setText(mList.get(position).getMessage());
        holder.userNickname.setText(mList.get(position).getNickName());
        if (mList.get(position).getSex().equals("男")) {
            holder.userSex.setImageResource(R.drawable.man);
        } else {
            holder.userSex.setImageResource(R.drawable.female);
        }
        //时间转换
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
//        String stdTime = sdf.format(new Date(mList.get(position).getCurrentTime()));

        long time = mList.get(position).getCurrentTime();//转换为分钟
        long minute = time / 1000 / 60;
        long currentTime = System.currentTimeMillis() / (1000 * 60);
        if ((currentTime - minute) < 60) {
            holder.endmessageTime.setText((currentTime - minute) + "分钟前");
        } else if ((currentTime - minute) < 1440) {
            holder.endmessageTime.setText((currentTime - minute) / 60 + "小时前");
        } else {
            holder.endmessageTime.setText((currentTime - minute) / 60 / 24 + "天前");
        }
        holder.messageNum.setText(mList.get(position).getMsgNum());

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

    class PrivateMessageViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.user_message_head)
        ImageView userMessageHead;
        @BindView(R.id.user_level)
        TextView userLevel;
        @BindView(R.id.user_nickname)
        TextView userNickname;
        @BindView(R.id.user_sex)
        ImageView userSex;
        @BindView(R.id.end_message)
        TextView endMessage;
        @BindView(R.id.endmessage_time)
        TextView endmessageTime;
        @BindView(R.id.message_num)
        TextView messageNum;
        @BindView(R.id.level_imagecolor)
        ImageView levelImagecolor;

        public PrivateMessageViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    // ###################################   item的点击事件（接口回调） ##############
    public interface OnItemClickListener {

        void onItemClick(View view, int postion);

    }

    private PrivateMessageAdapter.OnItemClickListener onItemClickListener;

    //对外提供一个监听的方法
    public void setOnItemClickListener(PrivateMessageAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
