package com.burnie.zhihu.net;


import com.burnie.zhihu.bean.ArticleEntity;
import com.burnie.zhihu.bean.News;
import com.burnie.zhihu.bean.Themes;


import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;


/**
 * Created by liuli on 2018/3/8.
 */

public interface ZhiHuApi {
    @GET("4/news/latest")
    Observable<News> latest();

    @GET("4/themes")
    Observable<Themes> getTheme();

    @GET("4/news/before/{date}")
    Observable<News> getBeforeNews(@Path("date") String date);

    @GET("4/news/{id}")
    Observable<ArticleEntity> getArticleEntity(@Path("id") long id);

}
