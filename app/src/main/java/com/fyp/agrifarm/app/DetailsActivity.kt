package com.fyp.agrifarm.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.fyp.agrifarm.R
import com.fyp.agrifarm.app.news.db.NewsEntity
import com.fyp.agrifarm.app.news.ui.NewsDetailsFragment
import com.fyp.agrifarm.app.news.viewmodel.NewsSharedViewModel

class DetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        val fm = supportFragmentManager
        val intent = intent
        val mode = intent.getStringExtra(MODE)
        when (mode) {
            MODE_NEWS -> {
                val title = intent.getStringExtra(KEY_TITLE)
                val desc = intent.getStringExtra(KEY_DESCRIPTION)
                val image = intent.getStringExtra(KEY_IMAGE)
                val date = intent.getStringExtra(KEY_DATE)
                val newsSharedViewModel = ViewModelProvider(this).get(NewsSharedViewModel::class.java)
                newsSharedViewModel.selectNews(NewsEntity(title, desc, image, date))

                var fragment = fm.findFragmentByTag(NewsDetailsFragment.TAG)
                fragment = fragment ?: NewsDetailsFragment()
                fragTransaction(fragment).commit()
            }
        }
    }

    companion object {
        const val MODE = "mode"
        const val MODE_NEWS = "newsMode"
        const val MODE_YOUTUBE = "youtubeMode"
        const val MODE_PRICE = "priceMode"
        const val KEY_TITLE = "title"
        const val KEY_DESCRIPTION = "desc"
        const val KEY_IMAGE = "image"
        const val KEY_DATE = "date"
    }
}