package com.biaoke.bklive.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.biaoke.bklive.R;
import com.biaoke.bklive.bean.LivingroomChatListBean;
import com.biaoke.bklive.utils.GlideUtis;

import java.util.List;

/**
 * Created by hasee on 2017/5/13.
 */

public class livingroomChatAdapter extends BaseAdapter {

    private List<LivingroomChatListBean> mList;
    private Context context;
    GlideUtis glideUtis;
    private int intLevel;

    public livingroomChatAdapter(List<LivingroomChatListBean> mList, Context context) {
        this.mList = mList;
        this.context = context;
        glideUtis = new GlideUtis(context);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_livingroom_message, null);
            holder = new ViewHolder();
            holder.ivUserHeadLiving = (ImageView) convertView.findViewById(R.id.iv_user_head_living);
            holder.levelImageColor = (ImageView) convertView.findViewById(R.id.level_image_color);
            holder.tvLevelLiving = (TextView) convertView.findViewById(R.id.tv_level_living);
            holder.tvNicknameLiving = (TextView) convertView.findViewById(R.id.tv_nickname_living);
            holder.tvMessageLivingroom = (TextView) convertView.findViewById(R.id.tv_message_livingroom);
            convertView.setTag(holder);//绑定ViewHolder对象
        } else {
            holder = (ViewHolder) convertView.getTag();//取出ViewHolder对象
        }
        if (mList.get(position).getImageUrl().isEmpty()) {
            //头像地址为空，设置默认头像
            glideUtis.glideCircle("http://server-test.bk5977.com:8800/BK/icon.png", holder.ivUserHeadLiving, true);
        } else {
            glideUtis.glideCircle(mList.get(position).getImageUrl(), holder.ivUserHeadLiving, true);
        }
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
        


        return convertView;
    }

    class ViewHolder {
        private ImageView ivUserHeadLiving;
        private ImageView levelImageColor;
        private TextView tvLevelLiving;
        private TextView tvNicknameLiving;
        private TextView tvMessageLivingroom;
    }
}
