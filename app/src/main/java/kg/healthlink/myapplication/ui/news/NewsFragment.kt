package kg.healthlink.myapplication.ui.news

import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kg.healthlink.myapplication.R
import kg.healthlink.myapplication.data.model.news.Article
import kg.healthlink.myapplication.data.remote.Status
import kg.healthlink.myapplication.databinding.FragmentNewsBinding
import kg.healthlink.myapplication.extensions.toast
import kg.healthlink.myapplication.ui.base.BaseFragment
import kg.healthlink.myapplication.utils.Constants
import org.koin.androidx.viewmodel.ext.android.viewModel

class NewsFragment : BaseFragment<FragmentNewsBinding>(FragmentNewsBinding::inflate) {

    private val listMainNews = arrayListOf<Article>()
    private val adapterMainNews: NewsAdapter by lazy {
        NewsAdapter(
            listMainNews,
            this::onClickNews
        )
    }

    private val viewModel: NewsViewModel by viewModel()

    override fun initView() {
        super.initView()
        loadRequestedNews("спорт")
    }

    private fun loadRequestedNews(s: String) {
        viewModel.loadRequestedNews(s).observe(this) { resource ->
            when (resource.status) {
                Status.SUCCESS -> {
                    vb.progressBar.visibility = GONE
                    listMainNews.clear()
                    if (resource.data != null)
                        listMainNews.addAll(resource.data.articles)
                    setUpNewsRecyclerView()
                }
                Status.ERROR -> {
                    vb.progressBar.visibility = GONE
                    toast("Error " + resource.message)
                }
                Status.LOADING -> vb.progressBar.visibility = VISIBLE
            }
        }
    }

    private fun setUpNewsRecyclerView() {
        vb.recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = adapterMainNews
        }
    }

    private fun onClickNews(article: Article) {
        val bundle = Bundle()
        bundle.putString(Constants.NEWS_URL, article.url)
        findNavController().navigate(R.id.action_navigation_news_to_detailedNewsFragment, bundle)
    }
}