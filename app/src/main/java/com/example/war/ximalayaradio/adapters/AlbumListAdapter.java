package com.example.war.ximalayaradio.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.war.ximalayaradio.R;
import com.example.war.ximalayaradio.base.BaseApplication;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.ArrayList;
import java.util.List;

/**
 * @author war
 */
public class AlbumListAdapter extends RecyclerView.Adapter<AlbumListAdapter.InnerHolder> {
    private static final String TAG = "AlbumListAdapter";
    private List<Album> mData = new ArrayList<>();
    private onAlbumItemClickListner mItemClickListner = null;
    private onAlbumItemLongClickListener mItemLongClickListener = null;

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        //载入view
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);


        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder innerHolder, final int position) {
        //封装数据
        innerHolder.itemView.setTag(position);
        innerHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick:... " + v.getTag());
                if (mItemClickListner != null) {
                    int mItemClickPostion = (int) v.getTag();
                    mItemClickListner.onItemClick(mItemClickPostion,mData.get(mItemClickPostion));
                }
            }
        });

        innerHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //true 表示消费事件
                int position = (int) v.getTag();
                Album album = mData.get(position);
                if (mItemLongClickListener != null) {
                    mItemLongClickListener.onItemLongClick(position,album);
                }
                return true;
            }
        });
        innerHolder.setData(mData.get(position));
    }

    @Override
    public int getItemCount() {
        //返回要显示的个数
        if (mData != null) {
            return mData.size();
        }
        return 0;
    }

    public void setData(List<Album> albumList) {
        if (mData != null) {
            mData.clear();
            mData.addAll(albumList);
        }
        //更新UI
        notifyDataSetChanged();
    }

    public int getDataSize() {
        return mData.size();
    }

    public class InnerHolder extends RecyclerView.ViewHolder {

        private ImageView albumCoverIv;
        private TextView albumContentCountTv;
        private TextView albumPlayCountTv;
        private TextView albumTitleTv;
        private TextView albumdesrcTv;

        public InnerHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void setData(Album album) {
            //找到控件，设置数据
            //专辑封面
            albumCoverIv = itemView.findViewById(R.id.album_cover);
            //专辑t描述
            albumdesrcTv = itemView.findViewById(R.id.album_description_tv);
            //title
            albumTitleTv = itemView.findViewById(R.id.album_title_tv);
            //播放数量
            albumPlayCountTv = itemView.findViewById(R.id.album_play_count);
            //专辑数
            albumContentCountTv = itemView.findViewById(R.id.album_countent_size);

            if (album.getAlbumTitle().isEmpty()) {
                Log.i(TAG, "WAR setData: --- > album.getAlbumTitle() isempty !!! ");
            }else {
                Log.i(TAG, "WAR setData: --- album.getAlbumTitle()"+album.getAlbumTitle());
            }
            albumTitleTv.setText(album.getAlbumTitle());


            albumdesrcTv.setText(album.getAlbumIntro());
            albumPlayCountTv.setText(album.getPlayCount() + "");
            albumContentCountTv.setText(album.getIncludeTrackCount() + "");
            String middleCover = album.getMiddleCover();
            if (!TextUtils.isEmpty(middleCover)) {
                Picasso.with(itemView.getContext()).load(middleCover).into(albumCoverIv);
            }else {
                albumCoverIv.setImageResource(R.drawable.ximalay_logo);
            }



        }
    }

    public void setOnAlbumItemClickListen(onAlbumItemClickListner listenr) {
        this.mItemClickListner = listenr;

    }

    public interface onAlbumItemClickListner {
        void onItemClick (int position,Album album);
    }

    public void setOnAlbumItemLongClickListener(onAlbumItemLongClickListener listener){
        this.mItemLongClickListener = listener;
    }

    public interface onAlbumItemLongClickListener{
        void onItemLongClick(int position,Album album);
    }

}
