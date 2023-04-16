package kg.healthlink.myapplication.ui.news

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import kg.healthlink.myapplication.data.model.news.NewsModel
import kg.healthlink.myapplication.data.remote.Resource

class NewsViewModel(private val repository: NewsRepository) : ViewModel() {

    fun loadRequestedNews(query: String?): LiveData<Resource<NewsModel?>> {
        return repository.loadRequestedNews(query)
    }
}