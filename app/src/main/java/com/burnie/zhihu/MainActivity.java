package com.burnie.zhihu;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.NavigationView;
import android.support.v4.util.Pair;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Slide;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.burnie.zhihu.adapter.SectionAdapter;
import com.burnie.zhihu.bean.MySection;
import com.burnie.zhihu.bean.News;
import com.burnie.zhihu.bean.Themes;
import com.burnie.zhihu.net.NetWork;
import com.burnie.zhihu.util.StatusBarUtil;
import com.burnie.zhihu.util.UIUtil;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

/**
 * @author liuli
 *         来新知乎 做新知者 得新知也
 */
public class MainActivity extends AppCompatActivity implements OnRefreshLoadMoreListener, BaseQuickAdapter.OnItemClickListener {

    public static final String YYYYMMDD = "yyyyMMdd";
    public static final String YYYY_MM_DD = "yyyy年MM月dd日";
    private static int[] ICON = {R.drawable.menu1, R.drawable.menu2, R.drawable.menu3, R.drawable.menu4, R.drawable.menu5, R.drawable.menu6, R.drawable.menu7, R.drawable.menu8, R.drawable.menu9, R.drawable.menu10, R.drawable.menu11, R.drawable.menu12};
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private List<MySection> mSections = new ArrayList<>();
    private SectionAdapter mHomeAdapter;
    private SmartRefreshLayout mSmartRefreshLayout;
    private DrawerLayout mDrawerLayout;
    private ImageView mHeader;
    private Menu mMenu;
    private String mDateIndex;
    private SimpleDateFormat mSimpleDateFormat;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Slide slide = new Slide();
        slide.setSlideEdge(Gravity.BOTTOM);
        getWindow().setExitTransition(slide);
        findView();
        setSupportActionBar(mToolbar);
        excuteStateBar();
        initView();
        initData();


    }

    private void reSetDateIndex() {
        mSimpleDateFormat = new SimpleDateFormat(YYYYMMDD, Locale.CHINA);
        mDateIndex = mSimpleDateFormat.format(new Date());
    }

    private void dateOffset(int index) {
        try {
            Date date = mSimpleDateFormat.parse(mDateIndex);
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.add(Calendar.DAY_OF_MONTH, index);
            mDateIndex = mSimpleDateFormat.format(c.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void initView() {


        mHomeAdapter = new SectionAdapter(R.layout.item_news, R.layout.list_section, mSections);
        mHomeAdapter.setOnItemClickListener(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mHomeAdapter);


        mSmartRefreshLayout.setEnableFooterFollowWhenLoadFinished(true);
        mSmartRefreshLayout.setOnRefreshLoadMoreListener(this);


        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.app_name, R.string.app_name);
        toggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.addDrawerListener(toggle);
        mDrawerLayout.setScrimColor(Color.TRANSPARENT);
        toggle.syncState();
        mToolbar.setNavigationIcon(R.drawable.ic_menu);

        RequestOptions transform = new RequestOptions().transform(new CircleCrop());
        Glide.with(this).load("http://img4.imgtn.bdimg.com/it/u=3142504940,1251128154&fm=27&gp=0.jpg")
                .apply(transform)
                .transition(withCrossFade())
                .into(mHeader);

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            mSmartRefreshLayout.setHeaderInsetStart(UIUtil.px2dip(MainActivity.this, mToolbar.getHeight()));
        }
    }

    private void findView() {
        mToolbar = findViewById(R.id.toolbar);
        NavigationView mNavigationView = findViewById(R.id.nav_view);
        mMenu = mNavigationView.getMenu();
        mRecyclerView = findViewById(R.id.recycler);
        mSmartRefreshLayout = findViewById(R.id.refreshLayout);
        mDrawerLayout = findViewById(R.id.main_drawer_layout);
        ViewGroup headerView = (ViewGroup) mNavigationView.getHeaderView(0);
        mHeader = headerView.findViewById(R.id.header);
        removeNavigationViewScrollbar(mNavigationView);
    }

    private void initData() {
        requestTheme();
        requestList();

    }

    private void requestTheme() {
        NetWork.getZhiHuAPI().getTheme().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Themes>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(Themes themes) {
                List<Themes.Theme> mThemes = themes.mThemes;
                mMenu.add("首页").setIcon(R.drawable.menu13);
                for (int i = 0; i < mThemes.size(); i++) {
                    Themes.Theme theme = mThemes.get(i);
                    mMenu.add(theme.name).setIcon(ICON[i % ICON.length]);
                }

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void requestList() {
        reSetDateIndex();

        NetWork.getZhiHuAPI().latest()
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(new Function<News, ArrayList<MySection>>() {
                    @Override
                    public ArrayList<MySection> apply(News news) throws Exception {
                        List<News.Story> stories = news.stories;
                        ArrayList<MySection> mySections = new ArrayList<>();


                        SimpleDateFormat formatterDest = new SimpleDateFormat(YYYY_MM_DD, Locale.CHINA);
                        SimpleDateFormat formatterSource = new SimpleDateFormat(YYYYMMDD, Locale.CHINA);
                        Date date = formatterSource.parse(news.date);
                        String dString;
                        if (formatterSource.format(new Date()).equals(news.date)) {
                            dString = "今日推荐";
                        } else {
                            dString = formatterDest.format(date);
                        }
                        mySections.add(new MySection(true, dString, false));
                        for (News.Story story : stories) {
                            mySections.add(new MySection(story));
                        }
                        return mySections;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<MySection>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ArrayList<MySection> mySections) {
                        mSections.clear();
                        mHomeAdapter.addData(mySections);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        mSmartRefreshLayout.finishRefresh();
                        mSmartRefreshLayout.setNoMoreData(false);
                        onLoadMore(mSmartRefreshLayout);
                    }
                });
    }

    /**
     * 状态栏透明和间距处理
     */
    private void excuteStateBar() {
        StatusBarUtil.darkMode(this);
        StatusBarUtil.setPaddingSmart(this, mRecyclerView);
        StatusBarUtil.setPaddingSmart(this, mToolbar);
        StatusBarUtil.setPaddingSmart(this, findViewById(R.id.blurview));
    }

    private void removeNavigationViewScrollbar(NavigationView navigationView) {
        if (navigationView != null) {
            NavigationMenuView navigationMenuView = (NavigationMenuView) navigationView.getChildAt(0);
            if (navigationMenuView != null) {
                navigationMenuView.setVerticalScrollBarEnabled(false);
            }
        }
    }


    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        NetWork.getZhiHuAPI().getBeforeNews(mDateIndex)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(new Function<News, ArrayList<MySection>>() {
                    @Override
                    public ArrayList<MySection> apply(News news) throws Exception {
                        List<News.Story> stories = news.stories;
                        ArrayList<MySection> mySections = new ArrayList<>();

                        SimpleDateFormat formatterDest = new SimpleDateFormat("MM月dd日", Locale.CHINA);
                        SimpleDateFormat formatterSource = new SimpleDateFormat(YYYYMMDD, Locale.CHINA);
                        Date date = formatterSource.parse(news.date);
                        String dString;
                        if (formatterSource.format(new Date()).equals(news.date)) {
                            dString = "今日推荐";
                        } else {
                            dString = formatterDest.format(date);
                        }
                        mySections.add(new MySection(true, dString, false));
                        for (News.Story story : stories) {
                            mySections.add(new MySection(story));
                        }
                        return mySections;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<MySection>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ArrayList<MySection> mySections) {
                        mHomeAdapter.addData(mySections);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        mSmartRefreshLayout.finishLoadMore();
                        dateOffset(-1);
                    }
                });

    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        if (mHomeAdapter != null) {
            requestList();
        }
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        MySection mySection = mSections.get(position);
        if (!mySection.isHeader) {
            long id = mySection.t.id;
            ArticleActivity.launch(this, id, Pair.create(view.findViewById(R.id.image), "img"));
        }

    }


}
