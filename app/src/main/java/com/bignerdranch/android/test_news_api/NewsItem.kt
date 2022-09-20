package com.bignerdranch.android.test2

import android.net.Uri
import com.google.gson.annotations.SerializedName


data class NewsItem(
    var author: String = "",
    @SerializedName("publishedAt") var data: String = "",
    var title: String = "",
    var description: String = "",
    var url: String = "",
    var urlToImage: String = ""
) {
    val pageUri: Uri
        get() {
            return Uri.parse(url)
        }
}