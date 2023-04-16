package kg.healthlink.myapplication.ui.news

import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import kg.healthlink.myapplication.data.model.news.NewsModel
import kg.healthlink.myapplication.data.remote.network.NewsAPI
import kg.healthlink.myapplication.data.remote.Resource
import kotlinx.coroutines.Dispatchers

class NewsRepository(private val newsApi: NewsAPI) {

    fun loadRequestedNews(query: String?): LiveData<Resource<NewsModel?>> {
        return liveData(Dispatchers.IO) {
            emit(Resource.loading(null))
            val response = newsApi.getEverything(query)
            emit(
                if (response.isSuccessful) {
                    Resource.success(response.body())
                } else {
                    Resource.error(response.message(), response.body(), response.code())
                }
            )
        }
    }
}