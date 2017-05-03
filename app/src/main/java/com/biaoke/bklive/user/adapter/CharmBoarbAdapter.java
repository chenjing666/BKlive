package com.biaoke.bklive.user.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.biaoke.bklive.R;
import com.biaoke.bklive.user.bean.Charm_boardBean;
import com.biaoke.bklive.utils.GlideUtis;
import com.xlibs.xrv.view.XRecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hasee on 2017/5/3.
 */

public class CharmBoarbAdapter extends XRecyclerView.Adapter<CharmBoarbAdapter.charmBoardViewHolder> {

    private List<Charm_boardBean> mList;
    private Context context;
    GlideUtis glideUtis;

    public CharmBoarbAdapter(Context context, List<Charm_boardBean> mList) {
        this.context = context;
        this.mList = mList;
        glideUtis = new GlideUtis(context);
    }

    public void bindData(List<Charm_boardBean> list) {
        this.mList = list;
        notifyDataSetChanged();
    }

    @Override
    public charmBoardViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.bk_contribution_item, parent, false);
        return new charmBoardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(charmBoardViewHolder holder, int position) {

        if (position == 0) {
            holder.bkContributionItem.setVisibility(View.INVISIBLE);
        }
        if (position == 1) {
            holder.charmBoardHeadSecond.setVisibility(View.VISIBLE);
            holder.charmBoardSecond.setVisibility(View.VISIBLE);
            holder.tvCharmBoard.setVisibility(View.GONE);
        }
        if (position == 2) {
            holder.charmBoardHeadThird.setVisibility(View.VISIBLE);
            holder.charmBoardThird.setVisibility(View.VISIBLE);
            holder.tvCharmBoard.setVisibility(View.GONE);
        }
        holder.tvCharmBoard.setText("NO." + (position + 2));
        glideUtis.glideCircle(mList.get(position).getHeadUrl(), holder.charmBoardHead, true);
        holder.charmBoardLevel.setText(mList.get(position).getLevel());
        holder.contributionNickname.setText(mList.get(position).getNickName());
        if (mList.get(position).getSex().equals("男")) {
            holder.contributionSex.setImageResource(R.drawable.man);
        } else {
            holder.contributionSex.setImageResource(R.drawable.female);
        }
        holder.contributionCharm.setText(mList.get(position).getCharm());
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    class charmBoardViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.bk_contribution_item)
        RelativeLayout bkContributionItem;
        @BindView(R.id.charm_board_second)
        ImageView charmBoardSecond;
        @BindView(R.id.charm_board_third)
        ImageView charmBoardThird;
        @BindView(R.id.tv_charm_board)
        TextView tvCharmBoard;
        @BindView(R.id.charm_board_head)
        ImageView charmBoardHead;
        @BindView(R.id.charm_board_head_second)
        ImageView charmBoardHeadSecond;
        @BindView(R.id.charm_board_head_third)
        ImageView charmBoardHeadThird;
        @BindView(R.id.charm_board_level)
        TextView charmBoardLevel;
        @BindView(R.id.contribution_nickname)
        TextView contributionNickname;
        @BindView(R.id.contribution_sex)
        ImageView contributionSex;
        @BindView(R.id.contribution_charm)
        TextView contributionCharm;

        public charmBoardViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
