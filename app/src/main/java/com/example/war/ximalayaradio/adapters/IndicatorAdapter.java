package com.example.war.ximalayaradio.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.example.war.ximalayaradio.R;

import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.LinePagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;

/**
 * @author war
 */
public class IndicatorAdapter extends CommonNavigatorAdapter {

    private final String[] mTitle;
    private OnIndicatorTapClickListener mOnIndicatorTapClickListener;
    public IndicatorAdapter(Context context){
        mTitle = context.getResources().getStringArray(R.array.indicator_name);
    }
    @Override
    public int getCount() {
        if (mTitle != null) {
            return mTitle.length;
        }
        return 0;
    }

    @Override
    public IPagerTitleView getTitleView(Context context, final int index) {
        //创建一个view
        ColorTransitionPagerTitleView colorTransitionPagerTitleView = new ColorTransitionPagerTitleView(context);
        //设置一般情况下颜色为灰色
        colorTransitionPagerTitleView.setNormalColor(Color.parseColor("#aaffff"));
        //设置选中情况下颜色为黑色
        colorTransitionPagerTitleView.setSelectedColor(Color.parseColor("#ffffff"));
        //单位sp
        colorTransitionPagerTitleView.setTextSize(18);
        //设置要显示的内容
        colorTransitionPagerTitleView.setText(mTitle[index]);
        //设置title的点击事件，这里的话如果点了title，那么就选中下面的viewpager到对应的index中
        colorTransitionPagerTitleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //切换viewpager
                if (mOnIndicatorTapClickListener != null) {
                    mOnIndicatorTapClickListener.onTabClick(index);
                }
            }
        });
        return colorTransitionPagerTitleView;

    }

    @Override
    public IPagerIndicator getIndicator(Context context) {
        LinePagerIndicator linePagerIndicator = new LinePagerIndicator(context);
        linePagerIndicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
        linePagerIndicator.setColors(Color.WHITE);
        return linePagerIndicator;
    }

    public void setOnIndicatorClickListener(OnIndicatorTapClickListener listener){
        this.mOnIndicatorTapClickListener = listener;
    }
    public interface OnIndicatorTapClickListener{
        void onTabClick(int index);
    }
}
