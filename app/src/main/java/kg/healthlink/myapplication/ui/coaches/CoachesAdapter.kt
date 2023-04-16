package kg.healthlink.myapplication.ui.coaches

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kg.healthlink.myapplication.data.model.coaches.CoachesModel
import kg.healthlink.myapplication.databinding.ItemFitnessCoachesBinding
import kg.healthlink.myapplication.extensions.glide
import java.util.*

class CoachesAdapter(
    private val onItemClick: (CoachesModel) -> Unit
) : ListAdapter<CoachesModel, CoachesAdapter.CoachesViewHolder>(ProverbsDiffUtil()),
    Filterable {

    private var list = mutableListOf<CoachesModel>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoachesViewHolder {
        return CoachesViewHolder(
            ItemFitnessCoachesBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: CoachesViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.itemView.setOnClickListener { onItemClick(getItem(holder.adapterPosition)) }
    }

    fun setData(list: MutableList<CoachesModel>) {
        this.list = list
        submitList(list)
    }

    override fun getFilter(): Filter {
        return filter
    }

    private val filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList = mutableListOf<CoachesModel>()
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
            submitList(filterResults?.values as MutableList<CoachesModel>)
        }
    }

    inner class CoachesViewHolder(
        private val vb: ItemFitnessCoachesBinding
    ) : RecyclerView.ViewHolder(vb.root) {

        fun bind(model: CoachesModel) {
            vb.imageMainNews.glide(model.urlPhoto)
            vb.tvNewsHeadline.text = model.name
            vb.tvDescription.text = model.description
        }
    }

    private class ProverbsDiffUtil : DiffUtil.ItemCallback<CoachesModel>() {
        override fun areItemsTheSame(
            oldItem: CoachesModel,
            newItem: CoachesModel
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: CoachesModel,
            newItem: CoachesModel
        ): Boolean {
            return oldItem == newItem
        }
    }
}