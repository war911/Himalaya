package com.example.war.ximalayaradio.adapters;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.war.ximalayaradio.R;
import com.example.war.ximalayaradio.base.BaseApplication;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.SimpleFormatter;

public class DetailListAdapter extends RecyclerView.Adapter<DetailListAdapter.InnerHolder> {

    private List<Track> mDetailData = new ArrayList<>();
    private int mCurrIndex = -1;

    //格式化时间
    private SimpleDateFormat mSimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat mSimpleFormatter = new SimpleDateFormat("mm:ss");

    private ItemClckListener mItemClckListener = null;
    private ItemLongClickListener mItemLongclick = null;

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View inflate = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_album_detail, viewGroup, false);
        return new InnerHolder(inflate);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull InnerHolder innerHolder, final int postion) {
        //找到控件
        View itemView = innerHolder.itemView;
        //顺序ID
        TextView orderTv = itemView.findViewById(R.id.item_id_text);
        //标题
        TextView titleTv = itemView.findViewById(R.id.trick_title_text);
        //播放次数
        TextView playCount = itemView.findViewById(R.id.track_play_count_text);
        //时长
        TextView durationTv = itemView.findViewById(R.id.detail_item_duration);
        //更新日期
        TextView updateTv = itemView.findViewById(R.id.update_time_text);

        //设置数据
        final Track track = mDetailData.get(postion);
        orderTv.setText((postion + 1) + "");
        titleTv.setText(track.getTrackTitle());
        playCount.setText(track.getPlayCount() + "");
        durationTv.setText(mSimpleFormatter.format(track.getDuration() * 1000));
        updateTv.setText(mSimpleDateFormat.format(track.getUpdatedAt()) + "");

        if (mCurrIndex == postion) {
            orderTv.setTextColor(BaseApplication.getAppContext().getResources().getColor(R.color.maincolor));
            titleTv.setTextColor(BaseApplication.getAppContext().getResources().getColor(R.color.maincolor));
            playCount.setTextColor(BaseApplication.getAppContext().getResources().getColor(R.color.maincolor));
            updateTv.setTextColor(BaseApplication.getAppContext().getResources().getColor(R.color.maincolor));
            durationTv.setTextColor(BaseApplication.getAppContext().getResources().getColor(R.color.maincolor));
        }else {
            orderTv.setTextColor(Color.BLACK);
            titleTv.setTextColor(Color.BLACK);
            playCount.setTextColor(Color.BLACK);
            updateTv.setTextColor(Color.BLACK);
        }

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mItemClckListener != null) {
                    mItemClckListener.onItemClick(mDetailData, postion);
                }
            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mItemLongclick != null) {
                    mItemLongclick.onItemLongCLick(track);
                }
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return mDetailData.size();
    }

    public void setData(List<Track> tracks) {
        //清除
        mDetailData.clear();
//        添加新数据
        mDetailData.addAll(tracks);
        //更新UI
        notifyDataSetChanged();
    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        public InnerHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public void setItemClickListener(ItemClckListener listener) {
        this.mItemClckListener = listener;
    }

    public interface ItemClckListener {
        void onItemClick(List<Track> list, int postion);
    }

    public void upDataTextColor(int postion) {

        mCurrIndex = postion;
        notifyDataSetChanged();
    }

    public void setItemLongClickListener(ItemLongClickListener listener){
        this.mItemLongclick = listener;
    }

    public interface  ItemLongClickListener{
        void onItemLongCLick(Track track);
    }
}
