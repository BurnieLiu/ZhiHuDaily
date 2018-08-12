package com.burnie.zhihu.adapter;


import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.burnie.zhihu.BolderTransformation;
import com.burnie.zhihu.R;
import com.burnie.zhihu.bean.MySection;
import com.burnie.zhihu.bean.News;
import com.burnie.zhihu.util.UIUtil;
import com.chad.library.adapter.base.BaseSectionQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;


/**
 * https://github.com/CymChad/BaseRecyclerViewAdapterHelper
 */
public class SectionAdapter extends BaseSectionQuickAdapter<MySection, BaseViewHolder> {
    /**
     * Same as QuickAdapter#QuickAdapter(Context,int) but with
     * some initialization data.
     *
     * @param sectionHeadResId The section head layout id for each item
     * @param layoutResId      The layout resource id of each item.
     * @param data             A new list is created out of this one to avoid mutable list
     */
    public SectionAdapter(int layoutResId, int sectionHeadResId, List data) {
        super(layoutResId, sectionHeadResId, data);
    }

    @Override
    protected void convertHead(BaseViewHolder helper, final MySection item) {
        helper.setText(R.id.sectiontitle, item.header);
    }


    @Override
    protected void convert(final BaseViewHolder helper, MySection item) {
        News.Story story = item.t;
        helper.setText(R.id.title, story.title);
        helper.setVisible(R.id.tip, story.multipic ? true : false);
        String url = story.images.get(0);
//        RequestOptions options = new RequestOptions();
//        options.transforms(new BolderTransformation(UIUtil.dip2px(mContext, 5)));

        Glide.with(mContext).asBitmap().load(url)
//                .apply(options)
                .transition(withCrossFade())
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
                                    View view = helper.getView(R.id.back);
                                    view.setBackgroundColor(swatch.getRgb());
                                }

                            }
                        });
                        return false;
                    }
                })
                .into((ImageView) helper.getView(R.id.image));

    }
}
