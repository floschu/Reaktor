package at.florianschuster.githubexample

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_repo.view.*

class RepoAdapter : ListAdapter<Repo, RepoViewHolder>(repoDiff) {
    var onClick: ((Repo) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoViewHolder =
        RepoViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_repo, parent, false))

    override fun onBindViewHolder(holder: RepoViewHolder, position: Int): Unit = holder.bind(getItem(position), onClick)
}

private val repoDiff: DiffUtil.ItemCallback<Repo> = object : DiffUtil.ItemCallback<Repo>() {
    override fun areItemsTheSame(oldItem: Repo, newItem: Repo): Boolean = oldItem.id == newItem.id
    override fun areContentsTheSame(oldItem: Repo, newItem: Repo): Boolean = oldItem == newItem
}

class RepoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    fun bind(repo: Repo, onClick: ((Repo) -> Unit)?) {
        itemView.setOnClickListener { onClick?.invoke(repo) }
        itemView.tvRepoName.text = repo.name
    }
}