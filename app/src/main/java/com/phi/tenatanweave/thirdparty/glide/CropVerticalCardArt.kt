package com.phi.tenatanweave.thirdparty.glide

import android.content.Context
import android.graphics.Bitmap
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.phi.tenatanweave.R
import java.security.MessageDigest
import kotlin.coroutines.coroutineContext

class CropVerticalCardArt : BitmapTransformation() {
    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
    }

    override fun transform(pool: BitmapPool, toTransform: Bitmap, outWidth: Int, outHeight: Int): Bitmap {
        return Bitmap.createBitmap(
            toTransform,
            (toTransform.width * .1333).toInt(),
            (toTransform.height * .1433).toInt(),
            (toTransform.width * .7333).toInt(),
            (toTransform.height * .44586).toInt()
        )
    }
}