package kg.healthlink.myapplication.ui.news

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kg.healthlink.myapplication.data.model.news.Article
import kg.healthlink.myapplication.databinding.ItemNewsBinding
import kg.healthlink.myapplication.extensions.glide

class NewsAdapter(private val list: List<Article>, private val onClick: (Article) -> Unit) :
    RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        return NewsViewHolder(
            ItemNewsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(list[position])
        holder.itemView.setOnClickListener { onClick(list[position]) }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    inner class NewsViewHolder(
        private val vb: ItemNewsBinding
    ) : RecyclerView.ViewHolder(vb.root) {

        fun bind(newsModel: Article) {
            vb.imageMainNews.glide(newsModel.urlToImage)
            vb.tvNewsHeadline.text = newsModel.title
        }
    }
}