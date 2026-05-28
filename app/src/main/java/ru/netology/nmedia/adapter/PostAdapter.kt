package ru.netology.nmedia.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.utils.completionPost

typealias LikeListener = (Post) -> Unit
typealias ShareListener = (Post) -> Unit
typealias ViewListener = (Post) -> Unit

class PostAdapter(
    private val likeListener: LikeListener,
    private val shareListener: ShareListener,
    private val viewListener: ViewListener
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

        return PostViewHolder(binding, likeListener, shareListener, viewListener)
    }

    override fun onBindViewHolder(viewHolder: PostViewHolder, position: Int) {
        val post = getItem(position)
        viewHolder.bind(post)
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,
    private val likeListener: LikeListener,
    private val shareListener: ShareListener,
    private val viewListener: ViewListener
): RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
        completionPost(
            post = post,
            likeListener,
            shareListener,
            viewListener,
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