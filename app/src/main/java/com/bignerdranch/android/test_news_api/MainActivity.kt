package com.bignerdranch.android.test_news_api

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bignerdranch.android.test2.NewsFragment
import com.bignerdranch.android.test_news_api.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val isFragmentContainerEmpty = savedInstanceState == null
        if (isFragmentContainerEmpty) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragmentContainer, NewsFragment.newInstance())
                .commit()
        }

    }


}