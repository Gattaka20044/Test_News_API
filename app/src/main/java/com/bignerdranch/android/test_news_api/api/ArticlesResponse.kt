package com.bignerdranch.android.test2.api

import com.bignerdranch.android.test2.NewsItem
import com.google.gson.annotations.SerializedName

class ArticlesResponse {
    @SerializedName("articles")
    lateinit var newsItems: List<NewsItem>
}
