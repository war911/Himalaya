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
import android.widget.TextView;
import android.widget.Toast;

import com.example.war.ximalayaradio.PlayerActivity;
import com.example.war.ximalayaradio.R;
import com.example.war.ximalayaradio.adapters.DetailListAdapter;
import com.example.war.ximalayaradio.base.BaseFragment;
import com.example.war.ximalayaradio.interfaces.IHistoryCallback;
import com.example.war.ximalayaradio.interfaces.IHistoryPresenter;
import com.example.war.ximalayaradio.presenters.HistoryPresenter;
import com.example.war.ximalayaradio.presenters.PlayerPresenter;
import com.example.war.ximalayaradio.views.ConfirmCheckBoxDialog;
import com.example.war.ximalayaradio.views.UILoader;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;



public class HistoryFragment extends BaseFragment implements IHistoryCallback, UILoader.OnRetryClickListener, DetailListAdapter.ItemClckListener, DetailListAdapter.ItemLongClickListener, ConfirmCheckBoxDialog.onDialogActionClickLinstener {

    private static final String TAG = "HistoryFragment";
    private IHistoryPresenter mIHistoryPresenter = null;
    private TwinklingRefreshLayout mRefreshLayout;
    private RecyclerView mHRecyclerView;
    private UILoader mUILoader;
    private DetailListAdapter mAdapter;
    private FrameLayout mRootView;
    private Track mCurrentClickHisTroyItem = null;
    private ConfirmCheckBoxDialog mDialog;

    @Override
    protected View onSubViewLoaded(final LayoutInflater layoutInflater, ViewGroup container) {
        FrameLayout mRootView = (FrameLayout) layoutInflater.inflate(R.layout.fragment_hostory,container,false);

        if (mUILoader == null) {
            mUILoader = new UILoader(getContext()) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView(layoutInflater,container);
                }

                @Override
                protected View getEmptyView() {
                    View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_empty_view, this, false);
                    TextView tv = view.findViewById(R.id.tv_empty);
                    tv.setText("没有历史内容");
                    return view;
                }
            };
        }else {
            if (mUILoader.getParent() instanceof ViewGroup) {
                ((ViewGroup) mUILoader.getParent()).removeView(mUILoader);
            }

        }



        //获取到逻辑层的对象
        mIHistoryPresenter = HistoryPresenter.getInstance();
        //注册回调
        mIHistoryPresenter.registerViewCallback(this);
        mUILoader.updateStatus(UILoader.UIStatus.LOADING);

        //获取历史记录
        mIHistoryPresenter.getListHistories();


        mUILoader.setOnRetryClickListener(this);



        mRootView.addView(mUILoader);
        return mRootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mIHistoryPresenter.unregisterViewCallback(this);
    }

    private View createSuccessView(LayoutInflater layoutInflater, ViewGroup container) {

        Log.i(TAG, "createSuccessView: ----  > ");
        View mRootView = layoutInflater.inflate(R.layout.item_histroy,container,false);
        //initView
        mRefreshLayout = mRootView.findViewById(R.id.history_refresh);
        mRefreshLayout.setPureScrollModeOn();

        mHRecyclerView = mRootView.findViewById(R.id.history_recyclerview);
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mHRecyclerView.setLayoutManager(linearLayoutManager);

        mHRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(),5);
                outRect.bottom = UIUtil.dip2px(view.getContext(), 5);
                outRect.left = UIUtil.dip2px(view.getContext(), 5);
                outRect.right = UIUtil.dip2px(view.getContext(), 5);
            }
        });

        mAdapter = new DetailListAdapter();
        mHRecyclerView.setAdapter(mAdapter);
        mAdapter.setItemClickListener(this);
        mAdapter.setItemLongClickListener(this);

        return mRootView;
    }

    /**
     * 历史内容有变化，刷新历史内容
     * @param list
     */
    @Override
    public void onHistoriesLoaded(List<Track> list) {
        if (list != null) {
            Log.i(TAG, "onHistoriesLoaded: --- > list.size" +list.size());
        }else {
            Log.i(TAG, "onHistoriesLoaded: ---- > list is null ");
        }
        if (list != null && !list.isEmpty()) {
            mAdapter.setData(list);
            if (mUILoader != null) {
                mUILoader.updateStatus(UILoader.UIStatus.SUCCESS);
            }
        }else {
            if (mUILoader != null) {
                mUILoader.updateStatus(UILoader.UIStatus.EMPTY);
            }
        }
    }

    /**
     * UIloader点击再次获取历史内容
     */
    @Override
    public void onRetryClick() {
        if (mIHistoryPresenter != null) {
            mIHistoryPresenter.getListHistories();
        }
    }

    /**
     * Adapter 点击事件 跳转详细列表界面
     * @param list
     * @param postion
     */
    @Override
    public void onItemClick(List<Track> list, int postion) {
        //设置播放器的数据
        PlayerPresenter playerPresenter = PlayerPresenter.getPlayerPresenter();
        playerPresenter.setPlayList(list, postion);

        Intent intent = new Intent(getActivity(), PlayerActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemLongCLick(Track track) {
        this.mCurrentClickHisTroyItem = track;
        //删除历史
        Toast.makeText(getActivity(), "删除历史", Toast.LENGTH_SHORT).show();

        mDialog = new ConfirmCheckBoxDialog(getActivity());
        mDialog.setOnDialogActionClickLinstener(this);
        mDialog.show();
    }

    @Override
    public void onCancelSubClick(Album album) {
        mDialog.dismiss();
    }

    @Override
    public void onConfirmClick(boolean checked) {
        if (checked) {
            if (mIHistoryPresenter != null && mCurrentClickHisTroyItem != null) {
                mIHistoryPresenter.clearHistories();
            }
        }else {
            //删除历史
            if (mIHistoryPresenter != null && mCurrentClickHisTroyItem != null) {
                mIHistoryPresenter.delHistory(mCurrentClickHisTroyItem);
            }
        }

        mDialog.dismiss();
    }
}
