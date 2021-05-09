package com.phi.tenatanweave.thirdparty.glide

import com.bumptech.glide.ListPreloader
import com.bumptech.glide.RequestBuilder
import com.phi.tenatanweave.data.Printing

class DeckListCardPreloadModelProvider : ListPreloader.PreloadModelProvider<Printing> {
    override fun getPreloadItems(position: Int): MutableList<Printing> {
        TODO("Not yet implemented")
    }

    override fun getPreloadRequestBuilder(item: Printing): RequestBuilder<*>? {
        TODO("Not yet implemented")
    }
}