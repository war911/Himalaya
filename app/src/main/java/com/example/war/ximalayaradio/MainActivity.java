 package com.example.war.ximalayaradio;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.war.ximalayaradio.adapters.IndicatorAdapter;
import com.example.war.ximalayaradio.adapters.MainContentAdapter;
import com.example.war.ximalayaradio.base.BaseApplication;
import com.example.war.ximalayaradio.data.HimalayaDHHelper;
import com.example.war.ximalayaradio.interfaces.IPlayerCallback;
import com.example.war.ximalayaradio.presenters.PlayerPresenter;
import com.example.war.ximalayaradio.presenters.RecommendPresenter;
import com.example.war.ximalayaradio.utils.LogUtil;
import com.example.war.ximalayaradio.views.RoundRectImageView;
import com.squareup.leakcanary.RefWatcher;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;

import java.util.List;

 public class MainActivity extends FragmentActivity implements IPlayerCallback {

     private static final String TAG = "MainActivity";
     private MagicIndicator mMagicIndicator;
     private ViewPager mContent_pager;
     private IndicatorAdapter mIndicatorAdapter;
     private PlayerPresenter mPlayerPresenter;
     private RoundRectImageView mRoundRectImageView;
     private TextView mHeaderTitle;
     private TextView mSubTitle;
     private ImageView mPlayConrol;
     private View mPlayControlItem;
     private View mSearchBtn;

     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
         RefWatcher refWatcher = BaseApplication.getRefWatcher(this);
         refWatcher.watch(this);
        initview();
        initEvent();
        
        //
         initPersenter();


     }

     private void initPersenter() {
         mPlayerPresenter = PlayerPresenter.getPlayerPresenter();
         mPlayerPresenter.registerViewCallback(this);
     }

     private void initEvent() {
         mIndicatorAdapter.setOnIndicatorClickListener(new IndicatorAdapter.OnIndicatorTapClickListener() {
             @Override
             public void onTabClick(int index) {
                 LogUtil.d(TAG,"click index is -->" + index);
                 if (mContent_pager != null) {
                     mContent_pager.setCurrentItem(index);
                 }
             }
         });

         mPlayConrol.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 if (mPlayerPresenter != null) {
                     boolean haslayList = mPlayerPresenter.haslayList();
                     if (!haslayList) {
                         //没有列表就播放默认第一个专辑推荐
                         //第一个专辑推荐每天都会变
                         playFirstRecommend();
                     }else {
                         if (mPlayerPresenter.isPlaying()) {
                             mPlayerPresenter.pause();
                         }else {
                             mPlayerPresenter.play();
                         }
                     }
                 }
             }
         });

         mPlayControlItem.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 boolean haslayList = mPlayerPresenter.haslayList();
                 if (!haslayList) {
                     playFirstRecommend();
                 }
                 startActivity(new Intent(MainActivity.this,PlayerActivity.class));
             }
         });

         mSearchBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 startActivity(new Intent(MainActivity.this,SearchActivity.class));
             }
         });
     }

     /**
      * 播放第一个推荐内容
      */
     private void playFirstRecommend() {
         List<Album> currentRecommend = RecommendPresenter.getsInstance().getCurrentRecommend();
         if (currentRecommend != null) {
             Album album = currentRecommend.get(0);
             long albumId = album.getId();
             mPlayerPresenter.playByAlbumId(albumId);
         }
     }

     private void initview() {
         mMagicIndicator = findViewById(R.id.main_indicator);
         mMagicIndicator.setBackgroundColor(ContextCompat.getColor(this,R.color.maincolor));
        //创建Indication的适配器
         mIndicatorAdapter = new IndicatorAdapter(this);
         CommonNavigator commonNavigator = new CommonNavigator(this);
         commonNavigator.setAdjustMode(true);
         commonNavigator.setAdapter(mIndicatorAdapter);


         //ViewPager
         mContent_pager = findViewById(R.id.content_pager);
         FragmentManager supportFragmentManager = getSupportFragmentManager();
         MainContentAdapter mainContentAdapter = new MainContentAdapter(supportFragmentManager);
         mContent_pager.setAdapter(mainContentAdapter);

         mMagicIndicator.setNavigator(commonNavigator);
         ViewPagerHelper.bind(mMagicIndicator, mContent_pager);

         //播放器相关

         mRoundRectImageView = findViewById(R.id.main_icon);
         mHeaderTitle = findViewById(R.id.main_head_title);
         mSubTitle = findViewById(R.id.main_sub_title);
         mPlayConrol = findViewById(R.id.main_play_control);

         mPlayControlItem = findViewById(R.id.main_play_control_item);
         //搜索相关
         mSearchBtn = findViewById(R.id.search_btn);

     }

     @Override
     protected void onDestroy() {
         super.onDestroy();
         if (mPlayerPresenter != null) {
             mPlayerPresenter.unregisterViewCallback(this);
         }
         HimalayaDHHelper himalayaDHHelper = new HimalayaDHHelper(getApplicationContext());
         himalayaDHHelper.close();
     }

     @Override
     public void onPlayStart() {
         updatePlayControl(true);
     }

     private void updatePlayControl(boolean isPlaying){
         if (mPlayConrol != null) {
             mPlayConrol.setImageResource(isPlaying?R.drawable.selecter_player_pause
                     :R.drawable.selecter_player_play);
         }
     }

     @Override
     public void onPlayPause() {
         updatePlayControl(false);
     }

     @Override
     public void onPlayStop() {
         updatePlayControl(false);
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
         if (track != null) {
             String trackTitle = track.getTrackTitle();
             String nickname = track.getAnnouncer().getNickname();
             String coverUrlMiddle = track.getCoverUrlMiddle();

             if (trackTitle != null) {
                 mHeaderTitle.setText(trackTitle);
             }

             if (nickname != null) {
                 mSubTitle.setText(nickname);
             }

             if (coverUrlMiddle != null && mRoundRectImageView!=null) {
                 Picasso.with(this).load(coverUrlMiddle).into(mRoundRectImageView);
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
     public void upDatePlayIndexForUi(int position) {

     }

     @Override
     public void onPlayByAlbumId(List<Track> tracks) {

     }
 }
