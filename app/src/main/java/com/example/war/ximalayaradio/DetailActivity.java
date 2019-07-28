package com.example.war.ximalayaradio;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.war.ximalayaradio.adapters.DetailListAdapter;
import com.example.war.ximalayaradio.base.BaseActivity;
import com.example.war.ximalayaradio.base.BaseApplication;
import com.example.war.ximalayaradio.interfaces.IAlbumDetalViewCallback;
import com.example.war.ximalayaradio.interfaces.IPlayerCallback;
import com.example.war.ximalayaradio.interfaces.ISubscriptionCallback;
import com.example.war.ximalayaradio.presenters.AlbumDetalPresenter;
import com.example.war.ximalayaradio.presenters.PlayerPresenter;
import com.example.war.ximalayaradio.presenters.SubscriptionPresenter;
import com.example.war.ximalayaradio.utils.ImageBlurMaker;
import com.example.war.ximalayaradio.views.UILoader;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.header.bezierlayout.BezierLayout;
import com.squareup.leakcanary.RefWatcher;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.security.interfaces.ECKey;
import java.util.ArrayList;
import java.util.List;

/**
 * @author war
 */
public class DetailActivity extends BaseActivity implements IAlbumDetalViewCallback, UILoader.OnRetryClickListener, DetailListAdapter.ItemClckListener, IPlayerCallback, ISubscriptionCallback {

    private static final String TAG = "DetailActivity";
    private ImageView largeCover;
    private ImageView smallCover;
    private AlbumDetalPresenter mAlbumDetalPresenter;
    private TextView albumTitle;
    private TextView albumAuthor;
    private int mCurrntPage = 1;
    private RecyclerView mDetailList;
    private DetailListAdapter mDetailListAdapter;
    private FrameLayout mDetailListContainer;
    private UILoader mUiLoader;
    private long mCurrentId = -1;
    private ImageView mPlayControlBtn;
    private TextView mMPlayControlTv;
    private PlayerPresenter mPlayerPresenter;
    private List<Track> mMCurrentTarck = null;
    private final static int DEFAULT_PLAY_INDEX = 0;
    private int mIndex = 0;
    private TwinklingRefreshLayout mRefreshLayout;

    private boolean isLoaderMore = false;
    private boolean isRefresh = false;
    private String mTrackTitle;
    private List<Track> mTrackList = new ArrayList<>();
    private Track mTrack;
    private TextView mSubBtn;
    private SubscriptionPresenter mSubscriptionPresenter;
    private Album mCurrentAlbum;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RefWatcher refWatcher = BaseApplication.getRefWatcher(this);
        refWatcher.watch(this);
        setContentView(R.layout.activity_detail);

        getWindow().getDecorView().setSystemUiVisibility(View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        initView();
        initPresenter();
        //设置订阅按钮的状态
        updateSubState();
        initListener();
    }

    private void updateSubState() {
        if (mSubscriptionPresenter != null) {
            // todo
//            mSubscriptionPresenter.isSub(a)
            boolean isSub = mSubscriptionPresenter.isSub(mCurrentAlbum);
            mSubBtn.setText(isSub ? R.string.cancel_sub_tip_text : R.string.sub_tips_text);

        }
    }

    private void initPresenter() {
        //播放爪机详情的Presenter
        mAlbumDetalPresenter = AlbumDetalPresenter.getsInstance();
        mAlbumDetalPresenter.registerViewCallback(this);

        //播放器的Presenter
        mPlayerPresenter = PlayerPresenter.getPlayerPresenter();
        mPlayerPresenter.registerViewCallback(this);

        //订阅相关的
        mSubscriptionPresenter = SubscriptionPresenter.getInstance();
        mSubscriptionPresenter.getSubscriptionList();
        mSubscriptionPresenter.registerViewCallback(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayerPresenter != null) {
            mPlayerPresenter.unregisterViewCallback(this);
        }
        if (mAlbumDetalPresenter != null) {
            mAlbumDetalPresenter.unregisterViewCallback(this);
        }
        if (mSubscriptionPresenter != null) {
            mSubscriptionPresenter.unregisterViewCallback(this);
        }
    }

    private void initListener() {
        if (mPlayControlBtn != null) {
            mPlayControlBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //判断是不是有播放列表
                    //todo
                    if (mPlayerPresenter != null) {
                        if (mPlayerPresenter.haslayList() && mAlbumDetalPresenter.isPlayCurrentAlbumList()) {
                            //控制播放器的状态
                            handPlayListToControl();
                        } else {
                            handleNoPlayList();
                        }
                    }

                }
            });
        }

        mSubBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSubscriptionPresenter != null) {
                    boolean sub = mSubscriptionPresenter.isSub(mCurrentAlbum);
                    if (sub) {
                        mSubscriptionPresenter.deleteSubscription(mCurrentAlbum);
                    } else {
                        mSubscriptionPresenter.addSubscription(mCurrentAlbum);
                    }
                }
            }
        });
    }

    /**
     * 有播放器列表
     */
    private void handPlayListToControl() {
        if (mPlayerPresenter.isPlaying()) {
            mPlayerPresenter.pause();
        } else {
            mPlayerPresenter.play();
        }
    }

    /**
     * 播放器没有列表
     */
    private void handleNoPlayList() {
        if (mMCurrentTarck != null) {
            mIndex = DEFAULT_PLAY_INDEX;
            mPlayerPresenter.setPlayList(mMCurrentTarck, DEFAULT_PLAY_INDEX);
        }
    }

    private void initView() {
        mDetailListContainer = findViewById(R.id.detail_list_container);
        //
        if (mUiLoader == null) {
            mUiLoader = new UILoader(this) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView(container);
                }
            };
            mDetailListContainer.removeAllViews();
            mDetailListContainer.addView(mUiLoader);
            mUiLoader.setOnRetryClickListener(DetailActivity.this);
        }

        largeCover = findViewById(R.id.iv_large_cover);
        smallCover = findViewById(R.id.iv_small_cover);
        albumTitle = findViewById(R.id.tv_album_title);
        albumAuthor = findViewById(R.id.tv_album_author);

        //播放图标
        mPlayControlBtn = findViewById(R.id.detail_play_control);
        mMPlayControlTv = findViewById(R.id.play_contral_tv);

        mSubBtn = findViewById(R.id.detail_sub_btn);

    }

    private View createSuccessView(ViewGroup container) {
        View detailListView = LayoutInflater.from(this).inflate(R.layout.item_detail_list, container
                , false);
        //recycleriew 设置布局管理器
        mDetailList = detailListView.findViewById(R.id.album_detail_list);
        mRefreshLayout = detailListView.findViewById(R.id.refresh_layout);


        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mDetailList.setLayoutManager(linearLayoutManager);
        //设置适配器
        mDetailListAdapter = new DetailListAdapter();
        mDetailList.setAdapter(mDetailListAdapter);
        //设置item的上下间距
        mDetailList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(), 5);
                outRect.bottom = UIUtil.dip2px(view.getContext(), 5);
                outRect.left = UIUtil.dip2px(view.getContext(), 5);
                outRect.right = UIUtil.dip2px(view.getContext(), 5);
            }
        });

        mDetailListAdapter.setItemClickListener(this);
        BezierLayout bezierLayout = new BezierLayout(this);

        mRefreshLayout.setHeaderView(bezierLayout);
        mRefreshLayout.setMaxHeadHeight(140);
        mRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                super.onRefresh(refreshLayout);
                if (mAlbumDetalPresenter != null) {
                    mAlbumDetalPresenter.pullToRefeeshMore();
                    isRefresh = true;
                }
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                super.onLoadMore(refreshLayout);
                //TODO 加载更多内容
                if (mAlbumDetalPresenter != null) {
                    mAlbumDetalPresenter.loadMore();
                    isLoaderMore = true;
                }
            }
        });
        return detailListView;


    }

    @Override
    public void onDetailListLoaded(List<Track> tracks) {
        mTrackList.clear();
        mTrackList.addAll(tracks);


        if (isLoaderMore && mRefreshLayout != null) {
            isLoaderMore = false;
            mRefreshLayout.finishLoadmore();
            if (tracks.isEmpty()) {
                return;
            }
        }

        if (isRefresh && mRefreshLayout != null) {
            isRefresh = false;
            mRefreshLayout.finishRefreshing();
        }

        this.mMCurrentTarck = tracks;
        //根据结果显示UI
        if (tracks == null || tracks.isEmpty()) {
            if (mUiLoader != null) {
                mUiLoader.updateStatus(UILoader.UIStatus.EMPTY);
            }
        } else {
            if (mUiLoader != null) {
                mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
            }
        }
        //更新或者设置UI
        mDetailListAdapter.setData(mTrackList);
        Log.i(TAG, "onDetailListLoaded: ---- > mDetailListAdapter.setData(tracks);");
        Log.i(TAG, "mDetailListAdapter.setData(tracks);" + mAlbumDetalPresenter.isPlayCurrentAlbumList());
        for (int i = 0; i < mTrackList.size(); i++) {
            Log.i(TAG, "onDetailListLoaded: --- > "+ i+" ----> "+mTrackList);

        }

        mTrackTitle = mPlayerPresenter.getCurrentPlayTitle();
        updatePlayState(mPlayerPresenter.isPlaying());
        mIndex = mPlayerPresenter.getCurrentPlayIndex();

        Log.i(TAG, "onCreate: --- .> mAlbumDetalPresenter.isPlayCurrentAlbumList()" + mAlbumDetalPresenter.isPlayCurrentAlbumList());

        //todo 比较列表是否相同
        if (mDetailListAdapter != null && mAlbumDetalPresenter.isPlayCurrentAlbumList()) {
            mDetailListAdapter.upDataTextColor(mIndex);
        }


    }

    /**
     * 请求错误网络异常
     *
     * @param errorCode
     * @param errorMsg
     */
    @Override
    public void onNetWorkError(int errorCode, String errorMsg) {
        mUiLoader.updateStatus(UILoader.UIStatus.NETWORKERROR);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public void onAlbumLoaded(Album album) {
        this.mCurrentAlbum = album;
        mCurrentId = album.getId();
        //获取专辑详情内容
        if (mAlbumDetalPresenter != null) {

            mAlbumDetalPresenter.getAlumDetail((int) album.getId(), mCurrntPage);
        }

        //拿数据，显示loading状态
        if (mUiLoader != null) {
            mUiLoader.updateStatus(UILoader.UIStatus.LOADING);
        }

        if (albumTitle != null) {
            albumTitle.setText(album.getAlbumTitle());
        }

        if (albumAuthor != null) {
            albumAuthor.setText(album.getAnnouncer().getNickname());
        }

        //做毛玻璃效果
        if (largeCover != null && largeCover != null) {
            Picasso.with(this).load(album.getCoverUrlLarge()).into(largeCover, new Callback() {
                @Override
                public void onSuccess() {
                    Drawable drawable = largeCover.getDrawable();
                    if (drawable != null) {
                        ImageBlurMaker.makeBlur(largeCover, DetailActivity.this);
                    }
                }

                @Override
                public void onError() {
                    Log.i(TAG, "onError: ---- > ");
                }
            });

        }

        if (smallCover != null) {
            Picasso.with(this).load(album.getCoverUrlLarge()).into(smallCover);

        }
    }

    @Override
    public void onLoaderMoreFinished(int size) {
        if (size > 0) {
            Toast.makeText(this, "成功加载" + size + "条数据", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "没有更多节目", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRefreshFinished(int size) {

    }

    /**
     * 重新加载
     */
    @Override
    public void onRetryClick() {
        if (mAlbumDetalPresenter != null) {
            mAlbumDetalPresenter.getAlumDetail((int) mCurrentId, mCurrntPage);
        }
    }

    /**
     * adapter点击事件
     */
    @Override
    public void onItemClick(List<Track> list, int position) {
        mIndex = position;
        //设置播放器的数据
        PlayerPresenter playerPresenter = PlayerPresenter.getPlayerPresenter();
        playerPresenter.setPlayList(list, position);
        Log.i(TAG, "onItemClick: --- > WAR!! --- > !"+ list.get(position));
        //TODO跳转到播放器界面
        Intent intent = new Intent(this, PlayerActivity.class);
        startActivity(intent);
    }

    /**
     * 根据播放状态修改状态图标
     *
     * @param playing
     */
    private void updatePlayState(boolean playing) {
        //todo 比较列表是否相同
        Log.i(TAG, "updatePlayState:  -- > mAlbumDetalPresenter.isPlayCurrentAlbumList() " + mAlbumDetalPresenter.isPlayCurrentAlbumList());
        if (mPlayerPresenter != null && mAlbumDetalPresenter.isPlayCurrentAlbumList()) {
            //修改为正在播放
            if (mPlayControlBtn != null && mMPlayControlTv != null) {

                if (mTrackTitle != null && !mTrackTitle.isEmpty()) {
                    mMPlayControlTv.setText(mTrackTitle);
                } else {
                    mMPlayControlTv.setText(R.string.click_play_tips_text);
                }

                mPlayControlBtn.setImageResource(playing ?
                        R.drawable.selector_play_control_pause : R.drawable.selector_play_control_play);
            }

        }
    }


    @Override
    public void onPlayStart() {
        updatePlayState(true);
        if (mPlayerPresenter != null) {
            mIndex = mPlayerPresenter.getCurrentPlayIndex();
        }
    }

    @Override
    public void onPlayPause() {
        //修改为已暂停
        updatePlayState(false);
    }

    @Override
    public void onPlayStop() {
        //修改为已暂停
        updatePlayState(false);
    }

    @Override
    public void onProgressChange(int current, int duration) {

    }

    @Override
    public void playPre() {

    }

    @Override
    public void playNext() {

    }

    @Override
    public void switchPlayMode(XmPlayListControl.PlayMode mode) {

    }

    @Override
    public void getPlayList() {

    }

    @Override
    public void playByIndex(int index) {

    }

    @Override
    public void seekTo(int prigress) {

    }

    @Override
    public void onTrackUpdate(Track track, int playIndex) {
        if (track != null && mMCurrentTarck != null && mMCurrentTarck.contains(track)) {
            mTrack = track;
            mTrackTitle = track.getTrackTitle();
            if (!mTrackTitle.isEmpty() && mMPlayControlTv != null) {
                mMPlayControlTv.setText(mTrackTitle);
            }
        }
    }

    @Override
    public void onListLoaded(List<Track> list) {

    }

    @Override
    public void onPlayModeChange(XmPlayListControl.PlayMode playMode) {

    }

    @Override
    public void updateListOrder(boolean isReverse) {

    }

    @Override
    public void upDatePlayIndexForUi(int postion) {
        Log.i(TAG, "upDatePlayIndexForUi: --- > mAlbumDetalPresenter.isPlayCurrentAlbumList()" + mAlbumDetalPresenter.isPlayCurrentAlbumList());
        if (mDetailListAdapter != null) {
            mDetailListAdapter.upDataTextColor(mIndex);
        }
    }

    @Override
    public void onPlayByAlbumId(List<Track> tracks) {

    }

    @Override
    public void onAddResult(boolean isSuccess) {
        if (isSuccess) {
            mSubBtn.setText(R.string.cancel_sub_tip_text);
        }
        String tipText = isSuccess ? "已订阅" : "订阅失败";
        Toast.makeText(this, tipText, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteResult(boolean isSuccess) {
        if (isSuccess) {
            mSubBtn.setText(R.string.sub_tips_text);
        }
        String tipText = isSuccess ? "取消订阅" : "删除失败";
        Toast.makeText(this, tipText, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSubscriptionsLoaded(List<Album> albums) {

    }

    @Override
    public void onSubFull() {
        //处理一个即可
        Toast.makeText(this, "订阅太多，不得操作100", Toast.LENGTH_SHORT).show();
    }
}
