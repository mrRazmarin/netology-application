package ru.netology.nmedia.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.utils.AndroidUtils.completionPost

interface PostListener{
    fun onLike(post: Post)
    fun onShare(post: Post)
    fun onView(post: Post)
    fun onRemove(post: Post)
    fun onEdit(post: Post)
}

class PostAdapter(
    private val postListener: PostListener
): ListAdapter<Post, PostViewHolder>(PostDiffUtilCallBack) {

    override fun onCreateViewHolder(
        viewGroup: ViewGroup,
        viewType: Int
    ): PostViewHolder {
        val binding = CardPostBinding.inflate(
            LayoutInflater.from(viewGroup.context),
            viewGroup,
            false
        )

        return PostViewHolder(
            binding, postListener
        )
    }

    override fun onBindViewHolder(viewHolder: PostViewHolder, position: Int) {
        val post = getItem(position)
        viewHolder.bind(post)
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val postListener: PostListener
): RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
        completionPost(
            post = post,
            postListener,
            cardPostBinding = binding
        )
    }
}

object PostDiffUtilCallBack: DiffUtil.ItemCallback<Post>() {
    override fun areItemsTheSame(
        oldItem: Post,
        newItem: Post
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: Post,
        newItem: Post
    ): Boolean {
        return oldItem == newItem
    }
}