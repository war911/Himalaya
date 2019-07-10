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

import com.example.war.ximalayaradio.DetailActivity;
import com.example.war.ximalayaradio.R;
import com.example.war.ximalayaradio.adapters.AlbumListAdapter;
import com.example.war.ximalayaradio.base.BaseApplication;
import com.example.war.ximalayaradio.base.BaseFragment;
import com.example.war.ximalayaradio.interfaces.ISubscriptionCallback;
import com.example.war.ximalayaradio.presenters.AlbumDetalPresenter;
import com.example.war.ximalayaradio.presenters.SubscriptionPresenter;
import com.example.war.ximalayaradio.views.ConfirmDialog;
import com.example.war.ximalayaradio.views.UILoader;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.List;

public class SubscriptionFragment extends BaseFragment implements ISubscriptionCallback, AlbumListAdapter.onAlbumItemClickListner, AlbumListAdapter.onAlbumItemLongClickListener, ConfirmDialog.onDialogActionClickLinstener {

    private SubscriptionPresenter mSubscriptionPresenter;
    private RecyclerView mRecyclerView;
    private AlbumListAdapter mListAdapter;
    private TwinklingRefreshLayout mRefreshLayout;
    private ConfirmDialog mConfirmDialog;
    private static final String TAG = "SubscriptionFragment";
    private UILoader mUiLoader;

    @Override
    protected View onSubViewLoaded(LayoutInflater layoutInflater, ViewGroup container) {
        FrameLayout rootView = (FrameLayout) layoutInflater.inflate(R.layout.fragment_subscription, container, false);

        if (mUiLoader == null) {
            mUiLoader = new UILoader(container.getContext()) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return creatSuccessView();
                }
                @Override
                protected View getEmptyView() {
                    View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_empty_view, this, false);
                    TextView tv = view.findViewById(R.id.tv_empty);
                    tv.setText(R.string.sub_no_text);
                    return view;
                }
            };
        } else {
            if (mUiLoader.getParent() instanceof ViewGroup) {
                ((ViewGroup) mUiLoader.getParent()).removeView(mUiLoader);
            }
        }
        rootView.addView(mUiLoader);

        return rootView;
    }

    private View creatSuccessView() {
        View itemView = LayoutInflater.from(BaseApplication.getAppContext()).inflate(R.layout.item_subscription, null);
        mRefreshLayout = itemView.findViewById(R.id.sub_refresh);
        mRefreshLayout.setPureScrollModeOn();
        mRecyclerView = itemView.findViewById(R.id.sub_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
        mListAdapter = new AlbumListAdapter();
        mRecyclerView.setAdapter(mListAdapter);


        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(), 5);
                outRect.bottom = UIUtil.dip2px(view.getContext(), 5);
                outRect.left = UIUtil.dip2px(view.getContext(), 5);
                outRect.right = UIUtil.dip2px(view.getContext(), 5);
            }
        });


        mListAdapter.setOnAlbumItemClickListen(this);
        mListAdapter.setOnAlbumItemLongClickListener(this);

        mSubscriptionPresenter = SubscriptionPresenter.getInstance();
        mSubscriptionPresenter.registerViewCallback(this);
        mSubscriptionPresenter.getSubscriptionList();
        Log.i(TAG, "onSubViewLoaded: --- >getSubscriptionList ");

        if (mUiLoader != null) {
            mUiLoader.updateStatus(UILoader.UIStatus.LOADING);
        }
        return itemView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mSubscriptionPresenter != null) {
            mSubscriptionPresenter.unregisterViewCallback(this);
            Log.i(TAG, "onDestroyView: --- > ");
        }
        mListAdapter.setOnAlbumItemClickListen(null);
    }

    @Override
    public void onAddResult(boolean isSuccess) {

    }

    @Override
    public void onDeleteResult(boolean isSuccess) {

    }

    @Override
    public void onSubscriptionsLoaded(List<Album> albums) {
        if (albums.size() == 0) {
            if (mUiLoader != null) {
                mUiLoader.updateStatus(UILoader.UIStatus.EMPTY);
            }
        } else {
            if (mListAdapter != null) {
                mListAdapter.setData(albums);
            }
            if (mUiLoader != null) {
                mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
            }
        }

    }

    @Override
    public void onSubFull() {
        //处理一个即可
        Toast.makeText(BaseApplication.getAppContext(), "订阅太多，不得操作100", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onItemClick(int postion, Album album) {
        AlbumDetalPresenter.getsInstance().setTargetAlbum(album);
        Intent intent = new Intent(getContext(), DetailActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(int position, Album album) {
        mConfirmDialog = new ConfirmDialog(getActivity());
        mConfirmDialog.setOnDialogActionClickLinstener(this, album);
        mConfirmDialog.show();
    }

    @Override
    public void onCancelSubClick(Album album) {
        mSubscriptionPresenter.deleteSubscription(album);
        if (mConfirmDialog != null) {
            mConfirmDialog.dismiss();
        }
    }

    @Override
    public void onGiveUpClick() {
        if (mConfirmDialog != null) {
            mConfirmDialog.dismiss();
        }
    }
}
