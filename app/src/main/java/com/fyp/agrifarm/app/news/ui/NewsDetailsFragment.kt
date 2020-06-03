package com.fyp.agrifarm.app.news.ui

import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.fyp.agrifarm.R
import com.fyp.agrifarm.app.log
import com.fyp.agrifarm.app.news.viewmodel.NewsSharedViewModel
import com.squareup.picasso.Picasso
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL


class NewsDetailsFragment : Fragment() {

    private lateinit var newsSharedViewModel: NewsSharedViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_news_details, container, false)
        val title = view.findViewById<TextView>(R.id.tvNewsTitleDetail)
        val date = view.findViewById<TextView>(R.id.tvNewsDate)
        val desc = view.findViewById<TextView>(R.id.tvNewsDesc)
        val imageView = view.findViewById<ImageView>(R.id.detailNewsImage)


        newsSharedViewModel = ViewModelProvider(requireActivity()).get(NewsSharedViewModel::class.java)

        newsSharedViewModel.getSelectedNews().observe(viewLifecycleOwner, Observer {
            it?.let { news ->
                Picasso.get().load(news.image).placeholder(R.color.colorPrimaryDark).into(imageView)
                title.text = news.title
                date.text = news.guid
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    desc.text = Html.fromHtml(news.link, Html.FROM_HTML_MODE_COMPACT)
                } else {
                    desc.text = Html.fromHtml(news.link)
                }
                log(news.title)
                // Make links clickable
                desc.movementMethod = LinkMovementMethod.getInstance()
            }
        })
        return view
    }

    companion object {
        const val TAG = "newsDetailFragment"
    }

}