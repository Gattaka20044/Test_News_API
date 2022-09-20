package com.bignerdranch.android.test2

import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bignerdranch.android.test2.api.NewsApi
import com.bignerdranch.android.test_news_api.R
import com.bignerdranch.android.test_news_api.databinding.NewsFragmentBinding
import com.bignerdranch.android.test_news_api.databinding.NewsItemBinding

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory

private const val TAG = "NewsFragment"

class NewsFragment : Fragment() {

    private lateinit var binding: NewsFragmentBinding

    private lateinit var newsViewModel: NewsViewModel
    private lateinit var newsRecyclerView: RecyclerView
    private lateinit var thumbnailDownloader: ThumbnailDownloader<NewsHolder>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        retainInstance = true

        newsViewModel = ViewModelProviders.of(this).get(NewsViewModel::class.java)
        val responseHandler = Handler()
        thumbnailDownloader = ThumbnailDownloader(responseHandler) { newsHolder, bitmap ->
            val drawable = BitmapDrawable(resources, bitmap)
            newsHolder.bindingClass.imageView.setImageDrawable(drawable)
        }
        lifecycle.addObserver(thumbnailDownloader.fragmentLifecycleObserver)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        newsViewModel.newsItemViewModel.observe(
            viewLifecycleOwner, Observer {

                newsRecyclerView.adapter = NewsAdapter(it)

            })
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = NewsFragmentBinding.inflate(inflater)
        newsRecyclerView = binding.recyclerNews
        newsRecyclerView.layoutManager = LinearLayoutManager(context)

        return binding.root
    }

    private inner class NewsHolder(item: View) : RecyclerView.ViewHolder(item),
        View.OnClickListener {

        val bindingClass = NewsItemBinding.bind(item)
        private lateinit var newsItem: NewsItem

        fun bind(news: NewsItem) = with(bindingClass) {
            title.text = news.title
            author.text = news.author
            data.text = news.data
            description.text = news.description
        }

        fun bindNewsItem(item: NewsItem) {
            newsItem = item
        }

        override fun onClick(view: View) {
            val intent = Intent(Intent.ACTION_VIEW, newsItem.pageUri)
            startActivity(intent)
        }

        init {
            item.setOnClickListener(this)
        }
    }

    private inner class NewsAdapter(private val newsItems: List<NewsItem>) :
        RecyclerView.Adapter<NewsHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsHolder {

            val view =
                LayoutInflater.from(parent.context).inflate(R.layout.news_item, parent, false)
            return NewsHolder(view)
        }

        override fun onBindViewHolder(holder: NewsHolder, position: Int) {

            holder.bind(newsItems[position])
            holder.bindNewsItem(newsItems[position])
            if (newsItems[position].urlToImage != null) {
                thumbnailDownloader.queueThumbnail(holder, newsItems[position].urlToImage)
            }

        }

        override fun getItemCount(): Int = newsItems.size

    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewLifecycleOwner.lifecycle.removeObserver(thumbnailDownloader.viewLifecycleObserver)
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycle.removeObserver(thumbnailDownloader.viewLifecycleObserver)
    }

    companion object {
        fun newInstance() = NewsFragment()
    }
}