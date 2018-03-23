package com.layer.xdk.ui.message.image.cache;

import android.graphics.Bitmap;
import android.widget.ImageView;

public interface ImageCacheWrapper {

    /**
     * Fetch Bitmap from any Image Caching Library
     * Set the Bitmap on the BitmapWrapper in your implementation
     * @see BitmapWrapper#setBitmap(Bitmap)
     * Pass the BitmapWrapper to the onSuccess of Callback
     * @see PicassoImageCacheWrapper for sample implementation
     * @param bitmapWrapper
     */
    void fetchBitmap(BitmapWrapper bitmapWrapper, Callback callback);

    /**
     * Makes call to cancel BitMap request
     * @see PicassoImageCacheWrapper for sample implementation
     * @param bitmapWrapper
     */
    void cancelBitmap(BitmapWrapper bitmapWrapper);

    @SuppressWarnings("unused")
    void pauseTag(String tag);

    @SuppressWarnings("unused")
    void resumeTag(String tag);

    void loadImage(ImageRequestParameters imageRequestParameters, ImageView imageView);

    void loadDefaultPlaceholder(ImageView imageView);

    /**
     * Callback when the Bitmap or Image is loaded from the ImageCache Library
     */
    interface Callback {
        void onSuccess();
        void onFailure();
    }
}
