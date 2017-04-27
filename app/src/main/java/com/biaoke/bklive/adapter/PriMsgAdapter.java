package com.biaoke.bklive.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.biaoke.bklive.R;
import com.biaoke.bklive.bean.PriMsg;
import com.biaoke.bklive.utils.GlideUtis;

import java.util.List;

/**
 * Created by hasee on 2017/4/27.
 */

public class PriMsgAdapter extends ArrayAdapter<PriMsg> {

    private int resourceId;
    private GlideUtis glideUtis;

    public PriMsgAdapter(Context context, int textViewResourceId, List<PriMsg> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
        glideUtis = new GlideUtis(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PriMsg msg = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.leftLayout = (LinearLayout) view.findViewById(R.id.left_layout);
            viewHolder.rightLayout = (LinearLayout) view.findViewById(R.id.right_layout);
            viewHolder.leftMsg = (TextView) view.findViewById(R.id.left_msg);
            viewHolder.rightMsg = (TextView) view.findViewById(R.id.right_msg);
            viewHolder.leftImageView = (ImageView) view.findViewById(R.id.left_head);
            viewHolder.rightImageView = (ImageView) view.findViewById(R.id.right_head);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        if (msg.getType() == PriMsg.TYPE_RECEIVED) {
            // 如果是收到的消息，则显示左边的消息布局，将右边的消息布局隐藏
            viewHolder.leftLayout.setVisibility(View.VISIBLE);
            viewHolder.rightLayout.setVisibility(View.GONE);
            viewHolder.leftMsg.setText(msg.getContent());
            glideUtis.glideCircle(msg.getHeadUrl(), viewHolder.leftImageView, true);
        } else if (msg.getType() == PriMsg.TYPE_SENT) {
            // 如果是发出的消息，则显示右边的消息布局，将左边的消息布局隐藏
            viewHolder.rightLayout.setVisibility(View.VISIBLE);
            viewHolder.leftLayout.setVisibility(View.GONE);
            viewHolder.rightMsg.setText(msg.getContent());
            glideUtis.glideCircle(msg.getHeadUrl(), viewHolder.rightImageView, true);
        }
        return view;
    }

    class ViewHolder {
        LinearLayout leftLayout;
        LinearLayout rightLayout;
        ImageView leftImageView;
        ImageView rightImageView;
        TextView leftMsg;
        TextView rightMsg;
    }
}
