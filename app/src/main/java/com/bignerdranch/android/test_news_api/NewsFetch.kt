package com.bignerdranch.android.test2

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bignerdranch.android.test2.api.ArticlesResponse
import com.bignerdranch.android.test2.api.NewsApi
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val TAG = "NewsFetch"

class NewsFetch {

    private val newsApi: NewsApi

    init {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://newsapi.org/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        newsApi = retrofit.create(NewsApi::class.java)
    }

    fun fetchNews(): LiveData<List<NewsItem>> {
        val responseLiveData: MutableLiveData<List<NewsItem>> = MutableLiveData()
        val newsRequest: Call<ArticlesResponse> = newsApi.fetchNews()

        newsRequest.enqueue(object : Callback<ArticlesResponse> {
            override fun onFailure(call: Call<ArticlesResponse>, t: Throwable) {
            }

            override fun onResponse(
                call: Call<ArticlesResponse>,
                response: Response<ArticlesResponse>
            ) {
                val newsResponse: ArticlesResponse? = response.body()
                val newsItems: List<NewsItem> = newsResponse?.newsItems
                    ?: mutableListOf()

                responseLiveData.value = newsItems
            }
        })
        return responseLiveData
    }

    @WorkerThread
    fun fetchPhoto(url: String): Bitmap? {
        val response: Response<ResponseBody> = newsApi.funUrlBytes(url).execute()
        val bitmap = response.body()?.byteStream()?.use(BitmapFactory::decodeStream)
        return bitmap
    }

}