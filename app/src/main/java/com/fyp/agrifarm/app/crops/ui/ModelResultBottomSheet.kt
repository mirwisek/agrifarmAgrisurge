package com.fyp.agrifarm.app.crops.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.TextView
import com.fyp.agrifarm.R
import com.fyp.agrifarm.app.gone
import com.fyp.agrifarm.app.log
import com.fyp.agrifarm.app.visible
import kotlinx.android.synthetic.main.bottomsheet_model_content.view.*
import kotlinx.android.synthetic.main.fragment_news_details.*


class ModelResultBottomSheet(query: String) : BaseBottomSheet() {

    private lateinit var webView: WebView
    private lateinit var modelResult: TextView
    private val result = query


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view: View = inflater.inflate(R.layout.bottomsheet_model_content, container, false)

        modelResult = view.findViewById(R.id.tvModelResult)
        webView = view.findViewById(R.id.modelWebView)
        val progressBar = view.modelProgress

        webView.settings.javaScriptEnabled = true

        webView.webChromeClient = object: WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                if (newProgress < 100 && progressBar.visibility != View.VISIBLE)
                    progressBar.visible()

                progressBar.progress = newProgress
                if (newProgress == 100)
                    progressBar.gone()
            }

        }

        webView.webViewClient = object: WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                view?.loadUrl(request?.url?.toString())
                return true
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                progressBar.gone()
            }

        }

        this.bottomSheet.setOnKeyListener { dialogInterface, keyCode, keyEvent ->
            if(keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
                webView.goBack()
                true
            } else {
                false
            }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        modelResult.text = result
        webView.loadUrl(getURL(result))

    }

    private fun getURL(query: String): String {
        val url = "https://www.google.com/search?q=${query.replace(" ", "+")}"
        log("GOT URL:: ${url}")

        return url
    }


}
