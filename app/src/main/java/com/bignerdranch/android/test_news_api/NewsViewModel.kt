package com.bignerdranch.android.test2

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel

class NewsViewModel : ViewModel() {

    val newsItemViewModel: LiveData<List<NewsItem>>

    init {
        newsItemViewModel = NewsFetch().fetchNews()
    }
}