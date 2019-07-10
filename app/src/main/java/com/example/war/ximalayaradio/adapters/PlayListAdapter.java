package com.example.war.ximalayaradio.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.war.ximalayaradio.R;
import com.example.war.ximalayaradio.base.BaseApplication;
import com.example.war.ximalayaradio.views.SobPopWindow;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

public class PlayListAdapter extends RecyclerView.Adapter<PlayListAdapter.InnerHolder> {
    private List<Track> mTrackList = new ArrayList<>();
    private int currentPlayIndex = 0;
    private SobPopWindow.playListItemOnclickListener mItemOnclickListener = null;

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_track_play, viewGroup, false);
        return new InnerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder innerHolder, final int index) {
        innerHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItemOnclickListener.onclick(index);
            }
        });
        TextView tv = innerHolder.itemView.findViewById(R.id.track_tv);
        tv.setText(mTrackList.get(index).getTrackTitle());

        ImageView img = innerHolder.itemView.findViewById(R.id.img);

        img.setVisibility(currentPlayIndex == index ? View.VISIBLE : View.INVISIBLE);

        tv.setTextColor(
                BaseApplication.getAppContext().getResources().getColor(currentPlayIndex == index?R.color.sendcolor:R.color.play_track_list_text_color)
        );

    }

    @Override
    public int getItemCount() {
        return mTrackList.size();
    }

    public void setData(List<Track> list) {
        mTrackList.clear();
        mTrackList.addAll(list);
        notifyDataSetChanged();
    }

    public void setPlayIndex(int index) {
        this.currentPlayIndex =  index;
        notifyDataSetChanged();
    }

    public void setOnItemClickLinstener(SobPopWindow.playListItemOnclickListener mlistener) {
        this.mItemOnclickListener = mlistener;
    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        public InnerHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
