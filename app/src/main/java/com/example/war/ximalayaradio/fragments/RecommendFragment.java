package com.example.war.ximalayaradio.fragments;

import android.content.Intent;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;


import com.example.war.ximalayaradio.DetailActivity;
import com.example.war.ximalayaradio.R;
import com.example.war.ximalayaradio.adapters.AlbumListAdapter;
import com.example.war.ximalayaradio.base.BaseFragment;
import com.example.war.ximalayaradio.interfaces.IRecommendViewCallback;
import com.example.war.ximalayaradio.presenters.AlbumDetalPresenter;
import com.example.war.ximalayaradio.presenters.RecommendPresenter;
import com.example.war.ximalayaradio.views.UILoader;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

public class RecommendFragment extends BaseFragment implements IRecommendViewCallback, UILoader.OnRetryClickListener, AlbumListAdapter.onAlbumItemClickListner {


    private static final String TAG = "RecommendFragment";
    private FrameLayout rootView;
    private RecyclerView recyclerView;
    private AlbumListAdapter mRecommendListAdapter;
    private RecommendPresenter mRecommendPresenter;
    private UILoader mUiLoader;
    private TwinklingRefreshLayout mRefreshLayout;

    @Override
    protected View onSubViewLoaded(final LayoutInflater layoutInflater, ViewGroup container) {
        rootView = (FrameLayout) layoutInflater.inflate(R.layout.fragment_recommend, container, false);
        if (mUiLoader == null) {
            mUiLoader = new UILoader(getContext()) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView(layoutInflater, container);
                }
            };
        }else {

            if (mUiLoader.getParent() instanceof ViewGroup) {
                ((ViewGroup) mUiLoader.getParent()).removeView(mUiLoader);
            }
        }


        //获取到逻辑层的对象
        mRecommendPresenter = RecommendPresenter.getsInstance();
        //先要设置通知接口注册
        mRecommendPresenter.registerViewCallback(this);
        //获取推荐列表
        mRecommendPresenter.getRecommendList();

        //返回view
        mUiLoader.setOnRetryClickListener(this);

        rootView.addView(mUiLoader);
        return rootView;
    }

    private View createSuccessView(LayoutInflater layoutInflater, ViewGroup container) {
        View rootView = layoutInflater.inflate(R.layout.item_recommend, container, false);
        //找控件
        mRefreshLayout = rootView.findViewById(R.id.recommend_refresh);
        mRefreshLayout.setPureScrollModeOn();

        recyclerView = rootView.findViewById(R.id.recommend_recycler_view);
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(), 5);
                outRect.bottom = UIUtil.dip2px(view.getContext(), 5);
                outRect.left = UIUtil.dip2px(view.getContext(), 5);
                outRect.right = UIUtil.dip2px(view.getContext(), 5);
            }
        });
        //设置适配器
        mRecommendListAdapter = new AlbumListAdapter();
        recyclerView.setAdapter(mRecommendListAdapter);
        mRecommendListAdapter.setOnAlbumItemClickListen(this);
        return rootView;
    }

    @Override
    public void onRecommendListLoaded(List<Album> result) {
        Log.d(TAG, "onRecommendListLoaded: ");
        //当我们获取玩推荐内容成功后，这个方法就会被调用成功
        //数据回来就可以更新UI了
        mRecommendListAdapter.setData(result);
        mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
    }

    @Override
    public void onNetworkError() {
        Log.d(TAG, "onNetworkError: ...");
        mUiLoader.updateStatus(UILoader.UIStatus.NETWORKERROR);

    }

    @Override
    public void onEmpty() {
        Log.d(TAG, "onEmpty: ...");

        mUiLoader.updateStatus(UILoader.UIStatus.EMPTY);

    }

    @Override
    public void onLoading() {
        Log.d(TAG, "onLoading: ...");

        mUiLoader.updateStatus(UILoader.UIStatus.LOADING);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消注册，避免内存泄漏
        if (mRecommendPresenter != null) {
            mRecommendPresenter.unregisterViewCallback(this);
        }
    }

    @Override
    public void onRetryClick() {
        //网络不佳的时候用户点击了重试
        //重新获取数据即可
        if (mRecommendPresenter != null) {
            mRecommendPresenter.getRecommendList();
        }
    }

    @Override
    public void onItemClick(int postion,Album album) {
        AlbumDetalPresenter.getsInstance().setTargetAlbum(album);
        //根据位置拿到数据
        //item被点击了，跳转到详情界面
        Log.d(TAG, "onItemClick: -----> " + postion);
        Intent intent = new Intent(getContext(), DetailActivity.class);
        startActivity(intent);

    }
}
