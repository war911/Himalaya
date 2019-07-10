package com.example.war.ximalayaradio.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.war.ximalayaradio.R;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

import java.util.ArrayList;
import java.util.List;

public class SearchRecommendAdapter extends RecyclerView.Adapter<SearchRecommendAdapter.InnerHolder>{
    private List<QueryResult> mData = new ArrayList<>();
    private ItemClickLinstener mItemClickLinstener = null;

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_search_recommend, viewGroup,false);

        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        final TextView textTitle = holder.itemView.findViewById(R.id.search_recommend_title);

        final QueryResult queryResult = mData.get(position);


        if (queryResult != null) {
            textTitle.setText(queryResult.getKeyword());
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo
                if (mItemClickLinstener != null) {
                    mItemClickLinstener.onItemClick(queryResult.getKeyword());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    /**
     * 设置数据
     * @param keyWordList
     */
    public void setData(List<QueryResult> keyWordList) {
        mData.clear();
        mData.addAll(keyWordList);
        notifyDataSetChanged();
    }

    public class InnerHolder extends RecyclerView.ViewHolder {

        public InnerHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public void setItemClickLinstener(ItemClickLinstener linstener){
        this.mItemClickLinstener = linstener;
    }

    public interface ItemClickLinstener{
        void onItemClick(String keyWord);
    }
}
