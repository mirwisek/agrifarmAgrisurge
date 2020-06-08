package com.fyp.agrifarm.app.news.ui

import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.fyp.agrifarm.R
import com.fyp.agrifarm.app.gone
import com.fyp.agrifarm.app.log
import com.fyp.agrifarm.app.news.viewmodel.NewsSharedViewModel
import com.fyp.agrifarm.app.visible
import com.google.android.material.chip.Chip
import kotlinx.android.synthetic.main.fragment_news_details.*
import kotlinx.android.synthetic.main.fragment_news_details.view.*
import kotlin.random.Random

class NewsDetailsFragment : Fragment() {

    private lateinit var newsSharedViewModel: NewsSharedViewModel
    private val colorArr = arrayOf(
            R.color.chip_00, R.color.chip_01, R.color.chip_02, R.color.chip_03,
            R.color.chip_04, R.color.chip_05, R.color.chip_06, R.color.chip_07,
            R.color.chip_08, R.color.chip_09
    )

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_news_details, container, false)

        newsSharedViewModel = ViewModelProvider(requireActivity()).get(NewsSharedViewModel::class.java)

        val webView = view.newsWebView
        webView.settings.javaScriptEnabled = true
        webView.settings.displayZoomControls = true
        webView.settings.setSupportMultipleWindows(true)

        webView.webChromeClient = object: WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                if (newProgress < 100 && webProgressBar.visibility == View.GONE)
                    webProgressBar.visible()

                webProgressBar.progress = newProgress
                if (newProgress == 100)
                    webProgressBar.gone()
            }

        }

        webView.webViewClient = object: WebViewClient () {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                view?.loadUrl(request?.url?.toString())
                return true
            }

        }

        val chipGroup = view.newsCategoryChipGroup
        newsSharedViewModel.getSelectedNews().observe(viewLifecycleOwner, Observer {
            it?.let { news ->
                webView.loadUrl(news.link)
                news.categories.forEach { category ->
                    chipGroup.addView(generateNewChip(category))
                }
            }
        })
        return view
    }

    companion object {
        const val TAG = "newsDetailFragment"
    }

    private fun generateNewChip(label: String) : Chip {
        val chip = Chip(context)
        chip.text = label
        chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.colorWhite))
        chip.chipBackgroundColor = ColorStateList.valueOf(randomColor())
        return chip
    }

    private fun randomColor(): Int {
        val colorName = colorArr.random()

        return ContextCompat.getColor(requireContext(), colorName)
    }

}