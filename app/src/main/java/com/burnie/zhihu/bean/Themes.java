package com.burnie.zhihu.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by liuli on 2018/3/15.
 */

public class Themes {
    public static class Theme {
        public String thumbnail;
        public String description;
        public int id;
        public String name;
    }

    @SerializedName("others")
    public List<Theme> mThemes;
}
