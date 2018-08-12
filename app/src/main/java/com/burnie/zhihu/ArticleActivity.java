package com.burnie.zhihu;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.burnie.zhihu.bean.ArticleEntity;
import com.burnie.zhihu.net.NetWork;
import com.burnie.zhihu.util.HtmlUtil;
import com.burnie.zhihu.util.StatusBarUtil;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;


/**
 * @author liuli
 */
public class ArticleActivity extends AppCompatActivity {

    public static final String ARTICLEID = "ARTICLEID";
    private WebView mWebView;
    private long mId;
    private Toolbar mToolbar;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private View mImageBg;
    private ImageView mImageMainBg;
    private ViewGroup mRootView;
    private AppBarLayout mAppBarLayout;
    private View mSpin_kit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportPostponeEnterTransition();
//        Slide slide = new Slide(Gravity.BOTTOM);
//        getWindow().setEnterTransition(slide);
//        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        setContentView(R.layout.activity_article);
        getExtras();
        findView();
        initView();
        excuteStateBar();
        requestData();

    }

    private void initView() {
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setDefaultTextEncodingName("UTF-8");
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setSupportZoom(true);
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAfterTransition();
            }
        });
        mRootView.setTransitionGroup(true);
        scheduleStartPostponedTransition(mImageMainBg);

    }

    private void getExtras() {
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        mId = extras.getLong(ARTICLEID, -1);
    }

    private void requestData() {
        NetWork.getZhiHuAPI().getArticleEntity(mId).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArticleEntity>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ArticleEntity articleEntity) {
                        String htmlData = HtmlUtil.createHtmlData(articleEntity.body, articleEntity.css, articleEntity.js);
                        mCollapsingToolbarLayout.setTitle(articleEntity.title);
                        mWebView.loadData(htmlData, HtmlUtil.MIME_TYPE, HtmlUtil.ENCODING);
//                        RequestOptions options = new RequestOptions();
//                        options.transforms(new BolderTransformation(UIUtil.dip2px(getApplicationContext(),5)));
                        Glide.with(ArticleActivity.this).asBitmap()
                                .listener(new RequestListener<Bitmap>() {
                                    @Override
                                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                                        return false;
                                    }

                                    @Override
                                    public boolean onResourceReady(Bitmap resource, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                                        Palette.from(resource).generate(new Palette.PaletteAsyncListener() {
                                            @Override
                                            public void onGenerated(@NonNull Palette palette) {
                                                Palette.Swatch swatch = palette.getLightMutedSwatch();
                                                if (swatch == null) {
                                                    swatch = palette.getLightVibrantSwatch();
                                                }
                                                if (swatch == null) {
                                                    swatch = palette.getDominantSwatch();
                                                }
                                                if (swatch == null) {
                                                    swatch = palette.getMutedSwatch();
                                                }
                                                if (swatch == null) {
                                                    swatch = palette.getVibrantSwatch();
                                                }
                                                if (swatch == null) {
                                                    swatch = palette.getDarkMutedSwatch();
                                                }
                                                if (swatch == null) {
                                                    swatch = palette.getDarkVibrantSwatch();
                                                }
                                                if (swatch != null) {
                                                    mImageBg.setBackgroundColor(swatch.getRgb());
                                                }

                                            }
                                        });

                                        return false;
                                    }
                                })
                                .load(articleEntity.image)
//                                .apply(options)
                                .transition(withCrossFade())
                                .into(mImageMainBg);

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    private void findView() {
        mWebView = findViewById(R.id.webview);
        mRootView = findViewById(R.id.rootView);
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mSpin_kit.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mSpin_kit.setVisibility(View.GONE);
            }
        });

        mToolbar = findViewById(R.id.toolbar);
        mSpin_kit = findViewById(R.id.spin_kit);
        mImageBg = findViewById(R.id.bg);
        mImageMainBg = findViewById(R.id.mainbg);
        mCollapsingToolbarLayout = findViewById(R.id.collapsing_toolbar_layout);
        mAppBarLayout = findViewById(R.id.app_bar);


    }

    public static void launch(Activity activity, long id, Pair<View, String>... views) {
        Intent intent = new Intent(activity, ArticleActivity.class);
        Bundle bundle = new Bundle();
        bundle.putLong(ARTICLEID, id);
        intent.putExtras(bundle);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(activity, views);
        activity.startActivity(intent, options.toBundle());

    }

    private void excuteStateBar() {
        StatusBarUtil.darkMode(this);
        StatusBarUtil.setPaddingSmart(this, mToolbar);
    }

    @Override
    protected void onDestroy() {
        ViewParent parent = mWebView.getParent();
        if (parent != null) {
            ((ViewGroup) parent).removeView(mWebView);
        }
        mWebView.stopLoading();
        // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
        mWebView.getSettings().setJavaScriptEnabled(false);
        mWebView.clearHistory();
        mWebView.clearView();
        mWebView.removeAllViews();
        mWebView.destroy();
        super.onDestroy();
//        ZhihuApp.getRefWatcher(getApplicationContext()).watch(this);
    }

    private void scheduleStartPostponedTransition(final View sharedElement) {
        sharedElement.getViewTreeObserver().addOnPreDrawListener(
                new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        sharedElement.getViewTreeObserver().removeOnPreDrawListener(this);
                        supportStartPostponedEnterTransition();
                        return true;
                    }
                });
    }


}
