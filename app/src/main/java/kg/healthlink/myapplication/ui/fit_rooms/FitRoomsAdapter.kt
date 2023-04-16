package kg.healthlink.myapplication.ui.fit_rooms

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kg.healthlink.myapplication.data.model.fit_rooms.FitRoomsModel
import kg.healthlink.myapplication.databinding.ItemFitnessCoachesBinding
import kg.healthlink.myapplication.extensions.glide
import java.util.*

class FitRoomsAdapter(
    private val onClickItem: (FitRoomsModel) -> Unit
) : ListAdapter<FitRoomsModel, FitRoomsAdapter.FitnessViewHolder>(ProverbsDiffUtil()),
    Filterable {

    private var list = mutableListOf<FitRoomsModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FitnessViewHolder {
        return FitnessViewHolder(
            ItemFitnessCoachesBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: FitnessViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.itemView.setOnClickListener { onClickItem(getItem(holder.adapterPosition)) }
    }

    fun setData(list: MutableList<FitRoomsModel>) {
        this.list = list
        submitList(list)
    }

    override fun getFilter(): Filter {
        return filter
    }

    private val filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList = mutableListOf<FitRoomsModel>()
            if (constraint == null || constraint.isEmpty()) {
                filteredList.addAll(list)
            } else {
                for (item in list) {
                    if (item.name.lowercase(Locale.getDefault())
                            .startsWith(constraint.toString().lowercase(Locale.getDefault()))
                    ) {
                        filteredList.add(item)
                    }
                }
            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        override fun publishResults(constraint: CharSequence?, filterResults: FilterResults?) {
            submitList(filterResults?.values as MutableList<FitRoomsModel>)
        }
    }

    inner class FitnessViewHolder(
        private val vb: ItemFitnessCoachesBinding
    ) : RecyclerView.ViewHolder(vb.root) {

        fun bind(model: FitRoomsModel) {
            vb.imageMainNews.glide(model.urlPhoto)
            vb.tvNewsHeadline.text = model.name
            vb.tvDescription.text = model.description
        }
    }

    private class ProverbsDiffUtil : DiffUtil.ItemCallback<FitRoomsModel>() {
        override fun areItemsTheSame(
            oldItem: FitRoomsModel,
            newItem: FitRoomsModel
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: FitRoomsModel,
            newItem: FitRoomsModel
        ): Boolean {
            return oldItem == newItem
        }
    }
}