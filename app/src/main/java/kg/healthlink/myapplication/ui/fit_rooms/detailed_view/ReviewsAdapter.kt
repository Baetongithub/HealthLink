package kg.healthlink.myapplication.ui.fit_rooms.detailed_view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kg.healthlink.myapplication.data.model.reviews.ReviewsModel
import kg.healthlink.myapplication.databinding.ItemReviewsBinding

class ReviewsAdapter(private val onItemClick: (ReviewsModel) -> Unit) :
    ListAdapter<ReviewsModel, ReviewsAdapter.ReviewViewHolder>(ProverbsDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        return ReviewViewHolder(
            ItemReviewsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.itemView.setOnClickListener { onItemClick(getItem(position)) }
    }

    inner class ReviewViewHolder(
        private val vb: ItemReviewsBinding
    ) : RecyclerView.ViewHolder(vb.root) {

        fun bind(model: ReviewsModel) {
            vb.tvReviewContent.text = model.review
            vb.ratingBar.rating = model.rating.toFloat()
            vb.tvReviewOwner.text = model.reviewOwner
        }
    }

    private class ProverbsDiffUtil : DiffUtil.ItemCallback<ReviewsModel>() {
        override fun areItemsTheSame(
            oldItem: ReviewsModel,
            newItem: ReviewsModel
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: ReviewsModel,
            newItem: ReviewsModel
        ): Boolean {
            return oldItem == newItem
        }
    }
}