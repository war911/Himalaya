package com.example.war.ximalayaradio;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.war.ximalayaradio.adapters.AlbumListAdapter;
import com.example.war.ximalayaradio.adapters.SearchRecommendAdapter;
import com.example.war.ximalayaradio.base.BaseActivity;
import com.example.war.ximalayaradio.base.BaseApplication;
import com.example.war.ximalayaradio.interfaces.ISearchCallBack;
import com.example.war.ximalayaradio.presenters.AlbumDetalPresenter;
import com.example.war.ximalayaradio.presenters.SearchPresenter;
import com.example.war.ximalayaradio.views.FlowTextLayout;
import com.example.war.ximalayaradio.views.UILoader;
import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.squareup.leakcanary.RefWatcher;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.word.HotWord;
import com.ximalaya.ting.android.opensdk.model.word.QueryResult;

import net.lucode.hackware.magicindicator.buildins.UIUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author war
 */
public class SearchActivity extends BaseActivity implements ISearchCallBack {

    private static final String TAG = "SearchActivity";
    private View mBackBtn;
    private EditText mInputBox;
    private TextView mSearchBtn;
    private FrameLayout mResultContainer;
    private SearchPresenter mSearchPresenter;
    private UILoader mUiLoader;
    private RecyclerView mResultListView;
    private AlbumListAdapter mSearchAdapter;
    private FlowTextLayout mFlowTextLayout;
    private InputMethodManager mImm;
    private View mDelBtn;
    public static final int DEFAULT_TIME = 500;
    private RecyclerView mSearchRecommendList;
    private SearchRecommendAdapter mSearchRecommendAdapter;
    private TwinklingRefreshLayout mRefreshLayout;
    private boolean mNeedSuggestWord = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search);
        RefWatcher refWatcher = BaseApplication.getRefWatcher(this);
        refWatcher.watch(this);
        initView();
        initEvent();
        initPresenter();
    }

    private void initPresenter() {
        mImm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);

        mSearchPresenter = SearchPresenter.getSearchPresenter();
        mSearchPresenter.registerViewCallback(this);
        //拿到热词
        mSearchPresenter.getHotWord();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSearchPresenter != null) {
            mSearchPresenter.unregisterViewCallback(this);
            mSearchPresenter = null;
        }
    }

    private void initEvent() {
        mRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                super.onRefresh(refreshLayout);


            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                super.onLoadMore(refreshLayout);

                if (mSearchPresenter != null) {
                    mSearchPresenter.loadMore();
                }

            }
        });
        if (mSearchRecommendAdapter != null) {

            mSearchRecommendAdapter.setItemClickLinstener(new SearchRecommendAdapter.ItemClickLinstener() {
                @Override
                public void onItemClick(String keyWord) {
                    mNeedSuggestWord = false;
                    switchToSearch(keyWord);
                }
            });
        }

        if (mUiLoader != null) {

            mUiLoader.setOnRetryClickListener(new UILoader.OnRetryClickListener() {
                @Override
                public void onRetryClick() {
                    if (mSearchPresenter != null) {
                        mSearchPresenter.reSearch();
                        if (mUiLoader != null) {
                            mUiLoader.updateStatus(UILoader.UIStatus.LOADING);
                        }
                    }
                }
            });
        }

        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //todo:去执行搜索
                String key = mInputBox.getText().toString().trim();
                if (TextUtils.isEmpty(key)) {
                    Toast.makeText(SearchActivity.this, "搜索内容不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (mSearchPresenter != null) {
                    mSearchPresenter.doSearch(key);
                    if (mUiLoader != null) {
                        mUiLoader.updateStatus(UILoader.UIStatus.LOADING);
                    }
                }
            }
        });

        mInputBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (TextUtils.isEmpty(s)) {
                    mSearchPresenter.getHotWord();
                    mDelBtn.setVisibility(View.GONE);


                } else {
                    mDelBtn.setVisibility(View.VISIBLE);
                    if (mNeedSuggestWord) {
                        //触发联想查询
                        getSuggestWorld((String) s.toString());

                    }else {
                        mNeedSuggestWord = true;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        mFlowTextLayout.setClickListener(new FlowTextLayout.ItemClickListener() {
            @Override
            public void onItemClick(String text) {
                //推荐热词的点击
                //不需要相关的联想

                mNeedSuggestWord = false;
                switchToSearch(text);
            }
        });

        mDelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mInputBox.setText("");
            }
        });

        if (mSearchAdapter != null) {
            mSearchAdapter.setOnAlbumItemClickListen(new AlbumListAdapter.onAlbumItemClickListner() {
                @Override
                public void onItemClick(int postion, Album album) {
                    AlbumDetalPresenter.getsInstance().setTargetAlbum(album);
                    //根据位置拿到数据
                    //item被点击了，跳转到详情界面
                    Log.d(TAG, "onItemClick: -----> " + postion);
                    Intent intent = new Intent(SearchActivity.this, DetailActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    private void switchToSearch(String text) {
        if (TextUtils.isEmpty(text)) {
            Toast.makeText(this, "搜索内容不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        //热词扔到输入框里
        mInputBox.setText(text);
        mInputBox.setSelection(text.length());

        //发起搜索
        mSearchPresenter.doSearch(text);
        if (mUiLoader != null) {
            mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
        }
    }

    /**
     * 获取联想词
     *
     * @param keyWorld
     */
    private void getSuggestWorld(String keyWorld) {
        if (mSearchPresenter != null) {
            mSearchPresenter.getRecommedKeyWord(keyWorld);
        }
    }

    private void initView() {
        mBackBtn = findViewById(R.id.img_back);
        mInputBox = findViewById(R.id.search_input);
        mDelBtn = findViewById(R.id.search_input_delete);
        mDelBtn.setVisibility(View.GONE);
        mInputBox.postDelayed(new Runnable() {
            @Override
            public void run() {
                mInputBox.requestFocus();
                mImm.showSoftInput(mInputBox, InputMethodManager.SHOW_IMPLICIT);
            }
        }, DEFAULT_TIME);
        mSearchBtn = findViewById(R.id.tv_search_control);
        mResultContainer = findViewById(R.id.search_container);
        mSearchAdapter = new AlbumListAdapter();
//        mFlow = findViewById(R.id.flow);
        if (mUiLoader == null) {

            mUiLoader = new UILoader(this) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView();
                }

                @Override
                protected View getEmptyView() {
                    View view = LayoutInflater.from(getContext()).inflate(R.layout.fragment_empty_view, this, false);
                    TextView tv = view.findViewById(R.id.tv_empty);
                    tv.setText(R.string.search_no_text);
                    return view;
                }
            };
            if (mUiLoader.getParent() instanceof ViewGroup) {
                ((ViewGroup) mUiLoader.getParent()).removeView(mUiLoader);
            }
            mResultContainer.addView(mUiLoader);

        }

    }

    /**
     * 创建一个数据请求成功的view
     *
     * @return
     */
    private View createSuccessView() {
        View resultView = LayoutInflater.from(this).inflate(R.layout.search_layout_view, null);
        //刷新控件
        mRefreshLayout = resultView.findViewById(R.id.search_refresh_layout);
        mRefreshLayout.setEnableRefresh(false);
        //显示热词
        mFlowTextLayout = resultView.findViewById(R.id.recommend_hot_word_view);


        mResultListView = resultView.findViewById(R.id.result_list_view);
        //设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mResultListView.setLayoutManager(linearLayoutManager);
        //设置适配器

        mResultListView.setAdapter(mSearchAdapter);

        mResultListView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(), 5);
                outRect.bottom = UIUtil.dip2px(view.getContext(), 5);
                outRect.left = UIUtil.dip2px(view.getContext(), 5);
                outRect.right = UIUtil.dip2px(view.getContext(), 5);
            }
        });

        //搜索推荐
        mSearchRecommendList = resultView.findViewById(R.id.search_recommend_list);

        //设置布局管理器
        LinearLayoutManager recommednLayoutManager = new LinearLayoutManager(this);
        mSearchRecommendList.setLayoutManager(recommednLayoutManager);
        //设置适配器
        mSearchRecommendAdapter = new SearchRecommendAdapter();
        mSearchRecommendList.setAdapter(mSearchRecommendAdapter);
        mSearchRecommendList.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(), 2);
                outRect.bottom = UIUtil.dip2px(view.getContext(), 2);
                outRect.left = UIUtil.dip2px(view.getContext(), 5);
                outRect.right = UIUtil.dip2px(view.getContext(), 5);
            }
        });
        return resultView;

    }

    @Override
    public void onSearchResultLoaded(List<Album> result) {
        hideSuccessView();
        handlerSearchResult(result);

        //隐藏键盘
        mImm.hideSoftInputFromWindow(mInputBox.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);


    }

    private void handlerSearchResult(List<Album> result) {
        mRefreshLayout.setVisibility(View.VISIBLE);
        if (result != null) {
            if (result.size() == 0) {
                //数据为空
                if (mUiLoader != null) {
                    mUiLoader.updateStatus(UILoader.UIStatus.EMPTY);
                }

            } else {
                if (mSearchAdapter != null) {
                    mSearchAdapter.setData(result);
                }
                if (mUiLoader != null) {
                    mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
                }

            }
        }
    }

    @Override
    public void onHotWordLoaded(List<HotWord> hotWords) {
        hideSuccessView();
        mFlowTextLayout.setVisibility(View.VISIBLE);
        if (mUiLoader != null) {
            mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
        }
        Log.i(TAG, "onHotWordLoaded: -- > " + hotWords.size());
        List<String> hostwords = new ArrayList<>();
        hostwords.clear();
        if (hotWords != null) {
            for (HotWord hotWord : hotWords) {
                String searchword = hotWord.getSearchword();
                hostwords.add(searchword);
            }
        }
        Collections.sort(hostwords);
        //更新UI
        mFlowTextLayout.setTextContents(hostwords);
    }

    @Override
    public void onLoadMoreResult(List<Album> result, boolean isOkay) {
        //处理加载更多的结果
        if (mRefreshLayout != null) {
            mRefreshLayout.finishLoadmore();
        }

        if (isOkay) {
            handlerSearchResult(result);
        } else {
            Toast.makeText(this, "没有更多内容", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRecommendWordLoaded(List<QueryResult> keyWordList) {
        //联想相关的关键字
        if (keyWordList != null && !keyWordList.isEmpty()) {
            if (mSearchRecommendAdapter != null) {
                mSearchRecommendAdapter.setData(keyWordList);
            }
            //控制UI 的状态和隐藏显示
            if (mUiLoader != null) {
                mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
            }

            //控制显示和隐藏
            hideSuccessView();
            mSearchRecommendList.setVisibility(View.VISIBLE);

        }

    }

    @Override
    public void onError(int errorCode, String errorMsg) {
        if (mUiLoader != null) {
            mUiLoader.updateStatus(UILoader.UIStatus.NETWORKERROR);
        }

    }

    private void hideSuccessView() {
        mRefreshLayout.setVisibility(View.GONE);
        mSearchRecommendList.setVisibility(View.GONE);
        mFlowTextLayout.setVisibility(View.GONE);
    }
}
