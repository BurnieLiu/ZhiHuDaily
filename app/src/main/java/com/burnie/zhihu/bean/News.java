package com.burnie.zhihu.bean;


import java.util.List;

/**
 * Created by liuli on 2018/3/8.
 */

public class News {

    public String date;
    public List<Story> stories;
    public List<TopStory> top_stories;

    public static class Story {
        public List<String> images;
        public int type;
        public long id;
        public boolean multipic;
        public String ga_prefix;
        public String title;
    }

    public static class TopStory {
        public String image;
        public int type;
        public long id;
        public String ga_prefix;
        public String title;
    }
}
