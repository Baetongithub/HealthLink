package kg.healthlink.myapplication.ui.news.detailed_news

import android.annotation.SuppressLint
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import kg.healthlink.myapplication.databinding.FragmentDetailedNewsBinding
import kg.healthlink.myapplication.ui.base.BaseFragment
import kg.healthlink.myapplication.utils.Constants

class DetailedNewsFragment :
    BaseFragment<FragmentDetailedNewsBinding>(FragmentDetailedNewsBinding::inflate) {

    @SuppressLint("SetJavaScriptEnabled")
    override fun initView() {
        super.initView()
        val newsUrl = arguments?.getString(Constants.NEWS_URL)

        if (newsUrl != null) {
            vb.webView.loadUrl(newsUrl)
        }

        val webSetting: WebSettings = vb.webView.settings
        webSetting.javaScriptEnabled = true
        webSetting.loadWithOverviewMode = true

        vb.webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?
            ): Boolean {
                return super.shouldOverrideUrlLoading(view, request)
            }
        }
    }
}