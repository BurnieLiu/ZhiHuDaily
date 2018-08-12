package com.burnie.zhihu.bean;

import java.util.List;

/**
 * Created by liuli on 2018/3/19.
 */

public class ArticleEntity {
    /**
     * body : <div class="main-wrap content-wrap">...</div>
     * image_source : 《四月物语》
     * title : 我喜欢你，但你别喜欢我：囚禁在单相思中的性单恋者
     * image : http://pic3.zhimg.com/4d37a2dff96d07f6a01e7b8aabd63032.jpg
     * share_url : http://daily.zhihu.com/story/9100667
     * js : []
     * ga_prefix : 122713
     * images : ["http://pic4.zhimg.com/a32e73507ebe9a963f48c3bcc9808773.jpg"]
     * type : 0
     * id : 9100667
     * css : ["http://news-at.zhihu.com/css/news_qa.auto.css?v=4b3e3"]
     */

    public String body;
    public String image_source;
    public String title;
    public String image;
    public String share_url;
    public String ga_prefix;
    public int type;
    public int id;
    public List<String> js;
    public List<String> images;
    public List<String> css;
    public String recommenders;
}
